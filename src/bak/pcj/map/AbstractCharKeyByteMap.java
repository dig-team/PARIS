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

import bak.pcj.hash.DefaultCharHashFunction;
import bak.pcj.hash.DefaultByteHashFunction;
import bak.pcj.util.Exceptions;

/**
 *  This class represents an abstract base for implementing
 *  maps from char values to byte values. All operations that can be implemented
 *  using iterators
 *  are implemented as such. In most cases, this is
 *  hardly an efficient solution, and at least some of those
 *  methods should be overridden by sub-classes.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.4     21-08-2003 19:34
 *  @since      1.0
 */
public abstract class AbstractCharKeyByteMap implements CharKeyByteMap {

    /** Default constructor to be invoked by sub-classes. */
    protected AbstractCharKeyByteMap() { }

    public void clear() {
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
    }

    public byte remove(char key) {
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getKey() == key) {
                byte value = i.getValue();
                i.remove();
                return value;
            }
        }
        return MapDefaults.defaultByte();
    }

    public void putAll(CharKeyByteMap map) {
        CharKeyByteMapIterator i = map.entries();
        while (i.hasNext()) {
            i.next();
            put(i.getKey(), i.getValue());
        }
    }

    public boolean containsKey(char key) {
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getKey() == key)
                return true;
        }
        return false;
    }

    public byte get(char key) {
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getKey() == key)
                return i.getValue();
        }
        return MapDefaults.defaultByte();
    }

    public boolean containsValue(byte value) {
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getValue() == value)
                return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CharKeyByteMap))
            return false;
        CharKeyByteMap map = (CharKeyByteMap)obj;
        if (size() != map.size())
            return false;
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            char k = i.getKey();
            if (!map.containsKey(k) || map.lget() != i.getValue())
                return false;
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            h += (DefaultCharHashFunction.INSTANCE.hash(i.getKey()) ^ DefaultByteHashFunction.INSTANCE.hash(i.getValue()));
        }
        return h;
    }

    public boolean isEmpty()
    { return size() == 0; }

    public int size() {
        int size = 0;
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            size++;
        }
        return size;
    }

    public byte tget(char key) {
        byte value = get(key);
        if (value == MapDefaults.defaultByte())
            if (!containsKey(key))
                Exceptions.noSuchMapping(String.valueOf(key));
        return value;
    }

    /**
     *  Returns a string representation of this map.
     *
     *  @return     a string representation of this map.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('[');
        CharKeyByteMapIterator i = entries();
        while (i.hasNext()) {
            if (s.length() > 1)
                s.append(',');
            i.next();
            s.append(String.valueOf(i.getKey()));
            s.append("->");
            s.append(String.valueOf(i.getValue()));
        }
        s.append(']');
        return s.toString();
    }

    /**
     *  Does nothing. Sub-classes may provide an implementation to
     *  minimize memory usage, but this is not required since many
     *  implementations will always have minimal memory usage.
     */
    public void trimToSize()
    { }

}
