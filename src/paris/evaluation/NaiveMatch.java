package paris.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import paris.FactStore;
import paris.Setting;

import javatools.administrative.Announce;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class matches two ontologies naively by just looking at the labels.
 * It does amazingly well. But we're better :-)
 */

public class NaiveMatch {
  
  /** Match naively*/
  public static void matchNaive(Setting setting, File result) throws Exception {
    Announce.doing("Computing naive equality");
    BufferedWriter out=new BufferedWriter(new FileWriter(result));
    FactStore ontology1=new FactStore(setting.ontology1);
    FactStore ontology2=new FactStore(setting.ontology2);
    Integer label1=ontology1.entity("rdfs:label").id;
    Integer label2=ontology2.entity("rdfs:label").id;
    Announce.progressStart("Computing", ontology1.numEntities());
    for(Integer ent1 : ontology1.entities()) {
      String ent1name=ontology1.toString(ent1);
      Announce.progressStep();
      for(Object name : ontology1.arg2ForRelationAndArg1(label1, ent1)) {
        for(Object ent2: ontology2.arg1ForRelationAndArg2(label2, name)) {
          String ent2name=ontology2.toString(ent2);
          out.write(ent1name+"\t"+ent2name+"\t1.0\n");
        }
      }
    }
    Announce.progressDone();
    ontology1.close();
    ontology2.close();
    Announce.done();
    out.close();
  }
  
  /** Match naively*/
  public static void main(String[] args) throws Exception {
    matchNaive(Setting.imdbyago,new File("/media/ssd/fabian/data/eqvtsv/naive.txt"));
  }
}
