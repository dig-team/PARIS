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
import bak.pcj.list.AbstractCharList;
import bak.pcj.list.CharListIterator;
import bak.pcj.util.Exceptions;

import java.util.List;

/**
 *  This class represents adaptions of Java Collections Framework
 *  lists to primitive lists of char values.
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
 *      ListToCharListAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      ListToCharListAdapter s;
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
public class ListToCharListAdapter extends AbstractCharList {

    /** The underlying list. */
    protected List list;

    /**
     *  Creates a new adaption of a list to a list of char
     *  values.
     *
     *  @param      list
     *              the underlying list. This list must
     *              consist of values of class
     *              {@link Character Character}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>list</tt> is <tt>null</tt>.
     */
    public ListToCharListAdapter(List list) {
        this(list, false);
    }

    /**
     *  Creates a new adaption of a list to a list of char
     *  values. The list to adapt is optionally validated.
     *
     *  @param      list
     *              the underlying list. This collection must
     *              consist of values of class
     *              {@link Character Character}. Otherwise a
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
     *              {@link Character Character}.
     */
    public ListToCharListAdapter(List list, boolean validate) {
        super();
        if (list == null)
            Exceptions.nullArgument("list");
        this.list = list;
        if (validate)
            evalidate();
    }

    public void add(int index, char v)
    { list.add(index, new Character(v)); }

    public char get(int index)
    { return ((Character)list.get(index)).charValue(); }

    public CharListIterator listIterator(int index)
    { return new ListIteratorToCharListIteratorAdapter(list.listIterator(index)); }

    public char removeElementAt(int index)
    { return ((Character)(list.remove(index))).charValue(); }

    public char set(int index, char v)
    { return ((Character)list.set(index, new Character(v))).charValue(); }

    public int size()
    { return list.size(); }

    /**
     *  Indicates whether the underlying list is valid for
     *  this adapter. For the underlying list to be valid, it
     *  can only contain {@link Character Character} values and no <tt>null</tt>
     *  values.
     *
     *  @return     <tt>true</tt> if the underlying list is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isCharAdaptable(list); }

    /**
     *  Validates the list underlying this adapter and throws
     *  an exception if it is invalid. For the underlying list
     *  to be valid, it can only contain {@link Character Character}
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
