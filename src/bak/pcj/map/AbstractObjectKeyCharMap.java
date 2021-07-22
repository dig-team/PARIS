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
import bak.pcj.util.Exceptions;

/**
 *  This class represents an abstract base for implementing
 *  maps from object values to char values. All operations that can be implemented
 *  using iterators
 *  are implemented as such. In most cases, this is
 *  hardly an efficient solution, and at least some of those
 *  methods should be overridden by sub-classes.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     21-08-2003 19:33
 *  @since      1.1
 */
public abstract class AbstractObjectKeyCharMap implements ObjectKeyCharMap {

    /** Default constructor to be invoked by sub-classes. */
    protected AbstractObjectKeyCharMap() { }

    public void clear() {
        ObjectKeyCharMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
    }

    public char remove(Object key) {
        ObjectKeyCharMapIterator i = entries();
        if (key == null)
            while (i.hasNext()) {
                if (i.getKey() == null) {
                    char value = i.getValue();
                    i.remove();
                    return value;
                }
            }
        else
            while (i.hasNext()) {
                i.next();
                if (key.equals(i.getKey())) {
                    char value = i.getValue();
                    i.remove();
                    return value;
                }
            }
        return MapDefaults.defaultChar();
    }

    public void putAll(ObjectKeyCharMap map) {
        ObjectKeyCharMapIterator i = map.entries();
        while (i.hasNext()) {
            i.next();
            put(i.getKey(), i.getValue());
        }
    }

    public boolean containsKey(Object key) {
        ObjectKeyCharMapIterator i = entries();
        if (key == null)
            while (i.hasNext()) {
                i.next();
                if (i.getKey() == null)
                    return true;
            }
        else
            while (i.hasNext()) {
                i.next();
                if (key.equals(i.getKey()))
                    return true;
            }
        return false;
    }

    public char get(Object key) {
        ObjectKeyCharMapIterator i = entries();
        if (key == null)
            while (i.hasNext()) {
                i.next();
                if (i.getKey() == null)
                    return i.getValue();
            }
        else
            while (i.hasNext()) {
                i.next();
                if (key.equals(i.getKey()))
                    return i.getValue();
            }
        return MapDefaults.defaultChar();
    }

    public boolean containsValue(char value) {
        ObjectKeyCharMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            if (i.getValue() == value)
                return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectKeyCharMap))
            return false;
        ObjectKeyCharMap map = (ObjectKeyCharMap)obj;
        if (size() != map.size())
            return false;
        ObjectKeyCharMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            Object k = i.getKey();
            if (!map.containsKey(k) || map.lget() != i.getValue())
                return false;
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        ObjectKeyCharMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            Object k = i.getKey();
            h += ((k == null ? 0 : k.hashCode()) ^ DefaultCharHashFunction.INSTANCE.hash(i.getValue()));
        }
        return h;
    }

    public boolean isEmpty()
    { return size() == 0; }

    public int size() {
        int size = 0;
        ObjectKeyCharMapIterator i = entries();
        while (i.hasNext()) {
            i.next();
            size++;
        }
        return size;
    }

    public char tget(Object key) {
        char value = get(key);
        if (value == MapDefaults.defaultChar())
            if (!containsKey(key))
                Exceptions.noSuchMapping(key);
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
        ObjectKeyCharMapIterator i = entries();
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
