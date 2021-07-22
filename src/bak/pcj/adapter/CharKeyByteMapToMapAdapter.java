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

import bak.pcj.ByteCollection;
import bak.pcj.map.CharKeyByteMap;
import bak.pcj.map.CharKeyByteMapIterator;
import bak.pcj.map.MapDefaults;
import bak.pcj.hash.DefaultCharHashFunction;
import bak.pcj.hash.DefaultByteHashFunction;
import bak.pcj.util.Exceptions;

import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Set;

/**
 *  This class represents adapters of primitive maps from
 *  char values to byte values to Java Collections
 *  Framework maps. The adapter is implemented as a wrapper 
 *  around a primitive map. Thus, 
 *  changes to the underlying map are reflected by this
 *  map and vice versa.
 *
 *  @see        CharKeyByteMap
 *  @see        java.util.Map
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.4     20-08-2003 23:03
 *  @since      1.0
 */
public class CharKeyByteMapToMapAdapter implements Map {

    /** The underlying primitive map. */
    protected CharKeyByteMap map;

    /**
     *  Creates a new adaption of a primitive map of char
     *  keys and byte values to a Java Collections Framework map.
     *
     *  @param      map
     *              the underlying primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public CharKeyByteMapToMapAdapter(CharKeyByteMap map) throws NullPointerException /* Exception marked to work around bug in Javadoc 1.3 */ {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
    }

    /**
     *  Clears this map. The underlying map is cleared.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the
     *              underlying map.
     */
    public void clear()
    { map.clear(); }

    /**
     *  Indicates whether this map contains a mapping from a specified
     *  key. This is so, only if the underlying collection contains
     *  the unwrapped key.
     *
     *  @param      key
     *              the key to test for.
     *
     *  @return     <tt>true</tt> if this map contains a mapping from
     *              the specified key; returns <tt>false</tt>
     *              otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>key</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>key</tt> is not of class {@link Character Character}.
     */
    public boolean containsKey(Object key) throws NullPointerException, ClassCastException /* Exceptions marked to work around bug in Javadoc 1.3 */
    { return map.containsKey(((Character)key).charValue()); }

    /**
     *  Indicates whether this map contains a mapping to a specified
     *  value. For this map to contain an object, the
     *  underlying map must contain its unwrapped value.
     *  <p>Note that this map can never contain <tt>null</tt>
     *  values or values of other classes than {@link Byte Byte}.
     *  In those cases, this method will return <tt>false</tt>.
     *
     *  @param      value
     *              the value to test for.
     *
     *  @return     <tt>true</tt> if this map contains at least one
     *              mapping to the specified value; returns
     *              <tt>false</tt> otherwise.
     */
    public boolean containsValue(Object value) {
        if (value == null)
            return false;
        return map.containsValue(((Byte)value).byteValue());
    }

    /**
     *  Returns a set view of the entries of this map. The returned
     *  set is a view, so changes to this map are reflected by the
     *  returned set and vice versa. All elements of the returned
     *  set implements {@link java.util.Map.Entry java.util.Map.Entry}.
     *
     *  @return     a set view of the entries of this map.
     */
    public Set entrySet() {
        return new EntrySet();
    }

    /**
     *  Indicates whether this map is equal to some object.
     *
     *  @param      obj
     *              the object with which to compare this map.
     *
     *  @return     <tt>true</tt> if this map is equal to the
     *              specified object; returns <tt>false</tt>
     *              otherwise.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Map))
            return false;
        Map m = (Map)obj;
        if (m.size() != map.size())
            return false;
        Iterator i = m.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            if (e.getKey() == null)
                return false;
            if (e.getValue() == null)
                return false;
            if ( !get(e.getKey()).equals(e.getValue()) )
                return false;
        }
        return true;
    }

    /**
     *  Maps a specified key to a value. Returns <tt>null</tt>
     *  if no mapping exists for the specified key.
     *  The returned value will always be of class {@link Byte Byte}.
     *
     *  @param      key
     *              the key to map to a value.
     *
     *  @return     the value that the specified key maps to, or
     *              <tt>null</tt>, if no such mapping exists.
     *
     *  @throws     NullPointerException
     *              if <tt>key</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>key</tt> is not of class {@link Character Character}.
     */
    public Object get(Object key) throws NullPointerException, ClassCastException /* Exceptions marked to work around bug in Javadoc 1.3 */ {
        char k = ((Character)key).charValue();
        byte v = map.get(k);
        if (v == MapDefaults.defaultByte())
            if (!map.containsKey(k))
                return null;
        return new Byte(v);
    }

    /**
     *  Returns a hash code value for this map. The hash code
     *  returned is that of the underlying map.
     *
     *  @return     a hash code value for this map.
     */
    public int hashCode()
    { return map.hashCode(); }

    /**
     *  Indicates whether this map is empty.
     *
     *  @return     <tt>true</tt> if this map is empty; returns
     *              <tt>false</tt> otherwise.
     */
    public boolean isEmpty()
    { return map.isEmpty(); }

    /**
     *  Returns a set view of the keys of this map. Removals from the
     *  returned set removes the corresponding entries in this map.
     *  Changes to the map are reflected in the set. All elements
     *  if the returned set is of class {@link Character Character}.
     *
     *  @return     a set view of the keys of this map.
     */
    public Set keySet() {
        return new CharSetToSetAdapter(map.keySet());
    }

    /**
     *  Adds a mapping from a specified key to a specified value to
     *  this map. If a mapping already exists for the specified key
     *  it is overwritten by the new mapping. The mapping is
     *  added to the underlying map.
     *
     *  @param      key
     *              the key of the mapping to add to this map.
     *
     *  @param      value
     *              the value of the mapping to add to this map.
     *
     *  @return     the old value if a
     *              mapping from the specified key already existed
     *              in this map; returns <tt>null</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this map.
     *
     *  @throws     NullPointerException
     *              if <tt>key</tt> is <tt>null</tt>;
     *              if <tt>value</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>key</tt> is not of class {@link Character Character};
     *              if <tt>value</tt> is not of class {@link Byte Byte}.
     */
    public Object put(Object key, Object value) throws NullPointerException, ClassCastException /* Exception marked to work around bug in Javadoc 1.3 */ {
        Object result = get(key);
        char k = ((Character)key).charValue();
        map.put(k, ((Byte)value).byteValue());
        return result;
    }

    /**
     *  Adds all mappings from a specified map to this map. Any
     *  existing mappings whose keys collide with a new mapping is
     *  overwritten by the new mapping. The mappings are
     *  added to the underlying map.
     *
     *  @param      map
     *              the map whose mappings to add to this map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this map.
     *
     *  @throws     NullPointerException
     *              if a key in <tt>map</tt> is <tt>null</tt>;
     *              if a value in <tt>map</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if a key in <tt>map</tt> is not of class {@link Character Character};
     *              if a value in <tt>value</tt> is not of class {@link Byte Byte}.
     */
    public void putAll(Map map) throws NullPointerException, ClassCastException /* Exceptions marked to work around bug in Javadoc 1.3 */ {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     *  Removes the mapping from a specified key from this map.
     *  The mapping is removed from the underlying map.
     *
     *  @param      key
     *              the key whose mapping to remove from this map.
     *
     *  @return     the old value if a
     *              mapping from the specified key already existed
     *              in this map; returns <tt>null</tt> otherwise.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by the 
     *              underlying map.
     */
    public Object remove(Object key) {
        if (key == null)
            return null;
        if (!(key instanceof Character))
            return null;
        Object result = get(key);
        char k = ((Character)key).charValue();
        map.remove(k);
        return result;
    }

    /**
     *  Returns the size of this map. The size is defined as the
     *  number of mappings from keys to values. The size is that
     *  of the underlying map.
     *
     *  @return     the size of this map.
     */
    public int size()
    { return map.size(); }

    /**
     *  Returns a collection view of the values in this map. The
     *  collection is not modifiable, but changes to the map are
     *  reflected in the collection. All elements
     *  in the returned set is of class {@link Byte Byte}.
     *
     *  @return     a collection view of the values in this map.
     */
    public Collection values() {
        return new ByteCollectionToCollectionAdapter(map.values());
    }

    class EntrySet extends AbstractSet {

        public Iterator iterator() {
            return new Iterator() {
                CharKeyByteMapIterator i = map.entries();

                public boolean hasNext()
                { return i.hasNext(); }

                public Object next() {
                    i.next();
                    return new Entry(i.getKey(), i.getValue());
                }

                public void remove()
                { i.remove(); }
            };
        }

        public boolean add(Object obj) {
            Map.Entry e = (Map.Entry)obj;
            if (contains(e))
                return false;
            put(e.getKey(), e.getValue());
            return true;
        }

        public int size()
        { return map.size(); }
    }

    class Entry implements Map.Entry {
        Character key;
        Byte value;

        Entry(char key, byte value) {
            this.key = new Character(key);
            this.value = new Byte(value);
        }

        public Object getKey()
        { return key; }

        public Object getValue()
        { return value; }

        public Object setValue(Object value)
        { return put(key, value); }

        public int hashCode()
        { return DefaultCharHashFunction.INSTANCE.hash(key.charValue()) ^ DefaultByteHashFunction.INSTANCE.hash(value.byteValue()); }

        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)obj;
            return key.equals(e.getKey()) && value.equals(e.getValue());
        }
    }


}
