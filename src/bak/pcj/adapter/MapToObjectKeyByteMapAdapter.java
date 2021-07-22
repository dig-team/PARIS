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
import bak.pcj.ByteCollection;
import bak.pcj.map.ObjectKeyByteMap;
import bak.pcj.map.AbstractObjectKeyByteMap;
import bak.pcj.map.ObjectKeyByteMapIterator;
import bak.pcj.map.MapDefaults;
import bak.pcj.map.NoSuchMappingException;
import bak.pcj.set.ByteSet;
import bak.pcj.util.Exceptions;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 *  This class represents adaptions of Java Collections Framework
 *  maps to primitive maps from object values to byte values.
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
 *      MapToObjectKeyByteMapAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      MapToObjectKeyByteMapAdapter s;
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
public class MapToObjectKeyByteMapAdapter extends AbstractObjectKeyByteMap implements ObjectKeyByteMap {

    /** The underlying map. */
    protected Map map;

    /** The value corresponding to the last key found by containsKey(). */
    protected Byte lastValue;

    /**
     *  Creates a new adaption to a map from object
     *  values to byte values.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of
     *              values of class
     *              {@link Byte Byte}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public MapToObjectKeyByteMapAdapter(Map map) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        lastValue = null;
    }

    /**
     *  Creates a new adaption to a map from object
     *  values to byte values. The map to adapt is optionally validated.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of 
     *              values of class
     *              {@link Byte Byte}. Otherwise a
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
     *              {@link Byte Byte}.
     */
    public MapToObjectKeyByteMapAdapter(Map map, boolean validate) {
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
        lastValue = (Byte)map.get(key);
        return lastValue != null;
    }

    public boolean containsValue(byte value)
    { return map.containsValue(new Byte(value)); }

    public ObjectKeyByteMapIterator entries() {
        return new ObjectKeyByteMapIterator() {
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

            public byte getValue() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return ((Byte)lastEntry.getValue()).byteValue();
            }

            public void remove() {
                i.remove();
                lastEntry = null;
            }
        };
    }

    public byte get(Object key) {
        Byte value = (Byte)map.get(key);
        return value == null ? MapDefaults.defaultByte() : value.byteValue();
    }

    public Set keySet()
    { return map.keySet(); }

    public byte lget() {
        if (lastValue == null)
            Exceptions.noLastElement();
        return lastValue.byteValue();
    }

    public byte put(Object key, byte value) {
        Byte oldValue = (Byte)map.put(key, new Byte(value));
        return oldValue == null ? MapDefaults.defaultByte() : oldValue.byteValue();
    }

    public byte remove(Object key) {
        Byte value = (Byte)map.remove(key);
        return value == null ? MapDefaults.defaultByte() : value.byteValue();
    }

    public int size()
    { return map.size(); }

    public ByteCollection values()
    { return new CollectionToByteCollectionAdapter(map.values()); }

    public byte tget(Object key) {
        Byte value = (Byte)map.get(key);
        if (value == null)
            Exceptions.noSuchMapping(key);
        return value.byteValue();
    }

    /**
     *  Indicates whether the underlying map is valid for
     *  this adapter. For the underlying map to be valid it
     *  can contain no <tt>null</tt>
     *  values and only {@link Byte Byte} values.
     *
     *  @return     <tt>true</tt> if the underlying map is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isObjectKeyByteAdaptable(map); }

    /**
     *  Validates the map underlying this adapter and throws
     *  an exception if it is invalid. For the underlying map to be valid it
     *  can contain no <tt>null</tt>
     *  values and only {@link Byte Byte} values.
     *
     *  @throws     IllegalStateException
     *              if the underlying map is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("map");
    }

}
