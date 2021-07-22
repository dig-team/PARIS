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

import bak.pcj.CharIterator;
import bak.pcj.CharCollection;
import bak.pcj.util.Exceptions;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.Serializable;

/**
 *  This class represents range based sets of char values.
 *  The implementation is optimized for cases where most set elements
 *  fall into ranges of consecutive char values.
 *
 *  <p>Implementation of
 *  CharSortedSet is supported from PCJ 1.2. Prior to 1.2, only CharSet
 *  was implemented.
 *
 *  @see        CharRange
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     20-08-2003 22:24
 *  @since      1.0
 */
public class CharRangeSet extends AbstractCharSet implements CharSortedSet, Cloneable, Serializable {

    /**
     *  The ranges of this set. Must always be sorted and normalized (non-adjacent and non-overlapping).
     *  @serial
     */
    private ArrayList ranges;

    /**
     *  The size of this set.
     *  @serial
     */
    private int size;

    /**
     *  Creates a new empty range set.
     */
    public CharRangeSet() {
        ranges = new ArrayList();
        size = 0;
    }

    /**
     *  Creates a new empty range set containing specified values.
     *
     *  @param      a
     *              the values that the new set should contain.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     */
    public CharRangeSet(char[] a) {
        this();
        addAll(a);
    }

    /**
     *  Creates a new range set with the same elements as a specified
     *  collection.
     *
     *  @param      c
     *              the collection whose elements to add to the new
     *              set.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public CharRangeSet(CharCollection c) {
        this();
        addAll(c);
    }

    // ---------------------------------------------------------------
    //      Range management
    // ---------------------------------------------------------------

    /**
     *  Returns a specified range.
     *
     *  @param      index
     *              the index of the range to return.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index/tt> does not denote a valid range
     *              number.
     */
    private CharRange range(int index) {
        return (CharRange)ranges.get(index);
    }

    /**
     *  Returns the range of a specified value.
     *
     *  @param      v
     *              the value to search for.
     *
     *  @return     the range containing the specified value; returns
     *              <tt>null</tt> if no range contains the specified
     *              value.
     */
    private CharRange getRangeOf(char v) {
        int index = getRangeIndexOf(v);
        return index >= 0 ? range(index) : null;
    }

    /**
     *  Returns the range index of a specified value.
     *
     *  @param      v
     *              the value to search for.
     *
     *  @return     the index of the range containing the specified
     *              value; returns <tt>(-(<i>insertion point</i>) - 1)</tt>
     *              if no range contains the specified value.
     */
    private int getRangeIndexOf(char v) {
        if (size == 0)
            return -1;
        //  Binary search
        CharRange r;
        int lo = 0;
        int hi = ranges.size()-1;
        int mid;
        while (lo <= hi) {
            mid = (lo+hi)/2;
            r = (CharRange)ranges.get(mid);
            if (r.contains(v))
                return mid;
            if (v < r.first()) {
                hi = mid-1;
            } else {    // v > r.last()
                lo = mid+1;
            }
        }
        return -(lo+1);
    }

    /**
     *  Inserts a range at the sorted position in the ranges.
     *
     *  @param      range
     *              the range to insert.
     *
     *  @return     the insertion index; returns <tt>-1</tt> if an
     *              equal range existed in the ranges.
     */
    private int insertRange(CharRange range) {
        //  Binary search
        CharRange r;
        int lo = 0;
        int hi = ranges.size()-1;
        int mid;
        while (lo <= hi) {
            mid = (lo+hi)/2;
            r = range(mid);
            int compare = range.compareTo(r);
            if (compare == 0)
                return -1;
            if (compare < 0) {
                hi = mid-1;
            } else {    // compare > 0
                lo = mid+1;
            }
        }
        ranges.add(lo, range);
        return lo;
    }

    /**
     *  Normalizes the ranges after the insertion of a new range and
     *  recalculates the size of this set. The range list must be
     *  sorted when this method is invoked.
     *
     *  @param      index
     *              the index at which to start the normalization.
     *              Usually the index before a new range was inserted.
     */
    private void normalize(int index) {
        while (index < ranges.size()-1) {
            CharRange r1 = range(index);
            CharRange r2 = range(index+1);
            CharRange r3 = r1.tryMergeWith(r2);
            if (r3 == null)
                break;
            ranges.set(index, r3);
            ranges.remove(index+1);
            size -= r1.intersectionLength(r2);
        }
    }


    /**
     *  Normalizes all ranges and recalculates the size of this set.
     *  The method is usually called when the whole range list has
     *  changed.  The range list must be sorted when this method is
     *  invoked.
     */
    private void normalize() {
        int index = 0;
        size = 0;
        CharRange r1, r2, r3;
        while (index < ranges.size()-1) {
            r1 = range(index);
            r2 = range(index+1);
            r3 = r1.tryMergeWith(r2);
            if (r3 != null) {
                ranges.set(index, r3);
                ranges.remove(index+1);
            } else {
                size += r1.length();
                index++;
            }
        }
        r3 = range(ranges.size()-1);
        size += r3.length();
    }

    // ---------------------------------------------------------------
    //      Operations not supported by abstract implementation
    // ---------------------------------------------------------------

    public boolean add(char v) {
        int index = getRangeIndexOf(v);
        if (index >= 0)
            return false;
        int insertionIndex = -index-1;
        ranges.add(insertionIndex, new CharRange(v, v));
        if (insertionIndex > 0)
            insertionIndex--;
        size++;
        normalize(insertionIndex);
        return true;
    }

    public CharIterator iterator() {
        return new CharIterator() {
            int nextIndex = 0;
            int lastIndex = -1;
            int currRange = 0;
            int currOffset = 0;
            char lastValue;

            public boolean hasNext() {
                return nextIndex < size;
            }

            public char next() {
                if (nextIndex >= size)
                    Exceptions.endOfIterator();
                lastIndex = nextIndex;
                lastValue = curr();
                nextIndex++;
                if (nextIndex < size) {
                    if (currOffset == range(currRange).length()-1) {
                        currRange++;
                        currOffset = 0;
                    } else {
                        currOffset++;
                    }
                }
                return lastValue;
            }

            public void remove() {
                if (lastIndex == -1)
                    Exceptions.noElementToRemove();
                CharRangeSet.this.remove(lastValue);
                nextIndex--;
                if (nextIndex < size)
                    recalc();
                lastIndex = -1;
            }

            private char curr() {
                return (char)(range(currRange).first() + currOffset);
            }

            private void recalc() {
                currRange = 0;
                currOffset = nextIndex;
                for (;;) {
                    int rs = range(currRange).length();
                    if (currOffset < rs)
                        break;
                    currOffset -= rs;
                    currRange++;
                }
            }

        };
    }

    /**
     *  @since      1.2
     */
    public char first() {
        if (size == 0)
            Exceptions.setNoFirst();
        return range(0).first();
    }

    private char firstFrom(char v) {
        int index = getRangeIndexOf(v);
        if (index >= 0)
            return v;
        //  Get first range after calculated insertion point.
        //  index is now (-(insertion point)-1), so the insertion point
        //  is -index-1
        index = -index - 1;
        if (index >= ranges.size())
            Exceptions.setNoFirst();
        return range(index).first();
    }

    /**
     *  @since      1.2
     */
    public char last() {
        if (size == 0)
            Exceptions.setNoLast();
        return range(ranges.size()-1).last();
    }

    private char lastFrom(char v) {
        int index = getRangeIndexOf(v);
        if (index >= 0)
            return v;
        //  Get first range before calculated insertion point.
        //  index is now (-(insertion point)-1), so the insertion point
        //  is -index-1
        index = -index - 1;
        index--;
        if (index < 0 || index >= ranges.size())
            Exceptions.setNoLast();
        return range(index).last();
    }

    /**
     *  @since      1.2
     */
    public CharSortedSet headSet(char to) {
        return new SubSet(false, (char)0, true, to);
    }

    /**
     *  @since      1.2
     */
    public CharSortedSet tailSet(char from) {
        return new SubSet(true, from, false, (char)0);
    }

    /**
     *  @since      1.2
     */
    public CharSortedSet subSet(char from, char to) {
        return new SubSet(true, from, true, to);
    }

    private class SubSet extends AbstractCharSet implements CharSortedSet, java.io.Serializable {

        private boolean hasLowerBound;
        private boolean hasUpperBound;
        private char lowerBound;
        private char upperBound;

        SubSet(boolean hasLowerBound, char lowerBound, boolean hasUpperBound, char upperBound) {
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

        public boolean add(char v) {
            if (!inSubRange(v))
                Exceptions.valueNotInSubRange(String.valueOf(v));
            return CharRangeSet.this.add(v);
        }

        public boolean remove(char v) {
            if (!inSubRange(v))
                Exceptions.valueNotInSubRange(String.valueOf(v));
            return CharRangeSet.this.remove(v);
        }

        public boolean contains(char v) {
            return inSubRange(v) && CharRangeSet.this.contains(v);
        }

        class EmptySubSetIterator implements CharIterator {
            public boolean hasNext()
            { return false; }
            
            public char next()
            { Exceptions.endOfIterator(); throw new RuntimeException(); }
            
            public void remove()
            { Exceptions.noElementToRemove(); }
        }

        class SimpleSubSetIterator implements CharIterator {
            int nextIndex;
            int size;
            int lastIndex;
            char lastValue;
            char from;
            char to;

            SimpleSubSetIterator(char from, char to) {
                size = (int)(to-from+1);
                nextIndex = 0;
                lastIndex = -1;
                this.from = from;
                this.to = to;
            }

            public boolean hasNext() {
                return nextIndex < size;
            }

            public char next() {
                if (!hasNext())
                    Exceptions.endOfIterator();
                lastValue = (char)(from + nextIndex);
                lastIndex = nextIndex;
                nextIndex++;
                return lastValue;
            }

            public void remove() {
                if (lastIndex == -1)
                    Exceptions.noElementToRemove();
                CharRangeSet.this.remove(lastValue);
                lastIndex = -1;
            }

        }

        class NonEmptySubSetIterator implements CharIterator {
            char first;
            char last;
            int rangeIndexLow;
            int rangeIndexHigh;
            CharRange rangeLow;
            CharRange rangeHigh;
            char previousValue;
            CharRange currRange;
            int currRangeIndex;
            int currOffset;
            boolean valueAvailable;
            int nextIndex;

            NonEmptySubSetIterator(char first, char last, int rangeIndexLow, int rangeIndexHigh) {
                if (rangeIndexLow == rangeIndexHigh)
                    throw new RuntimeException("Internal error");
                this.first = first;
                this.last = last;
                this.rangeIndexLow = rangeIndexLow;
                this.rangeIndexHigh = rangeIndexHigh;
                rangeLow = new CharRange(first, range(rangeIndexLow).last());
                rangeHigh = new CharRange(range(rangeIndexHigh).first(), last);
                currRangeIndex = rangeIndexLow;
                currRange = rangeLow;
                currOffset = 0;
                previousValue = first;
                valueAvailable = false;
                nextIndex = 0;
            }

            private CharRange getRange(int rangeIndex) {
                if (rangeIndex == rangeIndexLow)
                    return rangeLow;
                if (rangeIndex == rangeIndexHigh)
                    return rangeHigh;
                return range(rangeIndex);
            }

            private void recalc() {
                first = first();
                last = last();

                rangeIndexLow = getRangeIndexOf(first);
                rangeIndexHigh = getRangeIndexOf(last);
                if (rangeIndexLow == rangeIndexHigh)
                    rangeLow = rangeHigh = new CharRange(first, last);
                else {
                    rangeLow = new CharRange(first, range(rangeIndexLow).last());
                    rangeHigh = new CharRange(range(rangeIndexHigh).first(), last);
                }
                currOffset = nextIndex;
                currRangeIndex = rangeIndexLow;
                currRange = rangeLow;
                for (;;) {
                    int rs = currRange.length();
                    if (currOffset < rs)
                        break;
                    currOffset -= rs;
                    currRange = getRange(++currRangeIndex);
                }
            }

            public boolean hasNext() {
                return previousValue < last;
            }

            public char next() {
                if (!hasNext())
                    Exceptions.endOfIterator();
                previousValue = (char)(currRange.first() + currOffset++);
                if (currOffset == currRange.length() && previousValue < last) {
                    currOffset = 0;
                    currRange = getRange(++currRangeIndex);
                }
                nextIndex++;
                valueAvailable = true;
                return previousValue;
            }

            public void remove() {
                if (!valueAvailable)
                    Exceptions.noElementToRemove();
                CharRangeSet.this.remove(previousValue);
                nextIndex--;
                recalc();
                valueAvailable = false;
            }
        }

        public CharIterator iterator() {
            char first;
            char last;
            int rangeIndexLow;
            int rangeIndexHigh;

            try {
                first = first();
            } catch (NoSuchElementException e) {
                return new EmptySubSetIterator();
            }
            last = last();
            rangeIndexLow = getRangeIndexOf(first);
            rangeIndexHigh = getRangeIndexOf(last);
            if (rangeIndexLow == rangeIndexHigh)
                return new SimpleSubSetIterator(first, last);
            return new NonEmptySubSetIterator(first, last, rangeIndexLow, rangeIndexHigh);
        }

        public int size() {
            if (CharRangeSet.this.size() == 0)
                return 0;
            int size;
            char first;
            int rangeIndexLow;
            try {
                first = first();
                rangeIndexLow = getRangeIndexOf(first);
            } catch (NoSuchElementException e) {
                return 0;
            }
            char last = last();
            int rangeIndexHigh = getRangeIndexOf(last);
            if (rangeIndexLow == rangeIndexHigh) {
                size = (int)(last-first+1);
            } else {
                CharRange rangeLow = range(rangeIndexLow);
                CharRange rangeHigh = range(rangeIndexHigh);
                int sizeLow = (int)(rangeLow.last()-first+1);
                int sizeHigh = (int)(last-rangeHigh.first()+1);

                size = sizeLow + sizeHigh;
                for (int i = rangeIndexLow+1; i < rangeIndexHigh; i++)
                    size += range(i).length();
            }
            return size;
        }

        public char first() {
            char first = firstFrom(hasLowerBound ? lowerBound : 0);
            if (hasUpperBound && first >= upperBound)
                Exceptions.setNoFirst();
            return first;
        }

        public char last() {
            char last = lastFrom(hasUpperBound ? (char)(upperBound-1) : CharRangeSet.this.last());
            if (hasLowerBound && last < lowerBound)
                Exceptions.setNoLast();
            return last;
        }

        public CharSortedSet headSet(char to) {
            if (!inSubRange(to))
                Exceptions.invalidUpperBound(String.valueOf(to));
            return new SubSet(hasLowerBound, lowerBound, true, to);
        }

        public CharSortedSet tailSet(char from) {
            if (!inSubRange(from))
                Exceptions.invalidLowerBound(String.valueOf(from));
            return new SubSet(true, from, hasUpperBound, upperBound);
        }

        public CharSortedSet subSet(char from, char to) {
            if (!inSubRange(from))
                Exceptions.invalidLowerBound(String.valueOf(from));
            if (!inSubRange(to))
                Exceptions.invalidUpperBound(String.valueOf(to));
            return new SubSet(true, from, true, to);
        }

        private boolean inSubRange(char v) {
            if (hasLowerBound && v < lowerBound)
                return false;
            if (hasUpperBound && v >= upperBound)
                return false;
            return true;
        }

    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('[');
        for (int i = 0, rsize = ranges.size(); i < rsize; i++) {
            if (i > 0)
                s.append(',');
            s.append(range(i));
        }
        s.append(']');
        return s.toString();
    }

    public void trimToSize() {
        //ranges.trimToSize();
    }

    /**
     *  Returns a clone of this range set.
     *
     *  @return     a clone of this range set.
     *
     *  @since      1.1
     */
    public Object clone() {
        try {
            CharRangeSet c = (CharRangeSet)super.clone();
            c.ranges = (ArrayList)ranges.clone();
            return c;
        } catch (CloneNotSupportedException e) {
            Exceptions.cloning(); throw new RuntimeException();
        }
    }

    // ---------------------------------------------------------------
    //      Operations overwritten for efficiency
    // ---------------------------------------------------------------

    public void clear() {
        ranges.clear();
        size = 0;
    }

    public boolean contains(char v)
    { return getRangeIndexOf(v) >= 0; }

    public int hashCode() {
        int h = 0;
        for (int i = 0, index = 0, rsize = ranges.size(); i < rsize; i++) {
            CharRange r = range(i);
            for (char c = r.first(), last = r.last(); c <= last; c++)
                h += c;
        }
        return h;
    }

    public boolean isEmpty()
    { return size == 0; }

    public int size()
    { return size; }

    public boolean remove(char v) {
        int index = getRangeIndexOf(v);
        if (index < 0)
            return false;
        //  Treat end points special since we can avoid splitting a range
        CharRange r = range(index);
        if (v == r.first()) {
            if (r.length() == 1)
                ranges.remove(index);
            else
                ranges.set(index, new CharRange((char)(r.first()+1), r.last()));
        } else if (v == r.last()) {
            //  r.length() > 1
            ranges.set(index, new CharRange(r.first(), (char)(r.last()-1)));
        } else {
            //  Split the range
            CharRange r1 = new CharRange(r.first(), (char)(v-1));
            CharRange r2 = new CharRange((char)(v+1), r.last());
            ranges.set(index, r1);
            ranges.add(index+1, r2);
        }
        size--;
        return true;
    }

    public char[] toArray(char[] a) {
        if (a == null || a.length < size)
            a = new char[size];
        for (int i = 0, index = 0, rsize = ranges.size(); i < rsize; i++) {
            CharRange r = range(i);
            for (char c = r.first(), last = r.last(); c <= last; c++)
                a[index++] = c;
        }
        return a;
    }

    // ---------------------------------------------------------------
    //      Extra operations
    // ---------------------------------------------------------------

    /**
     *  Indicates whether all elements of a specified
     *  range is contained in this set.
     *
     *  @param      range
     *              the range whose elements to test for
     *              containment.
     *
     *  @return     <tt>true</tt> if all the elements of <tt>range</tt>
     *              are contained in this collection; returns
     *              <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     *
     *  @see        #containsAll(CharCollection)
     */
    public boolean containsAll(CharRange range) {
        /*
            In order for the set to contain the whole range
            the two range ends must be represented by the same
            range in the range list.
         */
        CharRange r = getRangeOf(range.first());
        return r != null ? r.contains(range.last()) : false;
    }

    /**
     *  Adds all the elements of a specified range set to
     *  this set.
     *
     *  @param      c
     *              the set whose elements to add to this
     *              set.
     *
     *  @return     <tt>true</tt> if this set was modified
     *              as a result of adding the elements of <tt>c</tt>;
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     *
     *  @see        #add(char)
     *  @see        #addAll(CharRange)
     *  @see        #addAll(CharCollection)
     *  @see        #addAll(char, char)
     *  @see        #addAll(char[])
     */
    public boolean addAll(CharRangeSet c) {
        int oldSize = size;
        for (int i = 0, rsize = c.ranges.size(); i < rsize; i++)
            addAll(c.range(i));
        return size != oldSize;
    }

    /**
     *  Adds a specified range to this set.
     *
     *  @param      range
     *              the range to add to this set.
     *
     *  @return     <tt>true</tt> if this set was modified
     *              as a result of adding the elements of
     *              <tt>range</tt>; returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     *
     *  @see        #add(char)
     *  @see        #addAll(CharRangeSet)
     *  @see        #addAll(CharCollection)
     *  @see        #addAll(char, char)
     *  @see        #addAll(char[])
     */
    public boolean addAll(CharRange range) {
        int oldSize = size;
        int index = insertRange(range);
        if (index != -1) {
            int nindex = index;
            if (nindex > 0)
                nindex--;
            size += range.length();
            normalize(nindex);
        }
        return size != oldSize;
    }

    /**
     *  Adds a specified range to this set.
     *
     *  @param      first
     *              the first value of the range to add to this set.
     *
     *  @param      last
     *              the last value of the range to add to this set.
     *
     *  @return     <tt>true</tt> if this set was modified
     *              as a result of adding the values <tt>first</tt>
     *              to <tt>last</tt>; returns <tt>false</tt>
     *              otherwise.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>first &gt; last</tt>.
     */
    public boolean addAll(char first, char last) {
        return addAll(new CharRange(first, last));
    }

    /**
     *  Adds an array of char values to this set.
     *
     *  @param      a
     *              the array of char values to add to this set.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     *
     *  @see        #add(char)
     *  @see        #addAll(CharRange)
     *  @see        #addAll(CharRangeSet)
     *  @see        #addAll(CharCollection)
     *  @see        #addAll(char, char)
     */
    public boolean addAll(char[] a) {
        if (a.length == 0)
            return false;

        //  Sort a
        /*
            We can decide if the array is sorted in at most n steps
            (n being the length of chars).
            If it is not sorted, it is probably much less than n steps,
            and if it is sorted, we can skip the sorting operation
            and cloning of chars (thus effectively having sorted in
            linear time).
        */
        int oldSize = size;
        char[] sa;
        if (!isSorted(a)) {
            sa = (char[])a.clone();
            java.util.Arrays.sort(sa);
        } else {
            sa = a;
        }

        //  Add ranges of a to range list
        int index = 0;
        char c0, c1;
        while (index < sa.length) {
            c0 = sa[index];
            index = range(sa, index);
            c1 = sa[index];
            ranges.add(new CharRange(c0, c1));
            index++;
        }

        //  Sort and normalize range list
        /*
            Is it better to sort and normalize once instead
            of inserting sorted and performing normalization at each step?
         */
        java.util.Collections.sort(ranges);
        normalize();
        return size != oldSize;
    }

    /**
     *  Finds a range in an ordered array which may
     *  contain duplicates.
     *
     *  @param      a
     *              the array of values to search.
     *
     *  @param      index
     *              the index from which to start the search.
     *
     *  @return     the index of the last value in the found
     *              range.
     */
    private int range(char[] a, int index) {
        char c0 = a[index++];
        //  Skip duplicates
        while (index < a.length && a[index] == c0)
            index++;
        //  While in sequence
        while (index < a.length && a[index] == (char)(c0+1)) {
            c0 = a[index++];
            //  Skip duplicates
            while (index < a.length && a[index] == c0)
                index++;
        }
        return index-1;
    }

    /**
     *  Indicates whether the specified array is sorted in
     *  ascending order.
     *
     *  @param      a
     *              the array to examine.
     *
     *  @return     <tt>true</tt> if <tt>s</tt> is sorted; returns
     *              <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     */
    private boolean isSorted(char[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i] < a[i-1])
                return false;
        return true;
    }

    /**
     *  Returns the ranges of this set. None of the ranges returned
     *  will overlap or be adjacent.
     *
     *  @return     the ranges of this set. The returned array is
     *              a fresh copy that can be modified without
     *              modifying this set.
     */
    public CharRange[] ranges() {
        CharRange[] a = new CharRange[ranges.size()];
        ranges.toArray(a);
        return a;
    }

}
