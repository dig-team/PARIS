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

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import java.io.Writer;
import java.io.Reader;
import java.io.IOException;

/**
 *  This class represents reports of results from benchmarks.
 *  Results are collected in a report that can finally be
 *  transformed to some external representation.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.1     2003/15/2
 *  @since      1.0
 */
public class Report {

    /** The results collected in the report. */
    private List results;

    /** Properties like time, machine, OS, VM, etc. */
    private Map properties;

    /**
     *  Creates a new report for benchmark results.
     */
    public Report() {
        results = new ArrayList();
        properties = new TreeMap();
    }

    /**
     *  Adds a result to this report.
     *
     *  @param      result
     *              the result to add.
     *
     *  @throws     NullPointerException
     *              if <tt>result</tt> is <tt>null</tt>.
     */
    public void addResult(Result result) {
        if (result == null)
            throw new NullPointerException();
        results.add(result);
    }

    /**
     *  Returns the results of this report in no particular 
     *  order.
     *
     *  @return     an unmodifiable collection of the results
     *              of this report in no particular order.
     */
    public Collection getResults() {
        return Collections.unmodifiableCollection(results);
    }

    /**
     *  Clears the results of this report.
     */
    public void clearResults() {
        results.clear();
    }


    /**
     *  Adds a property to this report.
     *
     *  @param      key
     *              the key of the property.
     *
     *  @param      value
     *              the value of the property. If the value is 
     *              <tt>null</tt>, the property is removed from
     *              the report.
     *
     *  @throws     NullPointerException
     *              if <tt>key</tt> is <tt>null</tt>.
     */
    public void putProperty(String key, String value) {
        if (key == null)
            throw new NullPointerException();
        if (value == null)
            properties.remove(key);
        else
            properties.put(key, value);
    }

    /**
     *  Returns a property of this report.
     *
     *  @param      key
     *              the key of the property to return.
     *
     *  @return     the value of the property with the specified
     *              key; returns <tt>null</tt> if no such property
     *              is in this report.
     *
     *  @throws     NullPointerException
     *              if <tt>key</tt> is <tt>null</tt>.
     */
    public String getProperty(String key) {
        if (key == null)
            throw new NullPointerException();
        return (String)properties.get(key);
    }

    // ---------------------------------------------------------------
    //      Report input/output
    // ---------------------------------------------------------------

    private String readLine(Reader in) throws IOException {
        StringBuffer s = new StringBuffer();
        int c;
        while ((c = in.read()) != -1 && c != '\n')
            s.append((char)c);
        if (s.length() == 0 && c == -1)
            return null;
        return s.toString();
    }

    private static String[] split(String s, char c) {
        ArrayList a = new ArrayList();
        int p = 0;
        int len = s.length();
        while (p < len) {
            if (a.size() == 0)
                a.add("");
            char pc = s.charAt(p);
            if (pc == c)
                a.add("");
            else
                a.set(a.size()-1, ((String)a.get(a.size()-1))+pc);
            p++;
        }
        String[] sa = new String[a.size()];
        a.toArray(sa);
        return sa;
    }

    /**
     *  Reads results into this report from a specified reader.
     *
     *  @param      in
     *              the reader from which to read results.
     *
     *  @throws     IOException
     *              if an error occurs reading from <tt>out</tt>.
     */
    public void readResults(Reader in) throws IOException {
        String s;
        int ptr, nptr, len;
        while ((s = readLine(in)) != null) {
            String[] fields = split(s, ';');
            Result result = new Result(
                fields[0],  //  benchmark id
                fields[1],  //  data set id
                fields[2],  //  class id
                fields[3],  //  task id
                fields[4],  //  task description
                Long.parseLong(fields[5])   //  time
            );
            addResult(result);
        }
    }

    /**
     *  Writes the results of this report to a specified writer.
     *
     *  @param      out
     *              the writer on which to write the results.
     *
     *  @throws     IOException
     *              if an error occurs writing to <tt>out</tt>.
     */
    public void writeResults(Writer out) throws IOException {
        Iterator i = results.iterator();
        while (i.hasNext()) {
            Result result = (Result)i.next();
            out.write(result.getBenchmarkId());
            out.write(';');
            out.write(result.getDataSetId());
            out.write(';');
            out.write(result.getClassId());
            out.write(';');
            out.write(result.getTaskId());
            out.write(';');
            out.write(result.getTaskDescription());
            out.write(';');
            out.write(String.valueOf(result.getTime()));
            out.write('\n');
        }
    }

    // ---------------------------------------------------------------
    //      Formatting
    // ---------------------------------------------------------------

    private static String stylesheet = 
        "body {\n" +
        "  margin-left: 2em;\n" +
        "  margin-right: 2em;\n" +
        "}\n" +
        "\n" +
        "h1, h2, h3, caption, th {\n" +
        "    font-family: helvetica, arial, verdana;\n" +
        "}\n" +
        "\n" +
        "h1 {\n" +
        "    font-size: 24pt;\n" +
        "    font-weight: bold;\n" +
        "}\n" +
        "\n" +
        "h2 {\n" +
        "    font-size: 16pt;\n" +
        "}\n" +
        "\n" +
        "thead {\n" +
        "    background-color: #CCCCFF;\n" +
        "}\n" +
        "";

    /**
     *  Formats this report as HTML on a specified writer.
     *
     *  @param      out
     *              the writer on which to format this report.
     *
     *  @throws     NullPointerException
     *              if <tt>out</tt> is <tt>null</tt>.
     *
     *  @throws     IOException
     *              if an error occurs writing to <tt>out</tt>.
     */
    public void writeHTML(Writer out) throws IOException {

        Set sortedResults = new TreeSet(new ResultComparator());
        sortedResults.addAll(results);
        String title = "PCJ Benchmark Results";
        if (getProperty("report.title") != null)
            title += " - " + getProperty("report.title");

        out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n");
        out.write("\"http://www.w3.org/TR/html4/strict.dtd\">\n");
        out.write("<html lang=\"en\">\n");

        out.write("<head>\n");
        out.write("  <title>" + title + "</title>\n");
        out.write("  <style>\n");
        out.write(stylesheet);
        out.write("  </style>\n");
        out.write("</head>\n");

        out.write("<body>\n");
        out.write("<h1>" + title + "</h1>\n");

        out.write("<table frame=\"border\" rules=\"groups\" cellspacing=\"0\" cellpadding=\"4\">\n");
        Iterator i = properties.entrySet().iterator();
        out.write("  <thead>\n");
        out.write("  <tr>\n");
        out.write("    ");
        out.write("<th>Property</th>");
        out.write("<th>Value</th>\n");
        out.write("  </tr>\n");
        out.write("  </thead>\n");
        out.write("  <tbody>\n");
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            String s = String.valueOf(e.getKey());
            out.write("  <tr>\n");
            out.write("    ");
            out.write("<td align=\"left\">"+String.valueOf(e.getKey())+"</td>");
            out.write("<td align=\"left\">"+String.valueOf(e.getValue())+"</td>");
            out.write("\n");
            out.write("  </tr>\n");
        }
        out.write("  </tbody>\n");
        out.write("</table>\n");

        String lastBenchmarkId = null;
        String lastClassId = null;
        String lastTaskId = null;
        String lastTaskDescription = null;
        boolean firstClass = true;
        boolean firstTask = true;

        Iterator ri = sortedResults.iterator();
        while (ri.hasNext()) {
            Result r = (Result)ri.next();

            if (!r.getClassId().equals(lastClassId)) {
                if (!firstClass) {
                    out.write("  </tbody>\n");
                    out.write("</table>\n");
                }
                if (!r.getBenchmarkId().equals(lastBenchmarkId)) {
                    out.write("<h1>Benchmark: " + r.getBenchmarkId() + "</h1>\n");
                }
                String cid = r.getClassId();
                String link;
                if (cid.startsWith("bak.pcj.")) {
                    String url = "../api/" + cid.replace('.', '/') + ".html";
                    link = "<a target=\"_blank\" href=\""+url+"\" title=\"API: "+cid+"\">" + cid + "</a>";
                } else if (cid.startsWith("java.")) {
                    String url = "http://java.sun.com/j2se/1.4/docs/api/" + cid.replace('.', '/') + ".html";
                    link = "<a target=\"_blank\" href=\""+url+"\" title=\"API: "+cid+"\">" + cid + "</a>";
                } else
                    link = cid;
                out.write("<h2>Class: " + link + "</h2>\n");
                out.write("<table frame=\"border\" rules=\"groups\" cellspacing=\"0\" cellpadding=\"4\">\n");

                out.write("  <thead>\n");
                out.write("  <tr>\n");
                out.write("    ");
                out.write("<th>Task</th>");
                out.write("<th>Description</th>");
                out.write("<th>Data set</th>");
                out.write("<th>Time (ms)</th>");
                out.write("\n");
                out.write("  </tr>\n");
                out.write("  </thead>\n");
                firstTask = true;
            } else {
                if (!r.getBenchmarkId().equals(lastBenchmarkId)) {
                    out.write("<h1>Benchmark: " + r.getBenchmarkId() + "</h1>\n");
                }
            }

            String taskIdHeading;
            if (!r.getTaskId().equals(lastTaskId)) {
                taskIdHeading = r.getTaskId();
                if (!firstTask)
                    out.write("  </tbody>\n");
                out.write("  <tbody>\n");
            } else {
                taskIdHeading = "";
            }

            String taskDescription;
            if (!r.getTaskDescription().equals(lastTaskDescription))
                taskDescription = r.getTaskDescription();
            else
                taskDescription = "";

            String dataSetIdHeading = r.getDataSetId();

            out.write("  <tr>\n");
            out.write("    ");
            out.write("<td align=\"left\" valign=\"top\">"+taskIdHeading+"</td>");
            out.write("<td align=\"left\" valign=\"top\">"+taskDescription+"</td>");
            out.write("<td align=\"left\" valign=\"top\">"+dataSetIdHeading+"</td>");
            out.write("<td align=\"right\" valign=\"top\">"+String.valueOf(r.getTime())+"</td>");
            out.write("\n");
            out.write("  </tr>\n");

            lastBenchmarkId = r.getBenchmarkId();
            lastClassId = r.getClassId();
            lastTaskId = r.getTaskId();
            lastTaskDescription = r.getTaskDescription();
            firstClass = false;
            firstTask = false;
        }
        out.write("  </tbody>\n");
        out.write("</table>\n");
        out.write("</body>\n");
        out.write("</html>\n");
    }

}
