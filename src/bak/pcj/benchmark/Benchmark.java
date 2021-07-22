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
 *  This class represents benchmark tests. All benchmarks should 
 *  inherit this class. When writing a benchmark, create public
 *  methods starting with "benchmark" and taking one argument of
 *  class {@link DataSet DataSet}. After invoking each of these
 *  methods, the benchmark runner will read the benchmark's timer
 *  to produce a result. Within such a benchmark method, the timer
 *  should be started when initialization is done and stopped when
 *  the operations are done. The pattern is this:
 *  <pre>
 *      public String benchmarkXXXXX(DataSet dataSet) {
 *          &lt;initialize&gt;
 *          startTimer();
 *          &lt;operations to benchmark&gt;
 *          stopTimer();
 *          &lt;clean up&gt;
 *          return "description of task";
 *      }
 *  </pre>
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/4/1
 *  @since      1.0
 */
public abstract class Benchmark {

    private long startTime;
    private long endTime;

    /**
     *  Starts the timer to measure operations.
     */
    protected void startTimer()
    { startTime = System.currentTimeMillis(); }

    /**
     *  Starts the timer to measure operations.
     */
    protected void stopTimer()
    { endTime = System.currentTimeMillis(); }

    /**
     *  Returns the last timing result. If no timing result is
     *  available, the return value is undefined.
     *
     *  @return     the last timing result.
     */
    public long readTimer()
    { return endTime - startTime; }

    /**
     *  Returns the name of the class that is benchmarked.
     *
     *  @return     the name of the class that is benchmarked.
     */
    public abstract String getClassId();

}
