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

/**
 *  This interface defines extends the IntSet interface to define
 *  sorted sets.
 *
 *  @see        java.util.SortedSet
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2002/12/04
 *  @since      1.2
 */
public interface IntSortedSet extends IntSet {

    /**
     *  Adds an element to this set.
     *
     *  @param      v
     *              the element to add to this set.
     *
     *  @return     <tt>true</tt> if this set was modified
     *              as a result of adding <tt>v</tt>; returns
     *              <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this
     *              set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>v</tt> is not permitted, because this set
     *              is a subset of another set.
     */
    boolean add(int v);

    /**
     *  Returns the lowest element of this set.
     *
     *  @return     the lowest element of this set.
     *
     *  @throws     NoSuchElementException
     *              if this set is empty.
     */
    int first();

    /**
     *  Returns the subset of values lower than a specified value.
     *  The returned subset is a view of this set, so changes to the
     *  subset are reflected by this set and vice versa.
     *
     *  @param      to
     *              the upper bound of the returned set (not included).
     *
     *  @throws     IllegalArgumentException
     *              if <tt>to</tt> is not permitted
     *              in this set (which can be the case with returned
     *              subsets).
     */
    IntSortedSet headSet(int to);

    /**
     *  Returns the highest element of this set.
     *
     *  @return     the highest element of this set.
     *
     *  @throws     NoSuchElementException
     *              if this set is empty.
     */    
    int last();

    /**
     *  Returns the subset of values lower that a specified value and
     *  higher than or equal to another specified value.
     *  The returned subset is a view of this set, so changes to the
     *  subset are reflected by this set and vice versa.
     *
     *  @param      from
     *              the lower bound of the returned set (included).
     *
     *  @param      to
     *              the upper bound of the returned set (not included).
     *
     *  @throws     IllegalArgumentException
     *              if <tt>from</tt> is greater than <tt>to</tt>;
     *              if <tt>from</tt> or <tt>to</tt> is not permitted
     *              in this set (which can be the case with returned
     *              subsets).
     */
    IntSortedSet subSet(int from, int to);

    /**
     *  Returns the subset of values higher than or equal to a 
     *  specified value.
     *  The returned subset is a view of this set, so changes to the
     *  subset are reflected by this set and vice versa.
     *
     *  @param      from
     *              the lower bound of the returned set (included).
     *
     *  @throws     IllegalArgumentException
     *              if <tt>from</tt> is not permitted
     *              in this set (which can be the case with returned
     *              subsets).
     */
    IntSortedSet tailSet(int from);

}
