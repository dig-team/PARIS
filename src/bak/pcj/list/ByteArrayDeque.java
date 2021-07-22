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
package bak.pcj.list;

import bak.pcj.ByteIterator;
import bak.pcj.ByteCollection;
import bak.pcj.hash.DefaultByteHashFunction;
import bak.pcj.util.Exceptions;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *  This class represents an array implementaion of deques of
 *  byte values.
 *
 *  @see        java.util.LinkedList
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     21-08-2003 19:25
 *  @since      1.0
 */
public class ByteArrayDeque extends AbstractByteList implements ByteDeque, Cloneable, Serializable {

    /** Constant indicating relative growth policy. */
    private static final int    GROWTH_POLICY_RELATIVE      = 0;

    /** Constant indicating absolute growth policy. */
    private static final int    GROWTH_POLICY_ABSOLUTE      = 1;

    /**
     *  The default growth policy of this deque.
     *  @see    #GROWTH_POLICY_RELATIVE
     *  @see    #GROWTH_POLICY_ABSOLUTE
     */
    private static final int    DEFAULT_GROWTH_POLICY       = GROWTH_POLICY_RELATIVE;

    /** The default factor with which to increase the capacity of this deque. */
    public static final double DEFAULT_GROWTH_FACTOR        = 1.0;

    /** The default chunk size with which to increase the capacity of this deque. */
    public static final int    DEFAULT_GROWTH_CHUNK         = 10;

    /** The default capacity of this deque. */
    public static final int    DEFAULT_CAPACITY             = 10;

    /** The elements of this deque (indices <tt>0</tt> to <tt>size-1</tt>). */
    private transient byte[] data;

    /** 
     *  The current size of this deque.
     *  @serial
     */
    private int size;

    /** The index of the first element in this deque. */
    private transient int first;

    /** The index of the last element in this deque. */
    private transient int last;

    /**
     *  The growth policy of this deque (0 is relative growth, 1 is absolute growth).
     *  @serial
     */
    private int growthPolicy;

    /**
     *  The growth factor of this deque, if the growth policy is
     *  relative.
     *  @serial
     */
    private double growthFactor;

    /**
     *  The growth chunk size of this deque, if the growth policy is
     *  absolute.
     *  @serial
     */
    private int growthChunk;

    private ByteArrayDeque(int capacity, int growthPolicy, double growthFactor, int growthChunk) {
        if (capacity < 0)
            Exceptions.negativeArgument("capacity", String.valueOf(capacity));
        if (growthFactor < 0.0)
            Exceptions.negativeArgument("growthFactor", String.valueOf(growthFactor));
        if (growthChunk < 0)
            Exceptions.negativeArgument("growthChunk", String.valueOf(growthChunk));
        data = new byte[capacity];
        size = 0;
        this.growthPolicy = growthPolicy;
        this.growthFactor = growthFactor;
        this.growthChunk = growthChunk;
        this.first = 0;
        this.last = 0;
    }

    /**
     *  Creates a new array deque with capacity 10 and a relative
     *  growth factor of 1.0.
     *
     *  @see        #ByteArrayDeque(int,double)
     */
    public ByteArrayDeque() {
        this(DEFAULT_CAPACITY);
    }

    /**
     *  Creates a new array deque with the same elements as a
     *  specified collection. The elements of the specified collection
     *  are added to the end of the deque in the collection's iteration
     *  order.
     *
     *  @param      c
     *              the collection whose elements to add to the new
     *              deque.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public ByteArrayDeque(ByteCollection c) {
        this(c.size());
        addAll(c);
    }

    /**
     *  Creates a new array deque with the same elements as a
     *  specified array. The elements of the specified array 
     *  are added the end of the deque in the order in which they
     *  appear in the array.
     *
     *  @param      a
     *              the array whose elements to add to the new 
     *              deque.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public ByteArrayDeque(byte[] a) {
        this(a.length);
        System.arraycopy(a, 0, data, 0, a.length);
        size = a.length;
        first = 0;
        last = a.length-1;
    }

    /**
     *  Creates a new array deque with a specified capacity and a
     *  relative growth factor of 1.0.
     *
     *  @param      capacity
     *              the initial capacity of the deque.
     *
     *  @see        #ByteArrayDeque(int,double)
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     */
    public ByteArrayDeque(int capacity) {
        this(capacity, DEFAULT_GROWTH_FACTOR);
    }

    /**
     *  Creates a new array deque with a specified capacity and
     *  relative growth factor.
     *
     *  <p>The array capacity increases to <tt>capacity()*(1+growthFactor)</tt>.
     *  This strategy is good for avoiding many capacity increases, but
     *  the amount of wasted memory is approximately the size of the deque.
     *
     *  @param      capacity
     *              the initial capacity of the deque.
     *
     *  @param      growthFactor
     *              the relative amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>growthFactor</tt> is negative.
     */
    public ByteArrayDeque(int capacity, double growthFactor) {
        this(capacity, GROWTH_POLICY_RELATIVE, growthFactor, DEFAULT_GROWTH_CHUNK);
    }

    /**
     *  Creates a new array deque with a specified capacity and
     *  absolute growth factor.
     *
     *  <p>The array capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
     *
     *  @param      capacity
     *              the initial capacity of the deque.
     *
     *  @param      growthChunk
     *              the absolute amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>growthChunk</tt> is negative.
     */
    public ByteArrayDeque(int capacity, int growthChunk) {
        this(capacity, GROWTH_POLICY_ABSOLUTE, DEFAULT_GROWTH_FACTOR, growthChunk);
    }

    // ---------------------------------------------------------------
    //      Array management
    // ---------------------------------------------------------------

    /**
     *  Computes the new capacity of the deque based on a needed
     *  capacity and the growth policy.
     *
     *  @param      capacity
     *              the needed capacity of the deque.
     *
     *  @return     the new capacity of the deque.
     */
    private int computeCapacity(int capacity) {
        int newcapacity;
        if (growthPolicy == GROWTH_POLICY_RELATIVE)
            newcapacity = (int)(data.length * (1.0 + growthFactor));
        else
            newcapacity = data.length + growthChunk;
        if (newcapacity < capacity)
            newcapacity = capacity;
        return newcapacity;
    }

    /**
     *  Ensures that this deque has at least a specified capacity.
     *  The actual capacity is calculated from the growth factor
     *  or growth chunk specified to the constructor.
     *
     *  @param      capacity
     *              the minimum capacity of this deque.
     *
     *  @return     the new capacity of this deque.
     *
     *  @see        #capacity()
     */
    public int ensureCapacity(int capacity) {
        if (capacity > data.length) {
            byte[] newdata = new byte[capacity = computeCapacity(capacity)];
            toArray(newdata);
            first = 0;
            last = size;
            data = newdata;
        }
        return capacity;
    }

    /**
     *  Returns the current capacity of this deque. The capacity is the
     *  number of elements that the deque can contain without having to
     *  increase the amount of memory used.
     *
     *  @return     the current capacity of this deque.
     *
     *  @see        #ensureCapacity(int)
     */
    public int capacity()
    { return data.length; }

    // ---------------------------------------------------------------
    //      Operations not supported by abstract list implementation
    // ---------------------------------------------------------------

    public void add(int index, byte v) {
        if (index == 0)
            addFirst(v);
        else if (index == size)
            addLast(v);
        else {
            if (index < 0 || index > size)
                Exceptions.indexOutOfBounds(index, 0, size);
            ensureCapacity(size+1);
            if (first < last || last == 0) {  // data is in one block
                int iidx = index+first;
                int end = last == 0 ? data.length : last;
                int block = end-iidx;
                if (last == 0) { // wrap one element around end
                    data[0] = data[data.length-1];
                    System.arraycopy(data, iidx, data, iidx+1, block-1);
                    last = 1;
                } else {
                    System.arraycopy(data, iidx, data, iidx+1, block);
                    if (++last == data.length)
                        last = 0;
                }
                data[iidx] = v;
            } else {    // data is split
                int iidx = (first+index) % data.length;
                if (iidx <= last) { // element is in left block
                    int block = last-iidx;
                    System.arraycopy(data, iidx, data, iidx+1, block);
                    last++;
                    data[iidx] = v;
                } else {  // element is in right block
                    int block = iidx-first; 
                    System.arraycopy(data, first, data, first-1, block);
                    first--;
                    data[iidx-1] = v;
                }
            }
            size++;
        }
    }

    public byte get(int index) {
        if (index < 0 || index >= size)
            Exceptions.indexOutOfBounds(index, 0, size-1);
        return data[(first+index) % data.length];
    }

    public byte set(int index, byte v) {
        if (index < 0 || index >= size)
            Exceptions.indexOutOfBounds(index, 0, size-1);
        int idx = (first+index) % data.length;
        byte result = data[idx];
        data[idx] = v;
        return result;
    }

    public byte removeElementAt(int index) {
        byte result;
        if (index == 0)
            result = removeFirst();
        else if (index == size-1)
            result = removeLast();
        else {
            if (index < 0 || index >= size)
                Exceptions.indexOutOfBounds(index, 0, size-1);
            int ridx = (first+index) % data.length;
            result = data[ridx];
            if (first < last || last == 0) { // data is in one block
                //  move the shorter block
                int block1 = ridx-first;
                int block2 = size-block1-1;
                if (block1 < block2) {  // move first block
                    System.arraycopy(data, first, data, first+1, block1);
                    first++;
                } else { // move last block
                    System.arraycopy(data, ridx+1, data, ridx, block2);
                    if (--last < 0)
                        last = data.length-1;
                }
            } else {    // data is split
                if (ridx < last) {  // element is in left block
                    int block = last-ridx-1;
                    System.arraycopy(data, ridx+1, data, ridx, block);
                    last--;
                } else {  // element is in right block
                    int block = ridx-first;
                    System.arraycopy(data, first, data, first+1, block);
                    if (++first == data.length)
                        first = 0;
                }
            }
            size--;
        }
        return result;
    }

    /**
     *  Minimizes the memory used by this array deque. The underlying
     *  array is replaced by an array whose size is exactly the number
     *  of elements in this array deque. The method can be used to
     *  free up memory after many removals.
     */
    public void trimToSize() {
        if (data.length > size) {
            byte[] newdata = toArray();
            first = 0;
            last = 0;
            data = newdata;
        }
    }

    /**
     *  Returns a clone of this array deque.
     *
     *  @return     a clone of this array deque.
     *
     *  @since      1.1
     */
    public Object clone() {
        try {
            ByteArrayDeque c = (ByteArrayDeque)super.clone();
            c.data = new byte[data.length];
            //  This could be improved to only copying the blocks in use.
            System.arraycopy(data, 0, c.data, 0, data.length);
            return c;
        } catch (CloneNotSupportedException e) {
            Exceptions.cloning(); return null;
        }
    }

    // ---------------------------------------------------------------
    //      Operations declared by ByteDeque
    // ---------------------------------------------------------------

    public byte getFirst() {
        if (size == 0)
            Exceptions.dequeNoFirst();
        return data[first];
    }

    public byte getLast() {
        if (size == 0)
            Exceptions.dequeNoLast();
        return data[last == 0 ? data.length-1 : last-1];
    }

    public void addFirst(byte v) {
        ensureCapacity(size+1);
        if (--first < 0)
            first = data.length-1;
        data[first] = v;
        size++;
    }

    public void addLast(byte v) {
        ensureCapacity(size+1);
        data[last] = v;
        if (++last == data.length)
            last = 0;
        size++;
    }

    public byte removeFirst() {
        if (size == 0)
            Exceptions.dequeNoFirstToRemove();
        byte result = data[first];
        if (++first == data.length)
            first = 0;
        size--;
        return result;
    }

    public byte removeLast() {
        if (size == 0)
            Exceptions.dequeNoLastToRemove();
        if (--last < 0)
            last = data.length-1;
        size--;
        return data[last];
    }

    // ---------------------------------------------------------------
    //      Operations overwritten for efficiency
    // ---------------------------------------------------------------

    public int size()
    { return size; }

    public boolean isEmpty()
    { return size == 0; }

    public void clear() {
        size = 0;
        first = 0;
        last = 0;
    }

    public boolean contains(byte v) {
        for (int i = 0, idx = first; i < size; i++) {
            if (data[idx] == v)
                return true;
            if (++idx == data.length)
                idx = 0;
        }
        return false;
    }

    public int indexOf(byte c) {
        for (int i = 0, idx = first; i < size; i++) {
            if (data[idx] == c)
                return i;
            if (++idx == data.length)
                idx = 0;
        }
        return -1;
    }

    public int lastIndexOf(byte c) {
        for (int i = size-1, idx = last-1; i >= 0; i--) {
            if (idx < 0)
                idx = data.length-1;
            if (data[idx] == c)
                return i;
            idx--;
        }
        return -1;
    }

    public boolean remove(byte v) {
        int index = indexOf(v);
        if (index != -1) {
            removeElementAt(index);
            return true;
        }
        return false;
    }

    public byte[] toArray(byte[] a) {
        if (a == null || a.length < size)
            a = new byte[size];
        if (last <= first) {
            if (last == 0) {  //  one block at end
                System.arraycopy(data, first, a, 0, size);
            } else {    //  two blocks
                int block1 = data.length-first;
                int block2 = size-block1;
                //  copy block at end
                System.arraycopy(data, first, a, 0, block1);
                //  copy block at start
                System.arraycopy(data, 0, a, block1, block2);
            }
        } else {  // one block in middle
            System.arraycopy(data, first, a, 0, size);
        }
        return a;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ByteDeque))
            return false;
        ByteDeque s = (ByteDeque)obj;
        if (size != s.size())
            return false;
        ByteListIterator i2 = s.listIterator();
        for (int i = 0, idx = first; i < size; i++) {
            if (data[idx] != i2.next())
                return false;
            if (++idx == data.length)
                idx = 0;
        }
        return true;
    }

    public int hashCode() {
        int h = 1;
        for (int i = 0, idx = first; i < size; i++) {
            h = (int)(31*h + DefaultByteHashFunction.INSTANCE.hash(data[idx]));
            if (++idx == data.length)
                idx = 0;
        }
        return h;
    }

    // ---------------------------------------------------------------
    //      Serialization
    // ---------------------------------------------------------------

    /**
     *  @serialData     Default fields; the capacity of the
     *                  deque (<tt>int</tt>); the deques's elements
     *                  (<tt>byte</tt>).
     *
     *  @since          1.1
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(data.length);
        ByteIterator i = iterator();
        while (i.hasNext())
            s.writeByte(i.next());
    }

    /**
     *  @since          1.1
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        data = new byte[s.readInt()];
        first = 0;
        last = size;
        for (int i = 0; i < size; i++)
            data[i] = s.readByte();
    }

}
