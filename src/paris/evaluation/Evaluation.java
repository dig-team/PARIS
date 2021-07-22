package paris.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import paris.Config;
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

	static boolean doCurve;

	public static class IterationResults {
		File iteration;
		double entitiesPrecision;
		double entitiesRecall;
		/** Precision of the relation alignment from factstore 1 to factstore 2 */
		double relationsPrecision0;
		/** Precision of the relation alignment from factstore 2 to factstore 1 */
		double relationsPrecision1;
		
		public IterationResults(File iteration, double entitiesPrecision, double entitiesRecall, double relationsPrecision0, double relationsPrecision1) {
			this.iteration = iteration;
			this.entitiesPrecision = entitiesPrecision;
			this.entitiesRecall = entitiesRecall;
			this.relationsPrecision0 = relationsPrecision0;
			this.relationsPrecision1 = relationsPrecision1;
		}
		
		public void print() {
			Announce.message(iteration, entitiesPrecision, entitiesRecall, relationsPrecision0, relationsPrecision1);
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
    double lastScore = 42;
    for (String line : new FileLines(eqvFile)) {
      String[] split = line.split("\t");
      if (split.length < 3) continue;
      try {
        String yagoentity = split[0];
        if (yagoentity.equals(lastEntity)) continue;
        String dbpediaentity = split[1];
        double score = Double.parseDouble(split[2]);
        if (doCurve) {
        	if (/*lastScore != score && */numAssignments > 0) {
        		Announce.message("@PR", lastScore, numCorrectAssignments, numAssignments, ((double) numCorrectAssignments)/numAssignments,
        				((double) numCorrectAssignments)/gold.numGoldStandard);
        	}
        }
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
        lastScore = score;
        
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Announce.done();
    return (Arrays.asList(numAssignments, numCorrectAssignments, numTotalAssignments));
  }

  /** Returns the matched subrelations corresponding to an eqv.tsv file*/
  public static Map<String, String> relations(File f, String suffix) throws IOException {
    Map<String, String> result = new TreeMap<String, String>();
    Map<String, Double> assignment = new TreeMap<String, Double>();
  	TSVFile tsvFile = null;
    try {
    	tsvFile = new TSVFile(new File(f.getParent(), f.getName().replace("eqv", suffix)));
    } catch (FileNotFoundException e) {
    	return result;
    }
    for (List<String> line : tsvFile) {
    	String sub = line.get(0);
    	String supr = line.get(1);
    	// normalize pairs to pairs where the first relation is positive
    	if (sub.endsWith("-")) {
    		// remove the '-'
    		sub = sub.replaceAll("-", "");
    		// now invert the other relation
    		if (supr.endsWith("-")) {
    			supr = supr.replaceAll("-", "");
    		} else {
    			supr = supr.concat("-");
    		}
    	}
      double val = Double.parseDouble(line.get(2));
      if(val<Config.THETA) continue;
      if (D.getOrZeroDouble(assignment, sub) > val) continue;
      assignment.put(sub, val);
      result.put(sub, supr);
    }
    tsvFile.close();
    return (result);
  }

  public static String sanitizeRelation(String key, String defaultPrefix) {
    // TODO hack for the refactored code
    if (!key.contains("/") && !key.contains(":"))
    	key = defaultPrefix + key;
    key = Config.compress(key);
    if (key.startsWith("dbp:") && !key.contains("ontology/")) {
    	key = "dbp:ontology/" + key.substring("dbp:".length(), key.length());
    }
    if (key.startsWith("<") && key.endsWith(">"))
    	key = key.substring(1, key.length() - 1);
    return key;
  }
  /** Evaluates one file*/
  public static IterationResults handle(File file, GoldStandard gold, Map<String, Integer>[] occurrences) throws IOException {
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
    int[] correct = {0, 0};
    int[] wrong = {0, 0};
    int[] dunno = {0, 0};
    int[] weightedCorrect = {0, 0};
    int[] weightedWrong = {0, 0};
    int[] weightedDunno = {0, 0};
    for (int direction : new int[] {0, 1}) {
	    for (Map.Entry<String, String> rel : relations(file, "superrelations" + (direction + 1)).entrySet()) {
	    	String key = sanitizeRelation(rel.getKey(), direction == 0?"y:":"imdb:");
	    	String value = sanitizeRelation(rel.getValue(), direction == 0?"imdb:":"y:");
	    	if (key.contains(">")) {
	    		continue; // skip join relations
	    	}
	      EvalVal eval = gold.evaluateRelation(key, value);
	      int nbOcc = occurrences[direction].get(key);
	      Announce.message("@RELATION", direction, key, value, eval, nbOcc);
	      if (eval == EvalVal.CORRECT) { 
	      	correct[direction]++;
	      	weightedCorrect[direction] += nbOcc;
	      }
	      else if (eval == EvalVal.WRONG) {
	      	wrong[direction]++;
	      	weightedWrong[direction] += nbOcc;
	      }
	      else { 
	      	dunno[direction]++;
	      	weightedDunno[direction] += nbOcc;
	      }
	    }
    }
    Announce.message("  Correct:", correct[0], correct[1]);
    Announce.message("  Wrong:", wrong[0], wrong[1]);
    Announce.message("  Dunno:", dunno[0], dunno[1]);
    double relPrec0 = correct[0] / (double) (correct[0] + wrong[0]);
    double relPrec1 = correct[1] / (double) (correct[1] + wrong[1]);
    double wRelPrec0 = weightedCorrect[0] / (double) (weightedCorrect[0] + weightedWrong[0]);
    double wRelPrec1 = weightedCorrect[1] / (double) (weightedCorrect[1] + weightedWrong[1]);
    Announce.message("  Precision (superrelations1):", relPrec0);
    Announce.message("  Precision (superrelations2):", relPrec1);
    Announce.message("  Weighted precision (superrelations1):", wRelPrec0);
    Announce.message("  Weighted precision (superrelations2):", wRelPrec1);
    // does not make much sense Announce.message("  Recall:", correct / (double) gold.numGoldStandardRelations());
    Announce.message("  Assignments (superrelations1):", correct[0] + wrong[0]);
    Announce.message("  Assignments (superrelations2):", correct[1] + wrong[1]);
    Announce.message("  Total (superrelations1):", correct[0] + wrong[0] + dunno[0]);
    Announce.message("  Total (superrelations2):", correct[1] + wrong[1] + dunno[1]);
    Announce.done();
    return new IterationResults(file, prec, rec, wRelPrec0, wRelPrec1);
  }

  /** Handles all result files in a given folder*/
  public static List<IterationResults> handleAll(File folder, GoldStandard gold, Map<String, Integer>[] occurrences) throws IOException {
    List<File> files = Arrays.asList(folder.listFiles());
    List<IterationResults> results = new LinkedList<IterationResults>();
    Collections.sort(files);
    for (File f : files) {
      if(f.getName().endsWith("eqv.tsv")) {
      	IterationResults result = handle(f, gold, occurrences);
      	results.add(result);
      }
    }
    return results;
  }

//  /** Handles all files for a setting*/
//  public static List<IterationResults> handle(Setting setting) throws IOException {
//    Announce.setWriter(new FileWriter(setting.home + "/eval.txt"));
//    return handleAll(setting.tsvFolder, setting.gold, occurrences);
//  }
  
  public static Map<String, Integer> readCountFromFile(File f) throws Exception {
  	Map<String, Integer> result = new HashMap<String, Integer>();
  	FileLines lines = new FileLines(f);
  	for (String line : lines) {
      String[] split = line.split(" ");
      if (split.length != 2) {
      	lines.close();
      	throw new Exception(); // illegal line format
      }
      split[0] = Config.compress("<" + split[0] + ">");
      if (split[0].startsWith("<") && split[0].endsWith(">"))
      	split[0] = split[0].substring(1, split[0].length() - 1);
      Announce.message(split[0]);
      result.put(split[0], Integer.parseInt(split[1]));
  	}
  	lines.close();
  	return result;
  }
  
  /** Runs the evaluation*/
  public static void main(String[] args) throws Exception {
    if(args==null || args.length != 5) Announce.help(
    		"Evaluation <directory> <goldStandardType> <goldStandardSize> <occurrences1> <occurrences2>\n",
    		"Evaluate the DBpedia-YAGO TSV files in <directory> according to <goldStandard> possible correct matchings\n",
    		"Look in <occurrences1> and <occurrences2> to find a count of the number of occurrences of each relation in factStores 1 and 2\n",
    		"(format: \"RELATION COUNT\\n\")\n",
    		"See README for indications on how to compute the goldStandardSize value");
    GoldStandard custom = null;
    if (args[1].equals("yago-dbpedia") || args[1].equals("yago-dbpedia-curve")) {
      custom = new GoldStandard(new Integer(args[2]),GoldStandard.yagoDbpediaRelations);
      if (args[1].equals("yago-dbpedia-curve")) {
      	doCurve = true;
      }
    } else if (args[1].equals("yago-imdb")) {
      custom = new GoldImdbYago();    	
    } else {
    	Announce.message("unknown task", args[1]);
    	throw new Exception();
    }
    Map<String, Integer> count1 = readCountFromFile(new File(args[3]));
    Map<String, Integer> count2 = readCountFromFile(new File(args[4]));
    
  	@SuppressWarnings("unchecked")
		List<IterationResults> results = handleAll(new File(args[0]), custom, new Map[] {count1, count2} );
  	for (IterationResults r : results) {
  		r.print();
  	}
  	Announce.close();
  }

}
