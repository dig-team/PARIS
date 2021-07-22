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
package bak.pcj.adapter;

import bak.pcj.set.ByteSortedSet;

import java.util.Comparator;
import java.util.AbstractSet;
import java.util.SortedSet;

/**
 *  This class represents adapters of byte sets to Java Collections
 *  Framework sets. The adapter
 *  is implemented as a wrapper around a primitive set. Thus, 
 *  changes to the underlying set are reflected by this
 *  set and vice versa.
 *
 *  @see        ByteSortedSet
 *  @see        java.util.SortedSet
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2002/12/04
 *  @since      1.2
 */
public class ByteSortedSetToSortedSetAdapter extends ByteSetToSetAdapter implements SortedSet {

    /**
     *  Creates a new adaption of a set of byte
     *  values to a Java Collections Framework set.
     *
     *  @param      set
     *              the underlying primitive set.
     *
     *  @throws     NullPointerException
     *              if <tt>set</tt> is <tt>null</tt>.
     */
    public ByteSortedSetToSortedSetAdapter(ByteSortedSet set) {
        super(set);
    }

    /**
     *  Returns the comparator used by this set. This method
     *  always returns <tt>null</tt>, since primitive sets are
     *  sorted by their natural ordering.
     *
     *  @return     <tt>null</tt>.
     */
    public Comparator comparator()
    { return null; }

    /**
     *  Returns the lowest element of this set.
     *
     *  @return     the lowest element of this set.
     *
     *  @throws     NoSuchElementException
     *              if this set is empty.
     */
    public Object first() 
    { return new Byte(((ByteSortedSet)set).first()); }

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
     *
     *  @throws     ClassCastException
     *              if <tt>to</tt> is not of class {@link Byte Byte}.
     *
     *  @throws     NullPointerException
     *              if <tt>to</tt> is <tt>null</tt>.
     */
    public SortedSet headSet(Object to) 
    { return new ByteSortedSetToSortedSetAdapter(((ByteSortedSet)set).headSet( ((Byte)to).byteValue() )); }

    /**
     *  Returns the highest element of this set.
     *
     *  @return     the highest element of this set.
     *
     *  @throws     NoSuchElementException
     *              if this set is empty.
     */    
    public Object last() 
    { return new Byte(((ByteSortedSet)set).last()); }

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
     *
     *  @throws     ClassCastException
     *              if <tt>from</tt> is not of class {@link Byte Byte};
     *              if <tt>to</tt> is not of class {@link Byte Byte}.
     *
     *  @throws     NullPointerException
     *              if <tt>from</tt> is <tt>null</tt>;
     *              if <tt>to</tt> is <tt>null</tt>.
     */
    public SortedSet subSet(Object from, Object to) {
        byte tfrom = ((Byte)from).byteValue();
        byte tto = ((Byte)to).byteValue();
        return new ByteSortedSetToSortedSetAdapter(((ByteSortedSet)set).subSet(tfrom, tto));
    }

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
     *
     *  @throws     ClassCastException
     *              if <tt>from</tt> is not of class {@link Byte Byte}.
     *
     *  @throws     NullPointerException
     *              if <tt>from</tt> is <tt>null</tt>.
     */
    public SortedSet tailSet(Object from)
    { return new ByteSortedSetToSortedSetAdapter(((ByteSortedSet)set).tailSet( ((Byte)from).byteValue() )); }

}
