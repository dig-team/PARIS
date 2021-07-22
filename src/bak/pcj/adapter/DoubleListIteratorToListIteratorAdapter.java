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

import bak.pcj.list.DoubleListIterator;
import bak.pcj.util.Exceptions;
import java.util.ListIterator;

/**
 *  This class represents adaptions of primitive list iterators over
 *  double values to Java Collections Framework list iterators.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     21-08-2003 18:59
 *  @since      1.0
 */
public class DoubleListIteratorToListIteratorAdapter implements ListIterator {

    /** The underlying primitive iterator. */
    protected DoubleListIterator iterator;

    /**
     *  Creates a new adaption of a primitive list iterator over
     *  double values to a Java Collections Framework list iterator.
     *
     *  @param      iterator
     *              the primitive iterator to adapt.
     *
     *  @throws     NullPointerException
     *              if <tt>iterator</tt> is <tt>null</tt>.
     */
    public DoubleListIteratorToListIteratorAdapter(DoubleListIterator iterator) {
        if (iterator == null)
            Exceptions.nullArgument("iterator");
        this.iterator = iterator;
    }

    /**
     *  Adds a specified element to the list at this iterator's
     *  current position.
     *
     *  @param      o
     *              the element to add.
     *
     *  @throws     UnsupportedOperationException
     *              if addition is not supported by this
     *              iterator.
     *
     *  @throws     ClassCastException
     *              if <tt>o</tt> is not of class
     *              {@link Double Double}.
     *
     *  @throws     NullPointerException
     *              if <tt>o</tt> is <tt>null</tt>.
     */
    public void add(Object o)
    { iterator.add( ((Double)o).doubleValue() ); }

    /**
     *  Indicates whether more values can be returned by this
     *  iterator.
     *
     *  @return     <tt>true</tt> if more values can be returned
     *              by this iterator; returns <tt>false</tt>
     *              otherwise.
     *
     *  @see        #next()
     */
    public boolean hasNext()
    { return iterator.hasNext(); }

    /**
     *  Indicates whether more values can be returned by this
     *  iterator by calling <tt>previous()</tt>.
     *
     *  @return     <tt>true</tt> if more values can be returned
     *              by this iterator in backwards direction; returns
     *              <tt>false</tt> otherwise.
     *
     *  @see        #previous()
     */
    public boolean hasPrevious()
    { return iterator.hasPrevious(); }

    /**
     *  Returns the next value of this iterator.
     *
     *  @return     the next value of this iterator.
     *
     *  @throws     NoSuchElementException
     *              if no more elements are available from this
     *              iterator.
     *
     *  @see        #hasNext()
     */
    public Object next()
    { return new Double(iterator.next()); }

    /**
     *  Returns the index of the element that would be returned by
     *  a call to <tt>next()</tt>.
     *
     *  @return     the index of the element that would be returned by
     *              a call to <tt>next()</tt>.
     *
     *  @see        #next()
     */
    public int nextIndex()
    { return iterator.nextIndex(); }

    /**
     *  Returns the previous value of this iterator.
     *
     *  @return     the previous value of this iterator.
     *
     *  @throws     NoSuchElementException
     *              if no more elements are available from this
     *              iterator in backwards direction.
     *
     *  @see        #hasPrevious()
     */
    public Object previous()
    { return new Double(iterator.previous()); }

    /**
     *  Returns the index of the element that would be returned by
     *  a call to <tt>previous()</tt>.
     *
     *  @return     the index of the element that would be returned by
     *              a call to <tt>previous()</tt>; if no more elements
     *              are available in backwards direction, <tt>-1</tt>
     *              is returned.
     *
     *  @see        #previous()
     */
    public int previousIndex()
    { return iterator.previousIndex(); }

    /**
     *  Removes the last value returned from the underlying
     *  collection.
     *
     *  @throws     UnsupportedOperationException
     *              if removal is not supported by this iterator.
     *
     *  @throws     IllegalStateException
     *              if no element has been returned by this iterator
     *              yet.
     */
    public void remove()
    { iterator.remove(); }

    /**
     *  Sets the last element returned to a specified value.
     *
     *  @param      o
     *              the new value of the element.
     *
     *  @throws     UnsupportedOperationException
     *              if replacement is not supported by this iterator.
     *
     *  @throws     IllegalStateException
     *              if no element has been returned by this iterator
     *              yet.
     *
     *  @throws     ClassCastException
     *              if <tt>o</tt> is not of class
     *              {@link Double Double}.
     *
     *  @throws     NullPointerException
     *              if <tt>o</tt> is <tt>null</tt>.
     */
    public void set(Object o)
    { iterator.set(((Double)o).doubleValue()); }

}
