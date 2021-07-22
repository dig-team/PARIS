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
package bak.pcj;

import bak.pcj.list.*;
import bak.pcj.set.*;
import bak.pcj.map.*;
import bak.pcj.adapter.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map;

/**
 *  This class provides static methods for creating adapters betweeen
 *  primitive collections and Java Collection Framework collections.
 *  Adapters are generally implemented as wrappers around an underlying
 *  collection. Thus, changes to the underlying collection are reflected
 *  by the adapting collection and vice versa.
 *
 *  <p>In order for adaptions from JCF to PCJ to work correctly, 
 *  a number of rules should be followed:
 *  <ul>
 *  <li>The underlying java.util collection/iterator should only
 *      contain values of the class representing the primitive type
 *      of the adapting collection/iterator. E.g. if a <tt>List</tt> is adapted
 *      to a <tt>FloatCollection</tt>, the <tt>List</tt> can only contain
 *      values of class <tt>Float</tt>. If this rule is not followed, a
 *      {@link ClassCastException ClassCastException} will likely be
 *      thrown by some or all of the adaption's methods.
 *  <li>The underlying java.util collection/iterator should not
 *      contain <tt>null</tt>-values. If this rule is not followed, a
 *      {@link NullPointerException NullPointerException} will likely be
 *      thrown by some or all of the adaption's methods.
 *  </ul>
 *  The adapter classes from JCF to PCJ all contains validation methods
 *  to ensure that these rules are followed, and a number of static methods
 *  is available in this class to check whether a JCF collection is
 *  adaptable (<tt>isTAdaptable(Collection)</tt>, <tt>isTKeyAdaptable(Map)</tt>, 
 *  and <tt>isTKeySAdaptable(Map)</tt>).
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.3     18-08-2003 23:53
 *  @since      1.0
 */
public class Adapter {

    /** Prevents instantiation. */
    private Adapter() { }

    // ---------------------------------------------------------------
    //      Iterator -> TIterator
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive boolean values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive boolean values.
     */
    public static BooleanIterator asBooleans(Iterator iterator)
    { return new IteratorToBooleanIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive char values.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive char values.
     */
    public static CharIterator asChars(Iterator iterator)
    { return new IteratorToCharIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive byte values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive byte values.
     */
    public static ByteIterator asBytes(Iterator iterator)
    { return new IteratorToByteIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive short values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive short values.
     */
    public static ShortIterator asShorts(Iterator iterator)
    { return new IteratorToShortIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive int values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive int values.
     */
    public static IntIterator asInts(Iterator iterator)
    { return new IteratorToIntIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive long values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive long values.
     */
    public static LongIterator asLongs(Iterator iterator)
    { return new IteratorToLongIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive float values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive float values.
     */
    public static FloatIterator asFloats(Iterator iterator)
    { return new IteratorToFloatIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator to an iterator over
     *  primitive double values.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator to an iterator over
     *              primitive double values.
     */
    public static DoubleIterator asDoubles(Iterator iterator)
    { return new IteratorToDoubleIteratorAdapter(iterator); }

    // ---------------------------------------------------------------
    //      TIterator -> Iterator
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of an iterator over primitive
     *  boolean values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              boolean values to an iterator.
     */
    public static Iterator asObjects(BooleanIterator iterator)
    { return new BooleanIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  char values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              char values to an iterator.
     */
    public static Iterator asObjects(CharIterator iterator)
    { return new CharIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  byte values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              byte values to an iterator.
     */
    public static Iterator asObjects(ByteIterator iterator)
    { return new ByteIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  short values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              short values to an iterator.
     */
    public static Iterator asObjects(ShortIterator iterator)
    { return new ShortIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  int values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              int values to an iterator.
     */
    public static Iterator asObjects(IntIterator iterator)
    { return new IntIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  long values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              long values to an iterator.
     */
    public static Iterator asObjects(LongIterator iterator)
    { return new LongIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  float values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              float values to an iterator.
     */
    public static Iterator asObjects(FloatIterator iterator)
    { return new FloatIteratorToIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of an iterator over primitive
     *  double values to an iterator.
     *
     *  @param      iterator
     *              the iterator to adapt.
     *
     *  @return     an adaption of an iterator over primitive
     *              double values to an iterator.
     */
    public static Iterator asObjects(DoubleIterator iterator)
    { return new DoubleIteratorToIteratorAdapter(iterator); }

    // ---------------------------------------------------------------
    //      Collection -> TCollection
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive boolean values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive boolean values.
     */
    public static BooleanCollection asBooleans(Collection collection)
    { return new CollectionToBooleanCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive char values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive char values.
     */
    public static CharCollection asChars(Collection collection)
    { return new CollectionToCharCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive byte values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive byte values.
     */
    public static ByteCollection asBytes(Collection collection)
    { return new CollectionToByteCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive short values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive short values.
     */
    public static ShortCollection asShorts(Collection collection)
    { return new CollectionToShortCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive int values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive int values.
     */
    public static IntCollection asInts(Collection collection)
    { return new CollectionToIntCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive long values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive long values.
     */
    public static LongCollection asLongs(Collection collection)
    { return new CollectionToLongCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive float values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive float values.
     */
    public static FloatCollection asFloats(Collection collection)
    { return new CollectionToFloatCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a collection to a collection of
     *  primitive double values.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a collection to a collection of
     *              primitive double values.
     */
    public static DoubleCollection asDoubles(Collection collection)
    { return new CollectionToDoubleCollectionAdapter(collection); }

    // ---------------------------------------------------------------
    //      TCollection -> Collection
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a primitive collection of boolean values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of boolean
     *              values to a collection.
     */
    public static Collection asObjects(BooleanCollection collection)
    { return new BooleanCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of char values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of char
     *              values to a collection.
     */
    public static Collection asObjects(CharCollection collection)
    { return new CharCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of byte values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of byte
     *              values to a collection.
     */
    public static Collection asObjects(ByteCollection collection)
    { return new ByteCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of short values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of short
     *              values to a collection.
     */
    public static Collection asObjects(ShortCollection collection)
    { return new ShortCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of int values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of int
     *              values to a collection.
     */
    public static Collection asObjects(IntCollection collection)
    { return new IntCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of long values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of long
     *              values to a collection.
     */
    public static Collection asObjects(LongCollection collection)
    { return new LongCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of float values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of float
     *              values to a collection.
     */
    public static Collection asObjects(FloatCollection collection)
    { return new FloatCollectionToCollectionAdapter(collection); }

    /**
     *  Returns an adaption of a primitive collection of double values
     *  to a collection.
     *
     *  @param      collection
     *              the collection to adapt.
     *
     *  @return     an adaption of a primitive collection of double
     *              values to a collection.
     */
    public static Collection asObjects(DoubleCollection collection)
    { return new DoubleCollectionToCollectionAdapter(collection); }

    // ---------------------------------------------------------------
    //      ListIterator -> TListIterator
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive boolean values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive boolean values.
     */
    public static BooleanListIterator asBooleans(ListIterator iterator)
    { return new ListIteratorToBooleanListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive char values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive char values.
     */
    public static CharListIterator asChars(ListIterator iterator)
    { return new ListIteratorToCharListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive byte values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive byte values.
     */
    public static ByteListIterator asBytes(ListIterator iterator)
    { return new ListIteratorToByteListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive short values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive short values.
     */
    public static ShortListIterator asShorts(ListIterator iterator)
    { return new ListIteratorToShortListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive int values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive int values.
     */
    public static IntListIterator asInts(ListIterator iterator)
    { return new ListIteratorToIntListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive long values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive long values.
     */
    public static LongListIterator asLongs(ListIterator iterator)
    { return new ListIteratorToLongListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive float values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive float values.
     */
    public static FloatListIterator asFloats(ListIterator iterator)
    { return new ListIteratorToFloatListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator to a list iterator over
     *  primitive double values.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator to a list iterator over
     *              primitive double values.
     */
    public static DoubleListIterator asDoubles(ListIterator iterator)
    { return new ListIteratorToDoubleListIteratorAdapter(iterator); }

    // ---------------------------------------------------------------
    //      TListIterator -> ListIterator
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a list iterator over
     *  primitive boolean values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive boolean values to a list iterator.
     */
    public static ListIterator asObjects(BooleanListIterator iterator)
    { return new BooleanListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive char values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive char values to a list iterator.
     */
    public static ListIterator asObjects(CharListIterator iterator)
    { return new CharListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive byte values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive byte values to a list iterator.
     */
    public static ListIterator asObjects(ByteListIterator iterator)
    { return new ByteListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive short values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive short values to a list iterator.
     */
    public static ListIterator asObjects(ShortListIterator iterator)
    { return new ShortListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive int values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive int values to a list iterator.
     */
    public static ListIterator asObjects(IntListIterator iterator)
    { return new IntListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive long values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive long values to a list iterator.
     */
    public static ListIterator asObjects(LongListIterator iterator)
    { return new LongListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive float values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive float values to a list iterator.
     */
    public static ListIterator asObjects(FloatListIterator iterator)
    { return new FloatListIteratorToListIteratorAdapter(iterator); }

    /**
     *  Returns an adaption of a list iterator over
     *  primitive double values to a list iterator.
     *
     *  @param      iterator
     *              the list iterator to adapt.
     *
     *  @return     an adaption of a list iterator over
     *              primitive double values to a list iterator.
     */
    public static ListIterator asObjects(DoubleListIterator iterator)
    { return new DoubleListIteratorToListIteratorAdapter(iterator); }

    // ---------------------------------------------------------------
    //      Set -> TSet
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a set to a set of
     *  primitive boolean values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive boolean values.
     */
    public static BooleanSet asBooleans(Set set)
    { return new SetToBooleanSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive char values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive char values.
     */
    public static CharSet asChars(Set set)
    { return new SetToCharSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive byte values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive byte values.
     */
    public static ByteSet asBytes(Set set)
    { return new SetToByteSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive short values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive short values.
     */
    public static ShortSet asShorts(Set set)
    { return new SetToShortSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive int values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive int values.
     */
    public static IntSet asInts(Set set)
    { return new SetToIntSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive long values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive long values.
     */
    public static LongSet asLongs(Set set)
    { return new SetToLongSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive float values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive float values.
     */
    public static FloatSet asFloats(Set set)
    { return new SetToFloatSetAdapter(set); }

    /**
     *  Returns an adaption of a set to a set of
     *  primitive double values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive double values.
     */
    public static DoubleSet asDoubles(Set set)
    { return new SetToDoubleSetAdapter(set); }

    // ---------------------------------------------------------------
    //      TSet -> Set
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a set of primitive boolean values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive boolean values
     *              to a set.
     */
    public static Set asObjects(BooleanSet set)
    { return new BooleanSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive char values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive char values
     *              to a set.
     */
    public static Set asObjects(CharSet set)
    { return new CharSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive byte values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive byte values
     *              to a set.
     */
    public static Set asObjects(ByteSet set)
    { return new ByteSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive short values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive short values
     *              to a set.
     */
    public static Set asObjects(ShortSet set)
    { return new ShortSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive int values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive int values
     *              to a set.
     */
    public static Set asObjects(IntSet set)
    { return new IntSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive long values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive long values
     *              to a set.
     */
    public static Set asObjects(LongSet set)
    { return new LongSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive float values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive float values
     *              to a set.
     */
    public static Set asObjects(FloatSet set)
    { return new FloatSetToSetAdapter(set); }

    /**
     *  Returns an adaption of a set of primitive double values
     *  to a set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive double values
     *              to a set.
     */
    public static Set asObjects(DoubleSet set)
    { return new DoubleSetToSetAdapter(set); }

    // ---------------------------------------------------------------
    //      SortedSet -> TSortedSet
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive boolean values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive boolean values.
     *
     *  @since      1.2
     */
    public static BooleanSortedSet asBooleans(SortedSet set)
    { return new SortedSetToBooleanSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive char values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive char values.
     *
     *  @since      1.2
     */
    public static CharSortedSet asChars(SortedSet set)
    { return new SortedSetToCharSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive byte values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive byte values.
     *
     *  @since      1.2
     */
    public static ByteSortedSet asBytes(SortedSet set)
    { return new SortedSetToByteSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive short values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive short values.
     *
     *  @since      1.2
     */
    public static ShortSortedSet asShorts(SortedSet set)
    { return new SortedSetToShortSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive int values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive int values.
     *
     *  @since      1.2
     */
    public static IntSortedSet asInts(SortedSet set)
    { return new SortedSetToIntSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive long values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive long values.
     *
     *  @since      1.2
     */
    public static LongSortedSet asLongs(SortedSet set)
    { return new SortedSetToLongSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive float values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive float values.
     *
     *  @since      1.2
     */
    public static FloatSortedSet asFloats(SortedSet set)
    { return new SortedSetToFloatSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set to a sorted set of
     *  primitive double values.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set to a set of
     *              primitive double values.
     *
     *  @since      1.2
     */
    public static DoubleSortedSet asDoubles(SortedSet set)
    { return new SortedSetToDoubleSortedSetAdapter(set); }

    // ---------------------------------------------------------------
    //      TSortedSet -> SortedSet
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a sorted set of primitive boolean values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive boolean values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(BooleanSortedSet set)
    { return new BooleanSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive char values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive char values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(CharSortedSet set)
    { return new CharSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive byte values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive byte values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(ByteSortedSet set)
    { return new ByteSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive short values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive short values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(ShortSortedSet set)
    { return new ShortSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive int values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive int values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(IntSortedSet set)
    { return new IntSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive long values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive long values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(LongSortedSet set)
    { return new LongSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive float values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive float values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(FloatSortedSet set)
    { return new FloatSortedSetToSortedSetAdapter(set); }

    /**
     *  Returns an adaption of a sorted set of primitive double values
     *  to a sorted set.
     *
     *  @param      set
     *              the set to adapt.
     *
     *  @return     an adaption of a set of primitive double values
     *              to a set.
     *
     *  @since      1.2
     */
    public static SortedSet asObjects(DoubleSortedSet set)
    { return new DoubleSortedSetToSortedSetAdapter(set); }

    // ---------------------------------------------------------------
    //      List -> TList
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a list to a list of
     *  primitive boolean values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive boolean values.
     */
    public static BooleanList asBooleans(List list)
    { return new ListToBooleanListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive char values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive char values.
     */
    public static CharList asChars(List list)
    { return new ListToCharListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive byte values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive byte values.
     */
    public static ByteList asBytes(List list)
    { return new ListToByteListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive short values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive short values.
     */
    public static ShortList asShorts(List list)
    { return new ListToShortListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive int values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive int values.
     */
    public static IntList asInts(List list)
    { return new ListToIntListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive long values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive long values.
     */
    public static LongList asLongs(List list)
    { return new ListToLongListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive float values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive float values.
     */
    public static FloatList asFloats(List list)
    { return new ListToFloatListAdapter(list); }

    /**
     *  Returns an adaption of a list to a list of
     *  primitive double values.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list to a list of
     *              primitive double values.
     */
    public static DoubleList asDoubles(List list)
    { return new ListToDoubleListAdapter(list); }

    // ---------------------------------------------------------------
    //      TList -> List
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a list of primitive boolean values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive boolean values
     *              to a list.
     */
    public static List asObjects(BooleanList list)
    { return new BooleanListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive char values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive char values
     *              to a list.
     */
    public static List asObjects(CharList list)
    { return new CharListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive byte values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive byte values
     *              to a list.
     */
    public static List asObjects(ByteList list)
    { return new ByteListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive short values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive short values
     *              to a list.
     */
    public static List asObjects(ShortList list)
    { return new ShortListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive int values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive int values
     *              to a list.
     */
    public static List asObjects(IntList list)
    { return new IntListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive long values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive long values
     *              to a list.
     */
    public static List asObjects(LongList list)
    { return new LongListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive float values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive float values
     *              to a list.
     */
    public static List asObjects(FloatList list)
    { return new FloatListToListAdapter(list); }

    /**
     *  Returns an adaption of a list of primitive double values
     *  to a list.
     *
     *  @param      list
     *              the list to adapt.
     *
     *  @return     an adaption of a list of primitive double values
     *              to a list.
     */
    public static List asObjects(DoubleList list)
    { return new DoubleListToListAdapter(list); }

    // ---------------------------------------------------------------
    //      Map -> TKeySMap
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to boolean values.
     */
    public static BooleanKeyBooleanMap asBooleanKeyBooleans(Map map)
    { return new MapToBooleanKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to char values.
     */
    public static BooleanKeyCharMap asBooleanKeyChars(Map map)
    { return new MapToBooleanKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to byte values.
     */
    public static BooleanKeyByteMap asBooleanKeyBytes(Map map)
    { return new MapToBooleanKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to short values.
     */
    public static BooleanKeyShortMap asBooleanKeyShorts(Map map)
    { return new MapToBooleanKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to int values.
     */
    public static BooleanKeyIntMap asBooleanKeyInts(Map map)
    { return new MapToBooleanKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to long values.
     */
    public static BooleanKeyLongMap asBooleanKeyLongs(Map map)
    { return new MapToBooleanKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to float values.
     */
    public static BooleanKeyFloatMap asBooleanKeyFloats(Map map)
    { return new MapToBooleanKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  boolean keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              boolean keys to double values.
     */
    public static BooleanKeyDoubleMap asBooleanKeyDoubles(Map map)
    { return new MapToBooleanKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to boolean values.
     */
    public static CharKeyBooleanMap asCharKeyBooleans(Map map)
    { return new MapToCharKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to char values.
     */
    public static CharKeyCharMap asCharKeyChars(Map map)
    { return new MapToCharKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to byte values.
     */
    public static CharKeyByteMap asCharKeyBytes(Map map)
    { return new MapToCharKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to short values.
     */
    public static CharKeyShortMap asCharKeyShorts(Map map)
    { return new MapToCharKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to int values.
     */
    public static CharKeyIntMap asCharKeyInts(Map map)
    { return new MapToCharKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to long values.
     */
    public static CharKeyLongMap asCharKeyLongs(Map map)
    { return new MapToCharKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to float values.
     */
    public static CharKeyFloatMap asCharKeyFloats(Map map)
    { return new MapToCharKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  char keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              char keys to double values.
     */
    public static CharKeyDoubleMap asCharKeyDoubles(Map map)
    { return new MapToCharKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to boolean values.
     */
    public static ByteKeyBooleanMap asByteKeyBooleans(Map map)
    { return new MapToByteKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to char values.
     */
    public static ByteKeyCharMap asByteKeyChars(Map map)
    { return new MapToByteKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to byte values.
     */
    public static ByteKeyByteMap asByteKeyBytes(Map map)
    { return new MapToByteKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to short values.
     */
    public static ByteKeyShortMap asByteKeyShorts(Map map)
    { return new MapToByteKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to int values.
     */
    public static ByteKeyIntMap asByteKeyInts(Map map)
    { return new MapToByteKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to long values.
     */
    public static ByteKeyLongMap asByteKeyLongs(Map map)
    { return new MapToByteKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to float values.
     */
    public static ByteKeyFloatMap asByteKeyFloats(Map map)
    { return new MapToByteKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  byte keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              byte keys to double values.
     */
    public static ByteKeyDoubleMap asByteKeyDoubles(Map map)
    { return new MapToByteKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to boolean values.
     */
    public static ShortKeyBooleanMap asShortKeyBooleans(Map map)
    { return new MapToShortKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to char values.
     */
    public static ShortKeyCharMap asShortKeyChars(Map map)
    { return new MapToShortKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to byte values.
     */
    public static ShortKeyByteMap asShortKeyBytes(Map map)
    { return new MapToShortKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to short values.
     */
    public static ShortKeyShortMap asShortKeyShorts(Map map)
    { return new MapToShortKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to int values.
     */
    public static ShortKeyIntMap asShortKeyInts(Map map)
    { return new MapToShortKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to long values.
     */
    public static ShortKeyLongMap asShortKeyLongs(Map map)
    { return new MapToShortKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to float values.
     */
    public static ShortKeyFloatMap asShortKeyFloats(Map map)
    { return new MapToShortKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  short keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              short keys to double values.
     */
    public static ShortKeyDoubleMap asShortKeyDoubles(Map map)
    { return new MapToShortKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to boolean values.
     */
    public static IntKeyBooleanMap asIntKeyBooleans(Map map)
    { return new MapToIntKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to char values.
     */
    public static IntKeyCharMap asIntKeyChars(Map map)
    { return new MapToIntKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to byte values.
     */
    public static IntKeyByteMap asIntKeyBytes(Map map)
    { return new MapToIntKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to short values.
     */
    public static IntKeyShortMap asIntKeyShorts(Map map)
    { return new MapToIntKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to int values.
     */
    public static IntKeyIntMap asIntKeyInts(Map map)
    { return new MapToIntKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to long values.
     */
    public static IntKeyLongMap asIntKeyLongs(Map map)
    { return new MapToIntKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to float values.
     */
    public static IntKeyFloatMap asIntKeyFloats(Map map)
    { return new MapToIntKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  int keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              int keys to double values.
     */
    public static IntKeyDoubleMap asIntKeyDoubles(Map map)
    { return new MapToIntKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to boolean values.
     */
    public static LongKeyBooleanMap asLongKeyBooleans(Map map)
    { return new MapToLongKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to char values.
     */
    public static LongKeyCharMap asLongKeyChars(Map map)
    { return new MapToLongKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to byte values.
     */
    public static LongKeyByteMap asLongKeyBytes(Map map)
    { return new MapToLongKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to short values.
     */
    public static LongKeyShortMap asLongKeyShorts(Map map)
    { return new MapToLongKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to int values.
     */
    public static LongKeyIntMap asLongKeyInts(Map map)
    { return new MapToLongKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to long values.
     */
    public static LongKeyLongMap asLongKeyLongs(Map map)
    { return new MapToLongKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to float values.
     */
    public static LongKeyFloatMap asLongKeyFloats(Map map)
    { return new MapToLongKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  long keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              long keys to double values.
     */
    public static LongKeyDoubleMap asLongKeyDoubles(Map map)
    { return new MapToLongKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to boolean values.
     */
    public static FloatKeyBooleanMap asFloatKeyBooleans(Map map)
    { return new MapToFloatKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to char values.
     */
    public static FloatKeyCharMap asFloatKeyChars(Map map)
    { return new MapToFloatKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to byte values.
     */
    public static FloatKeyByteMap asFloatKeyBytes(Map map)
    { return new MapToFloatKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to short values.
     */
    public static FloatKeyShortMap asFloatKeyShorts(Map map)
    { return new MapToFloatKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to int values.
     */
    public static FloatKeyIntMap asFloatKeyInts(Map map)
    { return new MapToFloatKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to long values.
     */
    public static FloatKeyLongMap asFloatKeyLongs(Map map)
    { return new MapToFloatKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to float values.
     */
    public static FloatKeyFloatMap asFloatKeyFloats(Map map)
    { return new MapToFloatKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  float keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              float keys to double values.
     */
    public static FloatKeyDoubleMap asFloatKeyDoubles(Map map)
    { return new MapToFloatKeyDoubleMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to boolean values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to boolean values.
     */
    public static DoubleKeyBooleanMap asDoubleKeyBooleans(Map map)
    { return new MapToDoubleKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to char values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to char values.
     */
    public static DoubleKeyCharMap asDoubleKeyChars(Map map)
    { return new MapToDoubleKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to byte values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to byte values.
     */
    public static DoubleKeyByteMap asDoubleKeyBytes(Map map)
    { return new MapToDoubleKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to short values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to short values.
     */
    public static DoubleKeyShortMap asDoubleKeyShorts(Map map)
    { return new MapToDoubleKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to int values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to int values.
     */
    public static DoubleKeyIntMap asDoubleKeyInts(Map map)
    { return new MapToDoubleKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to long values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to long values.
     */
    public static DoubleKeyLongMap asDoubleKeyLongs(Map map)
    { return new MapToDoubleKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to float values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to float values.
     */
    public static DoubleKeyFloatMap asDoubleKeyFloats(Map map)
    { return new MapToDoubleKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map from
     *  double keys to double values.
     *
     *  @param      map
     *              the map to adapt.
     *
     *  @return     an adaption of a map to a primitive map from
     *              double keys to double values.
     */
    public static DoubleKeyDoubleMap asDoubleKeyDoubles(Map map)
    { return new MapToDoubleKeyDoubleMapAdapter(map); }

    // ---------------------------------------------------------------
    //      TKeySMap -> Map
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyBooleanMap map)
    { return new BooleanKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyByteMap map)
    { return new BooleanKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyShortMap map)
    { return new BooleanKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyIntMap map)
    { return new BooleanKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyLongMap map)
    { return new BooleanKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyFloatMap map)
    { return new BooleanKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  boolean keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(BooleanKeyDoubleMap map)
    { return new BooleanKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyBooleanMap map)
    { return new CharKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyCharMap map)
    { return new CharKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyByteMap map)
    { return new CharKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyShortMap map)
    { return new CharKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyIntMap map)
    { return new CharKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyLongMap map)
    { return new CharKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyFloatMap map)
    { return new CharKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  char keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(CharKeyDoubleMap map)
    { return new CharKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyBooleanMap map)
    { return new ByteKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyCharMap map)
    { return new ByteKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyByteMap map)
    { return new ByteKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyShortMap map)
    { return new ByteKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyIntMap map)
    { return new ByteKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyLongMap map)
    { return new ByteKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyFloatMap map)
    { return new ByteKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  byte keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ByteKeyDoubleMap map)
    { return new ByteKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyBooleanMap map)
    { return new ShortKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyCharMap map)
    { return new ShortKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyByteMap map)
    { return new ShortKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyShortMap map)
    { return new ShortKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyIntMap map)
    { return new ShortKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyLongMap map)
    { return new ShortKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyFloatMap map)
    { return new ShortKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  short keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(ShortKeyDoubleMap map)
    { return new ShortKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyBooleanMap map)
    { return new IntKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyCharMap map)
    { return new IntKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyByteMap map)
    { return new IntKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyShortMap map)
    { return new IntKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyIntMap map)
    { return new IntKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyLongMap map)
    { return new IntKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyFloatMap map)
    { return new IntKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  int keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(IntKeyDoubleMap map)
    { return new IntKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyBooleanMap map)
    { return new LongKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyCharMap map)
    { return new LongKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyByteMap map)
    { return new LongKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyShortMap map)
    { return new LongKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyIntMap map)
    { return new LongKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyLongMap map)
    { return new LongKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyFloatMap map)
    { return new LongKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  long keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(LongKeyDoubleMap map)
    { return new LongKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyBooleanMap map)
    { return new FloatKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyCharMap map)
    { return new FloatKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyByteMap map)
    { return new FloatKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyShortMap map)
    { return new FloatKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyIntMap map)
    { return new FloatKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyLongMap map)
    { return new FloatKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyFloatMap map)
    { return new FloatKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  float keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(FloatKeyDoubleMap map)
    { return new FloatKeyDoubleMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyBooleanMap map)
    { return new DoubleKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyCharMap map)
    { return new DoubleKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyByteMap map)
    { return new DoubleKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyShortMap map)
    { return new DoubleKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyIntMap map)
    { return new DoubleKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyLongMap map)
    { return new DoubleKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyFloatMap map)
    { return new DoubleKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map from
     *  double keys to double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive
     *              map to a map.
     */
    public static Map asObjects(DoubleKeyDoubleMap map)
    { return new DoubleKeyDoubleMapToMapAdapter(map); }

    // ---------------------------------------------------------------
    //      TKeyMap -> Map
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a primitive map of boolean keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(BooleanKeyMap map)
    { return new BooleanKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of char keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(CharKeyMap map)
    { return new CharKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of byte keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(ByteKeyMap map)
    { return new ByteKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of short keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(ShortKeyMap map)
    { return new ShortKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of int keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(IntKeyMap map)
    { return new IntKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of long keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(LongKeyMap map)
    { return new LongKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of float keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(FloatKeyMap map)
    { return new FloatKeyMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of double keys
     *  to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     */
    public static Map asObjects(DoubleKeyMap map)
    { return new DoubleKeyMapToMapAdapter(map); }


    // ---------------------------------------------------------------
    //      Map -> TKeyMap
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a map to a primitive map
     *  from boolean keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static BooleanKeyMap asBooleanKeys(Map map)
    { return new MapToBooleanKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from char keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static CharKeyMap asCharKeys(Map map)
    { return new MapToCharKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from byte keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static ByteKeyMap asByteKeys(Map map)
    { return new MapToByteKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from short keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static ShortKeyMap asShortKeys(Map map)
    { return new MapToShortKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from int keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static IntKeyMap asIntKeys(Map map)
    { return new MapToIntKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from long keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static LongKeyMap asLongKeys(Map map)
    { return new MapToLongKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from float keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static FloatKeyMap asFloatKeys(Map map)
    { return new MapToFloatKeyMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from double keys to objects.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     */
    public static DoubleKeyMap asDoubleKeys(Map map)
    { return new MapToDoubleKeyMapAdapter(map); }

    // ---------------------------------------------------------------
    //      ObjectKeyTMap -> Map
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and boolean values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyBooleanMap map)
    { return new ObjectKeyBooleanMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and char values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyCharMap map)
    { return new ObjectKeyCharMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and byte values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyByteMap map)
    { return new ObjectKeyByteMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and short values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyShortMap map)
    { return new ObjectKeyShortMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and int values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyIntMap map)
    { return new ObjectKeyIntMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and long values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyLongMap map)
    { return new ObjectKeyLongMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and float values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyFloatMap map)
    { return new ObjectKeyFloatMapToMapAdapter(map); }

    /**
     *  Returns an adaption of a primitive map of object keys
     *  and double values to a map.
     *
     *  @param      map
     *              the primitive map to adapt.
     *
     *  @return     an adaption of the specified primitive map
     *              to a map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static Map asObjects(ObjectKeyDoubleMap map)
    { return new ObjectKeyDoubleMapToMapAdapter(map); }

    // ---------------------------------------------------------------
    //      Map -> ObjectKeyTMap
    // ---------------------------------------------------------------

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to boolean values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyBooleanMap asObjectKeyBooleans(Map map)
    { return new MapToObjectKeyBooleanMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to char values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyCharMap asObjectKeyChars(Map map)
    { return new MapToObjectKeyCharMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to byte values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyByteMap asObjectKeyBytes(Map map)
    { return new MapToObjectKeyByteMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to short values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyShortMap asObjectKeyShorts(Map map)
    { return new MapToObjectKeyShortMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to int values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyIntMap asObjectKeyInts(Map map)
    { return new MapToObjectKeyIntMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to long values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyLongMap asObjectKeyLongs(Map map)
    { return new MapToObjectKeyLongMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to float values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyFloatMap asObjectKeyFloats(Map map)
    { return new MapToObjectKeyFloatMapAdapter(map); }

    /**
     *  Returns an adaption of a map to a primitive map
     *  from object keys to double values.
     *
     *  @param      map
     *              the map to adapt to a primitive map.
     *
     *  @return     an adaption of the specified map to
     *              a primitive map.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static ObjectKeyDoubleMap asObjectKeyDoubles(Map map)
    { return new MapToObjectKeyDoubleMapAdapter(map); }

    // ---------------------------------------------------------------
    //      isTAdaptable(Collection c)
    // ---------------------------------------------------------------

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of boolean values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Boolean Boolean} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link BooleanCollection BooleanCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleans(Collection)
     *  @see        #asBooleans(List)
     *  @see        #asBooleans(Set)
     */
    public static boolean isBooleanAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Boolean))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of char values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Character Character} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link CharCollection CharCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asChars(Collection)
     *  @see        #asChars(List)
     *  @see        #asChars(Set)
     */
    public static boolean isCharAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Character))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of byte values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Byte Byte} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link ByteCollection ByteCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asBytes(Collection)
     *  @see        #asBytes(List)
     *  @see        #asBytes(Set)
     */
    public static boolean isByteAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Byte))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of short values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Short Short} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link ShortCollection ShortCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asShorts(Collection)
     *  @see        #asShorts(List)
     *  @see        #asShorts(Set)
     */
    public static boolean isShortAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Short))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of int values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Integer Integer} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link IntCollection IntCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asInts(Collection)
     *  @see        #asInts(List)
     *  @see        #asInts(Set)
     */
    public static boolean isIntAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Integer))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of long values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Long Long} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link LongCollection LongCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asLongs(Collection)
     *  @see        #asLongs(List)
     *  @see        #asLongs(Set)
     */
    public static boolean isLongAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Long))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of float values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Float Float} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link FloatCollection FloatCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asFloats(Collection)
     *  @see        #asFloats(List)
     *  @see        #asFloats(Set)
     */
    public static boolean isFloatAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Float))
                return false;
        return true;
    }

    /**
     *  Indicates whether a specified collection is adaptable
     *  to a primitive collection of double values. For a 
     *  collection to be adaptable it can only contain
     *  values of class {@link Double Double} and no 
     *  <tt>null</tt> values.
     *
     *  @param      collection
     *              the collection to examine.
     *
     *  @return     <tt>true</tt> if <tt>collection</tt> is adaptable to a 
     *              {@link DoubleCollection DoubleCollection};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>collection</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubles(Collection)
     *  @see        #asDoubles(List)
     *  @see        #asDoubles(Set)
     */
    public static boolean isDoubleAdaptable(Collection collection) {
        for (Iterator i = collection.iterator(); i.hasNext(); )
            if (!(i.next() instanceof Double))
                return false;
        return true;
    }

    // ---------------------------------------------------------------
    //      isTKeyAdaptable(Map map)
    // ---------------------------------------------------------------

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link BooleanKeyMap BooleanKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeys(Map)
     */
    public static boolean isBooleanKeyAdaptable(Map map) 
    { return isBooleanAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link CharKeyMap CharKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeys(Map)
     */
    public static boolean isCharKeyAdaptable(Map map) 
    { return isCharAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link ByteKeyMap ByteKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeys(Map)
     */
    public static boolean isByteKeyAdaptable(Map map) 
    { return isByteAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link ShortKeyMap ShortKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeys(Map)
     */
    public static boolean isShortKeyAdaptable(Map map) 
    { return isShortAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link IntKeyMap IntKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeys(Map)
     */
    public static boolean isIntKeyAdaptable(Map map) 
    { return isIntAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link LongKeyMap LongKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeys(Map)
     */
    public static boolean isLongKeyAdaptable(Map map) 
    { return isLongAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link FloatKeyMap FloatKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeys(Map)
     */
    public static boolean isFloatKeyAdaptable(Map map) 
    { return isFloatAdaptable(map.keySet()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys. For a 
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double} and no 
     *  <tt>null</tt> keys.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a 
     *              {@link DoubleKeyMap DoubleKeyMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeys(Map)
     */
    public static boolean isDoubleKeyAdaptable(Map map) 
    { return isDoubleAdaptable(map.keySet()); }

    // ---------------------------------------------------------------
    //      isTKeySAdaptable(Map map)
    // ---------------------------------------------------------------

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyBooleanMap BooleanKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyBooleans(Map)
     */
    public static boolean isBooleanKeyBooleanAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyCharMap BooleanKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyChars(Map)
     */
    public static boolean isBooleanKeyCharAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isCharAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyByteMap BooleanKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyBytes(Map)
     */
    public static boolean isBooleanKeyByteAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyShortMap BooleanKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyShorts(Map)
     */
    public static boolean isBooleanKeyShortAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyIntMap BooleanKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyInts(Map)
     */
    public static boolean isBooleanKeyIntAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyLongMap BooleanKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyLongs(Map)
     */
    public static boolean isBooleanKeyLongAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyFloatMap BooleanKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyFloats(Map)
     */
    public static boolean isBooleanKeyFloatAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with boolean keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Boolean Boolean},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link BooleanKeyDoubleMap BooleanKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asBooleanKeyDoubles(Map)
     */
    public static boolean isBooleanKeyDoubleAdaptable(Map map)
    { return isBooleanAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyBooleanMap CharKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyBooleans(Map)
     */
    public static boolean isCharKeyBooleanAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyCharMap CharKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyChars(Map)
     */
    public static boolean isCharKeyCharAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyByteMap CharKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyBytes(Map)
     */
    public static boolean isCharKeyByteAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyShortMap CharKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyShorts(Map)
     */
    public static boolean isCharKeyShortAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyIntMap CharKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyInts(Map)
     */
    public static boolean isCharKeyIntAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyLongMap CharKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyLongs(Map)
     */
    public static boolean isCharKeyLongAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyFloatMap CharKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyFloats(Map)
     */
    public static boolean isCharKeyFloatAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with char keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Character Character},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link CharKeyDoubleMap CharKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asCharKeyDoubles(Map)
     */
    public static boolean isCharKeyDoubleAdaptable(Map map)
    { return isCharAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyBooleanMap ByteKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyBooleans(Map)
     */
    public static boolean isByteKeyBooleanAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyCharMap ByteKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyChars(Map)
     */
    public static boolean isByteKeyCharAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyByteMap ByteKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyBytes(Map)
     */
    public static boolean isByteKeyByteAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyShortMap ByteKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyShorts(Map)
     */
    public static boolean isByteKeyShortAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyIntMap ByteKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyInts(Map)
     */
    public static boolean isByteKeyIntAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyLongMap ByteKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyLongs(Map)
     */
    public static boolean isByteKeyLongAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyFloatMap ByteKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyFloats(Map)
     */
    public static boolean isByteKeyFloatAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with byte keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Byte Byte},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ByteKeyDoubleMap ByteKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asByteKeyDoubles(Map)
     */
    public static boolean isByteKeyDoubleAdaptable(Map map)
    { return isByteAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyBooleanMap ShortKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyBooleans(Map)
     */
    public static boolean isShortKeyBooleanAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyCharMap ShortKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyChars(Map)
     */
    public static boolean isShortKeyCharAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyByteMap ShortKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyBytes(Map)
     */
    public static boolean isShortKeyByteAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyShortMap ShortKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyShorts(Map)
     */
    public static boolean isShortKeyShortAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyIntMap ShortKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyInts(Map)
     */
    public static boolean isShortKeyIntAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyLongMap ShortKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyLongs(Map)
     */
    public static boolean isShortKeyLongAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyFloatMap ShortKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyFloats(Map)
     */
    public static boolean isShortKeyFloatAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with short keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Short Short},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ShortKeyDoubleMap ShortKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asShortKeyDoubles(Map)
     */
    public static boolean isShortKeyDoubleAdaptable(Map map)
    { return isShortAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyBooleanMap IntKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyBooleans(Map)
     */
    public static boolean isIntKeyBooleanAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyCharMap IntKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyChars(Map)
     */
    public static boolean isIntKeyCharAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyByteMap IntKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyBytes(Map)
     */
    public static boolean isIntKeyByteAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyShortMap IntKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyShorts(Map)
     */
    public static boolean isIntKeyShortAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyIntMap IntKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyInts(Map)
     */
    public static boolean isIntKeyIntAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyLongMap IntKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyLongs(Map)
     */
    public static boolean isIntKeyLongAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyFloatMap IntKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyFloats(Map)
     */
    public static boolean isIntKeyFloatAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with int keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Integer Integer},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link IntKeyDoubleMap IntKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asIntKeyDoubles(Map)
     */
    public static boolean isIntKeyDoubleAdaptable(Map map)
    { return isIntAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyBooleanMap LongKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyBooleans(Map)
     */
    public static boolean isLongKeyBooleanAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyCharMap LongKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyChars(Map)
     */
    public static boolean isLongKeyCharAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyByteMap LongKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyBytes(Map)
     */
    public static boolean isLongKeyByteAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyShortMap LongKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyShorts(Map)
     */
    public static boolean isLongKeyShortAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyIntMap LongKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyInts(Map)
     */
    public static boolean isLongKeyIntAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyLongMap LongKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyLongs(Map)
     */
    public static boolean isLongKeyLongAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyFloatMap LongKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyFloats(Map)
     */
    public static boolean isLongKeyFloatAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with long keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Long Long},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link LongKeyDoubleMap LongKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asLongKeyDoubles(Map)
     */
    public static boolean isLongKeyDoubleAdaptable(Map map)
    { return isLongAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyBooleanMap FloatKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyBooleans(Map)
     */
    public static boolean isFloatKeyBooleanAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyCharMap FloatKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyChars(Map)
     */
    public static boolean isFloatKeyCharAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyByteMap FloatKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyBytes(Map)
     */
    public static boolean isFloatKeyByteAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyShortMap FloatKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyShorts(Map)
     */
    public static boolean isFloatKeyShortAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyIntMap FloatKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyInts(Map)
     */
    public static boolean isFloatKeyIntAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyLongMap FloatKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyLongs(Map)
     */
    public static boolean isFloatKeyLongAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyFloatMap FloatKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyFloats(Map)
     */
    public static boolean isFloatKeyFloatAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with float keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Float Float},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link FloatKeyDoubleMap FloatKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asFloatKeyDoubles(Map)
     */
    public static boolean isFloatKeyDoubleAdaptable(Map map)
    { return isFloatAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and boolean values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Boolean Boolean},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyBooleanMap DoubleKeyBooleanMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyBooleans(Map)
     */
    public static boolean isDoubleKeyBooleanAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isBooleanAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and char values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Character Character},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyCharMap DoubleKeyCharMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyChars(Map)
     */
    public static boolean isDoubleKeyCharAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isCharAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and byte values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Byte Byte},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyByteMap DoubleKeyByteMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyBytes(Map)
     */
    public static boolean isDoubleKeyByteAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isByteAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and short values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Short Short},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyShortMap DoubleKeyShortMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyShorts(Map)
     */
    public static boolean isDoubleKeyShortAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isShortAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and int values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Integer Integer},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyIntMap DoubleKeyIntMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyInts(Map)
     */
    public static boolean isDoubleKeyIntAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isIntAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and long values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Long Long},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyLongMap DoubleKeyLongMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyLongs(Map)
     */
    public static boolean isDoubleKeyLongAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isLongAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and float values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Float Float},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyFloatMap DoubleKeyFloatMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyFloats(Map)
     */
    public static boolean isDoubleKeyFloatAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isFloatAdaptable(map.values()); }
    
    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with double keys and double values. For a
     *  map to be adaptable it can only contain
     *  keys of class {@link Double Double},
     *  values of class {@link Double Double},
     *  and no <tt>null</tt> keys or <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link DoubleKeyDoubleMap DoubleKeyDoubleMap};
     *              returns <tt>false</tt> otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @see        #asDoubleKeyDoubles(Map)
     */
    public static boolean isDoubleKeyDoubleAdaptable(Map map)
    { return isDoubleAdaptable(map.keySet()) && isDoubleAdaptable(map.values()); }

    // ---------------------------------------------------------------
    //      isObjectKeyTAdaptable(Map map)
    // ---------------------------------------------------------------

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and boolean values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Boolean Boolean} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyBooleanMap ObjectKeyBooleanMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyBooleanAdaptable(Map map)
    { return isBooleanAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and char values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Character Character} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyCharMap ObjectKeyCharMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyCharAdaptable(Map map)
    { return isCharAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and byte values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Byte Byte} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyByteMap ObjectKeyByteMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyByteAdaptable(Map map)
    { return isByteAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and short values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Short Short} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyShortMap ObjectKeyShortMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyShortAdaptable(Map map)
    { return isShortAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and int values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Integer Integer} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyIntMap ObjectKeyIntMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyIntAdaptable(Map map)
    { return isIntAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and long values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Long Long} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyLongMap ObjectKeyLongMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyLongAdaptable(Map map)
    { return isLongAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and float values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Float Float} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyFloatMap ObjectKeyFloatMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyFloatAdaptable(Map map)
    { return isFloatAdaptable(map.values()); }

    /**
     *  Indicates whether a specified map is adaptable
     *  to a primitive map with object keys and double values. For a
     *  map to be adaptable it can only contain values of class
     *  {@link Double Double} and no <tt>null</tt> values.
     *
     *  @param      map
     *              the map to examine.
     *
     *  @return     <tt>true</tt> if <tt>map</tt> is adaptable to a
     *              {@link ObjectKeyDoubleMap ObjectKeyDoubleMap};
     *              returns false otherwise.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public static boolean isObjectKeyDoubleAdaptable(Map map)
    { return isDoubleAdaptable(map.values()); }

}
