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

import bak.pcj.IntIterator;
import bak.pcj.util.Exceptions;
import java.util.Iterator;

/**
 *  This class represents adaptions of primitive iterators over
 *  int values to Java Collections Framework iterators.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     21-08-2003 19:00
 *  @since      1.0
 */
public class IntIteratorToIteratorAdapter implements Iterator {

    /** The underlying iterator. */
    private IntIterator iterator;

    /**
     *  Creates a new adaption to an iterator from an iterator
     *  over int values.
     *
     *  @param      iterator
     *              the underlying iterator.
     *
     *  @throws     NullPointerException
     *              if <tt>iterator</tt> is <tt>null</tt>.
     */
    public IntIteratorToIteratorAdapter(IntIterator iterator) {
        if (iterator == null)
            Exceptions.nullArgument("iterator");
        this.iterator = iterator;
    }

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
    { return new Integer(iterator.next()); }

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

}
