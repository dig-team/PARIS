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
package bak.pcj.benchmark;

import bak.pcj.set.IntSet;

/**
 *  This interface represents factories of sets of int values. It
 *  is used with the {@link IntSetBenchmark IntSetBenchmark} class
 *  for producing set objects.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/4/1
 *  @since      1.0
 */
public interface IntSetFactory {

    /**
     *  Creates a new instance of a set of int values initially
     *  containing the specified elements.
     *
     *  @param      elements
     *              the elements that the resulting set will
     *              contain.
     *
     *  @return     a set of int values containing the specified
     *              elements.
     *
     *  @throws     NullPointerException
     *              if <tt>elements</tt> is <tt>null</tt>.
     */
    IntSet create(int[] elements);

}
