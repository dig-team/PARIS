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
package bak.pcj.map;

import bak.pcj.ByteCollection;
import bak.pcj.set.IntSet;

/**
 *  This interface represents maps from int values to byte values.
 *  It is not possible to obtain a set of entries from primitive
 *  collections maps. Instead, an iterator over entries can be
 *  obtained. This removes a number of implementation constraints
 *  imposed by having to implement an entry interface.
 *
 *  @see        bak.pcj.map.IntKeyByteMapIterator
 *  @see        java.util.Map
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     2003/6/1
 *  @since      1.0
 */
public interface IntKeyByteMap {

    /**
     *  Clears this map.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this map.
     */
    void clear();

    /**
     *  Indicates whether this map contains a mapping from a specified
     *  key. If the key is contained in this map, a succeeding call
     *  to {@link #lget() lget()} will return the corresponding value.
     *
     *  @param      key
     *              the key to test for.
     *
     *  @return     <tt>true</tt> if this map contains a mapping from
     *              the specified key; returns <tt>false</tt>
     *              otherwise.
     *
     *  @see        #lget()
     */
    boolean containsKey(int key);

    /**
     *  Indicates whether this map contains a mapping to a specified
     *  value.
     *
     *  @param      value
     *              the value to test for.
     *
     *  @return     <tt>true</tt> if this map contains at least one
     *              mapping to the specified value; returns
     *              <tt>false</tt> otherwise.
     */
    boolean containsValue(byte value);

    /**
     *  Returns an iterator over the entries of this map. It is
     *  possible to remove entries from this map using the iterator
     *  provided that the concrete map supports removal of
     *  entries.
     *
     *  @return     an iterator over the entries of this map.
     */
    IntKeyByteMapIterator entries();

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
    boolean equals(Object obj);

    /**
     *  Maps a specified key to a value. Returns a default value as
     *  specified by the <tt>MapDefaults</tt> class if no mapping
     *  exists for the specified key.
     *
     *  @param      key
     *              the key to map to a value.
     *
     *  @return     the value that the specified key maps to, or
     *              a default value, if no such mapping exists.
     *
     *  @see        MapDefaults
     *  @see        #tget(int)
     *  @see        #lget()
     */
    byte get(int key);

    /**
     *  Returns a hash code value for this map.
     *
     *  @return     a hash code value for this map.
     */
    int hashCode();

    /**
     *  Indicates whether this map is empty.
     *
     *  @return     <tt>true</tt> if this map is empty; returns
     *              <tt>false</tt> otherwise.
     */
    boolean isEmpty();

    /**
     *  Returns a set view of the keys of this map. Removals from the
     *  returned set removes the corresponding entries in this map.
     *  Changes to the map are reflected in the set.
     *
     *  @return     a set view of the keys of this map.
     */
    IntSet keySet();

    /**
     *  Returns the last value corresponding to a positive result
     *  from {@link #containsKey(int) containsKey(int)}. This is useful
     *  for checking checking the existence of a mapping while
     *  avoiding two lookups on the same key.
     *
     *  @return     the value corresponding to the key from the
     *              last invokation of 
     *              {@link #containsKey(int) containsKey(int)}.
     *
     *  @throws     IllegalStateException
     *              if {@link #containsKey(int) containsKey(int)} has
     *              not been called or the last call resulted in
     *              a return value of <tt>false</tt>.
     *
     *  @see        #get(int)
     *  @see        #tget(int)
     *  @see        #containsKey(int)
     */
    byte lget();

    /**
     *  Adds a mapping from a specified key to a specified value to
     *  this map. If a mapping already exists for the specified key
     *  it is overwritten by the new mapping.
     *
     *  @param      key
     *              the key of the mapping to add to this map.
     *
     *  @param      value
     *              the value of the mapping to add to this map.
     *
     *  @return     the old value if a
     *              mapping from the specified key already existed
     *              in this map; otherwise returns a default value as
     *              specified by the <tt>MapDefaults</tt> class.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this map.
     *
     *  @see        MapDefaults
     */
    byte put(int key, byte value);

    /**
     *  Adds all mappings from a specified map to this map. Any
     *  existing mappings whose keys collide with a new mapping is
     *  overwritten by the new mapping.
     *
     *  @param      map
     *              the map whose mappings to add to this map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this map.
     */
    void putAll(IntKeyByteMap map);

    /**
     *  Removes the mapping from a specified key from this map.
     *
     *  @param      key
     *              the key whose mapping to remove from this map.
     *
     *  @return     the old value if a
     *              mapping from the specified key already existed
     *              in this map; otherwise returns a default value as
     *              specified by the <tt>MapDefaults</tt> class.
     *
     *  @throws     UnsupportedOperationException
     *              if the operation is not supported by this map.
     *
     *  @see        MapDefaults
     */
    byte remove(int key);

    /**
     *  Returns the size of this map. The size is defined as the
     *  number of mappings from keys to values.
     *
     *  @return     the size of this map.
     */
    int size();

    /**
     *  Maps a specified key to a value. This method should be used
     *  when the key is known to be in the map.
     *
     *  @param      key
     *              the key to map to a value.
     *
     *  @return     the value that the specified key maps to.
     *
     *  @throws     NoSuchMappingException
     *              if the specified key does not map to any value.
     *
     *  @see        #get(int)
     *  @see        #lget()
     */
    byte tget(int key);

    /**
     *  Minimizes the memory used by this map. The exact
     *  operation of this method depends on the class implementing it.
     *  Implementors may choose to ignore it completely.
     */
    void trimToSize();

    /**
     *  Returns a collection view of the values in this map. The
     *  collection is not modifiable, but changes to the map are
     *  reflected in the collection.
     *
     *  @return     a collection view of the values in this map.
     */
    ByteCollection values();

}
