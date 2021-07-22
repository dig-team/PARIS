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

import java.util.Map;
import java.util.Iterator;

/**
 *  This class represents an abstract base for implementing benchmarks
 *  for maps.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public abstract class MapBenchmark extends Benchmark {

    protected abstract Map create(Integer[] keys, Integer[] values);

    protected Map create() {
        return create(new Integer[]{}, new Integer[]{});
    }

    public String getClassId() {
        Object v = create();
        String name = v.getClass().getName();
        return name;
    }

    public String benchmarkPutExisting(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(0);
        Integer[] k = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.put(l[i], k[i]);
        stopTimer();
        return l.length + " overwriting calls to put() with " + l.length + " mappings";
    }

    public String benchmarkPutNonExisting(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.put(l[i], l[i]);
        stopTimer();
        return l.length + " non-overwriting calls to put() with " + l.length + " mappings";
    }

    public String benchmarkGetExisting(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.get(l[i]);
        stopTimer();
        return l.length + " successful calls to get() with " + c.size() + " mappings";
    }

    public String benchmarkGetNonExisting(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.get(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to get() with " + c.size() + " mappings";
    }

    public String benchmarkRemoveExisting(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " successful calls to remove() with " + l.length + " mappings";
    }

    public String benchmarkRemoveNonExisting(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        Integer[] l = dataSet.getObjects(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to remove() with " + l.length + " mappings";
    }

    public String benchmarkIterator(DataSet dataSet) {
        Map c = create(dataSet.getObjects(0), dataSet.getObjects(0));
        startTimer();
        Iterator it = c.entrySet().iterator();
        while (it.hasNext()) {
            it.next();
            Map.Entry e = (Map.Entry)it.next();
            e.getKey();
            e.getValue();
        }
        stopTimer();
        return "Iteration over " + c.size() + " mappings";
    }

}
