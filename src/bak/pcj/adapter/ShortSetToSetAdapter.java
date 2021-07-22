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

import bak.pcj.set.ShortSet;
import bak.pcj.adapter.ShortIteratorToIteratorAdapter;
import bak.pcj.util.Exceptions;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Collection;

/**
 *  This class represents adapters of short sets to Java Collections
 *  Framework sets. The adapter
 *  is implemented as a wrapper around a primitive set. Thus, 
 *  changes to the underlying set are reflected by this
 *  set and vice versa.
 *
 *  @see        ShortSet
 *  @see        java.util.Set
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     20-08-2003 22:56
 *  @since      1.0
 */
public class ShortSetToSetAdapter extends AbstractSet {

    /** The underlying primitive set. */
    protected ShortSet set;

    /**
     *  Creates a new adaption of a set of short
     *  values to a Java Collections Framework set.
     *
     *  @param      set
     *              the underlying primitive set.
     *
     *  @throws     NullPointerException
     *              if <tt>set</tt> is <tt>null</tt>.
     */
    public ShortSetToSetAdapter(ShortSet set) {
        if (set == null)
            Exceptions.nullArgument("set");
        this.set = set;
    }

    /**
     *  Adds an element to this set. The unwrapped element is added
     *  to the underlying set.
     *
     *  @param      o
     *              the element to add to this set.
     *
     *  @return     <tt>true</tt> if this set was modified
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
     *              underlying set.
     */
    public boolean add(Object o) {
        if (o == null)
            Exceptions.nullElementNotAllowed();
        return set.add(((Short)o).shortValue());
    }

    /**
     *  Clears this collection. The underlying set is
     *  cleared.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the
     *              underlying set.
     */
    public void clear()
    { set.clear(); }

    /**
     *  Indicates whether this set contains a specified
     *  element. For this set to contain an object, the
     *  underlying set must contain its unwrapped value.
     *  <p>Note that this set can never contain <tt>null</tt>
     *  values or values of other classes than {@link Short Short}.
     *  In those cases, this method will return <tt>false</tt>.
     *
     *  @param      o
     *              the element to test for containment.
     *
     *  @return     <tt>true</tt> if <tt>o</tt> is contained in this
     *              set; returns <tt>false</tt> otherwise.
     */
    public boolean contains(Object o) {
        try {
            return set.contains( ((Short)o).shortValue() );
        } catch (ClassCastException cce) {
        } catch (NullPointerException npe) {
        }
        return false;
    }

    /**
     *  Returns a hash code value for this set. The hash code
     *  returned is that of the underlying set.
     *
     *  @return     a hash code value for this set.
     */
    public int hashCode()
    { return set.hashCode(); }

    /**
     *  Returns an iterator over this set.
     *
     *  @return     an iterator over this set.
     */
    public Iterator iterator()
    { return new ShortIteratorToIteratorAdapter(set.iterator()); }

    /**
     *  Removes a specified element from this set.
     *  The unwrapped element is removed from the underlying set.
     *  <p>Note that this set can never contain <tt>null</tt>
     *  values or values of other classes than {@link Short Short}.
     *  In those cases, this method will return <tt>false</tt>.
     *
     *  @param      o
     *              the Short value to remove from this set.
     *
     *  @return     <tt>true</tt> if this set was modified
     *              as a result of removing <tt>o</tt>; returns
     *              <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the 
     *              underlying set.
     */
    public boolean remove(Object o) {
        try {
            return set.remove( ((Short)o).shortValue() );
        } catch (ClassCastException cce) {
        } catch (NullPointerException npe) {
        }
        return false;
    }

    /**
     *  Retains only the elements of a specified collection in
     *  this set. The unwrapped elements are removed from
     *  the underlying set.
     *  <p>This method is only overridden to work
     *  around a bug in {@link AbstractSet AbstractSet},
     *  which does not throw a 
     *  {@link NullPointerException NullPointerException} when the
     *  argument is <tt>null</tt> and the set is empty. The
     *  bug is inherited from {@link java.util.AbstractCollection java.util.AbstractCollection}.
     *
     *  @param      c
     *              the collection whose elements to retain in this
     *              collection.
     *
     *  @return     <tt>true</tt> if this set was modified
     *              as a result of removing the elements not contained
     *              in <tt>c</tt>;
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the underlying
     *              set.
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
     *  Returns the number of elements in this set. The
     *  number of elements is the same as that of the underlying
     *  set.
     *
     *  @return     the number of elements in this set.
     */
    public int size()
    { return set.size(); }

}
