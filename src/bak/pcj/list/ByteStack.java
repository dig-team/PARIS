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

import java.util.Stack;    // Workaround for bug in Javadoc 1.3.

/**
 *  This interface represents stacks of byte values.
 *
 *  @see        java.util.Stack
 *  @see        ByteDeque
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     2003/15/2
 *  @since      1.0
 */
public interface ByteStack extends ByteList {

    /**
     *  Pushes a specified element onto this stack.
     *
     *  @param      v
     *              the element to push onto this stack.
     */
    void push(byte v);

    /**
     *  Pops an element off this stack.
     *
     *  @return     the element that was popped off this stack.
     *
     *  @throws     IndexOutOfBoundsException
     *              if the stack is empty.
     *
     *  @see        #peek()
     */
    byte pop();

    /**
     *  Returns the top element of this stack.
     *
     *  @return     the top element of this stack.
     *
     *  @throws     IndexOutOfBoundsException
     *              if the stack is empty.
     *
     *  @see        #pop()
     */
    byte peek();

}
