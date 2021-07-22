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
import bak.pcj.IntIterator;
import bak.pcj.DoubleCollection;
import bak.pcj.map.IntKeyDoubleMap;
import bak.pcj.map.AbstractIntKeyDoubleMap;
import bak.pcj.map.IntKeyDoubleMapIterator;
import bak.pcj.map.MapDefaults;
import bak.pcj.map.NoSuchMappingException;
import bak.pcj.set.IntSet;
import bak.pcj.util.Exceptions;

import java.util.Map;
import java.util.Iterator;

/**
 *  This class represents adaptions of Java Collections Framework
 *  maps to primitive maps from int values to double values.
 *  The adapter is implemented as a wrapper around the map. 
 *  Thus, changes to the underlying map are reflected by this
 *  map and vice versa.
 *
 *  <p>
 *  Adapters from JCF maps to primitive map will
 *  fail if the JCF collection contains <tt>null</tt> keys/values or
 *  keys/values of the wrong class. However, adapters are not fast
 *  failing in the case that the underlying map should
 *  contain illegal keys or values. To implement fast failure would require
 *  every operation to check every key and value of the underlying
 *  map before doing anything. Instead validation methods
 *  are provided. They can be called using the assertion facility
 *  in the client code:
 *  <pre>
 *      MapToIntKeyDoubleMapAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      MapToIntKeyDoubleMapAdapter s;
 *      ...
 *      s.evalidate();  // Throws an exception on illegal values
 *  </pre>
 *  Either way, validation must be invoked directly by the client
 *  code.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     21-08-2003 19:09
 *  @since      1.0
 */
public class MapToIntKeyDoubleMapAdapter extends AbstractIntKeyDoubleMap implements IntKeyDoubleMap {

    /** The underlying map. */
    protected Map map;

    /** The value corresponding to the last key found by containsKey(). */
    protected Double lastValue;

    /**
     *  Creates a new adaption to a map from int
     *  values to double values.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of keys of class
     *              {@link Integer Integer}.
     *              values of class
     *              {@link Double Double}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public MapToIntKeyDoubleMapAdapter(Map map) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
    }

    /**
     *  Creates a new adaption to a map from int
     *  values to double values. The map to adapt is optionally validated.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of keys of class
     *              {@link Integer Integer}.
     *              values of class
     *              {@link Double Double}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @param      validate
     *              indicates whether <tt>map</tt> should
     *              be checked for illegal values.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalStateException
     *              if <tt>validate</tt> is <tt>true</tt> and
     *              <tt>map</tt> contains a <tt>null</tt> key/value,
     *              a key that is not of class
     *              {@link Integer Integer},
     *              or a value that is not of class
     *              {@link Double Double}.
     */
    public MapToIntKeyDoubleMapAdapter(Map map, boolean validate) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
        if (validate)
            evalidate();
    }

    public void clear()
    { map.clear(); }

    public boolean containsKey(int key) {
        lastValue = (Double)map.get(new Integer(key));
        return lastValue != null;
    }

    public boolean containsValue(double value)
    { return map.containsValue(new Double(value)); }

    public IntKeyDoubleMapIterator entries() {
        return new IntKeyDoubleMapIterator() {
            Iterator i = map.entrySet().iterator();
            Map.Entry lastEntry = null;

            public boolean hasNext()
            { return i.hasNext(); }

            public void next()
            { lastEntry = (Map.Entry)i.next(); }

            public int getKey() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return ((Integer)lastEntry.getKey()).intValue();
            }

            public double getValue() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return ((Double)lastEntry.getValue()).doubleValue();
            }

            public void remove() {
                i.remove();
                lastEntry = null;
            }
        };
    }

    public double get(int key) {
        Double value = (Double)map.get(new Integer(key));
        return value == null ? MapDefaults.defaultDouble() : value.doubleValue();
    }

    public IntSet keySet()
    { return new SetToIntSetAdapter(map.keySet()); }

    public double lget() {
        if (lastValue == null)
            Exceptions.noLastElement();
        return lastValue.doubleValue();
    }

    public double put(int key, double value) {
        Double oldValue = (Double)map.put(new Integer(key), new Double(value));
        return oldValue == null ? MapDefaults.defaultDouble() : oldValue.doubleValue();
    }

    public double remove(int key) {
        Double value = (Double)map.remove(new Integer(key));
        return value == null ? MapDefaults.defaultDouble() : value.doubleValue();
    }

    public int size()
    { return map.size(); }

    public DoubleCollection values()
    { return new CollectionToDoubleCollectionAdapter(map.values()); }

    public double tget(int key) {
        Double value = (Double)map.get(new Integer(key));
        if (value == null)
            Exceptions.noSuchMapping(String.valueOf(key));
        return value.doubleValue();
    }

    /**
     *  Indicates whether the underlying map is valid for
     *  this adapter. For the underlying map to be valid, it
     *  can only contain {@link Integer Integer} keys, no <tt>null</tt>
     *  keys/values, and only {@link Double Double} values.
     *
     *  @return     <tt>true</tt> if the underlying map is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isIntKeyDoubleAdaptable(map); }

    /**
     *  Validates the map underlying this adapter and throws
     *  an exception if it is invalid. For the underlying map 
     *  to be valid, it
     *  can only contain {@link Integer Integer} keys, no <tt>null</tt>
     *  keys/values, and only {@link Double Double} values.
     *
     *  @throws     IllegalStateException
     *              if the underlying map is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("map");
    }

}
