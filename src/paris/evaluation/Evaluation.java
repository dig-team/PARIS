package paris.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import paris.Config;
import paris.Setting;
import paris.evaluation.GoldStandard.EvalVal;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.filehandlers.FileLines;
import javatools.filehandlers.TSVFile;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * Systematic evaluation of system results in comparison to a gold standard.
 */
public class Evaluation {

	public static class IterationResults {
		File iteration;
		double entitiesPrecision;
		double entitiesRecall;
		double relationsPrecision;
		
		public IterationResults(File iteration, double entitiesPrecision, double entitiesRecall, double relationsPrecision) {
			this.iteration = iteration;
			this.entitiesPrecision = entitiesPrecision;
			this.entitiesRecall = entitiesRecall;
			this.relationsPrecision = relationsPrecision;
		}
		
		public void print() {
			Announce.message(iteration, entitiesPrecision, entitiesRecall, relationsPrecision);
		}
	}
	
  /** Returns: Number of entities, number of correctly assigned entities, total number of assignemnts.
   * Ties take the first assignment.*/
  public static List<Integer> evaluate(File eqvFile, GoldStandard gold) throws IOException {
    int numCorrectAssignments = 0;
    int numAssignments = 0;
    int numTotalAssignments = 0;
    String lastEntity = "";
    int printWrong = 0;
    Announce.doing("Evaluating", eqvFile);
    for (String line : new FileLines(eqvFile)) {
      String[] split = line.split("\t");
      if (split.length < 3) continue;
      try {
        String yagoentity = split[0];
        if (yagoentity.equals(lastEntity)) continue;
        String dbpediaentity = split[1];
        lastEntity = yagoentity;
        numTotalAssignments++;
        switch (gold.evaluate(yagoentity, dbpediaentity)) {
          case CORRECT:
            if (printWrong++ < 20) Announce.message("Correct:", yagoentity, dbpediaentity);
            numCorrectAssignments++;
            numAssignments++;
            break;
          case WRONG:
            if (printWrong++ < 20) Announce.message("Wrong:", yagoentity, dbpediaentity);
            numAssignments++;
            break;
          case DONTKNOW:
            break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Announce.done();
    return (Arrays.asList(numAssignments, numCorrectAssignments, numTotalAssignments));
  }

  /** Returns the matched subrelations corresponding to an eqv.tsv file*/
  public static Map<String, String> relations(File f) throws IOException {
    Map<String, String> result = new TreeMap<String, String>();
    Map<String, Double> assignment = new TreeMap<String, Double>();
    try {
      for (String rep : new String[] { "superrelations1", "superrelations2" }) {
        for (List<String> line : new TSVFile(new File(f.getParent(), f.getName().replace("eqv", rep)))) {
          double val = Double.parseDouble(line.get(2));
          if(val<Config.THETA) continue;
          if (D.getOrZeroDouble(assignment, line.get(0)) > val) continue;
          assignment.put(line.get(0), val);
          result.put(line.get(0), line.get(1));
        }
      }
    } catch (Exception e) {
      result.put("ERROR:", e.getMessage());
    }
    return (result);
  }

  /** Evaluates one file*/
  public static IterationResults handle(File file, GoldStandard gold) throws IOException {
    Announce.doing("Checking", file);
    List<Integer> result = evaluate(file, gold);
    Announce.message("Number of entities matched:", result.get(2));
    Announce.message("Number of goldstandard entities matched:", result.get(0));
    Announce.message("Number of goldstandard entities:", gold.numGoldStandard());
    Announce.message("Number of correctly matched goldstandard entities:", result.get(1));
    double prec = result.get(1) / (double) result.get(0);
    Announce.message("Precision:", prec);
    double rec = result.get(1) / (double) gold.numGoldStandard();
    Announce.message("Recall:", rec);
    Announce.message("F-Measure:", 2 * prec * rec / (prec + rec));
    Announce.message("Relations computed from that:");
    int correct = 0;
    int wrong = 0;
    int dunno = 0;
    for (Map.Entry<String, String> rel : relations(file).entrySet()) {
      EvalVal eval = gold.evaluateRelation(rel.getKey(), rel.getValue());
      Announce.message("   ", rel, eval);
      if (eval == EvalVal.CORRECT) correct++;
      else if (eval == EvalVal.WRONG) wrong++;
      else dunno++;
    }
    Announce.message("  Correct:", correct);
    Announce.message("  Wrong:", wrong);
    Announce.message("  Dunno:", dunno);
    double relPrec = correct / (double) (correct + wrong);
    Announce.message("  Precision:", relPrec);
    Announce.message("  Recall:", correct / (double) gold.numGoldStandardRelations());
    Announce.message("  Assignments:", correct +wrong);
    Announce.message("  Total:", correct +wrong + dunno);
    Announce.done();
    return new IterationResults(file, prec, rec, relPrec);
  }

  /** Handles all result files in a given folder*/
  public static List<IterationResults> handleAll(File folder, GoldStandard gold) throws IOException {
    List<File> files = Arrays.asList(folder.listFiles());
    List<IterationResults> results = new LinkedList<IterationResults>();
    Collections.sort(files);
    for (File f : files) {
      if(f.getName().endsWith("eqv.tsv")) {
      	IterationResults result = handle(f, gold);
      	results.add(result);
      }
    }
    return results;
  }

  /** Handles all files for a setting*/
  public static List<IterationResults> handle(Setting setting) throws IOException {
    Announce.setWriter(new FileWriter(setting.home + "/eval.txt"));
    return handleAll(setting.tsvFolder, setting.gold);
  }

  /** Runs the evaluation*/
  public static void main(String[] args) throws Exception {
    if(args==null || args.length != 2) Announce.help(
    		"Evaluation <directory> <goldStandard>\n",
    		"Evaluate the DBpedia-YAGO TSV files in <directory> according to <goldStandard> possible correct matchings\n",
    		"See README for indications on how to compute the gold standard");
    GoldStandard custom = new GoldStandard(new Integer(args[1]),GoldStandard.yagoDbpediaRelations);
  	List<IterationResults> results = handleAll(new File(args[0]), custom);
  	for (IterationResults r : results) {
  		r.print();
  	}
  	Announce.close();
  }

}
