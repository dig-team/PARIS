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
 *  This class represents data sets for benchmarks. A data set contains
 *  three lists of equal length. Each list has only distinct values
 *  and the lists are all disjoint.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public abstract class DataSet {

    private int[][] data;
    private Integer[][] dataObjects;
    private String id;
    private int size;

    /**
     *  Creates a new data set with a specified id and size.
     *
     *  @param      id
     *              the identifier of the new data set. Only used for
     *              formatting a report.
     *
     *  @param      size
     *              the size of the lists in the new data set.
     *
     *  @throws     NullPointerException
     *              if <tt>id</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>size</tt> is not positive.
     */
    protected DataSet(String id, int size) {
        if (id == null)
            throw new NullPointerException();
        if (size <= 0)
            throw new IllegalArgumentException();
        this.id = id;
        this.size = size;
        data = new int[3][];
        dataObjects = new Integer[3][];
    }

    /**
     *  Returns a specified list of this data set.
     *
     *  @param      n
     *              the index of the list to return.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>n</tt> is negative or greater than <tt>2</tt>.
     */
    public int[] get(int n) {
        if (data[n] == null)
            data[n] = createList(n, size);
        return data[n];
    }

    /**
     *  Returns a specified list of this data set as objects.
     *
     *  @param      n
     *              the index of the list to return.
     *
     *  @throws     IndexOutOfBoundsException
     *              if <tt>n</tt> is negative or greater than <tt>2</tt>.
     */
    public Integer[] getObjects(int n) {
        if (dataObjects[n] == null) {
            if (data[n] == null)
                data[n] = createList(n, size);
            dataObjects[n] = new Integer[data[n].length];
            for (int i = 0; i < data[n].length; i++)
                dataObjects[n][i] = new Integer(data[n][i]);
        }
        return dataObjects[n];
    }

    /**
     *  Returns an identifier for this data set.
     *
     *  @return     an identifier for this data set.
     */
    public String getId() {
        return id;
    }

    /**
     *  Creates the list with the specified number.
     *
     *  @param      n
     *              the number of the list to create (0-2).
     *
     *  @param      size
     *              the size of the list to create.
     *
     *  @return     data list number <tt>n</tt> with size <tt>size</tt>.
     */
    protected abstract int[] createList(int n, int size);

}
