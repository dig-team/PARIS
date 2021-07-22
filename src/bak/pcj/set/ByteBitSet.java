/*
 *  Primitive Collections for Java.
 *  Copyright (C) 2002, 2003  S&oslash;ren Bak
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bak.pcj.set;

import bak.pcj.ByteIterator;
import bak.pcj.ByteCollection;
import bak.pcj.util.Exceptions;
import java.util.NoSuchElementException;

import java.io.Serializable;

/**
 *  This class represents bit array based sets of byte values. When a
 *  bit in the underlying array is set, the value having the same
 *  number as the bit is contained in the array. This implies that
 *  bit sets cannot contain negative values.
 *
 *  <p>Implementation of
 *  ByteSortedSet is supported from PCJ 1.2. Prior to 1.2, only ByteSet
 *  was implemented. 
 *
 *  <p>Note: There is no growth policy involved with bit sets. The number
 *  of bits to use depends on the value of the largest element and not
 *  the size of the set. While sizes are predictable (they grow), a
 *  new maximum element is generally not predictable making it
 *  meaningless to grow the array at a specific rate.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     21-08-2003 19:54
 *  @since      1.0
 */
public class ByteBitSet extends AbstractByteSet implements ByteSortedSet, Cloneable, Serializable {

    private static final int BITS_PER_LONG = 64;
    private static final int BIT_MASK = 0x0000003F;
    private static final int BIT_MASK_BITS = 6;
    private static final int DEFAULT_CAPACITY = BITS_PER_LONG;

    /**
     *  The array of bits backing up this set.
     *  @serial
     */
    private long[] data;

    /**
     *  The size of this set.
     *  @serial
     */
    private int size;

    /**
     *  Creates a new bit set with a specified maximum value.
     *
     *  @param      maximum
     *              the maximum value representable by the new bitset.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     */
    public ByteBitSet(byte maximum) {
        if (maximum < 0)
            Exceptions.negativeArgument("maximum", String.valueOf(maximum));
        data = new long[1+longIndex(maximum)];
        size = 0;
    }

    /**
     *  Creates a new empty bit set with a capacity of 64.
     */
    public ByteBitSet() {
        this((byte)DEFAULT_CAPACITY);
    }

    /**
     *  Creates a new bit set with the same elements as the specified
     *  collection.
     *
     *  @param      c
     *              the collection whose elements to add to the new
     *              bit set.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalArgumentException
     *              if any of the elements of the specified collection
     *              is negative.
     */
    public ByteBitSet(ByteCollection c) {
        this();
        addAll(c);
    }

    /**
     *  Creates a new bit set with the same elements as the specified
     *  array.
     *
     *  @param      a
     *              the array whose elements to add to the new
     *              bit set.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalArgumentException
     *              if any of the elements of the specified array
     *              is negative.
     *
     *  @since      1.1
     */
    public ByteBitSet(byte[] a) {
        //  Find max element n order to avoid repeated capacity increases
        this(amax(a));
        //  Add all elements
        for (int i = 0; i < a.length; i++)
            add(a[i]);
    }

    private static byte amax(byte[] a) {
        byte max = (byte)0;
        for (int i = 0; i < a.length; i++)
            if (a[i] > max) max = a[i];
        return max;
    }

    // ---------------------------------------------------------------
    //      Bit management
    // ---------------------------------------------------------------

    private static int longIndex(int index)
    { return index >> BIT_MASK_BITS; }

    private static int bitIndex(int index)
    { return index & BIT_MASK; }

    private static long bit(int bitno)
    { return 1L << bitno; }

    private static int largestBitIndexOf(long v) {
        if (v == 0L)
            throw new IllegalArgumentException("No elements left");
        int bitIndex = BITS_PER_LONG-1;
        long bit = 1L << bitIndex;
        while ((v & bit) == 0L) {
            bitIndex--;
            bit >>= 1;
        }
        return bitIndex;
    }

    private static int smallestBitIndexOf(long v) {
        if (v == 0L)
            throw new IllegalArgumentException("No elements left");
        int bitIndex = 0;
        long bit = 1L;
        while ((v & bit) == 0L) {
            bitIndex++;
            bit <<= 1;
        }
        return bitIndex;
    }

    private static int countBits(long v) {
        int count = 0;
        int bitIndex = 0;
        long bit = 1L;
        do {
            if ((v & bit) != 0L)
                count++;
            bitIndex++;
            bit <<= 1;
        } while (bitIndex < BITS_PER_LONG);
        return count;
    }

    private static long lowMask(int n) {
        long v = 0L;
        for (int i = 0; i < n; i++)
            v = (v << 1) | 1L;
        return v;
    }

    private static long highMask(int n) {
        return ~lowMask(n);
    }


    /**
     *  Ensures that this bit set can contain a specified maximum
     *  element without increasing the capacity. If many elements are
     *  added, and the maximum element among those is known before
     *  they are added, this method may improve performance.
     *
     *  @param      maximum
     *              the maximum element that this set should be able
     *              to contain without increasing the capacity.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>maximum</tt> is negative.
     */
    public void ensureCapacity(int maximum) {
        if (maximum < 0)
            Exceptions.negativeArgument("maximum", String.valueOf(maximum));
        int newcapacity = 1+longIndex(maximum);
        if (data.length < newcapacity) {
            long[] newdata = new long[newcapacity];
            System.arraycopy(data, 0, newdata, 0, data.length);
            data = newdata;
        }
    }

    // ---------------------------------------------------------------
    //      Operations not supported by abstract implementation
    // ---------------------------------------------------------------

    /**
     *  @throws     IllegalArgumentException
     *              if <tt>value</tt> is negative.
     */
    public boolean add(byte value) {
        if (value < 0)
            Exceptions.negativeArgument("value", String.valueOf(value));
        int longIndex = longIndex(value);
        if (data.length < 1+longIndex)
            ensureCapacity(value);
        long bit = bit(bitIndex(value));
        boolean result = (data[longIndex] & bit) == 0;
        if (result)
            size++;
        data[longIndex] |= bit;
        return result;
    }

    public ByteIterator iterator() {
        if (size == 0)
            return new ByteIterator() {
                public boolean hasNext()
                { return false; }
                public byte next()
                { Exceptions.endOfIterator(); throw new RuntimeException(); }
                public void remove()
                { Exceptions.noElementToRemove(); }
            };
        return new ByteIterator() {
            int nextLongIndex = nextLongIndex(0);
            int nextBitIndex = nextLongIndex < data.length ? nextBitIndex(nextLongIndex, 0) : 0;
            int lastValue = -1;

            int nextLongIndex(int index) {
                while (index < data.length && data[index] == 0)
                    index++;
                return index;
            }

            int nextBitIndex(int longIndex, int bitIndex) {
                long v = data[longIndex];
                long bit = 1L << bitIndex;
                while (bitIndex < BITS_PER_LONG && (v & bit) == 0) {
                    bitIndex++;
                    bit <<= 1;
                }
                return bitIndex;
            }

            public boolean hasNext() {
                return nextLongIndex < data.length;
            }

            public byte next() {
                if (!hasNext())
                    Exceptions.endOfIterator();
                lastValue = (byte)(nextLongIndex*BITS_PER_LONG + nextBitIndex);

                //  Advance pointers
                nextBitIndex = nextBitIndex(nextLongIndex, nextBitIndex+1);
                if (nextBitIndex == BITS_PER_LONG) {
                    nextLongIndex = nextLongIndex(nextLongIndex+1);
                    if (nextLongIndex < data.length)
                        nextBitIndex = nextBitIndex(nextLongIndex, 0);
                }
                return (byte)lastValue;
            }

            public void remove() {
                if (lastValue < 0)
                    Exceptions.noElementToRemove();
                ByteBitSet.this.remove((byte)lastValue);
                lastValue = -1;
            }

        };
    }

    /**
     *  Minimizes the memory used by this bit set. The underlying
     *  array is replaced by an array whose size corresponds to
     *  the maximum elements in this bit set. The method can be used to
     *  free up memory after many removals.
     */
    public void trimToSize() {
        //  Find maximum element
        int n = data.length-1;
        while (n >= 0 && data[n] == 0L)
            n--;
        //  Trim
        if (n < data.length-1) {
            long[] newdata = new long[1+n];
            System.arraycopy(data, 0, newdata, 0, newdata.length);
            data = newdata;
        }
    }

    /**
     *  Returns a clone of this bit set.
     *
     *  @return     a clone of this bit set.
     *
     *  @since      1.1
     */
    public Object clone() {
        try {
            ByteBitSet c = (ByteBitSet)super.clone();
            c.data = new long[data.length];
            System.arraycopy(data, 0, c.data, 0, data.length);
            return c;
        } catch (CloneNotSupportedException e) {
            Exceptions.cloning(); throw new RuntimeException();
        }
    }

    // ---------------------------------------------------------------
    //      Operations overwritten for efficiency
    // ---------------------------------------------------------------

    public void clear() {
        for (int i = 0; i < data.length; i++)
            data[i] = 0;
        size = 0;
    }

    public boolean contains(byte value) {
        if (value < 0)
            return false;
        int longIndex = longIndex(value);
        if (longIndex >= data.length)
            return false;
        long bit = bit(bitIndex(value));
        return (data[longIndex] & bit) != 0;
    }

    public boolean isEmpty()
    { return size == 0; }

    public boolean remove(byte value) {
        if (value < 0)
            return false;
        int longIndex = longIndex(value);
        if (longIndex >= data.length)
            return false;
        long bit = bit(bitIndex(value));
        boolean result = (data[longIndex] & bit) != 0;
        if (result)
            size--;
        data[longIndex] &= ~bit;
        return result;
    }

    public int size()
    { return size; }

    // ---------------------------------------------------------------
    //      Sorted set operations
    // ---------------------------------------------------------------

    private byte firstFrom(byte from) {
        if (size == 0)
            Exceptions.setNoFirst();
        int longIndex = longIndex(from);
        if (longIndex >= data.length)
            Exceptions.setNoFirst();
        long v = data[longIndex];
        //  Mask out all bits less than from
        v &= highMask(bitIndex(from));
        
        try {
            for (;;) {
                if (v != 0L) {
                    int bitIndex = smallestBitIndexOf(v);
                    return (byte)(BITS_PER_LONG*longIndex + bitIndex);
                }
                v = data[++longIndex];
            }
        } catch (IndexOutOfBoundsException e) {
            Exceptions.setNoFirst(); throw new RuntimeException();
        }
    }

    /**
     *  @since      1.2
     */
    public byte first() {
        return firstFrom((byte)0);
    }

    private byte lastFrom(byte from) {
        if (size == 0)
            Exceptions.setNoLast();
        int longIndex = Math.min(longIndex(from), data.length-1);
        long v = data[longIndex];
        //  Mask out all bits greater than from
        v &= lowMask(bitIndex(from)+1);
        try {
            for (;;) {
                if (v != 0L) {
                    int bitIndex = largestBitIndexOf(v);
                    return (byte)(BITS_PER_LONG*longIndex + bitIndex);
                }
                v = data[--longIndex];
            }
        } catch (IndexOutOfBoundsException e) {
            Exceptions.setNoLast(); throw new RuntimeException();
        }
    }

    /**
     *  @since      1.2
     */
    public byte last() {
        if (size == 0)
            Exceptions.setNoLast();
        int longIndex = data.length-1;
        //  Find last non-zero long
        while (data[longIndex] == 0)
            longIndex--;
        long v = data[longIndex];
        int bitIndex = BITS_PER_LONG-1;
        long bit = 1L << bitIndex;
        while ((v & bit) == 0) {
            bitIndex--;
            bit >>= 1;
        }
        return (byte)(BITS_PER_LONG*longIndex + bitIndex);
    }

    /**
     *  @since      1.2
     */
    public ByteSortedSet headSet(byte to) {
        return new SubSet(false, (byte)0, true, to);
    }

    /**
     *  @since      1.2
     */
    public ByteSortedSet tailSet(byte from) {
        return new SubSet(true, from, false, (byte)0);
    }

    /**
     *  @since      1.2
     */
    public ByteSortedSet subSet(byte from, byte to) {
        return new SubSet(true, from, true, to);
    }

    private class SubSet extends AbstractByteSet implements ByteSortedSet, java.io.Serializable {

        private boolean hasLowerBound;
        private boolean hasUpperBound;
        private byte lowerBound;
        private byte upperBound;

        SubSet(boolean hasLowerBound, byte lowerBound, boolean hasUpperBound, byte upperBound) {
            if (hasLowerBound) {
                if (lowerBound < 0)
                    Exceptions.negativeArgument("lower bound", String.valueOf(lowerBound));
                if (hasUpperBound)
                    if (upperBound < lowerBound)
                        Exceptions.invalidSetBounds(String.valueOf(lowerBound), String.valueOf(upperBound));
            }
            this.hasLowerBound = hasLowerBound;
            this.lowerBound = lowerBound;
            this.hasUpperBound = hasUpperBound;
            this.upperBound = upperBound;
        }

        public boolean add(byte v) {
            if (!inSubRange(v))
                Exceptions.valueNotInSubRange(String.valueOf(v));
            return ByteBitSet.this.add(v);
        }

        public boolean remove(byte v) {
            if (!inSubRange(v))
                Exceptions.valueNotInSubRange(String.valueOf(v));
            return ByteBitSet.this.remove(v);
        }

        public boolean contains(byte v) {
            return inSubRange(v) && ByteBitSet.this.contains(v);
        }

        class SubSetIterator implements ByteIterator {
            int longIndexLow;
            int longIndexHigh;
            long vLow;
            long vHigh;
            boolean isEmpty;

            int nextLongIndex;
            int nextBitIndex;
            int lastValue;

            SubSetIterator() {
                lastValue = -1;
                isEmpty = false;
                try {
                    longIndexLow = longIndex(first());
                } catch (NoSuchElementException e) {
                    isEmpty = true;
                }
                if (!isEmpty) {
                    longIndexHigh = longIndex(last());
                    if (longIndexLow == longIndexHigh) {
                        long v = data[longIndexLow];
                        //  Mask out all bits less than the lower bound
                        if (hasLowerBound)
                            v &= highMask(bitIndex(lowerBound));
                        //  Mask out all bits greater than or equal to the upper bound
                        if (hasUpperBound)
                            v &= lowMask(bitIndex(upperBound));
                        size = countBits(v);
                        vLow = vHigh = v;
                    } else {
                        //  Mask out all bits less than the lower bound
                        vLow = data[longIndexLow];
                        if (hasLowerBound)
                            vLow &= highMask(bitIndex(lowerBound));
                        
                        //  Mask out all bits greater than or equal to the upper bound
                        vHigh = data[longIndexHigh];
                        if (hasUpperBound)
                            vHigh &= lowMask(bitIndex(upperBound));
                    }
                    nextLongIndex = longIndexLow;
                    nextBitIndex = smallestBitIndexOf(vLow);
                }
            }

            long data(int longIndex) {
                if (longIndex == longIndexLow)
                    return vLow;
                if (longIndex == longIndexHigh)
                    return vHigh;
                return data[longIndex];
            }

            int nextLongIndex(int index) {
                while (index <= longIndexHigh && data(index) == 0)
                    index++;
                return index;
            }

            int nextBitIndex(int longIndex, int bitIndex) {
                long v = data(longIndex);
                long bit = 1L << bitIndex;
                while (bitIndex < BITS_PER_LONG && (v & bit) == 0) {
                    bitIndex++;
                    bit <<= 1;
                }
                return bitIndex;
            }

            public boolean hasNext() {
                return (!isEmpty) && (nextLongIndex <= longIndexHigh);
            }

            public byte next() {
                if (!hasNext())
                    Exceptions.endOfIterator();
                lastValue = (byte)(nextLongIndex*BITS_PER_LONG + nextBitIndex);

                //  Advance pointers
                nextBitIndex = nextBitIndex(nextLongIndex, nextBitIndex+1);
                if (nextBitIndex == BITS_PER_LONG) {
                    nextLongIndex = nextLongIndex(nextLongIndex+1);
                    if (nextLongIndex < data.length)
                        nextBitIndex = nextBitIndex(nextLongIndex, 0);
                }
                return (byte)lastValue;
            }

            public void remove() {
                if (lastValue < 0)
                    Exceptions.noElementToRemove();
                ByteBitSet.this.remove((byte)lastValue);
                lastValue = -1;
            }
        }

        public ByteIterator iterator() {
            return new SubSetIterator();
        }

        public int size() {
            if (ByteBitSet.this.size() == 0)
                return 0;
            int size;
            int longIndexLow;
            try {
                longIndexLow = longIndex(first());
            } catch (NoSuchElementException e) {
                return 0;
            }
            int longIndexHigh = longIndex(last());
            if (longIndexLow == longIndexHigh) {
                long v = data[longIndexLow];
                //  Mask out all bits less than the lower bound
                if (hasLowerBound)
                    v &= highMask(bitIndex(lowerBound));
                //  Mask out all bits greater than or equal to the upper bound
                if (hasUpperBound)
                    v &= lowMask(bitIndex(upperBound));
                size = countBits(v);
            } else {
                //  Mask out all bits less than the lower bound
                long vLow = data[longIndexLow];
                if (hasLowerBound)
                    vLow &= highMask(bitIndex(lowerBound));
                
                //  Mask out all bits greater than or equal to the upper bound
                long vHigh = data[longIndexHigh];
                if (hasUpperBound)
                    vHigh &= lowMask(bitIndex(upperBound));
                
                size = countBits(vLow) + countBits(vHigh);
                for (int i = longIndexLow+1; i < longIndexHigh; i++)
                    size += countBits(data[i]);
            }
            return size;
        }

        public byte first() {
            byte first = firstFrom(hasLowerBound ? lowerBound : 0);
            if (hasUpperBound && first >= upperBound)
                Exceptions.setNoFirst();
            return first;
        }

        public byte last() {
            byte last = lastFrom(hasUpperBound ? (byte)(upperBound-1) : ByteBitSet.this.last());
            if (hasLowerBound && last < lowerBound)
                Exceptions.setNoLast();
            return last;
        }

        public ByteSortedSet headSet(byte to) {
            if (!inSubRange(to))
                Exceptions.invalidUpperBound(String.valueOf(to));
            return new SubSet(hasLowerBound, lowerBound, true, to);
        }

        public ByteSortedSet tailSet(byte from) {
            if (!inSubRange(from))
                Exceptions.invalidLowerBound(String.valueOf(from));
            return new SubSet(true, from, hasUpperBound, upperBound);
        }

        public ByteSortedSet subSet(byte from, byte to) {
            if (!inSubRange(from))
                Exceptions.invalidLowerBound(String.valueOf(from));
            if (!inSubRange(to))
                Exceptions.invalidUpperBound(String.valueOf(to));
            return new SubSet(true, from, true, to);
        }

        private boolean inSubRange(byte v) {
            if (hasLowerBound && v < lowerBound)
                return false;
            if (hasUpperBound && v >= upperBound)
                return false;
            return true;
        }

    }

}
