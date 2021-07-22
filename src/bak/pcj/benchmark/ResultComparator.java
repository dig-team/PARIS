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

/**
 *  This class represents comparators of results from benchmarks.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/4/1
 *  @since      1.0
 */
class ResultComparator implements java.util.Comparator {

    /**
     *  Compares two {@link Result Result} objects for order. Results
     *  are ordered by benchmark id, class id, task id, and
     *  then data set id.
     *
     *  @return     <tt>-1</tt> if <tt>o1 &lt; o2</tt>; returns
     *              <tt>0</tt> if <tt>o1 == o2</tt>; returns
     *              <tt>1</tt> if <tt>o1 &gt; o2</tt>;
     *
     *  @throws     NullPointerException
     *              if <tt>o1</tt> is <tt>null</tt>;
     *              if <tt>o2</tt> is <tt>null</tt>.
     *
     *  @throws     ClassCastException
     *              if <tt>o1</tt> is not of class {@link Result Result};
     *              if <tt>o2</tt> is not of class {@link Result Result}.
     */
    public int compare(Object o1, Object o2) {
        Result r1 = (Result)o1;
        Result r2 = (Result)o2;

        int cBenchmarkId = r1.getBenchmarkId().compareTo(r2.getBenchmarkId());
        if (cBenchmarkId < 0)
            return -1;
        if (cBenchmarkId > 0)
            return 1;

        int cClassId = r1.getClassId().compareTo(r2.getClassId());
        if (cClassId < 0)
            return -1;
        if (cClassId > 0)
            return 1;
        
        int cTaskId = r1.getTaskId().compareTo(r2.getTaskId());
        if (cTaskId < 0)
            return -1;
        if (cTaskId > 0)
            return 1;

        int cDataSetId = r1.getDataSetId().compareTo(r2.getDataSetId());
        if (cDataSetId < 0)
            return -1;
        if (cDataSetId > 0)
            return 1;
        return 0;

    }

}
