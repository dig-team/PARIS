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
package bak.pcj.list;

import bak.pcj.util.Exceptions;

import bak.pcj.DoubleCollection;
import bak.pcj.UnmodifiableDoubleCollection;

/**
 *  This class represents unmodifiable lists of double values.
 *
 *  @see        java.util.Collections#unmodifiableList(java.util.List)
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     24-08-2003 20:52
 *  @since      1.0
 */
public class UnmodifiableDoubleList extends UnmodifiableDoubleCollection implements DoubleList {

    /**
     *  Creates a new unmodifiable list on an existing
     *  list. The result is a list whose elements and
     *  behaviour is the same as the existing list's except
     *  that the new list cannot be modified.
     *
     *  @param      l
     *              the existing list to make unmodifiable.
     *
     *  @throws     NullPointerException
     *              if <tt>l</tt> is <tt>null</tt>.
     */
    public UnmodifiableDoubleList(DoubleList l) {
        super(l);
    }

    private DoubleList list()
    { return (DoubleList)collection; }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public void add(int index, double v)
    { Exceptions.unmodifiable("list"); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean addAll(int index, DoubleCollection c)
    { Exceptions.unmodifiable("list"); return false; }

    public double get(int index)
    { return list().get(index); }

    public int indexOf(double c)
    { return list().indexOf(c); }

    /**
     *  @since      1.2
     */
    public int indexOf(int index, double c)
    { return list().indexOf(index, c); }

    public int lastIndexOf(double c)
    { return list().lastIndexOf(c); }

    /**
     *  @since      1.2
     */
    public int lastIndexOf(int index, double c)
    { return list().lastIndexOf(index, c); }

    public DoubleListIterator listIterator()
    { return listIterator(0); }

    public DoubleListIterator listIterator(int index) {
        final DoubleListIterator i = list().listIterator(index);
        return new DoubleListIterator() {
            public boolean hasNext()
            { return i.hasNext(); }

            public double next()
            { return i.next(); }

            //  It is necessary to override remove() since we have
            //  no way of knowing how iterators are implemented
            //  in the underlying class.
            public void remove()
            { Exceptions.unmodifiable("list"); }
            
            public void add(double v)
            { Exceptions.unmodifiable("list"); }

            public boolean hasPrevious()
            { return i.hasPrevious(); }

            public int nextIndex()
            { return i.nextIndex(); }

            public double previous()
            { return i.previous(); }
            
            public int previousIndex()
            { return i.previousIndex(); }

            public void set(double v)
            { Exceptions.unmodifiable("list"); }
        };
    }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public double removeElementAt(int index)
    { Exceptions.unmodifiable("list"); throw new RuntimeException(); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public double set(int index, double v)
    { Exceptions.unmodifiable("list"); throw new RuntimeException(); }

}
