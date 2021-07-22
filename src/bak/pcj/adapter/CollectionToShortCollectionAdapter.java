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

import bak.pcj.Adapter;
import bak.pcj.AbstractShortCollection;
import bak.pcj.ShortIterator;
import bak.pcj.util.Exceptions;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Iterator;

/**
 *  This class represents adaptions of Java Collections Framework
 *  collections to primitive collections of short values.
 *  The adapter is implemented as a wrapper around the collection. 
 *  Thus, changes to the underlying collection are reflected by this
 *  collection and vice versa.
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
 *      CollectionToShortCollectionAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      CollectionToShortCollectionAdapter s;
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
public class CollectionToShortCollectionAdapter extends AbstractShortCollection {

    /** The underlying collection. */
    protected Collection collection;

    /**
     *  Creates a new adaption of a collection to a collection of short
     *  values.
     *
     *  @param      collection
     *              the underlying collection. This collection must
     *              consist of values of class
     *              {@link Short Short}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     */
    public CollectionToShortCollectionAdapter(Collection collection) {
        this(collection, false);
    }

    /**
     *  Creates a new adaption of a collection to a collection of short
     *  values. The collection to adapt is optionally validated.
     *
     *  @param      collection
     *              the underlying collection. This collection must
     *              consist of values of class
     *              {@link Short Short}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @param      validate
     *              indicates whether <tt>collection</tt> should
     *              be checked for illegal values.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalStateException
     *              if <tt>validate</tt> is <tt>true</tt> and
     *              <tt>collection</tt> contains a <tt>null</tt> value
     *              or a value that is not of class
     *              {@link Short Short}.
     */
    public CollectionToShortCollectionAdapter(Collection collection, boolean validate) {
        super();
        if (collection == null)
            Exceptions.nullArgument("collection");
        this.collection = collection;
        if (validate)
            evalidate();
    }

    public boolean add(short v)
    { return collection.add(new Short(v)); }

    public void clear()
    { collection.clear(); }

    public boolean contains(short v)
    { return collection.contains(new Short(v)); }

    public int hashCode()
    { return collection.hashCode(); }

    public ShortIterator iterator()
    { return new IteratorToShortIteratorAdapter(collection.iterator()); }

    public boolean remove(short v)
    { return collection.remove(new Short(v)); }

    public int size()
    { return collection.size(); }

    /**
     *  Indicates whether the underlying collection is valid for
     *  this adapter. For the underlying collection to be valid, it
     *  can only contain {@link Short Short} values and no <tt>null</tt>
     *  values.
     *
     *  @return     <tt>true</tt> if the underlying collection is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isShortAdaptable(collection); }

    /**
     *  Validates the collection underlying this adapter and throws
     *  an exception if it is invalid. For the underlying collection
     *  to be valid, it can only contain {@link Short Short}
     *  values and no <tt>null</tt> values.
     *
     *  @throws     IllegalStateException
     *              if the underlying collection is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("collection");
    }

}
