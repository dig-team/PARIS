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

import bak.pcj.IntIterator;
import bak.pcj.set.IntSortedSet;
import bak.pcj.set.AbstractIntSet;
import bak.pcj.adapter.IteratorToIntIteratorAdapter;

import java.util.SortedSet;

/**
 *  This class represents adaptions of Java Collections Framework
 *  sets to primitive sets of int values.
 *  The adapter is implemented as a wrapper around the set. 
 *  Thus, changes to the underlying set are reflected by this
 *  set and vice versa.
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
 *      SortedSetToIntSortedSetAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      SortedSetToIntSortedSetAdapter s;
 *      ...
 *      s.evalidate();  // Throws an exception on illegal values
 *  </pre>
 *  Either way, validation must be invoked directly by the client
 *  code.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     20-08-2003 23:16
 *  @since      1.2
 */
public class SortedSetToIntSortedSetAdapter extends SetToIntSetAdapter implements IntSortedSet {

    /**
     *  Creates a new adaption to a set of int
     *  values.
     *
     *  @param      set
     *              the underlying set. This set must
     *              consist of values of class
     *              {@link Integer Integer}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>set</tt> is <tt>null</tt>.
     */
    public SortedSetToIntSortedSetAdapter(SortedSet set) {
        super(set);
    }

    /**
     *  Creates a new adaption to a set of int
     *  values. The set to adapt is optionally validated.
     *
     *  @param      set
     *              the underlying set. This set must
     *              consist of values of class
     *              {@link Integer Integer}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @param      validate
     *              indicates whether <tt>set</tt> should
     *              be checked for illegal values.
     *
     *  @throws     NullPointerException
     *              if <tt>set</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalStateException
     *              if <tt>validate</tt> is <tt>true</tt> and
     *              <tt>set</tt> contains a <tt>null</tt> value
     *              or a value that is not of class
     *              {@link Integer Integer}.
     */
    public SortedSetToIntSortedSetAdapter(SortedSet set, boolean validate) {
        super(set, validate);
    }

    public int first()
    { return ((Integer)(((SortedSet)set).first())).intValue(); }

    public IntSortedSet headSet(int to) 
    { return new SortedSetToIntSortedSetAdapter(((SortedSet)set).headSet(new Integer(to))); }

    public int last()
    { return ((Integer)(((SortedSet)set).last())).intValue(); }

    public IntSortedSet subSet(int from, int to)
    { return new SortedSetToIntSortedSetAdapter(((SortedSet)set).subSet(new Integer(from), new Integer(to))); }

    public IntSortedSet tailSet(int from)
    { return new SortedSetToIntSortedSetAdapter(((SortedSet)set).tailSet(new Integer(from))); }

}
