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
package bak.pcj.list;

import bak.pcj.FloatCollection;

/**
 *  This class represents an array implemenation of stacks of
 *  float values.
 *
 *  @see        java.util.ArrayList
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     2003/3/3
 *  @since      1.0
 */
public class FloatArrayStack extends FloatArrayList implements FloatStack {

    /**
     *  Creates a new array stack with capacity 10 and a relative
     *  growth factor of 1.0.
     *
     *  @see        #FloatArrayStack(int,double)
     */
    public FloatArrayStack() {
        super();
    }

    /**
     *  Creates a new array stack with the same elements as a
     *  specified collection. The elements of the specified collection
     *  are pushed in the collection's iteration order.
     *
     *  @param      c
     *              the collection whose elements to add to the new
     *              stack.
     *
     *  @throws     NullPointerException
     *              if <tt>c</tt> is <tt>null</tt>.
     */
    public FloatArrayStack(FloatCollection c) {
        super(c);
    }

    /**
     *  Creates a new array stack with the same elements as a
     *  specified array. The elements of the specified array
     *  are pushed in the order of the array.
     *
     *  @param      a
     *              the array whose elements to add to the new
     *              stack.
     *
     *  @throws     NullPointerException
     *              if <tt>a</tt> is <tt>null</tt>.
     *
     *  @since      1.1
     */
    public FloatArrayStack(float[] a) {
        super(a);
    }

    /**
     *  Creates a new array stack with a specified capacity and a
     *  relative growth factor of 1.0.
     *
     *  @param      capacity
     *              the initial capacity of the stack.
     *
     *  @see        #FloatArrayStack(int,double)
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative.
     */
    public FloatArrayStack(int capacity) {
        super(capacity);
    }

    /**
     *  Creates a new array stack with a specified capacity and
     *  relative growth factor.
     *
     *  <p>The array capacity increases to <tt>capacity()*(1+growthFactor)</tt>.
     *  This strategy is good for avoiding many capacity increases, but
     *  the amount of wasted memory is approximately the size of the stack.
     *
     *  @param      capacity
     *              the initial capacity of the stack.
     *
     *  @param      growthFactor
     *              the relative amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>growthFactor</tt> is negative.
     */
    public FloatArrayStack(int capacity, double growthFactor) {
        super(capacity, growthFactor);
    }

    /**
     *  Creates a new array stack with a specified capacity and
     *  absolute growth factor.
     *
     *  <p>The array capacity increases to <tt>capacity()+growthChunk</tt>.
     *  This strategy is good for avoiding wasting memory. However, an
     *  overhead is potentially introduced by frequent capacity increases.
     *
     *  @param      capacity
     *              the initial capacity of the stack.
     *
     *  @param      growthChunk
     *              the absolute amount with which to increase the
     *              the capacity when a capacity increase is needed.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>capacity</tt> is negative;
     *              if <tt>growthChunk</tt> is negative.
     */
    public FloatArrayStack(int capacity, int growthChunk) {
        super(capacity, growthChunk);
    }

    public void push(float v)
    { add(v); }

    public float pop()
    { return removeElementAt(size()-1); }

    public float peek()
    { return get(size()-1); }

}
