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

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.hash.LongHashFunction;
import bak.pcj.hash.DefaultLongHashFunction;
import bak.pcj.util.Exceptions;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *  This class represents chained hash table based sets of long values.
 *  Unlike the Java Collections <tt>HashSet</tt> instances of this class
 *  are not backed up by a map. It is implemented using a simple chained
 *  hash table where the keys are stored directly as entries.
 *
 *  @see        LongOpenHashSet
 *  @see        java.util.HashSet
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.4     21-08-2003 20:05
 *  @since      1.0
 */
public class LongChainedHashSet extends AbstractLongSet implements LongSet, Cloneable, Serializable {

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
    private LongHashFunction keyhash;

    /** 
     *  The size of this set.
     *  @serial
     */
    private int size;

    /** The hash table backing up this set. Contains set values directly. */
    private transient long[][] data;

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
     *  The next size at which to expand the data[].
     *  @serial
     */
    private int expandAt;

    private LongChainedHashSet(LongHashFunction keyhash, int capacity, int growthPolicy, double growthFactor, int growthChunk, double loadFactor) {
        if (keyhash == null)
            Exceptions.nullArgument("hash function");
        if (capacity < 0)
            Exceptions.negativeArgument("capacity", String.valueOf(capacity));
        if (growthFactor < 0.0)
            Exceptions.negativeArgument("growthFactor", String.valueOf(growthFactor));
        if (growthChunk < 0)
            Exceptions.negativeArgument("growthChunk", String.valueOf(growthChunk));
        if (loadFactor <= 0.0)
            Exceptions.negativeOrZeroArgument("loadFactor", String.valueOf(loadFactor));
        data = new long[capacity][];
        size = 0;
        expandAt = (int)Math.round(loadFactor*capacity);
        this.growthPolicy = growthPolicy;
        this.growthFactor = growthFactor;
        this.growthChunk = growthChunk;
        this.loadFactor = loadFactor;
        this.keyhash = keyhash;
    }

    private LongChainedHashSet(int capacity, int growthPolicy, double growthFactor, int growthChunk, double loadFactor) {
        this(DefaultLongHashFunction.INSTANCE, capacity, growthPolicy, growthFactor, growthChunk, loadFactor);
    }

    /**
     *  Creates a new hash set with capacity 11, a relative
     *  growth factor of 1.0, and a load factor of 75%.
     */
    public LongChainedHashSet() {
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
    public LongChainedHashSet(LongCollection c) {
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
    public LongChainedHashSet(long[] a) {
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
    public LongChainedHashSet(int capacity) {
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
     *              if <tt>loadFactor</tt> is negative.
     */
    public LongChainedHashSet(double loadFactor) {
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
    public LongChainedHashSet(int capacity, double loadFactor) {
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
    public LongChainedHashSet(int capacity, double loadFactor, double growthFactor) {
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
    public LongChainedHashSet(int capacity, double loadFactor, int growthChunk) {
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
    public LongChainedHashSet(LongHashFunction keyhash) {
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
    public LongChainedHashSet(LongHashFunction keyhash, int capacity) {
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
     *              if <tt>loadFactor</tt> is negative.
     *
     *  @throws     NullPointerException
     *              if <tt>keyhash</tt> is <tt>null</tt>.
     */
    public LongChainedHashSet(LongHashFunction keyhash, double loadFactor) {
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
    public LongChainedHashSet(LongHashFunction keyhash, int capacity, double loadFactor) {
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
    public LongChainedHashSet(LongHashFunction keyhash, int capacity, double loadFactor, double growthFactor) {
        this(keyhash, capacity, GROWTH_POLICY_RELATIVE, growthFactor, DEFAULT_GROWTH_CHUNK, loadFactor);
    }

    /**
     *  Creates a new hash set with a specified capacity,
     *  load factor, and absolute growth factor.
     *
     *  <p>The set capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
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
    public LongChainedHashSet(LongHashFunction keyhash, int capacity, double loadFactor, int growthChunk) {
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

            long[][] newdata = new long[newcapacity][];

            //  re-hash
            for (int i = 0; i < data.length; i++) {
                long[] list = data[i];
                if (list != null) {
                    for (int n = 0; n < list.length; n++) {
                        long v = list[n];
                        int index = Math.abs(keyhash.hash(v)) % newdata.length;
                        newdata[index] = addList(newdata[index], v);
                    }
                }
            }

            data = newdata;
        }
    }

    private long[] addList(long[] list, long v) {
        if (list == null)
            return new long[]{v};
        long[] newlist = new long[list.length+1];
        for (int i = 0; i < list.length; i++)
            newlist[i] = list[i];
        newlist[list.length] = v;
        return newlist;
    }

    private long[] removeList(long[] list, int index) {
        if (list.length == 1)
            return null;
        long[] newlist = new long[list.length-1];
        int n = 0;
        for (int i = 0; i < index; i++)
            newlist[n++] = list[i];
        for (int i = index+1; i < list.length; i++)
            newlist[n++] = list[i];
        return newlist;
    }

    private int searchList(long[] list, long v) {
        for (int i = 0; i < list.length; i++)
            if (list[i] == v)
                return i;
        return -1;
    }

    // ---------------------------------------------------------------
    //      Operations not supported by abstract implementation
    // ---------------------------------------------------------------

    public boolean add(long v) {
        ensureCapacity(size+1);

        int index = Math.abs(keyhash.hash(v)) % data.length;
        long[] list = data[index];
        if (list == null) {
            data[index] = new long[]{v};
            size++;
            return true;
        }
        for (int i = 0; i < list.length; i++)
            if (list[i] == v)
                return false;
        data[index] = addList(data[index], v);
        size++;
        return true;
    }

    public LongIterator iterator() {
        return new LongIterator() {
            int currList = nextList(0);
            int currLong = 0;
            int lastList = -1;
            int lastLong;
            long lastValue;

            int nextList(int index) {
                while (index < data.length && data[index] == null)
                    index++;
                return index < data.length ? index : -1;
            }

            public boolean hasNext() {
                return currList != -1;
            }

            public long next() {
                if (currList == -1)
                    Exceptions.endOfIterator();
                lastList = currList;
                lastLong = currLong;
                lastValue = data[currList][currLong];
                if (currLong == data[currList].length-1) {
                    currList = nextList(currList+1);
                    currLong = 0;
                } else {
                    currLong++;
                }
                return lastValue;
            }

            public void remove() {
                if (lastList == -1)
                    Exceptions.noElementToRemove();
                if (currList == lastList)
                    currLong--;
                data[lastList] = removeList(data[lastList], lastLong);
                size--;
                lastList = -1;
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
            LongChainedHashSet c = (LongChainedHashSet)super.clone();
            c.data = new long[data.length][];
            // Cloning each array is not necessary since they are immutable
            System.arraycopy(data, 0, c.data, 0, data.length);
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

    public void clear()
    { size = 0; }

    public boolean contains(long v) {
        long[] list = data[Math.abs(keyhash.hash(v)) % data.length];
        if (list == null)
            return false;
        return searchList(list, v) != -1;
    }

    public int hashCode() {
        int h = 0;
        for (int i = 0; i < data.length; i++) {
            long[] list = data[i];
            if (list != null) {
                for (int n = 0; n < list.length; n++)
                    h += list[n];
            }
        }
        return h;
    }

    public boolean remove(long v) {
        int index = Math.abs(keyhash.hash(v)) % data.length;
        long[] list = data[index];
        if (list != null) {
            int lindex = searchList(list, v);
            if (lindex == -1)
                return false;
            data[index] = removeList(list, lindex);
            size--;
            return true;
        }
        return false;
    }

    public long[] toArray(long[] a) {
        if (a == null || a.length < size)
            a = new long[size];

        int p = 0;
        for (int i = 0; i < data.length; i++) {
            long[] list = data[i];
            if (list != null) {
                for (int n = 0; n < list.length; n++)
                    a[p++] = list[n];
            }
        }
        return a;
    }

    // ---------------------------------------------------------------
    //      Serialization
    // ---------------------------------------------------------------

    /**
     *  @serialData     Default fields; the capacity of the
     *                  set (<tt>int</tt>); the set's elements
     *                  (<tt>long</tt>).
     *
     *  @since          1.1
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(data.length);
        LongIterator i = iterator();
        while (i.hasNext()) {
            long x = i.next();
            s.writeLong(x);
        }
    }

    /**
     *  @since          1.1
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        data = new long[s.readInt()][];
        for (int i = 0; i < size; i++) {
            long v = s.readLong();
            int index = Math.abs(keyhash.hash(v)) % data.length;
            long[] list = data[index];
            if (list == null)
                data[index] = new long[]{v};
            else
                data[index] = addList(data[index], v);
        }
    }

}
