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

import bak.pcj.CharIterator;
import bak.pcj.CharCollection;
import bak.pcj.AbstractCharCollection;
import bak.pcj.hash.DefaultCharHashFunction;
import bak.pcj.util.Exceptions;

/**
 *  This class represents an abstract base for implementing
 *  lists of char values. All operations that can be implemented
 *  using iterators and the <tt>get()</tt> and <tt>set()</tt> methods
 *  are implemented as such. In most cases, this is
 *  hardly an efficient solution, and at least some of those
 *  methods should be overridden by sub-classes.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     21-08-2003 19:14
 *  @since      1.0
 */
public abstract class AbstractCharList extends AbstractCharCollection implements CharList {

    /** Default constructor to be invoked by sub-classes. */
    protected AbstractCharList() { }

    public boolean add(char v)
    { add(size(), v); return true; }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public void add(int index, char v)
    { Exceptions.unsupported("add"); }


    public boolean addAll(int index, CharCollection c) {
        if (index < 0 || index > size())
            Exceptions.indexOutOfBounds(index, 0, size());
        CharIterator i = c.iterator();
        boolean result = i.hasNext();
        while (i.hasNext()) {
            add(index, i.next());
            index++;
        }
        return result;
    }

    public int indexOf(char c) {
        return indexOf(0, c);
    }

    /**
     *  @since      1.2
     */
    public int indexOf(int index, char c) {
        CharListIterator i = listIterator(index);
        while (i.hasNext())
            if (i.next() == c)
                return i.previousIndex();
        return -1;
    }

    public CharIterator iterator()
    { return listIterator(); }

    public int lastIndexOf(char c) {
        CharListIterator i = listIterator(size());
        while (i.hasPrevious())
            if (i.previous() == c)
                return i.nextIndex();
        return -1;
    }

    public int lastIndexOf(int index, char c) {
        CharListIterator i = listIterator(index);
        while (i.hasPrevious())
            if (i.previous() == c)
                return i.nextIndex();
        return -1;
    }

    public CharListIterator listIterator()
    { return listIterator(0); }

    public CharListIterator listIterator(final int index) {
        if (index < 0 || index > size())
            Exceptions.indexOutOfBounds(index, 0, size());

        return new CharListIterator() {
            private int ptr = index;
            private int lptr = -1;

            // -------------------------------------------------------
            //      Implementation of Iterator
            // -------------------------------------------------------

            public boolean hasNext() {
                return ptr < size();
            }

            public char next() {
                if (ptr == size())
                    Exceptions.endOfIterator();
                lptr = ptr++;
                return get(lptr);
            }

            public void remove() {
                if (lptr == -1)
                    Exceptions.noElementToRemove();
                AbstractCharList.this.removeElementAt(lptr);
                if (lptr < ptr) ptr--;
                lptr = -1;
            }

            // -------------------------------------------------------
            //      Implementation of ListIterator
            // -------------------------------------------------------

            public void add(char v) {
                AbstractCharList.this.add(ptr++, v);
                lptr = -1;
            }

            public boolean hasPrevious() {
                return ptr > 0;
            }

            public int nextIndex()
            { return ptr; }

            public char previous() {
                if (ptr == 0)
                    Exceptions.startOfIterator();
                ptr--;
                lptr = ptr;
                return get(ptr);
            }

            public int previousIndex()
            { return ptr-1; }

            public void set(char v) {
                if (lptr == -1)
                    Exceptions.noElementToSet();
                AbstractCharList.this.set(lptr, v);
            }

        };
    }

    /**
     *  Throws <tt>UnsupportedOperationException</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              unconditionally.
     */
    public char removeElementAt(int index)
    { Exceptions.unsupported("removeElementAt"); throw new RuntimeException(); }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CharList))
            return false;
        CharListIterator i1 = listIterator();
        CharListIterator i2 = ((CharList)obj).listIterator();
        while(i1.hasNext() && i2.hasNext())
            if (i1.next() != i2.next())
                return false;
        return !(i1.hasNext() || i2.hasNext());
    }

    public int hashCode() {
        int h = 1;
        CharIterator i = iterator();
        while (i.hasNext())
            h = (int)(31*h + DefaultCharHashFunction.INSTANCE.hash(i.next()));
        return h;
    }

}
