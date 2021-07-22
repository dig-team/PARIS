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
import bak.pcj.list.AbstractShortList;
import bak.pcj.list.ShortListIterator;
import bak.pcj.util.Exceptions;

import java.util.List;

/**
 *  This class represents adaptions of Java Collections Framework
 *  lists to primitive lists of short values.
 *  The adapter is implemented as a wrapper around the list. 
 *  Thus, changes to the underlying list are reflected by this
 *  list and vice versa.
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
 *      ListToShortListAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      ListToShortListAdapter s;
 *      ...
 *      s.evalidate();  // Throws an exception on illegal values
 *  </pre>
 *  Either way, validation must be invoked directly by the client
 *  code.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     20-08-2003 23:17
 *  @since      1.0
 */
public class ListToShortListAdapter extends AbstractShortList {

    /** The underlying list. */
    protected List list;

    /**
     *  Creates a new adaption of a list to a list of short
     *  values.
     *
     *  @param      list
     *              the underlying list. This list must
     *              consist of values of class
     *              {@link Short Short}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>list</tt> is <tt>null</tt>.
     */
    public ListToShortListAdapter(List list) {
        this(list, false);
    }

    /**
     *  Creates a new adaption of a list to a list of short
     *  values. The list to adapt is optionally validated.
     *
     *  @param      list
     *              the underlying list. This collection must
     *              consist of values of class
     *              {@link Short Short}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @param      validate
     *              indicates whether <tt>list</tt> should
     *              be checked for illegal values.
     *
     *  @throws     NullPointerException
     *              if <tt>list</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalStateException
     *              if <tt>validate</tt> is <tt>true</tt> and
     *              <tt>list</tt> contains a <tt>null</tt> value
     *              or a value that is not of class
     *              {@link Short Short}.
     */
    public ListToShortListAdapter(List list, boolean validate) {
        super();
        if (list == null)
            Exceptions.nullArgument("list");
        this.list = list;
        if (validate)
            evalidate();
    }

    public void add(int index, short v)
    { list.add(index, new Short(v)); }

    public short get(int index)
    { return ((Short)list.get(index)).shortValue(); }

    public ShortListIterator listIterator(int index)
    { return new ListIteratorToShortListIteratorAdapter(list.listIterator(index)); }

    public short removeElementAt(int index)
    { return ((Short)(list.remove(index))).shortValue(); }

    public short set(int index, short v)
    { return ((Short)list.set(index, new Short(v))).shortValue(); }

    public int size()
    { return list.size(); }

    /**
     *  Indicates whether the underlying list is valid for
     *  this adapter. For the underlying list to be valid, it
     *  can only contain {@link Short Short} values and no <tt>null</tt>
     *  values.
     *
     *  @return     <tt>true</tt> if the underlying list is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isShortAdaptable(list); }

    /**
     *  Validates the list underlying this adapter and throws
     *  an exception if it is invalid. For the underlying list
     *  to be valid, it can only contain {@link Short Short}
     *  values and no <tt>null</tt> values.
     *
     *  @throws     IllegalStateException
     *              if the underlying list is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("list");
    }

}
