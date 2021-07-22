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

import bak.pcj.list.IntList;

/**
 *  This class represents a standard data set for benchmarks.
 *  The data set contains three disjoint lists each containing
 *  a range of consecutive int values in order:
 *  <pre>
 *      [0 - (size-1)]
 *      [(size) - (2*size-1)]
 *      [(2*size) - (3*size-1)]
 *  <pre>
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public class DataSetOrderedCompact extends DataSet {

    /**
     *  Creates a new ordered compact data set of a specified
     *  size.
     *
     *  @param      size
     *              the size of the data set to create.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>size</tt> is not positive.
     */
    public DataSetOrderedCompact(int size) {
        super("Ordered/Compact/"+size, size);
    }

    protected int[] createList(int n, int size) {
        int[] s = new int[size];
        switch (n) {
        case 0:
            for (int i = 0; i < size; i++) s[i] = i;
            break;
        case 1:
            for (int i = 0; i < size; i++) s[i] = i+size;
            break;
        case 2:
            for (int i = 0; i < size; i++) s[i] = i+2*size;
            break;
        default:
            throw new IllegalArgumentException();
        }
        return s;
    }

}
