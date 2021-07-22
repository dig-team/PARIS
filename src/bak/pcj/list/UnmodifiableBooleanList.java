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

import bak.pcj.BooleanCollection;
import bak.pcj.UnmodifiableBooleanCollection;

/**
 *  This class represents unmodifiable lists of boolean values.
 *
 *  @see        java.util.Collections#unmodifiableList(java.util.List)
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     24-08-2003 20:52
 *  @since      1.0
 */
public class UnmodifiableBooleanList extends UnmodifiableBooleanCollection implements BooleanList {

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
    public UnmodifiableBooleanList(BooleanList l) {
        super(l);
    }

    private BooleanList list()
    { return (BooleanList)collection; }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public void add(int index, boolean v)
    { Exceptions.unmodifiable("list"); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean addAll(int index, BooleanCollection c)
    { Exceptions.unmodifiable("list"); return false; }

    public boolean get(int index)
    { return list().get(index); }

    public int indexOf(boolean c)
    { return list().indexOf(c); }

    /**
     *  @since      1.2
     */
    public int indexOf(int index, boolean c)
    { return list().indexOf(index, c); }

    public int lastIndexOf(boolean c)
    { return list().lastIndexOf(c); }

    /**
     *  @since      1.2
     */
    public int lastIndexOf(int index, boolean c)
    { return list().lastIndexOf(index, c); }

    public BooleanListIterator listIterator()
    { return listIterator(0); }

    public BooleanListIterator listIterator(int index) {
        final BooleanListIterator i = list().listIterator(index);
        return new BooleanListIterator() {
            public boolean hasNext()
            { return i.hasNext(); }

            public boolean next()
            { return i.next(); }

            //  It is necessary to override remove() since we have
            //  no way of knowing how iterators are implemented
            //  in the underlying class.
            public void remove()
            { Exceptions.unmodifiable("list"); }
            
            public void add(boolean v)
            { Exceptions.unmodifiable("list"); }

            public boolean hasPrevious()
            { return i.hasPrevious(); }

            public int nextIndex()
            { return i.nextIndex(); }

            public boolean previous()
            { return i.previous(); }
            
            public int previousIndex()
            { return i.previousIndex(); }

            public void set(boolean v)
            { Exceptions.unmodifiable("list"); }
        };
    }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean removeElementAt(int index)
    { Exceptions.unmodifiable("list"); throw new RuntimeException(); }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public boolean set(int index, boolean v)
    { Exceptions.unmodifiable("list"); throw new RuntimeException(); }

}
