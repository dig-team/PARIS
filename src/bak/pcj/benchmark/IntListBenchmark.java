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
import bak.pcj.list.IntList;

/**
 *  This class represents benchmark tests for lists of int values.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public class IntListBenchmark extends IntCollectionBenchmark {

    private static final int SMALL_SIZE = 2000;

    private IntListFactory factory;

    public IntListBenchmark(IntListFactory factory) {
        this.factory = factory;
    }

    protected IntCollection create(int[] elements)
    { return factory.create(elements); }

    // ---------------------------------------------------------------
    //      Overriden methods
    // ---------------------------------------------------------------

    public String benchmarkContainsExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++)
            c.contains(l[i % l.length]);
        stopTimer();
        return SMALL_SIZE + " successful calls to contains() with " + c.size() + " elements";
    }

    public String benchmarkContainsNonExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++)
            c.contains(l[i % l.length]);
        stopTimer();
        return SMALL_SIZE + " unsuccessful calls to contains() with " + c.size() + " elements";
    }

    public String benchmarkRemoveExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(0);
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++)
            c.remove(l[i % l.length]);
        stopTimer();
        return SMALL_SIZE + " successful calls to remove() with " + l.length + " existing elements";
    }

    public String benchmarkRemoveNonExisting(DataSet dataSet) {
        IntCollection c = create(dataSet.get(0));
        int[] l = dataSet.get(1);
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++)
            c.remove(l[i % l.length]);
        stopTimer();
        return SMALL_SIZE + " unsuccessful calls to remove() with " + l.length + " existing elements";
    }

    // ---------------------------------------------------------------
    //      List methods
    // ---------------------------------------------------------------

    public String benchmarkAddMiddle(DataSet dataSet) {
        IntList c = (IntList)create(dataSet.get(0));
        int[] l = dataSet.get(0);
        int size = l.length;
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++) {
            c.add(size/2, l[i % l.length]);
            size++;
        }
        stopTimer();
        return SMALL_SIZE + " calls to add(int,int) at middle of list with " + l.length + " existing elements";
    }

    public String benchmarkAddBeginning(DataSet dataSet) {
        IntList c = (IntList)create(dataSet.get(0));
        int[] l = dataSet.get(0);
        int size = l.length;
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++) {
            c.add(0, l[i % l.length]);
            size++;
        }
        stopTimer();
        return SMALL_SIZE + " calls to add(int,int) at beginning of list with " + l.length + " existing elements";
    }

    public String benchmarkRemoveMiddle(DataSet dataSet) {
        IntList c = (IntList)create(dataSet.get(0));
        int[] l = dataSet.get(0);
        int size = l.length;
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++) {
            if (size == 0) break;
            c.removeElementAt(size/2);
            size--;
        }
        stopTimer();
        return SMALL_SIZE + " calls to removeElementAt(int) at middle of list with " + l.length + " existing elements";
    }

    public String benchmarkRemoveBeginning(DataSet dataSet) {
        IntList c = (IntList)create(dataSet.get(0));
        int[] l = dataSet.get(0);
        int size = l.length;
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++) {
            if (size == 0) break;
            c.removeElementAt(0);
            size--;
        }
        stopTimer();
        return SMALL_SIZE + " calls to removeElementAt(int) at beginning of list with " + l.length + " existing elements";
    }

    public String benchmarkRemoveEnd(DataSet dataSet) {
        IntList c = (IntList)create(dataSet.get(0));
        int[] l = dataSet.get(0);
        int size = l.length;
        startTimer();
        for (int i = 0; i < SMALL_SIZE; i++) {
            if (size == 0) break;
            c.removeElementAt(size-1);
            size--;
        }
        stopTimer();
        return SMALL_SIZE + " calls to removeElementAt(int) at end of list with " + l.length + " existing elements";
    }

}
