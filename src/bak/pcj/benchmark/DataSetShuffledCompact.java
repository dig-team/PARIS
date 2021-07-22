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
 *  a range of consecutive int values in shuffled order:
 *  <pre>
 *      shuffle([0 - (size-1)])
 *      shuffle([(size) - (2*size-1)])
 *      shuffle([(2*size) - (3*size-1)])
 *  <pre>
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public class DataSetShuffledCompact extends DataSet {

    /**
     *  Creates a new shuffled compact data set of a specified
     *  size.
     *
     *  @param      size
     *              the size of the data set to create.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>size</tt> is not positive.
     */
    public DataSetShuffledCompact(int size) {
        super("Shuffled/Compact/"+size, size);
    }

    protected int[] createList(int n, int size) {
        int[] s = new int[size];
        int seed;
        switch (n) {
        case 0:
            for (int i = 0; i < size; i++) s[i] = i;
            seed = 107;
            break;
        case 1:
            for (int i = 0; i < size; i++) s[i] = i+size;
            seed = 938361;
            break;
        case 2:
            for (int i = 0; i < size; i++) s[i] = i+2*size;
            seed = 53925253;
            break;
        default:
            throw new IllegalArgumentException();
        }
        shuffle(s, seed);
        return s;
    }

    /**
     *  Shuffles an int list with a specified random seed value.
     *
     *  @param      list
     *              the list to shuffle.
     *
     *  @param      seed
     *              the random seed to use when shuffling.
     *
     *  @throws     NullPointerException
     *              if <tt>list</tt> is <tt>null</tt>.
     */
    private static void shuffle(int[] list, int seed) {
        java.util.Random rnd = new java.util.Random(seed);
        int size = list.length;
        for (int i = 0; i < size; i++) {
            int idx = rnd.nextInt(size);
            int tmp = list[i];
            list[i] = list[idx];
            list[idx] = tmp;
        }
    }

}
