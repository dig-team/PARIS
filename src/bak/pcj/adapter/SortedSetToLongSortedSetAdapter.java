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

import bak.pcj.LongIterator;
import bak.pcj.set.LongSortedSet;
import bak.pcj.set.AbstractLongSet;
import bak.pcj.adapter.IteratorToLongIteratorAdapter;

import java.util.SortedSet;

/**
 *  This class represents adaptions of Java Collections Framework
 *  sets to primitive sets of long values.
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
 *      SortedSetToLongSortedSetAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      SortedSetToLongSortedSetAdapter s;
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
public class SortedSetToLongSortedSetAdapter extends SetToLongSetAdapter implements LongSortedSet {

    /**
     *  Creates a new adaption to a set of long
     *  values.
     *
     *  @param      set
     *              the underlying set. This set must
     *              consist of values of class
     *              {@link Long Long}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>set</tt> is <tt>null</tt>.
     */
    public SortedSetToLongSortedSetAdapter(SortedSet set) {
        super(set);
    }

    /**
     *  Creates a new adaption to a set of long
     *  values. The set to adapt is optionally validated.
     *
     *  @param      set
     *              the underlying set. This set must
     *              consist of values of class
     *              {@link Long Long}. Otherwise a
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
     *              {@link Long Long}.
     */
    public SortedSetToLongSortedSetAdapter(SortedSet set, boolean validate) {
        super(set, validate);
    }

    public long first()
    { return ((Long)(((SortedSet)set).first())).longValue(); }

    public LongSortedSet headSet(long to) 
    { return new SortedSetToLongSortedSetAdapter(((SortedSet)set).headSet(new Long(to))); }

    public long last()
    { return ((Long)(((SortedSet)set).last())).longValue(); }

    public LongSortedSet subSet(long from, long to)
    { return new SortedSetToLongSortedSetAdapter(((SortedSet)set).subSet(new Long(from), new Long(to))); }

    public LongSortedSet tailSet(long from)
    { return new SortedSetToLongSortedSetAdapter(((SortedSet)set).tailSet(new Long(from))); }

}
