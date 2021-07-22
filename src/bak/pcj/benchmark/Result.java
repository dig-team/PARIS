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
 *  This class represents results from benchmarks.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/4/1
 *  @since      1.0
 */
public class Result {

    /** The time registered for this result. */
    private long time;

    /** The benchmark identifier of this result. */
    private String benchmarkId;

    /** The task identifier of this result. */
    private String taskId;

    /** The description of the task of this result. */
    private String taskDescription;

    /** The data set identifier of this result. */
    private String dataSetId;

    /** The class identifier of this result. */
    private String classId;

    /**
     *  Creates a new result.
     *
     *  @param      benchmarkId
     *              an identifier of the benchmark that produced the
     *              result. Typically the class name.
     *
     *  @param      dataSetId
     *              an identifier of the data set against which the 
     *              benchmark was run.
     *
     *  @param      classId
     *              an identifier of the class that is benchmarked.
     *              Typically the class name.
     *
     *  @param      taskId
     *              an identifier of the task that was measured. Typically
     *              the name of a method in a benchmark.
     *
     *  @param      taskDescription
     *              a description of the task.
     *
     *  @param      time
     *              the time measured for completing the task.
     *
     *  @throws     NullPointerException
     *              if <tt>benchmarkId</tt> is <tt>null</tt>;
     *              if <tt>dataSetId</tt> is <tt>null</tt>;
     *              if <tt>classId</tt> is <tt>null</tt>;
     *              if <tt>taskId</tt> is <tt>null</tt>;
     *              if <tt>taskDescription</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalArgumentException
     *              if <tt>time</tt> is negative.
     */
    public Result(String benchmarkId, String dataSetId, String classId, String taskId, String taskDescription, long time) {
        if (benchmarkId == null || dataSetId == null || classId == null || taskId == null || taskDescription == null)
            throw new NullPointerException();
        if (time < 0L)
            throw new IllegalArgumentException();
        this.benchmarkId = benchmarkId;
        this.taskId = taskId;
        this.dataSetId = dataSetId;
        this.classId = classId;
        this.taskDescription = taskDescription;
        this.time = time;
    }

    /**
     *  Returns the benchmark identifier of this result.
     *
     *  @return     the benchmark identifier of this result.
     */
    public String getBenchmarkId()
    { return benchmarkId; }
    
    /**
     *  Returns the task identifier of this result.
     *
     *  @return     the task identifier of this result.
     */
    public String getTaskId()
    { return taskId; }

    /**
     *  Returns the description of the task of this result.
     *
     *  @return     the description of the task of this result.
     */
    public String getTaskDescription()
    { return taskDescription; }
    
    /**
     *  Returns the data set identifier of this result.
     *
     *  @return     the data set identifier of this result.
     */
    public String getDataSetId()
    { return dataSetId; }
    
    /**
     *  Returns the class identifier of this result.
     *
     *  @return     the class identifier of this result.
     */
    public String getClassId()
    { return classId; }

    /**
     *  Returns the time registered for this result.
     *
     *  @return     the time registered for this result.
     */
    public long getTime()
    { return time; }


}
