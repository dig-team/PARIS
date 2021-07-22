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

import bak.pcj.list.LongList;
import bak.pcj.list.LongListIterator;
import bak.pcj.util.Exceptions;

import java.util.AbstractList;
import java.util.ListIterator;
import java.util.Collection;
import java.util.AbstractCollection;

/**
 *  This class represents adapters of long lists to Java Collection Framework lists.
 *  The adapter is implemented as a wrapper around a primitive list. Thus, 
 *  changes to the underlying list are reflected by this
 *  list and vice versa.
 *
 *  @see        bak.pcj.list.LongList
 *  @see        java.util.List
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     20-08-2003 22:58
 *  @since      1.0
 */
public class LongListToListAdapter extends AbstractList {

    /** The underlying primitive list. */
    protected LongList list;

    /**
     *  Creates a new adaption of a collection of long
     *  values to a Java Collections Framework collection.
     *
     *  @param      list
     *              the underlying primitive collection.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     */
    public LongListToListAdapter(LongList list) {
        if (list == null)
            Exceptions.nullArgument("list");
        this.list = list;
    }

    /**
     *  Adds all the elements of a specified collection to
     *  this list starting at a specified index. The elements are
     *  inserted in the specified collection's iteration order.
     *  All elements from the specified index and forward are pushed
     *  to their successors' indices (<tt>c.size()</tt> indices).
     *  All elements are added to the underlying list.
     *  <p>This method is only overridden to work
     *  around a bug in {@link AbstractList AbstractList},
     *  which does not throw a 
     *  {@link NullPointerException NullPointerException} when the
     *  argument is <tt>null</tt> and the list is empty.
     *
     *  @param      index
     *              the index at which to insert the elements of
     *              the specified collection. If
     *              <tt>index == size()</tt> the elements are appended
     *              to this list.
     *
     *  @param      c
     *              the collection whose elements to add to this
     *              list.
     *
     *  @return     <tt>true</tt> if this list was modified
     *              as a result of adding the elements of <tt>c</tt>;
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the
     *              underlying list.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index</tt> does not denote a valid insertion
     *              position (valid: <tt>0 - size()</tt>).
     *
     *  @throws     IllegalArgumentException
     *              if an element of <tt>c</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if an element of <tt>c</tt> is not of class
     *              {@link Long Long}.
     */
    public boolean addAll(int index, Collection c) {
        if (index > size() || index < 0)
            Exceptions.indexOutOfBounds(index, 0, size());
        return super.addAll(index, c);
    }

    /**
     *  Adds an element to this list at a specified index. All
     *  elements from the specified index and forward are pushed
     *  to their successor's indices. The element is added to
     *  the underlying collection.
     *
     *  @param      index
     *              the index at which to add the element. If
     *              <tt>index == size()</tt> the element is appended
     *              to this list.
     *
     *  @param      o
     *              the element to add to this list.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this
     *              list.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index</tt> does not denote a valid insertion
     *              position (valid: <tt>0 - size()</tt>).
     *
     *  @throws     IllegalArgumentException
     *              if <tt>o</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>o</tt> is not of class {@link Long Long}.
     */
    public void add(int index, Object o) {
        if (o == null)
            Exceptions.nullElementNotAllowed();
        list.add(index, ((Long)o).longValue() );
    }

    /**
     *  Returns the element at a specified position in this list.
     *  The returned value will always be of class {@link Long Long}.
     *
     *  @param      index
     *              the position of the element to return.
     *
     *  @return     the element at the specified position.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index</tt> does not denote a valid index
     *              in this list.
     */
    public Object get(int index)
    { return new Long(list.get(index)); }

    /**
     *  Returns a list iterator over this list, starting from a
     *  specified index.
     *
     *  @param      index
     *              the index at which to begin the iteration.
     *
     *  @return     a list iterator over this list.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index</tt> does not denote a valid
     *              iteration position (valid: <tt>0 - size()</tt>).
     */
    public ListIterator listIterator(int index)
    { return new LongListIteratorToListIteratorAdapter(list.listIterator(index)); }

    /**
     *  Removes the element at a specified index in this list. All
     *  elements following the removed element are pushed to their
     *  predecessor's indices. The element is removed from the
     *  underlying collection.
     *
     *  @param      index
     *              the index of the element to remove.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the underlying
     *              list.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index</tt> does not denote a valid
     *              element position (valid: <tt>0 - size()-1</tt>).
     */
    public Object remove(int index)
    { return new Long(list.removeElementAt(index)); }

    /**
     *  Removes all the elements of a specified collection from
     *  this list. The elements are removed from the
     *  underlying list.
     *  <p>This method is only overridden to work
     *  around a bug in {@link AbstractList AbstractList},
     *  which does not throw a 
     *  {@link NullPointerException NullPointerException} when the
     *  argument is <tt>null</tt> and the list is empty. The
     *  bug is inherited from {@link AbstractCollection AbstractCollection}.
     *
     *  @param      c
     *              the collection whose elements to remove from this
     *              collection.
     *
     *  @return     <tt>true</tt> if this list was modified
     *              as a result of removing the elements of <tt>c</tt>;
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the underlying
     *              collection.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public boolean removeAll(Collection c) {
        if (c == null)
            Exceptions.nullArgument("collection");
        return super.removeAll(c);
    }

    /**
     *  Retains only the elements of a specified collection in
     *  this list. The elements are removed from the
     *  underlying list.
     *  <p>This method is only overridden to work
     *  around a bug in {@link AbstractList AbstractList},
     *  which does not throw a 
     *  {@link NullPointerException NullPointerException} when the
     *  argument is <tt>null</tt> and the list is empty. The
     *  bug is inherited from {@link AbstractCollection AbstractCollection}.
     *
     *  @param      c
     *              the collection whose elements to retain in this
     *              collection.
     *
     *  @return     <tt>true</tt> if this list was modified
     *              as a result of removing the elements not contained
     *              in <tt>c</tt>;
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the underlying
     *              list.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public boolean retainAll(Collection c) {
        if (c == null)
            Exceptions.nullArgument("collection");
        return super.retainAll(c);
    }

    /**
     *  Sets a specified element to a new value. The corresponding
     *  element of the underlying list is set to the unwrapped value.
     *
     *  @param      index
     *              the index of the element whose value to set.
     *
     *  @param      o
     *              the new value of the specified element.
     *
     *  @return     the previous value of the element.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the underlying
     *              list.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>index</tt> does not denote a valid
     *              element position (valid: <tt>0 - size()-1</tt>).
     *
     *  @throws     IllegalArgumentException
     *              if <tt>o</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>o</tt> is not of class {@link Long Long}.
     */
    public Object set(int index, Object o) {
        if (o == null)
            Exceptions.nullElementNotAllowed();
        return new Long(list.set(index, ((Long)o).longValue()));
    }

    /**
     *  Returns the number of elements in this list. The
     *  number of elements is the same as that of the underlying
     *  list.
     *
     *  @return     the number of elements in this list.
     */
    public int size()
    { return list.size(); }


}
