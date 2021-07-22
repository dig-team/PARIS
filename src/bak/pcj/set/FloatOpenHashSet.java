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
package bak.pcj.set;

import bak.pcj.FloatCollection;
import bak.pcj.FloatIterator;
import bak.pcj.hash.FloatHashFunction;
import bak.pcj.hash.DefaultFloatHashFunction;
import bak.pcj.util.Exceptions;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *  This class represents open addressing hash table based sets of float values.
 *  Unlike the Java Collections <tt>HashSet</tt> instances of this class
 *  are not backed up by a map. It is implemented using a simple open addressing
 *  hash table where the keys are stored directly as entries.
 *
 *  @see        FloatOpenHashSet
 *  @see        java.util.HashSet
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     22-08-2003 20:19
 *  @since      1.0
 */
public class FloatOpenHashSet extends AbstractFloatSet implements FloatSet, Cloneable, Serializable {

    /** Constant indicating relative growth policy. */
    private static final int    GROWTH_POLICY_RELATIVE      = 0;

    /** Constant indicating absolute growth policy. */
    private static final int    GROWTH_POLICY_ABSOLUTE      = 1;

    /**
     *  The default growth policy of this set.
     *  @see    #GROWTH_POLICY_RELATIVE
     *  @see    #GROWTH_POLICY_ABSOLUTE
     */
    private static final int    DEFAULT_GROWTH_POLICY       = GROWTH_POLICY_RELATIVE;

    /** The default factor with which to increase the capacity of this set. */
    public static final double DEFAULT_GROWTH_FACTOR        = 1.0;

    /** The default chunk size with which to increase the capacity of this set. */
    public static final int    DEFAULT_GROWTH_CHUNK         = 10;

    /** The default capacity of this set. */
    public static final int    DEFAULT_CAPACITY             = 11;

    /** The default load factor of this set. */
    public static final double DEFAULT_LOAD_FACTOR          = 0.75;

    /** 
     *  The hash function used to hash keys in this set.
     *  @serial
     */
    private FloatHashFunction keyhash;

    /**
     *  The size of this set.
     *  @serial
     */
    private int size;

    /**
     *  The hash table backing up this set. Contains set values directly.
     *  Due to the use of a secondary hash function, the length of this
     *  array must be a prime.
     */
    private transient float[] data;

    /** The states of each cell in the keys[]. */
    private transient byte[] states;

    private static final byte EMPTY = 0;
    private static final byte OCCUPIED = 1;
    private static final byte REMOVED = 2;

    /** The number of entries in use (removed or occupied). */
    private transient int used;

    /**
     *  The growth policy of this set (0 is relative growth, 1 is absolute growth).
     *  @serial
     */
    private int growthPolicy;

    /**
     *  The growth factor of this set, if the growth policy is
     *  relative.
     *  @serial
     */
    private double growthFactor;

    /**
     *  The growth chunk size of this set, if the growth policy is
     *  absolute.
     *  @serial
     */
    private int growthChunk;

    /**
     *  The load factor of this set.
     *  @serial
     */
    private double loadFactor;

    /**
     *  The next size at which to expand the keys[].
     *  @serial
     */
    private int expandAt;

    private FloatOpenHashSet(FloatHashFunction keyhash, int capacity, int growthPolicy, double growthFactor, int growthChunk, double loadFactor) {
        if (keyhash == null)
            Exceptions.nullArgument("hash function");
        if (capacity < 0)
            Exceptions.negativeArgument("capacity", String.valueOf(capacity));
        if (growthFactor <= 0.0)
            Exceptions.negativeOrZeroArgument("growthFactor", String.valueOf(growthFactor));
        if (growthChunk <= 0)
            Exceptions.negativeOrZeroArgument("growthChunk", String.valueOf(growthChunk));
        if (loadFactor <= 0.0)
            Exceptions.negativeOrZeroArgument("loadFactor", String.valueOf(loadFactor));
        this.keyhash = keyhash;
        capacity = bak.pcj.hash.Primes.nextPrime(capacity);
        data = new float[capacity];
        this.states = new byte[capacity];
        size = 0;
        expandAt = (int)Math.round(loadFactor*capacity);
        used = 0;
        this.growthPolicy = growthPolicy;
        this.growthFactor = growthFactor;
        this.growthChunk = growthChunk;
        this.loadFactor = loadFactor;
    }

    private FloatOpenHashSet(int capacity, int growthPolicy, double growthFactor, int growthChunk, double loadFactor) {
        this(DefaultFloatHashFunction.INSTANCE, capacity, growthPolicy, growthFactor, growthChunk, loadFactor);
    }

    /**
     *  Creates a new hash set with capacity 11, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     */
    public FloatOpenHashSet() {
        this(DEFAULT_CAPACITY);
    }

    /**
     *  Creates a new hash set with the same elements as a specified
     *  collection.
     *
     *  @param      c
     *              the collection whose elements to add to the new
     *              set.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatCollection c) {
        this();
        addAll(c);
    }

    /**
     *  Creates a new hash set with the same elements as the specified
     *  array.
     *
     *  @param      a
     *              the array whose elements to add to the new
     *              set.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public FloatOpenHashSet(float[] a) {
        this();
        for (int i = 0; i < a.length; i++)
            add(a[i]);
    }

    /**
     *  Creates a new hash set with a specified capacity, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     */
    public FloatOpenHashSet(int capacity) {
        this(capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, DEFAULT_LOAD_FACTOR);
    }

    /**
     *  Creates a new hash set with a capacity of 11, a relative
     *  growth factor of 1.0, and a specified load factor.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>loadFactor</tt> is negative or zero.
     */
    public FloatOpenHashSet(double loadFactor) {
        this(DEFAULT_CAPACITY, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity and
     *  load factor, and a relative growth factor of 1.0.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive.
     */
    public FloatOpenHashSet(int capacity, double loadFactor) {
        this(capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity,
     *  load factor, and relative growth factor.
     *
     *  <p>The set capacity increases to <tt>capacity()*(1+growthFactor)</tt>.
     *  This strategy is good for avoiding many capacity increases, but
     *  the amount of wasted memory is approximately the size of the set.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @param      growthFactor
     *              the relative amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthFactor</tt> is not positive.
     */
    public FloatOpenHashSet(int capacity, double loadFactor, double growthFactor) {
        this(capacity, GROWTH_POLICY_RELATIVE, growthFactor, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity,
     *  load factor, and absolute growth factor.
     *
     *  <p>The set capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @param      growthChunk
     *              the absolute amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthChunk</tt> is not positive.
     */
    public FloatOpenHashSet(int capacity, double loadFactor, int growthChunk) {
        this(capacity, GROWTH_POLICY_ABSOLUTE, DEFAULT_GROWTH_FACTOR, growthChunk, loadFactor);
    }

    // ---------------------------------------------------------------
    //      Constructors with hash function argument
    // ---------------------------------------------------------------

    /**
     *  Creates a new hash set with capacity 11, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatHashFunction keyhash) {
        this(keyhash, DEFAULT_CAPACITY, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, DEFAULT_LOAD_FACTOR);
    }

    /**
     *  Creates a new hash set with a specified capacity, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatHashFunction keyhash, int capacity) {
        this(keyhash, capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, DEFAULT_LOAD_FACTOR);
    }

    /**
     *  Creates a new hash set with a capacity of 11, a relative
     *  growth factor of 1.0, and a specified load factor.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>loadFactor</tt> is negative or zero.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatHashFunction keyhash, double loadFactor) {
        this(keyhash, DEFAULT_CAPACITY, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity and
     *  load factor, and a relative growth factor of 1.0.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatHashFunction keyhash, int capacity, double loadFactor) {
        this(keyhash, capacity, DEFAULT_GROWTH_POLICY, DEFAULT_GROWTH_FACTOR, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity,
     *  load factor, and relative growth factor.
     *
     *  <p>The set capacity increases to <tt>capacity()*(1+growthFactor)</tt>.
     *  This strategy is good for avoiding many capacity increases, but
     *  the amount of wasted memory is approximately the size of the set.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @param      growthFactor
     *              the relative amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthFactor</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatHashFunction keyhash, int capacity, double loadFactor, double growthFactor) {
        this(keyhash, capacity, GROWTH_POLICY_RELATIVE, growthFactor, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity,
     *  load factor, and absolute growth factor.
     *
     *  @param      keyhash
     *              the hash function to use when hashing keys.
     *
     *  <p>The set capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
     *
     *  @param      capacity
     *              the initial capacity of the set.
     *
     *  @param      loadFactor
     *              the load factor of the set.
     *
     *  @param      growthChunk
     *              the absolute amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>loadFactor</tt> is not positive;
     *              if <tt>growthChunk</tt> is not positive.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public FloatOpenHashSet(FloatHashFunction keyhash, int capacity, double loadFactor, int growthChunk) {
        this(keyhash, capacity, GROWTH_POLICY_ABSOLUTE, DEFAULT_GROWTH_FACTOR, growthChunk, loadFactor);
    }

    // ---------------------------------------------------------------
    //      Hash table management
    // ---------------------------------------------------------------

    private void ensureCapacity(int elements) {
        if (elements >= expandAt) {
            int newcapacity;
            if (growthPolicy == GROWTH_POLICY_RELATIVE)
                newcapacity = (int)(data.length * (1.0 + growthFactor));
            else
                newcapacity = data.length + growthChunk;
            if (newcapacity*loadFactor < elements)
                newcapacity = (int)Math.round(((double)elements/loadFactor));
            newcapacity = bak.pcj.hash.Primes.nextPrime(newcapacity);
            expandAt = (int)Math.round(loadFactor*newcapacity);

            float[] newdata = new float[newcapacity];
            byte[] newstates = new byte[newcapacity];

            used = 0;
            //  re-hash
            for (int i = 0; i < data.length; i++) {
                if (states[i] == OCCUPIED) {
                    used++;
                    float v = data[i];
                    //  first hash
                    int h = Math.abs(keyhash.hash(v));
                    int n = h % newcapacity;
                    if (newstates[n] == OCCUPIED) {
                        //  second hash
                        int c = 1 + (h % (newcapacity - 2));
                        for (;;) {
                            n -= c;
                            if (n < 0)
                                n += newcapacity;
                            if (newstates[n] == EMPTY)
                                break;
                        }
                    }
                    newstates[n] = OCCUPIED;
                    newdata[n] = v;
                }
            }

            data = newdata;
            states = newstates;
        }
    }

    // ---------------------------------------------------------------
    //      Operations not supported by abstract implementation
    // ---------------------------------------------------------------

    public boolean add(float v) {
        ensureCapacity(used+1);

        //  first hash
        int h = Math.abs(keyhash.hash(v));
        int i = h % data.length;
        if (states[i] == OCCUPIED) {
            if (data[i] == v)
                return false;
            //  second hash
            int c = 1 + (h % (data.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += data.length;
                //  Removed entries are re-used
                if (states[i] == EMPTY || states[i] == REMOVED)
                    break;
                if (states[i] == OCCUPIED && data[i] == v)
                    return false;
            }
        }
        if (states[i] == EMPTY)
            used++;
        states[i] = OCCUPIED;
        data[i] = v;
        size++;
        return true;
    }

    public FloatIterator iterator() {
        return new FloatIterator() {
            int nextEntry = nextEntry(0);
            int lastEntry = -1;

            int nextEntry(int index) {
                while (index < data.length && states[index] != OCCUPIED)
                    index++;
                return index;
            }

            public boolean hasNext() {
                return nextEntry < data.length;
            }

            public float next() {
                if (!hasNext())
                    Exceptions.endOfIterator();
                lastEntry = nextEntry;
                nextEntry = nextEntry(nextEntry+1);
                return data[lastEntry];
            }

            public void remove() {
                if (lastEntry == -1)
                    Exceptions.noElementToRemove();
                states[lastEntry] = REMOVED;
                size--;
                lastEntry = -1;
            }
        };
    }

    public void trimToSize()
    {  }

    /**
     *  Returns a clone of this hash set.
     *
     *  @return     a clone of this hash set.
     *
     *  @since      1.1
     */
    public Object clone() {
        try {
            FloatOpenHashSet c = (FloatOpenHashSet)super.clone();
            c.data = new float[data.length];
            System.arraycopy(data, 0, c.data, 0, data.length);
            c.states = new byte[data.length];
            System.arraycopy(states, 0, c.states, 0, states.length);
            return c;
        } catch (CloneNotSupportedException e) {
            Exceptions.cloning(); throw new RuntimeException();
        }
    }

    // ---------------------------------------------------------------
    //      Operations overwritten for efficiency
    // ---------------------------------------------------------------

    public int size()
    { return size; }

    public void clear() {
        size = 0;
        used = 0;
        java.util.Arrays.fill(states, EMPTY);
    }

    public boolean contains(float v) {
        int h = Math.abs(keyhash.hash(v));
        int i = h % data.length;
        if (states[i] != EMPTY) {
            if (states[i] == OCCUPIED && data[i] == v)
                return true;

            //  second hash
            int c = 1 + (h % (data.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += data.length;
                if (states[i] == EMPTY)
                    return false;
                if (states[i] == OCCUPIED && data[i] == v)
                    return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h = 0;
        for (int i = 0; i < data.length; i++)
            if (states[i] == OCCUPIED)
                h += data[i];
        return h;
    }

    public boolean remove(float v) {
        int h = Math.abs(keyhash.hash(v));
        int i = h % data.length;
        if (states[i] != EMPTY) {
            if (states[i] == OCCUPIED && data[i] == v) {
                states[i] = REMOVED;
                size--;
                return true;
            }
            //  second hash
            int c = 1 + (h % (data.length - 2));
            for (;;) {
                i -= c;
                if (i < 0)
                    i += data.length;
                if (states[i] == EMPTY)
                    return false;
                if (states[i] == OCCUPIED && data[i] == v) {
                    states[i] = REMOVED;
                    size--;
                    return true;
                }
            }
        }
        return false;
    }

    public float[] toArray(float[] a) {
        if (a == null || a.length < size)
            a = new float[size];

        int p = 0;
        for (int i = 0; i < data.length; i++)
            if (states[i] == OCCUPIED)
                a[p++] = data[i];
        return a;
    }

    // ---------------------------------------------------------------
    //      Serialization
    // ---------------------------------------------------------------

    /**
     *  @serialData     Default fields; the capacity of the
     *                  set (<tt>int</tt>); the set's elements
     *                  (<tt>float</tt>).
     *
     *  @since          1.1
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(data.length);
        FloatIterator i = iterator();
        while (i.hasNext()) {
            float x = i.next();
            s.writeFloat(x);
        }
    }

    /**
     *  @since          1.1
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        data = new float[s.readInt()];
        states = new byte[data.length];
        used = size;
        for (int n = 0; n < size; n++) {
            float v = s.readFloat();

            //  first hash
            int h = Math.abs(keyhash.hash(v));
            int i = h % data.length;
            if (states[i] == OCCUPIED) {
                //  second hash
                int c = 1 + (h % (data.length - 2));
                for (;;) {
                    i -= c;
                    if (i < 0)
                        i += data.length;
                    if (states[i] == EMPTY)
                        break;
                }
            }
            states[i] = OCCUPIED;
            data[i] = v;
        }
    }

}
