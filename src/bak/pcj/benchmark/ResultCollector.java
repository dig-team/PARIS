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

import java.io.Reader;
import java.io.FileReader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  This class represents a collector of benchmark results. The purpose
 *  of the class is to collect results from different benchmarks in
 *  one report. This class may be run from the command line.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.0     2003/5/1
 *  @since      1.0
 */
public class ResultCollector {

    /** The report in which results are collected. */
    private Report report;

    /**
     *  Creates a new result collector on a specified report.
     *  All collected results will be added to the report.
     *
     *  @param      report
     *              the report in which to collect the results.
     *
     *  @throws     NullPointerException
     *              if <tt>report</tt> is <tt>null</tt>.
     */
    public ResultCollector(Report report) {
        if (report == null)
            throw new NullPointerException();
        this.report = report;
    }

    /**
     *  Creates a new result collector on a new report.
     */
    public ResultCollector() {
        this(new Report());
    }

    /**
     *  Collects the results of a report from a specified reader.
     *  The reader should deliver data on the form output by
     *  reports.
     *
     *  @param      in
     *              the reader from which to collect the results.
     *
     *  @see        Report#writeResults(Writer out)
     *  @see        #collect(String)
     */
    public void collect(Reader in) throws IOException {
        report.readResults(in);
    }

    /**
     *  Collects the results of a report from a specified file.
     *  The file should contain data on the form output by
     *  reports.
     *
     *  @param      filename
     *              the name of the file from which to collect 
     *              the results.
     *
     *  @see        Report#writeResults(Writer out)
     *  @see        #collect(Reader)
     */
    public void collect(String filename) throws IOException {
        collect(new FileReader(filename));
    }

    /**
     *  Returns the report in which the results are collected.
     *
     *  @return     the report in which the results are collected.
     */
    public Report getReport() {
        return report;
    }

    private static void printUsageAndExit(Throwable e) {
        System.err.println("Usage: bak.pcj.benchmark.ResultCollector <output file> <report title> [<results files>] ");
        if (e != null) {
            System.err.println("An exception was raised:");
            e.printStackTrace();
        }
        System.exit(1);
    }

    /**
     *  Runs a result collector on a set of files and formats a report as HTML.
     *  The first argument is the name of a file on which to write the report.
     *  The second argument is a title for the report.
     *  The following arguments are names of result files as output by reports.
     *
     *  @param      args
     *              as specified above.
     */
    public static void main(String[] args) {
        if (args.length < 2)
            printUsageAndExit(null);
        try {
            ResultCollector c = new ResultCollector();
            Report report = c.getReport();
            report.putProperty("report.title", args[1]);
            report.putProperty("benchmark.time", (new java.util.Date()).toString());
            report.putProperty("java.version", System.getProperty("java.version"));
            report.putProperty("java.vendor", System.getProperty("java.vendor"));
            report.putProperty("java.vm.specification.version", System.getProperty("java.vm.specification.version"));
            report.putProperty("java.vm.specification.vendor", System.getProperty("java.vm.specification.vendor"));
            report.putProperty("java.vm.specification.name", System.getProperty("java.vm.specification.name"));
            report.putProperty("java.vm.version", System.getProperty("java.vm.version"));
            report.putProperty("java.vm.vendor", System.getProperty("java.vm.vendor"));
            report.putProperty("java.vm.name", System.getProperty("java.vm.name"));
            report.putProperty("java.specification.version", System.getProperty("java.specification.version"));
            report.putProperty("java.specification.vendor", System.getProperty("java.specification.vendor"));
            report.putProperty("java.specification.name", System.getProperty("java.specification.name"));
            report.putProperty("java.compiler", System.getProperty("java.compiler"));
            report.putProperty("os.name", System.getProperty("os.name"));
            report.putProperty("os.arch", System.getProperty("os.arch"));
            report.putProperty("os.version", System.getProperty("os.version"));

            for (int i = 2; i < args.length; i++)
                c.collect(args[i]);
            Writer out = new FileWriter(args[0]);
            c.report.writeHTML(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            printUsageAndExit(e);
        }
    }

}
