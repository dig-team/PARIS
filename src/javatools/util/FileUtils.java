package javatools.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javatools.filehandlers.FileLines;

/** 
This class is part of the Java Tools (see http://mpii.de/yago-naga/javatools).
It is licensed under the Creative Commons Attribution License 
(see http://creativecommons.org/licenses/by/3.0) by 
the YAGO-NAGA team (see http://mpii.de/yago-naga)

Some utility methods for arrays
*/
public class FileUtils {
  /**
   * Creates a BufferedReader for UTF-8-encoded files
   * 
   * @param file  File in UTF-8 encoding
   * @return      BufferedReader for file
   * @throws FileNotFoundException
   */
  public static BufferedReader getBufferedUTF8Reader(File file) throws FileNotFoundException {
    return new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
  }
  
  /**
   * Creates a BufferedReader for UTF-8-encoded files
   * 
   * @param fileName  Path to file in UTF-8 encoding
   * @return      BufferedReader for file
   * @throws FileNotFoundException
   */
  public static BufferedReader getBufferedUTF8Reader(String fileName) throws FileNotFoundException {
    return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8")));
  }
  
  /**
   * Creates a BufferedReader the UTF-8-encoded InputStream
   * 
   * @param inputStream  InputStream in UTF-8 encoding
   * @return      BufferedReader for inputStream
   */
  public static BufferedReader getBufferedUTF8Reader(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
  }

  /**
   * Creates a BufferedWriter for UTF-8-encoded files
   * 
   * @param file  File in UTF-8 encoding
   * @return      BufferedWriter for file
   * @throws FileNotFoundException
   */
  public static BufferedWriter getBufferedUTF8Writer(File file) throws FileNotFoundException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
  }
  
  /**
   * Creates a BufferedWriter for UTF-8-encoded files
   * 
   * @param fileName  Path to file in UTF-8 encoding
   * @return      BufferedWriter for file
   * @throws FileNotFoundException
   */
  public static BufferedWriter getBufferedUTF8Writer(String fileName) throws FileNotFoundException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("UTF-8")));
  }
    
  /**
   * Verifies that a file is lexicographically order (ascending or descending)
   * 
   * @param check         File to check
   * @param descending    true if ordering should be descending, false if it should be ascending
   * @return              true if file is order, false otherwise
   * @throws IOException
   */
  public static boolean verifyOrderedFile(File check, boolean descending) throws IOException {
    if (check == null || check.isDirectory() || !check.canRead()) {
      System.out.println("Unable to verify sort order of file, make sure it is readable and not a directory");
    }
    
    boolean first = true;
    
    String one = "";
    String two = "";
    
    long lineCount = 0;
    
    for (String line : new FileLines(check, "UTF-8", "Verifying that '" + check + "' is sorted")) {
      lineCount++;
      
      if (first) {
        one = line;
        two = line;
        first = false;
        continue;
      }
      
      one = two;
      two = line;
      
      int comp = two.compareTo(one);
      
      if (!descending && comp < 0) {
        System.out.println("Ascending order violated in line " + lineCount + ": '" + one + "' vs. '" + two + "'");
        return false;
      }
      
      if (descending && comp > 0) {
        System.out.println("Descending order violated in line " + lineCount + ": '" + one + "' vs. '" + two + "'");
        return false;
      }
    }
    
    return true;
  }
  
  public static void main(String[] args) throws IOException {
    verifyOrderedFile(new File(args[0]), true);
  }
}
