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

import bak.pcj.FloatIterator;
import bak.pcj.util.Exceptions;
import java.util.Iterator;

/**
 *  This class represents adaptions of Java Collections Framework
 *  iterators to primitive iterators over float values.
 *
 *  <p>
 *  Adapters from JCF collections to primitive collections will
 *  fail if the JCF collection contains <tt>null</tt> values or
 *  values of the wrong class. However, adapters are not fast
 *  failing in the case that the underlying collection should
 *  contain illegal values. To implement fast failure would require
 *  every operation to check every element of the underlying
 *  collection before doing anything. Instead validation methods
 *  are provided. They can be called using the assertion facility
 *  in the client code:
 *  <pre>
 *      CollectionToFloatCollectionAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      CollectionToFloatCollectionAdapter s;
 *      ...
 *      s.evalidate();  // Throws an exception on illegal values
 *  </pre>
 *  Either way, validation must be invoked directly by the client
 *  code.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     20-08-2003 23:18
 *  @since      1.0
 */
public class IteratorToFloatIteratorAdapter implements FloatIterator {

    /** The underlying iterator. */
    private Iterator iterator;

    /**
     *  Creates a new adaption to an iterator over float
     *  values.
     *
     *  @param      iterator
     *              the underlying iterator. This iterator must
     *              return values of class
     *              {@link Float Float}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by
     *              {@link #next() next()}.
     *
     *  @throws     NullPointerException
     *              if <tt>iterator</tt> is <tt>null</tt>.
     */
    public IteratorToFloatIteratorAdapter(Iterator iterator) {
        if (iterator == null)
            Exceptions.nullArgument("iterator");
        this.iterator = iterator;
    }

    public boolean hasNext()
    { return iterator.hasNext(); }

    /**
     *  @throws     ClassCastException
     *              if the underlying iterator returns an object
     *              that is not of class
     *              {@link Float Float}.
     */
    public float next()
    { return ((Float)iterator.next()).floatValue(); }

    public void remove()
    { iterator.remove(); }

}
