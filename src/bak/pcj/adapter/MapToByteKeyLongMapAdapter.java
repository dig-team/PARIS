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
import bak.pcj.ByteIterator;
import bak.pcj.LongCollection;
import bak.pcj.map.ByteKeyLongMap;
import bak.pcj.map.AbstractByteKeyLongMap;
import bak.pcj.map.ByteKeyLongMapIterator;
import bak.pcj.map.MapDefaults;
import bak.pcj.map.NoSuchMappingException;
import bak.pcj.set.ByteSet;
import bak.pcj.util.Exceptions;

import java.util.Map;
import java.util.Iterator;

/**
 *  This class represents adaptions of Java Collections Framework
 *  maps to primitive maps from byte values to long values.
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
 *      MapToByteKeyLongMapAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      MapToByteKeyLongMapAdapter s;
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
public class MapToByteKeyLongMapAdapter extends AbstractByteKeyLongMap implements ByteKeyLongMap {

    /** The underlying map. */
    protected Map map;

    /** The value corresponding to the last key found by containsKey(). */
    protected Long lastValue;

    /**
     *  Creates a new adaption to a map from byte
     *  values to long values.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of keys of class
     *              {@link Byte Byte}.
     *              values of class
     *              {@link Long Long}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public MapToByteKeyLongMapAdapter(Map map) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
    }

    /**
     *  Creates a new adaption to a map from byte
     *  values to long values. The map to adapt is optionally validated.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of keys of class
     *              {@link Byte Byte}.
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
     *              <tt>map</tt> contains a <tt>null</tt> key/value,
     *              a key that is not of class
     *              {@link Byte Byte},
     *              or a value that is not of class
     *              {@link Long Long}.
     */
    public MapToByteKeyLongMapAdapter(Map map, boolean validate) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
        if (validate)
            evalidate();
    }

    public void clear()
    { map.clear(); }

    public boolean containsKey(byte key) {
        lastValue = (Long)map.get(new Byte(key));
        return lastValue != null;
    }

    public boolean containsValue(long value)
    { return map.containsValue(new Long(value)); }

    public ByteKeyLongMapIterator entries() {
        return new ByteKeyLongMapIterator() {
            Iterator i = map.entrySet().iterator();
            Map.Entry lastEntry = null;

            public boolean hasNext()
            { return i.hasNext(); }

            public void next()
            { lastEntry = (Map.Entry)i.next(); }

            public byte getKey() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return ((Byte)lastEntry.getKey()).byteValue();
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

    public long get(byte key) {
        Long value = (Long)map.get(new Byte(key));
        return value == null ? MapDefaults.defaultLong() : value.longValue();
    }

    public ByteSet keySet()
    { return new SetToByteSetAdapter(map.keySet()); }

    public long lget() {
        if (lastValue == null)
            Exceptions.noLastElement();
        return lastValue.longValue();
    }

    public long put(byte key, long value) {
        Long oldValue = (Long)map.put(new Byte(key), new Long(value));
        return oldValue == null ? MapDefaults.defaultLong() : oldValue.longValue();
    }

    public long remove(byte key) {
        Long value = (Long)map.remove(new Byte(key));
        return value == null ? MapDefaults.defaultLong() : value.longValue();
    }

    public int size()
    { return map.size(); }

    public LongCollection values()
    { return new CollectionToLongCollectionAdapter(map.values()); }

    public long tget(byte key) {
        Long value = (Long)map.get(new Byte(key));
        if (value == null)
            Exceptions.noSuchMapping(String.valueOf(key));
        return value.longValue();
    }

    /**
     *  Indicates whether the underlying map is valid for
     *  this adapter. For the underlying map to be valid, it
     *  can only contain {@link Byte Byte} keys, no <tt>null</tt>
     *  keys/values, and only {@link Long Long} values.
     *
     *  @return     <tt>true</tt> if the underlying map is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isByteKeyLongAdaptable(map); }

    /**
     *  Validates the map underlying this adapter and throws
     *  an exception if it is invalid. For the underlying map 
     *  to be valid, it
     *  can only contain {@link Byte Byte} keys, no <tt>null</tt>
     *  keys/values, and only {@link Long Long} values.
     *
     *  @throws     IllegalStateException
     *              if the underlying map is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("map");
    }

}
