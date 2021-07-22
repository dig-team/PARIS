package paris;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import paris.Setting.LiteralDistance;
import paris.shingling.QueryResult;
import paris.shingling.ShinglingTable;
import paris.storage.FactStore;

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
 * This class holds all information that is computed in of PARIS: 
 * subclass, equality, subproperty*/

public class Result implements Closeable {

  // -----------------------------------------------------------------
  //             Stores
  // -----------------------------------------------------------------

  /** First fact store*/
  protected FactStore factStore1;

  /** Second fact store*/
  protected FactStore factStore2;

  /** Stores equalities. */
  public EqualityStore equalityStore;

  /** Maps classes of 1 to super classes of 2*/
  public HashSubThingStore<Integer> superClassesOf1;

  /** Maps classes of 2 to super classes of 1*/
  public HashSubThingStore<Integer> superClassesOf2;
  
  Setting setting;
  
  /** Raw outputs of the computation */
  public MapperOutput mapperOutput1;
  public MapperOutput mapperOutput2;
  
  /** Maps relations of 1 to super relations of 2. Redundant with neighborhoods and relationNormalizers, but more efficient to query. */
  public SubRelationStore superRelationsOf1;

  /** Maps relations of 2 to super relations of 1*/
  public SubRelationStore superRelationsOf2;

  /** TSV File folder*/
  public final File tsvFolder;
  
  boolean allowWrite;
  
  ShinglingStore shinglingStore1;
  ShinglingStore shinglingStore2;
    
  // -----------------------------------------------------------------
  //             Constructor
  // -----------------------------------------------------------------

  /** Constructor
   * @throws IOException 
   * @throws InterruptedException */
  public Result(Setting setting, FactStore fs1, FactStore fs2, File tsvfolder) throws IOException, InterruptedException {
    Announce.doing("Creating computing environment");
    this.tsvFolder=tsvfolder;
    
    equalityStore = new EqualityStore(fs1, fs2);
    superClassesOf1 = new SubClassStore(fs1, fs2);
    superClassesOf2 = new SubClassStore(fs2, fs1);
    if (setting.matrixSubRelationStores) {
	    superRelationsOf1 = new MatrixSubRelationStore(fs1, fs2);
	    superRelationsOf2 = new MatrixSubRelationStore(fs2, fs1);
    } else {
	    superRelationsOf1 = new HashSubRelationStore(fs1, fs2);
	    superRelationsOf2 = new HashSubRelationStore(fs2, fs1);    	
    }
    factStore1 = fs1;
    factStore2 = fs2;
		mapperOutput1 = new MapperOutput(fs1);
		mapperOutput2 = new MapperOutput(fs2);
    this.setting = setting;
    if (setting.precomputeShinglings && (
    		setting.literalDistance == Setting.LiteralDistance.SHINGLING
    		|| setting.literalDistance == Setting.LiteralDistance.SHINGLINGLEVENSHTEIN)) {
    	ShinglingStore ss1 = new ShinglingStore(fs1, fs2, this);
    	ShinglingStore ss2 = null;
    	if (setting.bothWays)
    		ss2 = new ShinglingStore(fs2, fs1, this);
    	// don't assign right away because ShinglingStore will use literalEqualToScored which should not think that the shinglingStore's have been computed
    	shinglingStore1 = ss1;
    	shinglingStore2 = ss2;
    	shinglingStore1.dump(new File(setting.tsvFolder, "shinglings1.tsv"));
    	if (setting.bothWays)
    	  shinglingStore2.dump(new File(setting.tsvFolder, "shinglings2.tsv"));
    }
    Announce.done();
    print();
  }
  
  public ShinglingStore shinglingStoreForFactStore(FactStore fs) {
  	if (fs == factStore1) return shinglingStore1;
  	if (fs == factStore2) return shinglingStore2;
  	return null;
  }
  public SubRelationStore superRelationsForFactStore(FactStore fs) {
  	if (fs == factStore1) return superRelationsOf1;
  	if (fs == factStore2) return superRelationsOf2;
    return null;
  }
  
  public HashSubThingStore<Integer> superClassesForFactStore(FactStore fs) {
  	if (fs == factStore1) return superClassesOf1;
  	if (fs == factStore2) return superClassesOf2;
    return null;
  }
  
  public MapperOutput mapperOutputForFactStore(FactStore fs) {
  	if (fs == factStore1) return mapperOutput1;
  	if (fs == factStore2) return mapperOutput2;
  	assert(false);
    return null;
  }
  
  /** Return the other factStore */
  public FactStore other(FactStore fs) {
  	if (fs == factStore1) return factStore2;
  	if (fs == factStore2) return factStore1;
  	assert(false);
  	return null;
  }
  
  @Override
  public void close() throws IOException {
    Announce.doing("Closing stores");
    equalityStore.close();
    superClassesOf1.close();
    superClassesOf2.close();
    superRelationsOf1.close();
    superRelationsOf2.close();
    Announce.done();
  }
  

	public void resetAndPrune(FactStore fs) {
		Announce.doing("Resetting and pruning neighborhoods...");
	
		HashArrayNeighborhood[] n = mapperOutputForFactStore(fs).neighborhoods; 
		for (int i = 0; i < fs.maxJoinRelationCode(); i++) {
			if (n[i] != null)
				n[i].reset();
		}
		for (int i = 0; i < fs.maxJoinRelationCode(); i++) {
//			if (n[i] != null)
//				n[i].prune(HashNeighborhood.occurrenceThreshold);
		}
	}

  /** Sets the TSV writers to new files*/
  public void startIteration(int iteration) throws IOException {
    Announce.doing("Creating tsv files",iteration,"in",tsvFolder);
    equalityStore.setTSVfile(new File(tsvFolder,iteration+"_eqv.tsv"));
    superClassesOf1.setTSVfile(new File(tsvFolder,iteration+"_superclasses1.tsv"));
    superClassesOf2.setTSVfile(new File(tsvFolder,iteration+"_superclasses2.tsv"));
    Announce.done();
  }


  // -----------------------------------------------------------------
  //             Subrelations and classes
  // -----------------------------------------------------------------

  /** Returns the superclasses of a class*/
  public Collection<Integer> superClassesOf(FactStore fs, Integer x) {
  	return superClassesForFactStore(fs).superOf(x);
  }

  public int reversed(int x) { return -x; }
  
  public JoinRelation reversed(JoinRelation r) {
  	JoinRelation r2 = new JoinRelation(r);
  	r2.reverse();
  	return r2;
  }

  /** Returns the subclasses of a class*/
  public Collection<Integer> subClassesOf(FactStore fs, Integer x) {
  	return superClassesForFactStore(other(fs)).subOf(x);
  }

  /** Return how much the first is a subrelation of the second*/
  public double subRelation(FactStore fssub, JoinRelation sub, JoinRelation supr) {
  	return superRelationsForFactStore(fssub).getValue(sub, supr);
  }

  /** Return how much the first is a subrelation of the second*/
  public double subRelation(FactStore fssub, int sub, int supr) {
  	return superRelationsForFactStore(fssub).getValueCode(sub, supr);
  }
  
  /** Return how much the first is a subclass of the second*/
  public double subClass(FactStore fssub, Integer sub, Integer supr) {
  	return superClassesForFactStore(fssub).getValue(sub, supr);
  }

  /** Makes the first is a subclass of the second*/
  public void setSubclass(FactStore fssub, Integer sub, Integer supr, double val) {
    superClassesForFactStore(fssub).setValue(sub, supr, val);
  }
  
  // -----------------------------------------------------------------
  //             Equality
  // -----------------------------------------------------------------

public double stringEquality(String s1, String s2) {
  if (s1.equals(s2)) return (1);
  switch (Config.entityType(s1)) {
  case NUMBER:
    if (Config.entityType(s2) == Config.EntityType.STRING)
    	return (dateCompare(s1, s2));
    return (numCompare(s1, s2));
  case STRING:
    return (stringCompare(setting, s1, s2));
  case DATE:
    return (dateCompare(s1, s2));
	case RESOURCE:
		assert(false);
		return -1;
	default:
		assert(false);
		return -1;
  }
}
  /** Returns the equality. */
  public double equality(FactStore fs1, int s1, int s2) {
  	FactStore fs2 = other(fs1);
    if (fs1.isLiteral(s1)) {
      if (!fs2.isLiteral(s2)) 
      	return 0.;
      return stringEquality(fs1.entity(s1), fs2.entity(s2));
    } else {
    	if (fs2.isLiteral(s2))
    		return 0.;
    	if (fs1 == factStore1)
      	return equalityStore.getValueInt(s1, s2);
      return equalityStore.getValueInt(s2, s1);
    }
  }
  
  /** Says to whom you are equal with scores*/
  public Collection<Pair<Object,Double>> equalToScoredId(FactStore fs, int x1) {
  	if (!fs.isLiteral(x1))
  		return equalToScored(fs, (Integer) x1);
  	// The implementation of EqualityStore does assert(this.setting.takeMaxMax), so no point in doing this check
//    if(this.setting.takeMax)
//    	return(equalToScoredMax(fs,x1));
    return literalEqualToScored(fs, fs.entity(x1));
  }

  
  /** Says to whom you are equal with score*/
  public Collection<Pair<Object,Double>> equalToScored(FactStore fs, Integer x1) {
    // The implementation of EqualityStore does assert(this.setting.takeMaxMax), so no point in doing this check
//    if (this.setting.takeMax)
//    	return(equalToScoredMax(fs,x1));
    return trueEqualToScored(fs, x1);
  }

//  /** Says to whom you are equal with scores*/
//  public Collection<Pair<Object,Double>> equalToScoredMax(FactStore fs, Object x1) {
//    if (x1 instanceof Integer)
//    	return (equalToScoredMax(fs, (Integer) x1));
//    return (literalEqualToScored(fs, x1));
//  }

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
        if(!this.setting.takeMaxMax || result.isEmpty()) result.add(new Pair<Object,Double>((Integer)eq.first(),max));
      }
    }
    return(result);
  }

  /** Says to whom you are equal with score*/
  protected Collection<Pair<Object,Double>> trueEqualToScored(FactStore fs, Integer x1) {
    if (fs == factStore1)
    	return (equalityStore.superOfScored((Integer) x1));
    else
    	return (equalityStore.subOfScored((Integer) x1));
  }
  
  /** Says to whom you are equal as a literal */
  public Collection<Pair<Object, Double>> literalEqualToScored(FactStore fs, String x1) {
  	if (!Config.literalDistanceForEquality || setting.literalDistance == LiteralDistance.IDENTITY
  			|| Config.entityType((String) x1) != Config.EntityType.STRING) {
	    return literalExactEqualToScored(fs, x1);
    } else {
    	Collection<Pair<Object, Double>> exactResult = literalExactEqualToScored(fs, x1);
    	// don't query the index if an exact match exists
    	if (!exactResult.isEmpty() && setting.noApproxIfExact)
    		return exactResult;
    	// perform approximate matching
  		Collection<Pair<Object, Double>> l = new ArrayList<Pair<Object, Double>>();
  		assert (!Config.treatIdAsRelation);
  		if (setting.literalDistance == LiteralDistance.SHINGLING || setting.literalDistance == LiteralDistance.SHINGLINGLEVENSHTEIN) {
  			if (shinglingStore1 != null) {
  				ShinglingStore ss = shinglingStoreForFactStore(fs);
  				int e = fs.entity(x1);
  				for (int i = 0; i < ss.indexMatch[e].length; i++) {
  					l.add(new Pair<Object, Double>(ss.indexMatch[e][i], ss.indexScore[e][i]));
  				}
  			} else {
	    		Iterator<QueryResult> i1 = other(fs).similarLiterals((String) x1, Config.literalDistanceThreshold).iterator();					
					while (i1.hasNext()) {
						QueryResult qr = i1.next();
						assert(other(fs).isLiteral(other(fs).entity(qr.result)));
						double score = computeQueryResultScore(qr, x1, setting.literalDistance);
						if (score > 0.)
							l.add(new Pair<Object, Double>(other(fs).entity(qr.result), score));
					}
  			}
  		} else {
	  		for (int i = 0; i < other(fs).numEntities(); i++) {
	  			if (!other(fs).isLiteral(i))
	  				continue;
  				double score = stringEquality(x1, other(fs).entity(i));
  				// we're not supposed to find an exact match now
  				assert (!setting.noApproxIfExact || score < 0.99999);
  				if (score > Config.literalDistanceThreshold) {
						assert(other(fs).isLiteral(i));
  					l.add(new Pair<Object, Double>(other(fs).entity(i), score));
  				}
	  		}
  		}
  		return l;
  	}
  }

  public double computeQueryResultScore(QueryResult qr, String x1, LiteralDistance ld) {
  	double score;
  	if (ld == LiteralDistance.SHINGLING) {
			score = qr.trueScore;
		} else {
			score = LevenshteinDistance.similarity(qr.result, (String) x1);
		}
		assert (score >= 0 && score <= 1);
		// we're not supposed to find an exact match now
		assert(!setting.noApproxIfExact || !qr.result.equals((String) x1));
		if (score < setting.postLiteralDistanceThreshold)
			return 0;
		score /= setting.penalizeApproxMatches; // we must do it here because we don't call equality()
		// squaring
		if (setting.shinglingSquare)
			score = score*score;
		return score;
	}

	/** Check if the literal exists exactly in the other ontology */
	private Collection<Pair<Object, Double>> literalExactEqualToScored(
			FactStore fs, String x1) {
		if (other(fs).entity(x1) == 0) return (Collections.emptyList());
		assert(other(fs).isLiteral(other(fs).entity(x1)));
		return (Arrays.asList(new Pair<Object,Double>(other(fs).entity(x1),1.0)));
	}

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
  public static double stringCompare(Setting setting, String s1, String s2) {
  	double score;
    s1=Config.stripQuotes(s1);
    s2=Config.stripQuotes(s2);    
    String splitBy="";
    switch(setting.literalDistance) {
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
      	score = ShinglingTable.goldStandard(s1, s2, setting.shinglingSize);
      	break;
      default:
      	assert(false);
      	score = -1;
      	break;
    }
    if (s1.equals(s2))
    	assert(score > 0.999);
    else
    	score /= setting.penalizeApproxMatches;
    return score;
  }
  
  /** Compares two dates*/
  public static double dateCompare(String s1, String s2) {
    s1 = Config.stripQuotes(s1);
    s2 = Config.stripQuotes(s2);
    if (!DateParser.isDate(s2) && !NumberParser.isInt(s2)) return (0);
    return (DateParser.includes(s1, s2) || DateParser.includes(s2, s1) ? 1 : 0);
  }


  /** Prints a human-readable summary*/
  public void print() {
    Announce.message("Equalities examples:");
    for (Triple<Integer, Integer, Double> pair2 : equalityStore.sample()) {
      Announce.message("    " + factStore1.entity(pair2.first()) + " = " + factStore2.entity(pair2.second()) + " " + pair2.third());
    }
//    Announce.message("Subclasses: " + (superClassesOf1.size() + superClassesOf2.size()) + ", for example");
//    for (Pair<Integer, Integer> pair : superClassesOf1.sample()) {
//      Announce.message("    " + factStore1.toString(pair.first()) + " < " + factStore2.toString(pair.second()));
//    }
//    for (Pair<Integer, Integer> pair : superClassesOf2.sample()) {
//      Announce.message("    " + factStore2.toString(pair.first()) + " < " + factStore1.toString(pair.second()));
//    }
    //Announce.message("Subrelations: " + (superRelationsOf1.size() + superRelationsOf2.size()) + ", for example");
    Announce.message("Subrelation examples:");
    for (Triple<JoinRelation, JoinRelation,Double> pair : superRelationsOf1.sample()) {
      Announce.message("    " + pair.first().toString() + " < " + pair.second().toString()+"  "+pair.third());
    }
    for (Triple<JoinRelation, JoinRelation,Double> pair : superRelationsOf2.sample()) {
      Announce.message("    " + pair.first().toString() + " < " + pair.second().toString()+"  "+pair.third());
    }
    Announce.message("Memory (Mb):");
    Announce.message("   Java Free: " + Runtime.getRuntime().freeMemory() / 1000 / 1000);
    Announce.message("   Java Max: " + Runtime.getRuntime().maxMemory() / 1000 / 1000);
    Announce.message("   Java Total: " + Runtime.getRuntime().totalMemory() / 1000 / 1000);
  }

	
	public void printNeighborhoodsForFactStore(FactStore fs) {
  	for (int i = 0; i < fs.maxJoinRelationCode(); i++) {
  		if (mapperOutputForFactStore(fs).neighborhoods[i] == null) continue;
  		JoinRelation r = fs.joinRelationByCode(i);
  		Announce.message("== neighborhood for", r, "==");
  		mapperOutputForFactStore(fs).neighborhoods[i].print(new JoinRelation(other(fs)));
  	}
  }
  public void printNeighborhoods() {
  	printNeighborhoodsForFactStore(factStore1);
  	Announce.message("== // // ==");
  	printNeighborhoodsForFactStore(factStore2);
  }
  
}
