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

import bak.pcj.DoubleIterator;
import bak.pcj.set.DoubleSortedSet;
import bak.pcj.set.AbstractDoubleSet;
import bak.pcj.adapter.IteratorToDoubleIteratorAdapter;

import java.util.SortedSet;

/**
 *  This class represents adaptions of Java Collections Framework
 *  sets to primitive sets of double values.
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
 *      SortedSetToDoubleSortedSetAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      SortedSetToDoubleSortedSetAdapter s;
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
public class SortedSetToDoubleSortedSetAdapter extends SetToDoubleSetAdapter implements DoubleSortedSet {

    /**
     *  Creates a new adaption to a set of double
     *  values.
     *
     *  @param      set
     *              the underlying set. This set must
     *              consist of values of class
     *              {@link Double Double}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>set</tt> is <tt>null</tt>.
     */
    public SortedSetToDoubleSortedSetAdapter(SortedSet set) {
        super(set);
    }

    /**
     *  Creates a new adaption to a set of double
     *  values. The set to adapt is optionally validated.
     *
     *  @param      set
     *              the underlying set. This set must
     *              consist of values of class
     *              {@link Double Double}. Otherwise a
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
     *              {@link Double Double}.
     */
    public SortedSetToDoubleSortedSetAdapter(SortedSet set, boolean validate) {
        super(set, validate);
    }

    public double first()
    { return ((Double)(((SortedSet)set).first())).doubleValue(); }

    public DoubleSortedSet headSet(double to) 
    { return new SortedSetToDoubleSortedSetAdapter(((SortedSet)set).headSet(new Double(to))); }

    public double last()
    { return ((Double)(((SortedSet)set).last())).doubleValue(); }

    public DoubleSortedSet subSet(double from, double to)
    { return new SortedSetToDoubleSortedSetAdapter(((SortedSet)set).subSet(new Double(from), new Double(to))); }

    public DoubleSortedSet tailSet(double from)
    { return new SortedSetToDoubleSortedSetAdapter(((SortedSet)set).tailSet(new Double(from))); }

}
