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
 *  This class represents synchronized collections of char values. As in
 *  the Java Collections API iterations over the collection must be
 *  synchronized on the collection itself.
 *
 *  @see        java.util.Collections#synchronizedCollection(java.util.Collection)
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     21-08-2003 20:17
 *  @since      1.0
 */
public class SynchronizedCharCollection implements CharCollection {

    /** The collection underlying this synchronized collection. */
    protected CharCollection collection;

    /** An object on which to synchronize this collection's methods. */
    protected Object m;

    /**
     *  Creates a new synchronized collection on an existing
     *  collection. The result is a collection whose elements and
     *  behaviour is the same as the existing collection's except
     *  that the new collection's methods are synchronized.
     *
     *  @param      c
     *              the existing collection to make synchronized.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public SynchronizedCharCollection(CharCollection c) {
        if (c == null)
            Exceptions.nullArgument("collection");
        this.collection = c;
        this.m = this;
    }

    public boolean add(char v)
    { synchronized (m) { return collection.add(v); } }

    public boolean addAll(CharCollection c)
    { synchronized (m) { return collection.addAll(c); } }

    public void clear()
    { synchronized (m) { collection.clear(); } }

    public boolean contains(char v)
    { synchronized (m) { return collection.contains(v); } }

    public boolean containsAll(CharCollection c)
    { synchronized (m) { return collection.containsAll(c); } }

    public boolean equals(Object obj)
    { synchronized (m) { return collection.equals(obj); } }

    public int hashCode()
    { synchronized(m) { return collection.hashCode(); } }

    public boolean isEmpty()
    { synchronized (m) { return collection.isEmpty(); } }

    public CharIterator iterator()
    { synchronized (m) { return collection.iterator(); } }

    public boolean remove(char v)
    { synchronized (m) { return collection.remove(v); } }

    public boolean removeAll(CharCollection c)
    { synchronized (m) { return collection.removeAll(c); } }

    public boolean retainAll(CharCollection c)
    { synchronized (m) { return collection.retainAll(c); } }

    public int size()
    { synchronized (m) { return collection.size(); } }

    public char[] toArray()
    { synchronized (m) { return collection.toArray(); } }

    public char[] toArray(char[] a)
    { synchronized (m) { return collection.toArray(a); } }

    public void trimToSize()
    { synchronized (m) { collection.trimToSize(); } }

}
