package paris.evaluation;

import java.io.File;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import javatools.administrative.Announce;
import javatools.filehandlers.FileLines;
import javatools.filehandlers.FileSet;
import javatools.filehandlers.UTF8Writer;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class is OBSOLETE. The new computations avoid duplicates automatically.
 */
public class RemoveDupsFromOutput {
 
  /** Removes duplicate assignments from a result file*/
  public static void removeDups(File in, File out) throws Exception {
    if(out.equals(in)) out=FileSet.newExtension(out, "tsv2");
    Announce.doing("Removing dups from",in);
    Map<String,Double> targetWithMax=new TreeMap<String, Double>();
    for(String line : new FileLines(in,"UTF8","Loading maxes")) {
      String[] split=line.split("\t");
      if(split.length!=3) {
        //Announce.message("Invalid line:",line);
        continue;
      }
      Double val=Double.parseDouble(split[2]);
      if(!targetWithMax.containsKey(split[1]) || targetWithMax.get(split[1])<val) targetWithMax.put(split[1], val);
    }
    Announce.message(targetWithMax.size(),"mappings loaded, e.g.",targetWithMax.entrySet().iterator().next());
    Writer w=new UTF8Writer(out);
    for(String line : new FileLines(in,"UTF8","Writing maxes")) {
      String[] split=line.split("\t");
      if(split.length!=3) {
        //Announce.message("Invalid line:",line);
        continue;
      }
      Double val=Double.parseDouble(split[2]);
      if(targetWithMax.containsKey(split[1]) && targetWithMax.get(split[1]).equals(val)) {
        targetWithMax.remove(split[1]);
        w.write(line+"\n");
      }
    }
    w.close();
    Announce.done();
  }
  
  /** Guess what, this removes the dups, babe! */
  public static void main(String[] args) throws Exception {
    //removeDups(new File("c:/fabian/data/runVLDB/imdb/naiveMatch_eqv.tsv"), new File("c:/fabian/data/runVLDB/imdb/naiveMatch_eqv_nodup.tsv"));
    //removeDups(new File("c:/fabian/data/runBDA/0_eqv.tsv"), new File("c:/fabian/data/runBDA/0_eqv_nodup.tsv"));
    //removeDups(new File("c:/fabian/data/runBDA/1_eqv.tsv"), new File("c:/fabian/data/runBDA/1_eqv_nodup.tsv"));
    removeDups(new File("c:/fabian/data/runBDA/2_eqv.tsv"), new File("c:/fabian/data/runBDA/2_eqv_nodup.tsv"));
    removeDups(new File("c:/fabian/data/runBDA/3_eqv.tsv"), new File("c:/fabian/data/runBDA/3_eqv_nodup.tsv"));
}
}
