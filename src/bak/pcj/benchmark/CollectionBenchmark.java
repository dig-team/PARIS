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


import java.util.Collection;
import java.util.Iterator;

/**
 *  This class represents an abstract base for implementing benchmarks
 *  for collections of {@link Integer Integer} values.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/4/1
 *  @since      1.0
 */
public abstract class CollectionBenchmark extends Benchmark {

    protected abstract Collection create(Integer[] elements);

    protected Collection create() {
        return create(new Integer[]{});
    }

    public String getClassId() {
        Object v = create();
        String name = v.getClass().getName();
        return name;
    }

    public String benchmarkAddExisting(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.add(l[i]);
        stopTimer();
        return l.length + " overwriting calls to add() with " + l.length + " elements";
    }

    public String benchmarkAddNonExisting(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.add(l[i]);
        stopTimer();
        return l.length + " non-overwriting calls to add() with " + l.length + " elements";
    }

    public String benchmarkContainsExisting(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.contains(l[i]);
        stopTimer();
        return l.length + " successful calls to contains() with " + c.size() + " elements";
    }

    public String benchmarkContainsNonExisting(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.contains(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to contains() with " + c.size() + " elements";
    }

    public String benchmarkRemoveExisting(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " successful calls to remove() with " + l.length + " elements";
    }

    public String benchmarkRemoveNonExisting(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to remove() with " + l.length + " elements";
    }

    public String benchmarkIterator(DataSet dataSet) {
        Collection c = create(dataSet.getObjects(0));
        startTimer();
        Iterator it = c.iterator();
        while (it.hasNext()) it.next();
        stopTimer();
        return "Iteration over " + c.size() + " elements";
    }

}
