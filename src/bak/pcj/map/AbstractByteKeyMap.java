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

import bak.pcj.hash.DefaultByteHashFunction;

/**
 *  This class represents an abstract base for implementing
 *  maps from byte values to objects. All operations that can be implemented
 *  using iterators
 *  are implemented as such. In most cases, this is
 *  hardly an efficient solution, and at least some of those
 *  methods should be overridden by sub-classes.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/10/1
 *  @since      1.0
 */
public abstract class AbstractByteKeyMap implements ByteKeyMap {

    /** Default constructor to be invoked by sub-classes. */
    protected AbstractByteKeyMap() { }

    public void clear() {
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
    }

    public Object remove(byte key) {
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getKey() == key) {
                Object value = i.getValue();
                i.remove();
                return value;
            }
        }
        return null;
    }

    public void putAll(ByteKeyMap map) {
        ByteKeyMapIterator i = map.entries();
        while (i.hasNext()) {
            i.next();
            put(i.getKey(), i.getValue());
        }
    }

    public boolean containsKey(byte key) {
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getKey() == key)
                return true;
        }
        return false;
    }

    public Object get(byte key) {
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getKey() == key)
                return i.getValue();
        }
        return null;
    }

    public boolean containsValue(Object value) {
        ByteKeyMapIterator i = entries();
        if (value == null) {
            while (i.hasNext()) {
                i.next();
                if (value == null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                i.next();
                if (value.equals(i.getValue()))
                    return true;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ByteKeyMap))
            return false;
        ByteKeyMap map = (ByteKeyMap)obj;
        if (size() != map.size())
            return false;
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            byte k = i.getKey();
            Object v = i.getValue();
            if (v == null) {
                if (map.get(k) != null)
                    return false;
                if (!map.containsKey(k))
                    return false;
            } else {
                if (!v.equals(map.get(k)))
                    return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            h += (DefaultByteHashFunction.INSTANCE.hash(i.getKey()) ^ i.getValue().hashCode());
        }
        return h;
    }

    public boolean isEmpty()
    { return size() == 0; }

    public int size() {
        int size = 0;
        ByteKeyMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            size++;
        }
        return size;
    }

    /**
     *  Returns a string representation of this map.
     *
     *  @return     a string representation of this map.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append('[');
        ByteKeyMapIterator i = entries();
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
