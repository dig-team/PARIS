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

import bak.pcj.map.IntKeyMap;

/**
 *  This interface represents factories of maps of int keys. It
 *  is used with the {@link IntKeyMapBenchmark IntKeyMapBenchmark} class
 *  for producing map objects.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/8/1
 *  @since      1.0
 */
public interface IntKeyMapFactory {

    /**
     *  Creates a new instance of a map from int key to objects initially
     *  containing the specified elements.
     *
     *  @param      keys
     *              the keys that the resulting set will
     *              contain.
     *
     *  @param      values
     *              the values that the resulting set will
     *              contain.
     *
     *  @return     a map of int keys/values containing the specified
     *              elements.
     *
     *  @throws     NullPointerException
     *              if <tt>elements</tt> is <tt>null</tt>.
     */
    IntKeyMap create(int[] keys, Integer[] values);

}
