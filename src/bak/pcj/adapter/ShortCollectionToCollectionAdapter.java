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

import bak.pcj.ShortCollection;
import bak.pcj.util.Exceptions;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Iterator;

/**
 *  This class represents adaptions of primitive collections of
 *  short values to Java Collections Framework collections. The adapter
 *  is implemented as a wrapper around a primitive collection. Thus, 
 *  changes to the underlying collection are reflected by this
 *  collection and vice versa.
 *
 *  @see        ShortCollection
 *  @see        java.util.Collection
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     21-08-2003 19:01
 *  @since      1.0
 */
public class ShortCollectionToCollectionAdapter extends AbstractCollection {

    /** The underlying primitive collection. */
    protected ShortCollection collection;

    /**
     *  Creates a new adaption of a collection of short
     *  values to a Java Collections Framework collection.
     *
     *  @param      collection
     *              the underlying primitive collection.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     */
    public ShortCollectionToCollectionAdapter(ShortCollection collection) {
        super();
        if (collection == null)
            Exceptions.nullArgument("collection");
        this.collection = collection;
    }

    /**
     *  Adds an element to this collection. The element is added
     *  to the underlying collection.
     *
     *  @param      o
     *              the element to add to this collection.
     *
     *  @return     <tt>true</tt> if this collection was modified
     *              as a result of adding <tt>o</tt>; returns
     *              <tt>false</tt> otherwise.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>o</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>o</tt> is not of class {@link Short Short}.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the
     *              underlying collection.
     */
    public boolean add(Object o) {
        if (o == null)
            Exceptions.nullElementNotAllowed();
        return collection.add(((Short)o).shortValue());
    }

    /**
     *  Clears this collection. The underlying collection is
     *  cleared.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the
     *              underlying collection.
     */
    public void clear()
    { collection.clear(); }

    /**
     *  Indicates whether this collection contains a specified
     *  element. For this collection to contain an object, the
     *  underlying collection must contain its unwrapped value.
     *  <p>Note that this collection can never contain <tt>null</tt>
     *  values or values of other classes than {@link Short Short}.
     *  In those cases, this method will return <tt>false</tt>.
     *
     *  @param      o
     *              the element to test for containment.
     *
     *  @return     <tt>true</tt> if <tt>o</tt> is contained in this
     *              collection; returns <tt>false</tt> otherwise.
     */
    public boolean contains(Object o) {
        try {
            return collection.contains(((Short)o).shortValue());
        } catch (ClassCastException cce) {
        } catch (NullPointerException npe) {
        }
        return false;
    }

    /**
     *  Returns an iterator over this collection.
     *
     *  @return     an iterator over this collection.
     */
    public Iterator iterator()
    { return new ShortIteratorToIteratorAdapter(collection.iterator()); }

    /**
     *  Removes a specified element from this collection.
     *  The unwrapped element is removed from the underlying collection.
     *  <p>Note that this collection can never contain <tt>null</tt>
     *  values or values of other classes than {@link Short Short}.
     *  In those cases, this method will return <tt>false</tt>.
     *
     *  @param      o
     *              the Short value to remove from this collection.
     *
     *  @return     <tt>true</tt> if this collection was modified
     *              as a result of removing <tt>o</tt>; returns
     *              <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the 
     *              underlying collection.
     */
    public boolean remove(Object o) {
        try {
            return collection.remove(((Short)o).shortValue());
        } catch (ClassCastException cce) {
        } catch (NullPointerException npe) {
        }
        return false;
    }

    /**
     *  Removes all the elements of a specified collection from
     *  this collection. The unwrapped elements are removed from
     *  the underlying collection.
     *  <p>This method is only overridden to work
     *  around a bug in {@link AbstractCollection AbstractCollection},
     *  which does not throw a 
     *  {@link NullPointerException NullPointerException} when the
     *  argument is <tt>null</tt> and the collection is empty.
     *
     *  @param      c
     *              the collection whose elements to remove from this
     *              collection.
     *
     *  @return     <tt>true</tt> if this collection was modified
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
     *  this collection. The unwrapped elements are removed from
     *  the underlying collection.
     *  <p>This method is only overridden to work
     *  around a bug in {@link AbstractCollection AbstractCollection},
     *  which does not throw a 
     *  {@link NullPointerException NullPointerException} when the
     *  argument is <tt>null</tt> and the collection is empty.
     *
     *  @param      c
     *              the collection whose elements to retain in this
     *              collection.
     *
     *  @return     <tt>true</tt> if this collection was modified
     *              as a result of removing the elements not contained
     *              in <tt>c</tt>;
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the underlying
     *              collection.
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
     *  Returns the number of elements in this collection. The
     *  number of elements is the same as that of the underlying
     *  collection.
     *
     *  @return     the number of elements in this collection.
     */
    public int size()
    { return collection.size(); }

    /**
     *  Returns a hash code value for this collection. The hash code
     *  returned is that of the underlying collection.
     *
     *  @return     a hash code value for this collection.
     */
    public int hashCode()
    { return collection.hashCode(); }

}
