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

import java.util.LinkedList;    // Workaround for bug in Javadoc 1.3.

/**
 *  This interface represents deques of double values. Deques are lists
 *  that have specialized (and efficient) methods for adding and 
 *  removing elements from the beginning and end.
 *
 *  @see        java.util.LinkedList
 *  @see        DoubleStack
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     2003/15/2
 *  @since      1.0
 */
public interface DoubleDeque extends DoubleList {

    /**
     *  Returns the first element of this deque.
     *
     *  @return     the first element of this deque.
     *
     *  @throws     IndexOutOfBoundsException
     *              if this deque is empty.
     */
    double getFirst();

    /**
     *  Returns the last element of this deque.
     *
     *  @return     the first element of this deque.
     *
     *  @throws     IndexOutOfBoundsException
     *              if this deque is empty.
     */
    double getLast();

    /**
     *  Removes the first element of this deque.
     *
     *  @return     the element that was removed.
     *
     *  @throws     IndexOutOfBoundsException
     *              if this deque is empty.
     */
    double removeFirst();

    /**
     *  Removes the last element of this deque.
     *
     *  @return     the element that was removed.
     *
     *  @throws     IndexOutOfBoundsException
     *              if this deque is empty.
     */
    double removeLast();

    /**
     *  Adds an element to the beginning of this deque.
     *
     *  @param      v
     *              the element to add to this deque.
     */
    void addFirst(double v);

    /**
     *  Adds an element to the end of this deque.
     *
     *  @param      v
     *              the element to add to this deque.
     */
    void addLast(double v);

}
