package paris;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import paris.Config.LiteralDistance;
import paris.shingling.QueryResult;
import paris.shingling.ShinglingTable;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.Pair;
import javatools.datatypes.Triple;
import javatools.parsers.DateParser;
import javatools.parsers.NumberParser;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class holds all information that is computed in one run of PARIS: 
 * subclass, equality, subproperty*/

public class Result implements Closeable {

  // -----------------------------------------------------------------
  //             Stores
  // -----------------------------------------------------------------

  /** First fact store*/
  protected FactStore factStore1;

  /** Second fact store*/
  protected FactStore factStore2;

  /** Stores equalities. "sub" is factStore1, "super" is factStore2*/
  public SubThingStore equalityStore;

  /** Maps classes of 1 to super classes of 2*/
  public SubThingStore superClassesOf1;

  /** Maps classes of 2 to super classes of 1*/
  public SubThingStore superClassesOf2;

  /** Maps relations of 1 to super relations of 2*/
  public SubThingStore superRelationsOf1;

  /** Maps relations of 2 to super relations of 1*/
  public SubThingStore superRelationsOf2;

  /** TSV File folder*/
  public final File tsvFolder;

  /** Berkeley File folder*/
  public final File berkeleyFolder;
  
  boolean allowWrite;
    
  // -----------------------------------------------------------------
  //             Constructor
  // -----------------------------------------------------------------

  /** Constructor*/
  public Result(FactStore fs1, FactStore fs2, File folder, File tsvfolder, boolean allowWrite) {
    Announce.doing("Creating computing environment");
    this.allowWrite = allowWrite;
    this.berkeleyFolder = folder;
    this.tsvFolder=tsvfolder;
    
    //EnvironmentConfig envConfig = new EnvironmentConfig();
    // Do some memory management: everybody gets 1/3 of 60% of the total available memory
    //envConfig.setCachePercent(20);
    //envConfig.setAllowCreate(allowWrite);
    //envConfig.setReadOnly(!allowWrite);
    //environment = new Environment(berkeleyFolder=folder, envConfig);
    
    equalityStore = new MemorySubThingStore();
    superClassesOf1 = new MemorySubThingStore();
    superClassesOf2 = new MemorySubThingStore();
    superRelationsOf1 = new MemorySubThingStore();
    superRelationsOf2 = new MemorySubThingStore();
    factStore1 = fs1;
    factStore2 = fs2;
    Announce.done();
    print();
  }
    
  
  @Override
  public void close() throws IOException {
    Announce.doing("Closing stores");
    equalityStore.close();
    superClassesOf1.close();
    superClassesOf2.close();
    superRelationsOf1.close();
    superRelationsOf2.close();
    /*if(!Paris.test) environment.cleanLog();
    environment.close();*/
    Announce.done();
  }

  /** Sets the TSV writers to new files*/
  public void startIteration(int iteration) throws IOException {
    Announce.doing("Creating tsv files",iteration,"in",tsvFolder);
    equalityStore.setTSVfile(new File(tsvFolder,iteration+"_eqv.tsv"));
    superClassesOf1.setTSVfile(new File(tsvFolder,iteration+"_superclasses1.tsv"));
    superClassesOf2.setTSVfile(new File(tsvFolder,iteration+"_superclasses2.tsv"));
    superRelationsOf1.setTSVfile(new File(tsvFolder,iteration+"_superrelations1.tsv"));
    superRelationsOf2.setTSVfile(new File(tsvFolder,iteration+"_superrelations2.tsv"));
    Announce.done();
  }

  // -----------------------------------------------------------------
  //             Equality
  // -----------------------------------------------------------------

  /** Returns the equality. */
  public double equality(FactStore fs1, Object s1, Object s2) {
    if (s1 instanceof String) {
      if (s1.equals(s2)) return (1);
      if (!(s2 instanceof String)) return (0);
      //if(Config.literalDistance==Config.LiteralDistance.IDENTITY && !s1.equals(s2)) return(0);
      switch (Config.entityType(s1.toString())) {
        case NUMBER:
          if (Config.entityType(s2.toString()) == Config.EntityType.STRING) return (dateCompare(s1.toString(), s2.toString()));
          return (numCompare(s1.toString(), s2.toString()));
        case STRING:
          return (stringCompare(s1.toString(), s2.toString()));
        case DATE:
          return (dateCompare(s1.toString(), s2.toString()));
      }
    }
    if (s1 instanceof Integer) {
      if (!(s2 instanceof Integer)) return (0);
      if (fs1 == factStore1) return (equalityStore.getValue((Integer) s1, (Integer) s2));
      return (equalityStore.getValue((Integer) s2, (Integer) s1));
    }
    return (-1);
  }

  /** Says to whom you are equal*/
  public Collection<? extends Object> equalTo(FactStore fs, Object x1) {
    if((x1 instanceof Integer) && Config.takeMax) return(equalToMax(fs,(Integer)x1));
    if (x1 instanceof Integer) return (equalTo(fs, (Integer) x1));
    return (literalEqualTo(fs, x1));
  }

  /** Says to whom you are equal with scores*/
  public Collection<Pair<Object,Double>> equalToScored(FactStore fs, Object x1) {
    if(Config.takeMax) return(equalToScoredMax(fs,x1));
    if (x1 instanceof Integer) return (equalToScored(fs, (Integer) x1));
    return (literalEqualToScored(fs, x1));
  }

  /** Says to whom you are equal with scores*/
  public Collection<Pair<Object,Double>> equalToScoredMax(FactStore fs, Object x1) {
    if (x1 instanceof Integer) return (equalToScoredMax(fs, (Integer) x1));
    return (literalEqualToScored(fs, x1));
  }

  /** Says to whom you are equal*/
  public Collection<Integer> equalTo(FactStore fs, Integer x1) {
    if(Config.takeMax) return(equalToMax(fs,x1));
    if (fs == factStore1) return (equalityStore.superOf((Integer) x1));
    return (equalityStore.subOf((Integer) x1));
  }

  /** Says to whom you are maximally equal*/
  public Set<Integer> equalToMax(FactStore fs, Integer x1) {
    Set<Integer> result=new TreeSet<Integer>();
    double max=-1;
    for(Pair<Object,Double> eq : trueEqualToScored(fs,x1)) {
      if(!(eq.first() instanceof Integer)) continue;
      if(eq.second()>max) {
        result.clear();
        max=eq.second();        
      }
      if(eq.second()==max) {
        if(!Config.takeMaxMax || result.isEmpty()) result.add((Integer)eq.first());
      }
    }
    return(result);
  }

  /** Says to whom you are maximally equal*/
  public Collection<Pair<Object,Double>> equalToScoredMax(FactStore fs, Integer x1) {
    Set<Pair<Object,Double>> result=new HashSet<Pair<Object,Double>>();
    double max=-1;
    for(Pair<Object,Double> eq : trueEqualToScored(fs,x1)) {
      if(!(eq.first() instanceof Integer)) continue;
      if(eq.second()>max) {
        result.clear();
        max=eq.second();        
      }
      if(eq.second()==max) {
        if(!Config.takeMaxMax || result.isEmpty()) result.add(new Pair<Object,Double>((Integer)eq.first(),max));
      }
    }
    return(result);
  }

  /** Says to whom you are equal with score*/
  public Collection<Pair<Object,Double>> equalToScored(FactStore fs, Integer x1) {
    if(Config.takeMax) return(equalToScoredMax(fs,x1));
    if (fs == factStore1) return (equalityStore.superOfScored((Integer) x1));
    return (equalityStore.subOfScored((Integer) x1));
  }

  /** Says to whom you are equal with score*/
  protected Collection<Pair<Object,Double>> trueEqualToScored(FactStore fs, Integer x1) {
    if (fs == factStore1) return (equalityStore.superOfScored((Integer) x1));
    return (equalityStore.subOfScored((Integer) x1));
  }

  /** Says to whom you are equal as a literal that exists in the other ontology*/
  public Collection<Object> literalEqualTo(FactStore fs, Object x1) {
    if (fs == factStore1 && !factStore2.exists(x1)) return (Collections.emptyList());
    if (fs == factStore2 && !factStore1.exists(x1)) return (Collections.emptyList());
    return (Arrays.asList(x1));
  }
  /** Return the other factStore */
  public FactStore other(FactStore fs) {
        if (fs == factStore1) return factStore2;
        if (fs == factStore2) return factStore1;
        assert(false);
        return null;
  }

  /** Says to whom you are equal as a literal that exists in the other ontology*/
  @SuppressWarnings("unchecked")
  public Collection<Pair<Object, Double>> literalEqualToScored(FactStore fs, Object x1) {
  	if (!Config.literalDistanceForEquality || Config.literalDistance == LiteralDistance.IDENTITY
  			|| Config.entityType((String) x1) != Config.EntityType.STRING) {
	    if (!other(fs).exists(x1)) return (Collections.emptyList());
	    if (!other(fs).exists(x1)) return (Collections.emptyList());
	    return(Arrays.asList(new Pair<Object,Double>(x1,1.0)));
  		
    } else {
  		List<Pair<Object, Double>> l = new LinkedList<Pair<Object, Double>>();
  		boolean foundExact = false;
  		boolean shouldFindExact = false;
  		if (other(fs).exists(x1)) shouldFindExact = true;
  		if (other(fs).exists(x1)) shouldFindExact = true;
  		assert (!Config.treatIdAsRelation);
  		if (Config.literalDistance == LiteralDistance.SHINGLING || Config.literalDistance == LiteralDistance.SHINGLINGLEVENSHTEIN) {    		
    		Iterator<QueryResult> i1 = other(fs).similarLiterals((String) x1, Config.literalDistanceThreshold).iterator();					
				while (i1.hasNext()) {
					QueryResult qr = i1.next();
					double score;
					if (Config.literalDistance == LiteralDistance.SHINGLING) {
						score = qr.trueScore / 100.;
					} else {
						score = LevenshteinDistance.similarity(qr.result, (String) x1);
					}
					if (qr.result.equals(Config.stripQuotes((String) x1))) {
						foundExact = true;
					} else {
						score /= Config.penalizeApproxMatches; // we must do it here because we don't call equality()
					}
					l.add(new Pair<Object, Double>('"'+qr.result+'"', score));
				}
  		} else {
	  		for (FactStore.Fact fact : other(fs).facts()) {
	  			if (fact.arg2 == 0) {
	  				double score = equality(fs, x1, fact.arg2String);
	  				//Announce.debug(x1, fact.arg2(), score);
	  				if (score > 0.999999) {
	  					foundExact = true;
	  					assert(shouldFindExact);
	  				}
	  				if (score > Config.literalDistanceThreshold)
	  					l.add(new Pair<Object, Double>(fact.arg2String, score));
	  			}
	  		}
  		}
  		assert (shouldFindExact == foundExact);
  		return l;
  	}
  }
//
//  /** Sets equality. s1 is in the first store, s2 is in the second store*/
//  public void setEquality(FactStore fs1, Integer s1, Integer s2, double val) {
//    if (fs1 == factStore1) equalityStore.setValue(factStore1,s1, factStore2,s2, val);
//    else equalityStore.setValue(factStore1, s2, factStore2, s1, val);
//  }

  /** Returns equality*/
  public static double numCompare(String s1, String s2) {
    String[] n1 = NumberParser.getNumberAndUnit(s1, new int[2]);
    if (n1 == null) return (-1);
    String[] n2 = NumberParser.getNumberAndUnit(s2, new int[2]);
    if (n2 == null) return (-1);
    if (D.equal(n1[1], n2[1])) return (0);
    try {
      double d1 = Double.parseDouble(s1);
      double d2 = Double.parseDouble(s2);
      double val = 1 / (1 + 100 * Math.abs((d1 - d2) / d1));
      return (val);
    } catch (Exception e) {
      return (-1);
    }
  }

  /** Compares two strings*/
  public static double stringCompare(String s1, String s2) {
  	double score;
    s1=Config.stripQuotes(s1);
    s2=Config.stripQuotes(s2);    
    String splitBy="";
    switch(Config.literalDistance) {
      case IDENTITY:
      	score = (s1.equals(s2)?1:0);
      	break;
      case NORMALIZE:
      	score = (Config.normalizeString(s1).equals(Config.normalizeString(s2))?1:0);
      	break;
      case BAGOFWORDS:
        splitBy="\\W";
      case BAGOFCHARS:
        List<String> s1split=Arrays.asList(s1.split(splitBy));
        List<String> s2split=Arrays.asList(s2.split(splitBy));        
        Set<String> intersection = new TreeSet<String>(s1split);
        intersection.retainAll(s2split);
        if (intersection.size() == 0) return (0);
        Set<String> union = new TreeSet<String>(s1split);
        union.addAll(s2split);
        double val = intersection.size() / (double) union.size();
        score = (val);
        break;
      case LEVENSHTEIN:
      case SHINGLINGLEVENSHTEIN:
      	score = LevenshteinDistance.similarity(s1, s2);
      	break;
//      case JARO_WINKLER:
//        JaroWinklerDistance jwd = new JaroWinklerDistance();
//      	double d = jwd.proximity(s1, s2);
//      	//Announce.debug("proximity of ", s1, " and ", s2, " is ", d);
//      	score = d;
//      	break;
      case SHINGLING:
      	score = ShinglingTable.goldStandard(s1, s2, MemoryFactStore.shinglingSize)/100.;
      	break;
      default:
      	assert(false);
      	score = -1;
      	break;
    }
    if (s1.equals(s2))
    	assert(score > 0.999);
    else
    	score /= Config.penalizeApproxMatches;
    return score;
  }
  
  /** Compares two dates*/
  public static double dateCompare(String s1, String s2) {
    s1 = Config.stripQuotes(s1);
    s2 = Config.stripQuotes(s2);
    if (!DateParser.isDate(s2) && !NumberParser.isInt(s2)) return (0);
    return (DateParser.includes(s1, s2) || DateParser.includes(s2, s1) ? 1 : 0);
  }

  // -----------------------------------------------------------------
  //             Subrelations and classes
  // -----------------------------------------------------------------

  /** Returns the superclasses of a class*/
  public Collection<Integer> superClassesOf(FactStore fs, Integer x) {
    if (fs == factStore1) return (superClassesOf1.superOf(x));
    else return (superClassesOf2.superOf(x));
  }

  /** Returns the superrelations of a relation*/
  public Collection<Integer> superRelationsOf(FactStore fs, Integer x) {
    if(x<0) return(FactStore.invertRelations(superRelationsOf(fs, -x)));
    if (fs == factStore1) return (superRelationsOf1.superOf(x));
    else return (superRelationsOf2.superOf(x));
  }

  /** Returns the subclasses of a class*/
  public Collection<Integer> subClassesOf(FactStore fs, Integer x) {
    if (fs == factStore1) return (superClassesOf2.subOf(x));
    else return (superClassesOf1.subOf(x));
  }

  /** Returns the subrelations of a relation*/
  public Set<Integer> subRelationsOf(FactStore fs, Integer x) {
    Set<Integer> result=new TreeSet<Integer>();
    if (fs == factStore1) {
      for(Integer r: superRelationsOf2.subOf(x)) result.add(r);
      for(Integer r: superRelationsOf2.subOf(-x)) result.add(-r);      
    } else {
      for(Integer r: superRelationsOf1.subOf(x)) result.add(r);
      for(Integer r: superRelationsOf1.subOf(-x)) result.add(-r);            
    }
    return (result);
  }

  /** TRUE if the first is a subrelation of the second*/
  public double subRelation(FactStore fssub, Integer sub, Integer supr) {
    if(sub<0) return(subRelation(fssub,-sub,-supr));
    if (fssub == factStore1) return (superRelationsOf1.getValue(sub, supr));
    else return (superRelationsOf2.getValue(sub, supr));
  }

  /** TRUE if the first is a subclass of the second*/
  public double subClass(FactStore fssub, Integer sub, Integer supr) {
    if (fssub == factStore1) return (superClassesOf1.getValue(sub, supr));
    else return (superClassesOf2.getValue(sub, supr));
  }

  /** Makes the first is a subclass of the second*/
  public void setSubclass(FactStore fssub, Integer sub, Integer supr, double val) {
    if (fssub == factStore1) superClassesOf1.setValue(factStore1,sub, factStore2, supr, val);
    else superClassesOf2.setValue(factStore2,sub, factStore1,supr, val);
  }

  /** Makes the first is a subrelation of the second*/
  public void setSubrelation(FactStore fssub, Integer sub, Integer supr, double val) {
    if(sub<0) {
      setSubrelation(fssub, -sub, -supr, val);
      return;
    }
    Announce.message("Setting " + sub + " as subrel of " + supr + " with score " + val);
    if (fssub == factStore1) superRelationsOf1.setValue(factStore1,sub, factStore2,supr, val);
    else superRelationsOf2.setValue(factStore2,sub, factStore1,supr, val);
  }

  /** Returns the equality between two classes*/
  public double classEquality(FactStore fs1, Object c1, Object c2) {
    if (!(c1 instanceof Integer) || !(c2 instanceof Integer)) return (0);
    double v1 = subClass(fs1, (Integer) c1, (Integer) c2);
    if (v1 == -1) return (-1);
    double v2 = subClass(fs1 == factStore1 ? factStore2 : factStore1, (Integer) c2, (Integer) c1);
    if (v2 < 0) return (-1);
    return (v1 * v2);
  }

  /** Number of stored subrelation pairs */
  public int numSubrelationPairs() {
    return ((int) (superRelationsOf1.size() + superRelationsOf2.size()));
  }

  /** Prints a human-readable summary*/
  public void print() {
    Announce.message("Equalities: " + equalityStore.size() + ", for example");
    for (Triple<Integer, Integer, Double> pair : equalityStore.sample()) {
      Announce.message("    " + factStore1.toString(pair.first()) + " = " + factStore2.toString(pair.second()) + " " + pair.third());
    }
    Announce.message("Subclasses: " + (superClassesOf1.size() + superClassesOf2.size()) + ", for example");
    for (Pair<Integer, Integer> pair : superClassesOf1.sample()) {
      Announce.message("    " + factStore1.toString(pair.first()) + " < " + factStore2.toString(pair.second()));
    }
    for (Pair<Integer, Integer> pair : superClassesOf2.sample()) {
      Announce.message("    " + factStore2.toString(pair.first()) + " < " + factStore1.toString(pair.second()));
    }
    Announce.message("Subrelations: " + (superRelationsOf1.size() + superRelationsOf2.size()) + ", for example");
    for (Triple<Integer, Integer,Double> pair : superRelationsOf1.sample()) {
      Announce.message("    " + factStore1.toString(pair.first()) + " < " + factStore2.toString(pair.second())+"  "+pair.third());
    }
    for (Triple<Integer, Integer,Double> pair : superRelationsOf2.sample()) {
      Announce.message("    " + factStore2.toString(pair.first()) + " < " + factStore1.toString(pair.second())+"  "+pair.third());
    }
    Announce.message("Memory (Mb):");
    Announce.message("   Java Free: " + Runtime.getRuntime().freeMemory() / 1000 / 1000);
    Announce.message("   Java Max: " + Runtime.getRuntime().maxMemory() / 1000 / 1000);
    Announce.message("   Java Total: " + Runtime.getRuntime().totalMemory() / 1000 / 1000);
    //Announce.message("   BDB cache percent: " + environment.getConfig().getCachePercent());
    //Announce.message("   BDB cache size: " + environment.getConfig().getCacheSize() / 1000 / 1000);
  }
  
  /** Saves the data. This process is orthogonal to the step-by-step-printing. Be sure to use only one of these. */
  public void saveTo(File folder) throws IOException {
    Announce.doing("Saving");
    Announce.doing("Saving equality"); 
    equalityStore.saveTo(new File(folder, "eqv.tsv"), factStore1, factStore2); 
    Announce.doneDoing("Saving subclasses");
    superClassesOf1.saveTo(new File(folder, "superclasses1.tsv"), factStore1, factStore2);
    superClassesOf2.saveTo(new File(folder, "superclasses2.tsv"), factStore2, factStore1);
    Announce.doneDoing("Saving subrelations"); 
    superRelationsOf1.saveTo(new File(folder, "superrelations1.tsv"), factStore1, factStore2);
    superRelationsOf2.saveTo(new File(folder, "superrelations2.tsv"), factStore2, factStore1);
    Announce.done();
    Announce.done();
  }


}
