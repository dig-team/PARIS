package paris.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.CombinedIterable;
import javatools.datatypes.FinalSet;
import javatools.filehandlers.FileLines;
import javatools.filehandlers.UTF8Writer;
import javatools.parsers.Char;
import javatools.parsers.NumberParser;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class serves to evaluate class assignments.
 * 
 */

public class SampleClasses {
    
  /** Classes that we exclude*/
  public static final Set<String> exclude=new FinalSet<String>(
      "http://www.opengis.net/gml/_Feature","y:wordnet_physical_entity_100001930",
      "y:wordnet_whole_100003553","y:wordnet_physical_entity_100001930",
      "y:wordnet_object_100002684","owl:Thing",
      "y:wordnet_psychological_feature_100023100",
      "y:wordnet_abstraction_100002137","y:wordnet_living_thing_100004258","y:wordnet_artifact_100021939","y:wordnet_causal_agent_100007347",
      "y:wordnet_attribute_100024264","y:wordnet_group_100031264","y:wordnet_thing_100002452","y:wordnet_communication_100033020",
      "y:wordnet_entity_100001740","y:yagoLegalActorGeo","y:yagoGeoEntity","y:yagoLegalActor");
  
  /** Produces a random subset of a class file, with instance counts for the classes if desired (requires a special count file)
   * @throws IOException */
  public static void sampleClasses(File in, File out, int sampleSize, boolean withInstanceCounts) throws IOException {
    Map<String,Integer> classCounts=new TreeMap<String, Integer>();
    if(withInstanceCounts) {
    for(String line : new FileLines(new File("yagoclasses_star.tsv"),"Loading classes")) {
      String[] split=line.split("\t");
      D.addKeyValue(classCounts, Char.decodeBackslash(split[0]), Integer.parseInt(split[1]));
    }
    }
    Writer w=new UTF8Writer(out);    
    Random random=new Random();
    int total=0;
    int totalClasses=0;
    String prev="";
    int pseudoperiod=(int)in.length()/sampleSize;
    if(pseudoperiod==0) pseudoperiod=1;
    for(String line : new FileLines(in,"UTF8","Selecting classes")) {
      String[] split=line.split("\t");
      if(exclude.contains(split[0]) || exclude.contains(split[1])) continue;
      total++;
      if(!prev.equals(split[0])) totalClasses++;
      prev=split[0];
      if(random.nextInt(pseudoperiod)>0) continue;
      Integer num=classCounts.get(split[0].replace("y:wordnet_","wn_").replace("y:wikicategory_", "wc_"));
      if(num==null) num=classCounts.get(split[1].replace("y:wordnet_","wn_").replace("y:wikicategory_", "wc_"));
      w.write(line+"\t"+num+"\n");
    }
    w.close();
    Announce.message("Total number of classes:",totalClasses);
    Announce.message("Total number of assignments:",total);
  }

  /** Counts number of class assignments above a certain threshold (for all thresholds between 0 and 1)
   * @throws IOException */
  public static void countClassesAboveThresholds(File in) throws IOException {
    int[] totalClasses=new int[10];
    String[] totalClassesPrev=new String[10];
    for(String line : new FileLines(in,"Analyzing classes")) {
      String[] split=line.split("\t");
      if(exclude.contains(split[0]) || exclude.contains(split[1])) continue;
      Double score=new Double(split[2]);
      for(int i=0;i<score*10;i++) {
        if(totalClassesPrev[i]==null || !totalClassesPrev[i].equals(split[0])) totalClasses[i]++;
        totalClassesPrev[i]=split[0];
      }
    }
    Announce.message("Total number of classes assigned with at least score:");
    for(int i=0;i<10;i++) Announce.message(i/10.0,totalClasses[i]);
  }

  /** Counts class assignment precision in a sample file. The sample file should have as first character in every line
   * - 0 for "wrong assignment"
   * - 1 for "good assignment"
   * - "-" for "ignore"
   * - everything else stops the process.*/
  public static void evalClasses(File in) throws Exception {
    Announce.doing("Evaluating classes in",in);
    int[] totalByScore=new int[10];
    int[] correctByScore=new int[10];
    int[] totalByNum=new int[10];
    int[] correctByNum=new int[10];
    for(String line : new FileLines(in)) {
      if(line.startsWith("-")) continue;
      if(line.startsWith("\u00EF")) line=line.substring(3);
      if(!line.startsWith("0") && !line.startsWith("1")) break;
      boolean correct=line.startsWith("1");
      String[] split=line.split("\\t");
      Double score=NumberParser.getDouble(split[2]);
      if(score==null) continue;
      Integer num=split.length<4?1:NumberParser.getInt(split[3]);
      if(num==null) continue;
      for(int i=0;i<score*10 && i<10;i++) {
        totalByScore[i]++;
        if(correct) correctByScore[i]++;
      }
      for(int i=0;i<num/50 && i<10;i++) {
        totalByNum[i]++;
        if(correct) correctByNum[i]++;
      }      
    }
    Announce.doing("Count and precision by score");
    for(int i=0;i<10;i++) {
      Announce.message(i/10.0,totalByScore[i],correctByScore[i]/(double)totalByScore[i]);
    }
    Announce.doneDoing("Count and precision by num instances");
    for(int i=0;i<10;i++) {
      Announce.message(i*50.0,totalByNum[i],correctByNum[i]/(double)totalByNum[i]);
    }    
    Announce.done();
    Announce.done();
  }
  
  /** Counts the instances of the classes in YAGO. Used only once, now OBSOLETE*/
  public static void countInstances() throws Exception{
    final Map<String,Integer> instances=new TreeMap<String, Integer>();
    for(String line : new CombinedIterable<String>(new FileLines(new File("c:/fabian/data/yago/fact/type_star.tsv"),"Parsing star"),new FileLines(new File("c:/fabian/data/yago/fact/type.tsv"),"Parsing type"))) {
      String[] split=line.split("\t");
      String cls=split[2];
      if(cls.startsWith("yago")) continue;
      if(cls.startsWith("wikicategory")) cls="wc_"+cls.substring(13);
      else if(cls.startsWith("wordnet")) cls="wn_"+cls.substring(8);
      D.addKeyValue(instances, cls, 1);
      if(instances.size()>1000000) break;
    }
    Announce.doing("Sorting");
    List<String> classes=new ArrayList<String>(instances.keySet());
    Collections.sort(classes, new Comparator<String>() {

      @Override
      public int compare(String arg0, String arg1) {
        return instances.get(arg1).compareTo(instances.get(arg0));
      }});
    Announce.doneDoing("Writing");
    Writer w=new FileWriter("yagoclasses_star.tsv");
    for(String c : classes) {
      w.write(c+"\t"+instances.get(c)+"\n");
    }
    w.close();
    Announce.done();
  }

  /** Runs whatever you want to run */
  public static void main(String[] args) throws Exception {
    //countClasses();
    //sampleClasses(new File("c:/fabian/data/runvldb/dbpedia/superclasses1.tsv"), new File("c:/fabian/data/runvldb/dbpedia/superclasses1_sample.tsv"),114*300);
    //sampleClasses(new File("c:/fabian/data/runvldb/dbpedia/superclasses2.tsv"), new File("c:/fabian/data/runvldb/dbpedia/superclasses2_sample.tsv"),100*300);
    //sampleClasses(new File("c:/fabian/data/runvldb/imdb/superclasses1.tsv"), new File("c:/fabian/data/runvldb/imdb/superclasses1_sample.tsv"),75*300);
    //sampleClasses(new File("c:/fabian/data/runvldb/imdb/superclasses2.tsv"), new File("c:/fabian/data/runvldb/imdb/superclasses2_sample.tsv"),200*300);
    countClassesAboveThresholds(new File("c:/fabian/data/runvldb/imdb/superclasses1.tsv"));
    //evalClasses(new File("c:/fabian/data/runvldb/imdb/superclasses1_eval.tsv"));
    //countGoodClasses();
  }

}
