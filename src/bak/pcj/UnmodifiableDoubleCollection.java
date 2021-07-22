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
package bak.pcj;

import bak.pcj.util.Exceptions;

/**
 *  This class represents unmodifiable collections of double values.
 *
 *  @see        java.util.Collections#unmodifiableCollection(java.util.Collection)
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     21-08-2003 20:18
 *  @since      1.0
 */
public class UnmodifiableDoubleCollection implements DoubleCollection {

    /** The collection underlying this unmodifiable collection. */
    protected DoubleCollection collection;

    /**
     *  Creates a new unmodifiable collection on an existing
     *  collection. The result is a collection whose elements and
     *  behaviour is the same as the existing collection's except
     *  that the new collection cannot be modified.
     *
     *  @param      c
     *              the existing collection to make unmodifiable.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public UnmodifiableDoubleCollection(DoubleCollection c) {
        if (c == null)
            Exceptions.nullArgument("collection");
        this.collection = c;
    }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean add(double v)
    { Exceptions.unsupported("add"); throw new RuntimeException(); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean addAll(DoubleCollection c)
    { Exceptions.unsupported("addAll"); throw new RuntimeException(); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public void clear()
    { Exceptions.unsupported("clear"); }

    public boolean contains(double v)
    { return collection.contains(v); }

    public boolean containsAll(DoubleCollection c)
    { return collection.containsAll(c); }

    public boolean equals(Object obj)
    { return collection.equals(obj); }

    public int hashCode()
    { return collection.hashCode(); }

    public boolean isEmpty()
    { return collection.isEmpty(); }

    public DoubleIterator iterator() {
        final DoubleIterator i = collection.iterator();
        return new DoubleIterator() {
            public boolean hasNext()
            { return i.hasNext(); }

            public double next()
            { return i.next(); }

            //  It is necessary to override remove() since we have
            //  no way of knowing how iterators are implemented
            //  in the underlying class.
            public void remove()
            { Exceptions.unsupported("remove"); }
        };
    }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean remove(double v)
    { Exceptions.unsupported("remove"); throw new RuntimeException(); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean removeAll(DoubleCollection c)
    { Exceptions.unsupported("removeAll"); throw new RuntimeException(); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean retainAll(DoubleCollection c)
    { Exceptions.unsupported("retainAll"); throw new RuntimeException(); }

    public int size()
    { return collection.size(); }

    public double[] toArray()
    { return collection.toArray(); }

    public double[] toArray(double[] a)
    { return collection.toArray(a); }

    public void trimToSize()
    {  }

}
