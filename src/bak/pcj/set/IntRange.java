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

import bak.pcj.util.Exceptions;
import java.io.Serializable;

/**
 *  This class represents ranges of consecutive int values.
 *
 *  <p>Design note: Empty ranges cannot exist. It gives too many
 *  problems defining adjacency and intersections, and empty ranges
 *  are not really useful for their purpose of backing up range sets.
 *  It also removes the problem of overflow checking.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     21-08-2003 20:10
 *  @since      1.0
 */
public class IntRange implements Comparable, Serializable {

    /**
     *  The first value in this range.
     *  @serial
     */
    private int first;

    /**
     *  The last value in this range.
     *  @serial
     */
    private int last;

    /**
     *  Creates a new range of consecutive int values.
     *
     *  @param      first
     *              the first int value in the range.
     *
     *  @param      last
     *              the last int value in the range.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>first &gt; last</tt>.
     */
    public IntRange(int first, int last) {
        if (last < first)
            Exceptions.invalidRangeBounds(String.valueOf(first), String.valueOf(last));
        this.first = first;
        this.last = last;
    }

    /*
     *  Creates a new range of consecutive int values.
     *
     *  @param      first
     *              the first int value in the range.
     *
     *  @param      length
     *              the number of consecutive values in the new
     *              range.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>length &lt;= 0</tt>;
     *              if the specified length results in an overflow,
     *              i.e. there are values not in the defined range of
     *              int values.
     */
    /*
    public IntRange(int first, int length) {
        if (length <= 0)
            throw new IllegalArgumentException();
        //  first+length-1 > MAX_VALUE rewritten to avoid overflow
        if (first-1 > Int.MAX_VALUE-length)
            throw new IllegalArgumentException();
        this.first = first;
        this.last = (int)(first+length-1);
    }
    */

    /**
     *  Returns the first int value in this range.
     *
     *  @return     the first int value in this range.
     */
    public int first()
    { return first; }

    /**
     *  Returns the last int value in this range.
     *
     *  @return     the last int value in this range.
     */
    public int last()
    { return last; }

    /**
     *  Returns the number of values in this range.
     *
     *  @return     the number of values in this range.
     */
    public int length() {
        return (int)(last-first+1);
    }

    /**
     *  Indicates whether this range intersects a specified range.
     *
     *  @param      range
     *              the range with which to compare this range.
     *
     *  @return     <tt>true</tt> if this range has at least one
     *              value in common with the specified range.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     */
    public boolean intersects(IntRange range) {
        return (first >= range.first && first <= range.last)
            || (range.first >= first && range.first <= last);
    }

    /**
     *  Indicates whether this range is adjacent to a specified
     *  range.
     *
     *  @param      range
     *              the range with which to compare this range.
     *
     *  @return     <tt>true</tt> if this range is adjacent to the
     *              specified range.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     */
    public boolean adjacentTo(IntRange range) {
        return (last+1 == range.first)
            || (range.last+1 == first);
    }

    /**
     *  Indicates whether this can be merged with a specified range.
     *  Two ranges can be merged if a range of consecutive values
     *  containing all values of both ranges exists.
     *
     *  @param      range
     *              the range to merge with.
     *
     *  @return     <tt>true</tt> if this range can be merged with
     *              the specified range; returns <tt>false</tt>
     *              otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     */
    public boolean canMergeWith(IntRange range) {
        return intersects(range) || adjacentTo(range);
    }

    /**
     *  Creates a new range as a merge between this range and a
     *  specified range.
     *
     *  @param      range
     *              the range with which to merge this range.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalArgumentException
     *              if this range cannot be merged with the specified
     *              range.
     */
    public IntRange mergeWith(IntRange range) {
        if (!canMergeWith(range))
            Exceptions.cannotMergeRanges(this, range);
        return quickMergeWith(range);
    }

    private IntRange quickMergeWith(IntRange range) {
        int nfirst = first < range.first ? first : range.first;
        int nlast = last > range.last ? last : range.last;
        return new IntRange(nfirst, nlast);
    }

    public IntRange tryMergeWith(IntRange range) {
        if (!canMergeWith(range))
            return null;
        return quickMergeWith(range);
    }

    /**
     *  Returns the length of the intersection between this
     *  range and a specified range.
     *
     *  @param      range
     *              the range with which to intersect this rance.
     *
     *  @return     the length of the intersection between this
     *              range and a specified range.
     *
     *  @throws     NullPointerException
     *              if <tt>range</tt> is <tt>null</tt>.
     */
    public int intersectionLength(IntRange range) {
        int len;
        if (first >= range.first && first <= range.last) {
            int end = last <= range.last ? last : range.last;
            len = ((int)(end-first))+1;
        } else
            if (range.first >= first && range.first <= last) {
                int end = last <= range.last ? last : range.last;
                len = ((int)(end-range.first))+1;
            } else
                len = 0;
        return len;
    }
    

    /**
     *  Indicates whether a specified value is a member of this
     *  range.
     *
     *  @param      v
     *              the value to test for membership.
     *
     *  @return     <tt>true</tt> if the specified value is a member
     *              of this range; returns <tt>false</tt> otherwise.
     */
    public boolean contains(int v) {
        return v >= first && v <= last;
    }

    /**
     *  Indicates whether this range is equal to some object.
     *
     *  @param      obj
     *              the object with which to compare this range.
     *
     *  @return     <tt>true</tt> if this range is equal to the
     *              specified object; returns <tt>false</tt>
     *              otherwise.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof IntRange))
            return false;
        IntRange range = (IntRange)obj;
        return first == range.first && last == range.last;
    }

    /**
     *  Compares this range with some object for order.
     *
     *  @param      obj
     *              the object with which to compare this range.
     *
     *  @return     <tt>-1</tt> if this range is less than
     *              <tt>obj</tt>; returns <tt>1</tt> if this range is
     *              greater than <tt>obj</tt>; returns <tt>0</tt>
     *              otherwise, in which case <tt>this.equals(obj)</tt>
     *              is <tt>true</tt>.
     *
     *  @throws     NullPointerException
     *              if <tt>obj</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>obj</tt> is not of class <tt>IntRange</tt>.
     */
    public int compareTo(Object obj) {
        IntRange range = (IntRange)obj;
        if (first < range.first)
            return -1;
        if (first > range.first)
            return 1;
        if (last < range.last)
            return -1;
        if (last > range.last)
            return 1;
        return 0;
    }

    /**
     *  Returns a hash code value for this range.
     *
     *  @return     a hash code value for this range.
     */
    public int hashCode() {
        return (int)(first ^ last);
    }

    /**
     *  Returns a string representation of this range.
     *
     *  @return     a string representation of this range.
     */
    public String toString() {
        return bak.pcj.util.Display.display(first) + "-" + bak.pcj.util.Display.display(last);
    }

}
