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

import bak.pcj.IntCollection;
import bak.pcj.IntIterator;
import bak.pcj.list.IntList;
import bak.pcj.list.IntArrayList;

/**
 *  This class represents an abstract base for implementing benchmarks
 *  for collections of int values.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public abstract class IntCollectionBenchmark extends Benchmark {

    protected abstract IntCollection create(int[] elements);

    protected IntCollection create() {
        return create(new int[]{});
    }

    public String getClassId() {
        Object v = create();
        String name = v.getClass().getName();
        return name;
    }

    public String benchmarkAddExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.add(l[i]);
        stopTimer();
        return l.length + " overwriting calls to add() with " + l.length + " elements";
    }

    public String benchmarkAddNonExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.add(l[i]);
        stopTimer();
        return l.length + " non-overwriting calls to add() with " + l.length + " elements";
    }

    public String benchmarkContainsExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.contains(l[i]);
        stopTimer();
        return l.length + " successful calls to contains() with " + c.size() + " elements";
    }

    public String benchmarkContainsNonExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.contains(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to contains() with " + c.size() + " elements";
    }

    public String benchmarkRemoveExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " successful calls to remove() with " + l.length + " elements";
    }

    public String benchmarkRemoveNonExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to remove() with " + l.length + " elements";
    }

    public String benchmarkIterator(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        startTimer();
        IntIterator it = c.iterator();
        while (it.hasNext()) it.next();
        stopTimer();
        return "Iteration over " + c.size() + " elements";
    }

}
