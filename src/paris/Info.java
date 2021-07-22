package paris;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import javatools.administrative.D;
import javatools.filehandlers.FileLines;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class contains run-once-then-forget methods.
 * 
 * The main method is useful, though. It allows printing information about entities 
 * in the ontologies.

 */
public class Info {

//  /** Prints some info from some fact store*/
//  public static void printFactStoreInfo() throws Exception {
//    FactStore factStore = new FactStore(new File("/media/ssd/fabian/data/dbpedia/berkeley"));
//    factStore.printFactsAbout("dbp:resource/Lippstadt");
//    factStore.close();
//  }

//  /** Counts how many entities have how many facts*/
//  public static void countDBpediaFacts() throws Exception {
//    FactStore f = new FactStore(Setting.yagodbpedia.ontology2);
//    Map<Integer, Integer> numFacts2numEntities=new TreeMap<Integer, Integer>();
//    Announce.progressStart("Counting facts",f.numEntities());
//    int entities=0;
//    int facts=0;
//    for(Integer entity : f.entities()) {
//      Announce.progressStep();
//      entities++;
//      int num=PeekIterator.numElements(f.factsAbout(entity));
//      facts+=num;
//      D.addKeyValue(numFacts2numEntities, num, 1);
//    }
//    Announce.progressDone();
//    Announce.doing("Histogram of <number of outgoing links> : <number of entities>");
//    for(Entry<Integer,Integer> e : numFacts2numEntities.entrySet()) {
//      Announce.message(e.getKey(),":",e.getValue());
//    }
//    Announce.done();
//    Announce.message("Entities:",entities);
//    Announce.message("Facts:",facts);
//    f.close();
//  }

  /** Counts the predicates in DBpedia*/
  public static void countPredicatesDBpedia(File f) throws Exception {
    Map<String, Integer> count = new TreeMap<String, Integer>();
    //int c=0;
    for (String line : new FileLines(f, "Parsing")) {
      //if(c++>1000) break;
      String pred = line.split(" ")[1];
      D.addKeyValue(count, pred, 1);
    }
    Writer w = new FileWriter("c:/fabian/data/dbpedia/predicates.tsv");
    for (String pred : D.sorted(count)) {
      w.write(pred + "\t" + count.get(pred) + "\n");
    }
    w.close();
  }

//  /** Counts the intersection of YAGO and DBpedia */
//  public static void intersectionYagoDBpedia() throws Exception {
//    FactStore yago=new FactStore(Setting.yagodbpedia.ontology1);
//    FactStore dbpedia=new FactStore(Setting.yagodbpedia.ontology2);
//    int[] intersection=new int[10];
//    Announce.progressStart("Counting Yago/DBpedia intersection", dbpedia.numEntities());
//    for(Integer entity : dbpedia.entities()) {
//      String name=dbpedia.toString(entity);
//      if(name.contains("ontology")) continue;
//      name=Config.stripQuotes(name.replace("dbp:resource/", "y:"));
//      if(yago.entity(name)!=null) {
//        int numFacts=PeekIterator.numElements(dbpedia.factsAbout(entity));
//        for(int i=0;i<=numFacts && i<intersection.length;i++) intersection[i]++;
//      }
//      Announce.progressStep();
//    }
//    Announce.progressDone();
//    yago.close();
//    dbpedia.close();
//    Announce.doing("Intersection @ numFacts:");
//    for(int i=0;i<intersection.length;i++) {
//      Announce.message(i,":",intersection[i]);
//    }
//    Announce.done();
//  }
  
//  /** Computes Precision and recall of the Yago/DBpedia alignment for instances with more than 10 facts in DBpedia*/
//  public static void computePrecRec10() throws Exception {
//    Paris.test=true;
//    Setting setting=Setting.yagodbpedia;
//    FactStore yago=new FactStore(setting.ontology1);
//    FactStore dbpedia=new FactStore(setting.ontology2);
//    Result computed = new Result(yago, dbpedia, setting.berkeleyFolder, setting.tsvFolder, false);
//    Announce.progressStart("Counting Yago/DBpedia prec/rec @ 10", dbpedia.numEntities());
//    int totalMapped=0;
//    int correctlyMapped=0;
//    int totalShouldBeMapped=0;
//    for(Integer entity : dbpedia.entities()) {
//      Announce.progressStep();
//      String name=dbpedia.toString(entity);
//      if(!name.startsWith("dbp:resource/")) continue;
//      int numFacts=PeekIterator.numElements(dbpedia.factsAbout(entity));
//      if(numFacts<10) continue;
//      name=Config.stripQuotes(name.replace("dbp:resource/", "y:"));
//      Entity goldtargetentity=yago.entity(name);
//      Integer goldtarget=goldtargetentity==null?null:goldtargetentity.id;
//      Integer mappedTo=D.pick(computed.equalTo(dbpedia, entity));
//      if(mappedTo!=null) totalMapped++;
//      if(goldtarget!=null) totalShouldBeMapped++;
//      if(goldtarget!=null && mappedTo!=null && goldtarget.equals(mappedTo)) correctlyMapped++;
//    }
//    Announce.progressDone();
//    Announce.message("Total mapped:",totalMapped);
//    Announce.message("Total correctly mapped:",correctlyMapped);
//    Announce.message("Total should be mapped:",totalShouldBeMapped);
//    Announce.message("Precision:",correctlyMapped/(double)totalMapped);
//    Announce.message("Recall:",correctlyMapped/(double)totalShouldBeMapped);    
//    computed.close();
//    yago.close();
//    dbpedia.close();    
//  }
  
//  /** Prints information about the ontologies*/
//  public static void main(String[] args) throws Exception {    
//    FactStore factStore1 = new FactStore(Setting.imdbyago.ontology1);
//    FactStore factStore2 = new FactStore(Setting.imdbyago.ontology2);
//    while (true) {
//      D.p("\n-----------------------\nEnter the name of an entity:");
//      String entity = System.console().readLine();
//      if (entity == null || entity.length() == 0) break;
//      factStore1.printFactsAbout(entity);
//      factStore2.printFactsAbout(entity);
//    }
//    factStore1.close();
//    factStore2.close();
//  }
}
