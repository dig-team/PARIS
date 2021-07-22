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
import bak.pcj.LongIterator;
import bak.pcj.LongCollection;
import bak.pcj.map.ObjectKeyLongMap;
import bak.pcj.map.AbstractObjectKeyLongMap;
import bak.pcj.map.ObjectKeyLongMapIterator;
import bak.pcj.map.MapDefaults;
import bak.pcj.map.NoSuchMappingException;
import bak.pcj.set.LongSet;
import bak.pcj.util.Exceptions;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 *  This class represents adaptions of Java Collections Framework
 *  maps to primitive maps from object values to long values.
 *  The adapter is implemented as a wrapper around the map. 
 *  Thus, changes to the underlying map are reflected by this
 *  map and vice versa.
 *
 *  <p>
 *  Adapters from JCF maps to primitive map will
 *  fail if the JCF collection contains <tt>null</tt> values or
 *  values of the wrong class. However, adapters are not fast
 *  failing in the case that the underlying map should
 *  contain illegal keys or values. To implement fast failure would require
 *  every operation to check every key and value of the underlying
 *  map before doing anything. Instead validation methods
 *  are provided. They can be called using the assertion facility
 *  in the client code:
 *  <pre>
 *      MapToObjectKeyLongMapAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      MapToObjectKeyLongMapAdapter s;
 *      ...
 *      s.evalidate();  // Throws an exception on illegal values
 *  </pre>
 *  Either way, validation must be invoked directly by the client
 *  code.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     21-08-2003 19:12
 *  @since      1.1
 */
public class MapToObjectKeyLongMapAdapter extends AbstractObjectKeyLongMap implements ObjectKeyLongMap {

    /** The underlying map. */
    protected Map map;

    /** The value corresponding to the last key found by containsKey(). */
    protected Long lastValue;

    /**
     *  Creates a new adaption to a map from object
     *  values to long values.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of
     *              values of class
     *              {@link Long Long}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public MapToObjectKeyLongMapAdapter(Map map) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
    }

    /**
     *  Creates a new adaption to a map from object
     *  values to long values. The map to adapt is optionally validated.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of 
     *              values of class
     *              {@link Long Long}. Otherwise a
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
     *              <tt>map</tt> contains a <tt>null</tt> value,
     *              or a value that is not of class
     *              {@link Long Long}.
     */
    public MapToObjectKeyLongMapAdapter(Map map, boolean validate) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
        if (validate)
            evalidate();
    }

    public void clear()
    { map.clear(); }

    public boolean containsKey(Object key) {
        lastValue = (Long)map.get(key);
        return lastValue != null;
    }

    public boolean containsValue(long value)
    { return map.containsValue(new Long(value)); }

    public ObjectKeyLongMapIterator entries() {
        return new ObjectKeyLongMapIterator() {
            Iterator i = map.entrySet().iterator();
            Map.Entry lastEntry = null;

            public boolean hasNext()
            { return i.hasNext(); }

            public void next()
            { lastEntry = (Map.Entry)i.next(); }

            public Object getKey() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return lastEntry.getKey();
            }

            public long getValue() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return ((Long)lastEntry.getValue()).longValue();
            }

            public void remove() {
                i.remove();
                lastEntry = null;
            }
        };
    }

    public long get(Object key) {
        Long value = (Long)map.get(key);
        return value == null ? MapDefaults.defaultLong() : value.longValue();
    }

    public Set keySet()
    { return map.keySet(); }

    public long lget() {
        if (lastValue == null)
            Exceptions.noLastElement();
        return lastValue.longValue();
    }

    public long put(Object key, long value) {
        Long oldValue = (Long)map.put(key, new Long(value));
        return oldValue == null ? MapDefaults.defaultLong() : oldValue.longValue();
    }

    public long remove(Object key) {
        Long value = (Long)map.remove(key);
        return value == null ? MapDefaults.defaultLong() : value.longValue();
    }

    public int size()
    { return map.size(); }

    public LongCollection values()
    { return new CollectionToLongCollectionAdapter(map.values()); }

    public long tget(Object key) {
        Long value = (Long)map.get(key);
        if (value == null)
            Exceptions.noSuchMapping(key);
        return value.longValue();
    }

    /**
     *  Indicates whether the underlying map is valid for
     *  this adapter. For the underlying map to be valid it
     *  can contain no <tt>null</tt>
     *  values and only {@link Long Long} values.
     *
     *  @return     <tt>true</tt> if the underlying map is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isObjectKeyLongAdaptable(map); }

    /**
     *  Validates the map underlying this adapter and throws
     *  an exception if it is invalid. For the underlying map to be valid it
     *  can contain no <tt>null</tt>
     *  values and only {@link Long Long} values.
     *
     *  @throws     IllegalStateException
     *              if the underlying map is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("map");
    }

}
