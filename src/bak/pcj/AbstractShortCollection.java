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
 *  This class represents an abstract base for implementing
 *  collections of short values. All operations that can be implemented
 *  using iterators are implemented as such. In most cases, this is
 *  hardly an efficient solution, and at least some of those
 *  methods should be overridden by sub-classes.
 *
 *  <p>In this implementation, <tt>size()</tt> is calculated by
 *  iterating over the collection. Make sure that <tt>size()</tt>
 *  is overwritten or that iterators do not depend on the
 *  <tt>size()</tt> method.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     21-08-2003 20:16
 *  @since      1.0
 */
public abstract class AbstractShortCollection implements ShortCollection {

    /** Default constructor to be invoked by sub-classes. */
    protected AbstractShortCollection() { }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean add(short v)
    { Exceptions.unsupported("add"); return false; }

    public boolean addAll(ShortCollection c) {
        ShortIterator i = c.iterator();  //  Throws NullPointerException
        boolean result = false;
        while (i.hasNext())
            result = result | add(i.next());
        return result;
    }

    public void clear() {
        ShortIterator i = iterator();
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
    }

    public boolean contains(short v) {
        ShortIterator i = iterator();
        while (i.hasNext())
            if (i.next() == v)
                return true;
        return false;
    }

    public boolean containsAll(ShortCollection c) {
        ShortIterator i = c.iterator();  //  Throws NullPointerException
        while (i.hasNext())
            if (!contains(i.next()))
                return false;
        return true;
    }

    public boolean isEmpty()
    { return size() == 0; }

    public boolean remove(short v) {
        ShortIterator i = iterator();
        boolean result = false;
        while (i.hasNext()) {
            if (i.next() == v) {
                i.remove();
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean removeAll(ShortCollection c) {
        if (c == null)
            Exceptions.nullArgument("collection");
        ShortIterator i = iterator();
        boolean result = false;
        while (i.hasNext()) {
            if (c.contains(i.next())) {
                i.remove();
                result = true;
            }
        }
        return result;
    }

    public boolean retainAll(ShortCollection c) {
        if (c == null)
            Exceptions.nullArgument("collection");
        ShortIterator i = iterator();
        boolean result = false;
        while (i.hasNext()) {
            if (!c.contains(i.next())) {
                i.remove();
                result = true;
            }
        }
        return result;
    }

    public int size() {
        ShortIterator i = iterator();
        int size = 0;
        while (i.hasNext()) {
            i.next();
            size++;
        }
        return size;
    }

    public short[] toArray() {
        return toArray(null);
    }

    public short[] toArray(short[] a) {
        int size = size();
        if (a == null || a.length < size)
            a = new short[size];
        ShortIterator i = iterator();
        int index = 0;
        while (i.hasNext()) {
            a[index] = i.next();
            index++;
        }
        return a;
    }

    /**
     *  Does nothing. Sub-classes may provide an implementation to
     *  minimize memory usage, but this is not required since many
     *  implementations will always have minimal memory usage.
     */
    public void trimToSize()
    { }

    /**
     *  Returns a string representation of this collection.
     *
     *  @return     a string representation of this collection.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('[');
        ShortIterator i = iterator();
        while (i.hasNext()) {
            if (s.length() > 1)
                s.append(',');
            s.append(bak.pcj.util.Display.display(i.next()));
        }
        s.append(']');
        return s.toString();
    }

}
