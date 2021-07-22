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

import bak.pcj.map.IntKeyIntMap;
import bak.pcj.map.IntKeyIntMapIterator;

/**
 *  This class represents an abstract base for implementing benchmarks
 *  for maps of int values.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public abstract class IntKeyIntMapBenchmark extends Benchmark {

    private IntKeyIntMapFactory factory;

    public IntKeyIntMapBenchmark(IntKeyIntMapFactory factory) {
        this.factory = factory;
    }

    protected IntKeyIntMap create(int[] keys, int[] values) {
        return factory.create(keys, values);
    }

    protected IntKeyIntMap create() {
        return create(new int[]{}, new int[]{});
    }

    public String getClassId() {
        Object v = create();
        String name = v.getClass().getName();
        return name;
    }

    public String benchmarkPutExisting(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        int[] l = dataSet.get(0);
        int[] k = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.put(l[i], k[i]);
        stopTimer();
        return l.length + " overwriting calls to put() with " + l.length + " mappings";
    }

    public String benchmarkPutNonExisting(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.put(l[i], l[i]);
        stopTimer();
        return l.length + " non-overwriting calls to put() with " + l.length + " mappings";
    }

    public String benchmarkGetExisting(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.get(l[i]);
        stopTimer();
        return l.length + " successful calls to get() with " + c.size() + " mappings";
    }

    public String benchmarkGetNonExisting(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.get(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to get() with " + c.size() + " mappings";
    }

    public String benchmarkRemoveExisting(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " successful calls to remove() with " + l.length + " mappings";
    }

    public String benchmarkRemoveNonExisting(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < l.length; i++)
            c.remove(l[i]);
        stopTimer();
        return l.length + " unsuccessful calls to remove() with " + l.length + " mappings";
    }

    public String benchmarkIterator(DataSet dataSet) {
        IntKeyIntMap c = create(dataSet.get(0), dataSet.get(0));
        startTimer();
        IntKeyIntMapIterator it = c.entries();
        while (it.hasNext()) {
            it.next();
            it.getKey();
            it.getValue();
        }
        stopTimer();
        return "Iteration over " + c.size() + " mappings";
    }

}
