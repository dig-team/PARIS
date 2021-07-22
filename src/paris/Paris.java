package paris;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java.util.concurrent.NotifyingBlockingThreadPoolExecutor;

import paris.Config.EntityType;

import javatools.administrative.Announce;
import javatools.administrative.Announce.Level;
import javatools.administrative.D;
import javatools.datatypes.CombinedIterable;
import javatools.datatypes.MappedIterator;
import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;
import javatools.parsers.NumberFormatter;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class implements a very cool probabilistic framework for ontology matching
 */
public class Paris {

  public final static int reportInterval = 1000;

  /** TRUE for test runs*/
  public static boolean test = false;

  /** TRUE for debug on. Make it final to allow conditional compiling. */
  public static boolean debug = false;
  
  /** If this file exists, we stop immediately*/
  public static File stop = new File("stop").getAbsoluteFile();

  /** First ontology*/
  public static FactStore factStore1;

  /** Second ontology*/
  public static FactStore factStore2;

  /** Stores the equality */
  public static Result computed;

  /** Optimizer for finding equals*/
  public static Statistics statistics = new Statistics();

  /** The setting in which we work*/
  public static Setting setting;

  /** Finds the super classes of a class*/
  public static void findSuperClassesOf(Integer subclass, FactStore subStore, FactStore superStore) {
    // We ignore classes that contain practically all entities
    if (subStore.entity(subclass).name.startsWith("owl:") || 
        subStore.entity(subclass).name.equals("http://www.opengis.net/gml/_Feature")) return;
    if (debug) Announce.debug("Computing superclasses of", subStore.lazyToString(subclass));

    /*
    // The following deduces whether a class is a subclass of another class
    // by looking at the domains and ranges of relations.
    // In practice, we do not do that.
     
    // Check the domains
    Map<Integer, Double> domainSuperclassDegree = new TreeMap<Integer, Double>();
    // maps each superclass d to      PROD r,s: 1-P(c<dom(r))*P(dom(s)<d)*P(r<s)
    // since we assume r and c to live in the same ontology
    // and s and d to live in the same ontology, this becomes
    //  PROD r,s such that c<dom(r),dom(s)<d:   1-P(r<s)
    for (Integer superclass : subStore.superclasses(subclass)) {
      for (Integer subrelation : subStore.relationsWithDomain(superclass)) {
        for (Integer superrelation : computed.superRelationsOf(subStore, subrelation)) {
          Integer superdom = superStore.domain(superrelation);
          if (superdom == null) continue;
          double subrelscore = computed.subRelation(subStore, subrelation, superrelation);
          if (subrelscore == -1 || subrelscore < SubThingStore.THRESHOLD) continue;
          for (Integer supersuperclass : superStore.superclasses(superdom)) {
            double val = domainSuperclassDegree.containsKey(supersuperclass) ? domainSuperclassDegree.get(supersuperclass) : 1;
            val *= 1 - subrelscore;
            domainSuperclassDegree.put(supersuperclass, val);
          }
        }
      }
    }
    */

    // maps each superclass d to
    //   SUM x such that type(x,c):  1 - PROD y such that type(y,d): 1-P(x=y)
    Map<Integer, Double> superclassDegree = new TreeMap<Integer, Double>();
    // contains the value
    //   # x such that type(x,c)    // no longer: and exists y: y=x and type(y,some class)
    double normalizer = 0;
    int counter = 0;
    // Don't compute that for classes that are too far up in the hierarchy
    CombinedIterable<Integer> subInstances = subStore.instancesOf(subclass, 10000);
    if (subInstances == null) return;
    for (Integer subclassInstance : subInstances) {
      if (debug && counter++ > 100) break;
      if (debug) Announce.debug("   Looking at instance", subStore.lazyToString(subclassInstance));
      // For each instance x of c...
      boolean foundeqv = false;
      Map<Integer, Double> membershipProduct = new TreeMap<Integer, Double>();
      // maps each superclass d to
      //     PROD y such that type(y,d): 1-P(x=y)
      for (Pair<Object, Double> superclassInstancePair : computed.equalToScored(subStore, subclassInstance)) {
        if (!(superclassInstancePair.first() instanceof Integer)) continue;
        Integer superclassInstance = (Integer) superclassInstancePair.first();
        double equality = superclassInstancePair.second();
        if (debug) Announce.debug("     Is equal to", superStore.lazyToString(superclassInstance), equality);

        if (equality < Config.THETA) continue;
        for (Integer superClass : superStore.classesOf(superclassInstance)) {
          double prod = membershipProduct.containsKey(superClass) ? membershipProduct.get(superClass) : 1;
          if (debug) Announce.debug("        Scoring for", superStore.lazyToString(superClass), prod, 1 - equality * equality, prod * (1 - equality));
          prod *= 1 - equality;
          membershipProduct.put(superClass, prod);
          foundeqv = true;
        }
      }
      if (foundeqv) {
        for (Integer superclass : membershipProduct.keySet()) {
          D.addKeyValueDbl(superclassDegree, superclass, 1 - membershipProduct.get(superclass));
        }
      }
      normalizer++;
    }
    close(subInstances);
    // We do not do the domain/range deduction
    // Collect all classes about which we know something in superclassDegree
    // Say that if we have no instances, the superclassDegree is 0
    //for (Integer superclass : domainSuperclassDegree.keySet()) {
    //  if (!superclassDegree.containsKey(superclass)) superclassDegree.put(superclass, 0.0);
    //}

    // If the normalizer is 0, superclassDegree(x)=0 for all x. 
    // So instanceScore will be 0 anyway. Hence, set the normalizer to 1
    // to avoid NAN values when we compute superclassDegree(x)/normalizer.
    if (normalizer == 0) normalizer = 1;

    // Set the final values
    for (Integer superclass : superclassDegree.keySet()) {
      double instanceScore = superclassDegree.get(superclass) / normalizer;
      double domainScore = 1.0; //domainSuperclassDegree.containsKey(superclass) ? domainSuperclassDegree.get(superclass) : 1.0;
      if (1 - (1 - instanceScore) * domainScore < Config.THETA) continue;
      if (debug) Announce.debug("Setting final value:", superStore.toString(superclass), superclassDegree.get(superclass), normalizer,
          superclassDegree.get(superclass) / normalizer, 1 - (1 - instanceScore) * domainScore);
      if (!test) computed.setSubclass(subStore, subclass, superclass, 1 - (1 - instanceScore) * domainScore);
    }
  }

  /** Closes a closeable*/
  public static void close(Closeable facts) {
    try {
      facts.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Finds the super relations of a relation*/
  public static void findSuperrelationsOf(Integer subrelation, FactStore subStore, FactStore superStore) {
    if (subrelation < 0) return;
    int counter = 0;
    if (debug) Announce.debug("Computing super relations of", subStore.toString(subrelation));
    // maps each superproperty s to 
    //     SUM x,y such that r(x,y): 1 - PROD x2,y2 st s(x2,y2): 1-P(x=x2)*P(y=y2)
    Map<Integer, Double> superPropScore = new TreeMap<Integer, Double>();
    // holds the value
    // //    # { x,y such that r(x,y) and exists x',y', z': x'=x, y'=y, r'(x',z') OR r'(z',y') }
    //Map<Integer, Double> normalizer = new TreeMap<Integer, Double>();
    double normalizer = 0;
    MappedIterator<?, Pair<Object, Object>> facts = subStore.factsForRelation(subrelation);
    // Holds the previous fact
    Pair<Object, Object> previous = new Pair<Object, Object>("", "");
    // Holds the x's to which x1 is equivalent with their scores
    Collection<Pair<Object, Double>> x2s = null;
    // Holds the y's to which y1 is equivalent with their scores
    Collection<Pair<Object, Double>> y2s = null;
    for (Pair<Object, Object> fact : facts) {
      Object x1 = fact.first();
      Object y1 = fact.second();
      if (debug && (subStore.toString(x1).startsWith("y:wordnet") || subStore.toString(x1).startsWith("y:wikicategory"))) continue;
      if (counter++ > 10000) break;
      if (debug) Announce.debug("   Fact", subStore.toString(x1), subStore.toString(subrelation), subStore.toString(y1));
      // holds the value
      //     PROD x2,y2: 1-P(x1=x2)*P(y1=y2)
      Map<Integer, Double> localSuperPropScore = new TreeMap<Integer, Double>();
      // Find equivalent x2s and y2s
      if (!previous.first().equals(x1) || x2s == null) x2s = computed.equalToScored(subStore, x1);
      if (x2s.size() == 0) continue;
      if (!previous.second().equals(y1) || y2s == null) y2s = computed.equalToScored(subStore, y1);
      if (y2s.size() == 0) continue;
      // For every matching pair...
      for (Pair<Object, Double> x2sc : x2s) {
        Object x2 = x2sc.first();
        double xeqv = x2sc.second();
        if (xeqv < Config.THETA) continue;
        for (Pair<Object, Double> y2sc : y2s) {
          Object y2 = y2sc.first();
          double yeqv = y2sc.second();
          if (yeqv < Config.THETA) continue;
          if (debug) Announce.debug("       Matching x2=", superStore.toString(x2), xeqv);
          if (debug) Announce.debug("       Matching y2", superStore.toString(y2), yeqv);
          //for(Integer relation : superStore.relationsForArg1(x2)) {
          //D.addKeyValueDbl(normalizer,relation,xeqv*yeqv);
          //if (debug) Announce.debug("         Penalizing r", superStore.toString(relation), normalizer.get(relation));
          //}
          normalizer += xeqv * yeqv;
          Iterator<Integer> relations = superStore.relationsForArg1Arg2(x2, y2);
          while (relations.hasNext()) {
          	Integer superrelation = relations.next();
            double val = localSuperPropScore.containsKey(superrelation) ? localSuperPropScore.get(superrelation) : 1.0;
            if (debug) Announce.debug("         Match r=", superStore.toString(superrelation), "before=", val, "after=", val * (1 - xeqv * yeqv));
            val *= 1 - xeqv * yeqv;
            localSuperPropScore.put(superrelation, val);
          }
        }
      }
      for (Integer superrelation : localSuperPropScore.keySet()) {
        Announce.debug("  ", superStore.toString(superrelation), superPropScore.get(superrelation), "+", 1 - localSuperPropScore.get(superrelation),
            "=", (superPropScore.get(superrelation) == null ? 0 : superPropScore.get(superrelation)) + 1 - localSuperPropScore.get(superrelation));
        D.addKeyValueDbl(superPropScore, superrelation, 1 - localSuperPropScore.get(superrelation));
      }
      previous = fact;
    }
    close(facts);
    for (Integer superrelation : superPropScore.keySet()) {
      if (debug) Announce.debug("Final set", superStore.toString(superrelation), superPropScore.get(superrelation), normalizer,
          superPropScore.get(superrelation) / normalizer);
      if (!test) 
      	computed.setSubrelation(subStore, subrelation, superrelation, superPropScore.get(superrelation) / normalizer);
    }
  }

  /** Reduces a map to the maximums*/
  public static void reduceToMax(Map<Integer, Double> map) {
    double max = -1;
    for (Double val : map.values())
      if (val > max) max = val;
    Iterator<Integer> it = map.keySet().iterator();
    while (it.hasNext()) {
      if (map.get(it.next()) < max) it.remove();
    }
  }

  /** Reduces a map to the minimums*/
  public static void reduceToMin(Map<Integer, Double> map) {
    double min = 2;
    for (Double val : map.values())
      if (val < min) min = val;
    Iterator<Integer> it = map.keySet().iterator();
    while (it.hasNext()) {
      if (map.get(it.next()) > min) it.remove();
    }
  }

  /** Reduces a map to one value with the minimum*/
  public static void reduceToMinMin(Map<Integer, Double> map) {
    if (map.isEmpty()) return;
    double min = 2;
    Integer key = null;
    for (Integer v : map.keySet()) {
      if (map.get(v) < min) {
        key = v;
        min = map.get(v);
      }
    }
    map.clear();
    map.put(key, min);
  }

  /** Reduces a map to one value with the maximum*/
  public static void reduceToMaxMax(Map<Integer, Double> map) {
    if (map.isEmpty()) return;
    double max = -1;
    Integer key = null;
    for (Integer v : map.keySet()) {
      if (map.get(v) > max) {
        key = v;
        max = map.get(v);
      }
    }
    map.clear();
    map.put(key, max);
  }

  /** Finds the equals of a resource*/
  public static boolean findEqualsOf(Integer y1, boolean isFirstRun, AtomicBoolean someChanged,
  		MemoryEqualityStore equalities) {
    if (debug) Announce.debug("Computing equality for", factStore1.lazyToString(y1));
    Set<Integer> maxEqualities = computed.equalToMax(factStore1, y1);

    // ---------------------------------
    //     Compute first the equalities
    // ---------------------------------

    // Cache whatever we can cache to avoid too many database accesses
    Map<Integer, Double> equalityProduct = new TreeMap<Integer, Double>();
    Map<Integer, Set<Integer>> r1_to_r2s = new TreeMap<Integer, Set<Integer>>();
    Map<Integer, Set<Object>> r1_to_x1s = new TreeMap<Integer, Set<Object>>();

    // Loop through all facts about y
    for (Pair<Integer, Object> fact1 : factStore1.factsForArg2(y1)) {
      Integer r1 = fact1.first();
      Object x1 = fact1.second();

      // We sometimes have duplicate facts. Avoid these.
      if (r1_to_x1s.containsKey(r1) && r1_to_x1s.get(r1).contains(x1)) continue;
      // Also avoid classes
      if (debug) Announce.debug(factStore1.toString(r1), factStore1.toString(x1));
      if (factStore1.isClass(x1)) continue;
      if (debug) Announce.debug("... will be examined");
      if (setting.nThreads == 1) {
	      statistics.trial(r1, factStore1, factStore2);
        if (!statistics.isWorthTrying(r1)) {
          if (debug) Announce.debug(r1, "not worth trying");
          continue;
        }
	    }

      // Compute the functionality
      double fun1 = factStore1.functionality(r1, factStore2);
      if (fun1 < Config.THETA) {
        if (debug) Announce.debug(r1, "has too small a functionality:", fun1);
        continue;
      }

      // Find the subproperties we want to try out
      Set<Integer> subprops = statistics.worthTrying(r1);
      if (subprops == null) subprops = r1_to_r2s.get(r1);
      if (subprops == null) {
        subprops = computed.subRelationsOf(factStore1, r1);
        if (Config.subAndSuper) subprops.addAll(computed.superRelationsOf(factStore1, r1));
        /*
        // The following would make rdfs:label in YAGO equal to rdfs:label in DBpedia
        // However, these relationships are used very differently, not everything has a label in DBpedia
        // Therefore, it is better to compute the "true" subrelationship relation
        Integer r1In2 = factStore1.translateTo(r1, factStore2);
        if (r1In2 != null) {
          if (debug) Announce.debug("Adding translated subrelation", factStore1.lazyToString(r1), r1, factStore2.lazyToString(r1In2), r1In2);
          if (!test) computed.setSubrelation(factStore1, r1, r1In2, 1.0);
          if (!test) computed.setSubrelation(factStore2, r1In2, r1, 1.0);
          subprops.add(r1In2);
        }
        */
        if (isFirstRun && Config.initialSmallEquivalence) {
          EntityType sourceType = factStore1.sourceType(r1);
          if (sourceType != null && sourceType != EntityType.RESOURCE) {
            for (Integer r2 : factStore2.relationsWithSourceType(sourceType)) {
              subprops.add(r2);
            }
          }
          if (debug) Announce.debug("Considering by default", subprops);
        }
      }
      r1_to_r2s.put(r1, subprops);
      if (debug) Announce.debug(factStore1.toString(r1), factStore1.toString(x1));
      if (debug) Announce.debug("Considering", subprops);
      if (subprops.isEmpty()) continue;

      // Store the x1 for the inequality computation. In the first run, we will consider only literals 
      if (!(x1 instanceof Integer && isFirstRun)) {
        D.addKeyValue(r1_to_x1s, r1, x1, HashSet.class);
      }

      // Try out all subproperties
      for (Integer r2 : subprops) {
        if (!Config.treatIdAsRelation && factStore2.getIdRel() != null && r2 == -factStore2.getIdRel().id) continue;
        double subprop = computed.subRelation(factStore2, r2, r1);
        double superprop = computed.subRelation(factStore1, r1, r2);
        if (subprop < Config.THETA && superprop < Config.THETA) {
          if (isFirstRun && Config.initialSmallEquivalence) {
            subprop = Config.IOTA;
            superprop = Config.IOTA;
          } else continue;
        }
        double fun2 = factStore2.functionality(r2, factStore1);
        if (debug) Announce.debug(factStore1.toString(r1), factStore1.toString(x1));
        if (debug) Announce.debug("Will look for equals...");
        // Try out all equal x2's
        for (Pair<Object, Double> x2pair : computed.equalToScored(factStore1, x1)) {
        	Object x2 = x2pair.first();
          double xeqv = x2pair.second();
          if (debug) Announce.debug(factStore2.toString(x2), xeqv);
          if (xeqv < Config.THETA) continue;
          
          // Move over to all equality candidates y2
          Set<Object> y2s = new HashSet<Object>();
          for (Object y2 : factStore2.arg2ForRelationAndArg1(r2, x2)) {
            //factSum++;
            // We sometimes have duplicates here. Avoid them.
            if (y2s.contains(y2) || !(y2 instanceof Integer)) continue;
            y2s.add(y2);
            if (setting.nThreads == 1)
              statistics.hit(r1, r2);
            double val = equalityProduct.containsKey(y2) ? equalityProduct.get(y2) : 1.0;
            if (subprop != -1 && fun1 != -1) val *= 1 - xeqv * subprop * fun1;
            if (Config.subAndSuper && superprop != -1 && fun2 != -1) val *= 1 - xeqv * superprop * fun2;
            if (debug) {
            	Announce.debug("  ", factStore2.lazyToString(y2), "scores with");
              Announce.debug("    ", factStore1.lazyToString(x1), factStore1.lazyToString(r1));
              Announce.debug("    ", factStore2.lazyToString(x2), factStore2.lazyToString(r2));
              Announce.debug("     fun=", fun1, "r1<r2=", subprop, "val=", 1 - val);
            }
            equalityProduct.put((Integer) y2, val);
          }
        }
      }
    }

    // ---------------------------------
    //     Invert the equalities
    // ---------------------------------

    if (Config.takeMaxMax) reduceToMinMin(equalityProduct);
    else if (Config.takeMax) reduceToMin(equalityProduct);

    // If we don't punish, we can write directly to the database

    if (!Config.punish) {
    	double max = 0;
    	Set<Integer> vmax = new TreeSet<Integer>();
      boolean foundEquality = false;
      for (Integer y2 : equalityProduct.keySet()) {
        //try {
          double val = 1 - equalityProduct.get(y2);
          if (val < Config.THETA) continue;
          foundEquality = true;
          if (!test) equalities.setValue(factStore1, y1, factStore2, y2, val);
          if (val > max) {
          	vmax.clear();
          	vmax.add(y2);
          	max = val;
          }
          if (val == max)
            if(!Config.takeMaxMax || vmax.isEmpty()) vmax.add (y2);
          if (debug) Announce.debug("Final:", factStore2.toString(y2), val);
        //} catch(NullPointerException ex) {
        //  Announce.warning(ex,y2,equalityProduct);
        //  continue; // There was some null pointer exception in the treemap (?)
        //}
      }
      //timeSum += (int) (System.currentTimeMillis() - startTime);
      boolean isDifferent = !maxEqualities.equals(vmax);
      //if (isDifferent) 
      	//if (setting.nThreads == 1)
      		//numChanged++;
      if (isDifferent)
      	someChanged.set(true);
      return (foundEquality);
    }
    
    assert(false);
		return false; // unreachable

    // this code is stale
    // Otherwise, we have to first invert, then punish, then write to the database
//    boolean changed = false;
//    Iterator<Integer> it = equalityProduct.keySet().iterator();
//    while (it.hasNext()) {
//      Integer y2 = it.next();
//      double val = (1 - equalityProduct.get(y2));
//      if (val < Config.THETA) {
//        it.remove();
//      } else {
//        equalityProduct.put(y2, val);
//        changed = true;
//      }
//    }
//    if (!changed) {
//      //timeSum += (int) (System.currentTimeMillis() - startTime);
//      return (false);
//    }
//
//    // ------------------------------------------------------
//    //     Compute the inequalities
//    // ------------------------------------------------------
//
//    // Fix y2
//    for (Integer y2 : equalityProduct.keySet()) {
//      if (debug) Announce.debug("Punishing for", factStore2.lazyToString(y2));
//      double y1_eqv_y2 = equalityProduct.get(y2);
//
//      // Fix r1
//      nextr1: for (Integer r1 : r1_to_x1s.keySet()) {
//        double ifun1 = factStore1.inverseFunctionality(r1, factStore2);
//        if (ifun1 < Config.THETA) continue;
//
//        // Fix x1
//        for (Object x1 : r1_to_x1s.get(r1)) {
//          if (debug) Announce.debug("  fixed r1, x1=", factStore1.lazyToString(r1), factStore1.lazyToString(x1));
//
//          // Fix r2          
//          if (r1_to_r2s.get(r1) != null) nextsubrel: for (Integer r2 : r1_to_r2s.get(r1)) {
//            double r1_subprop_r2 = computed.subRelation(factStore1, r1, r2);
//            double r2_subprop_r1 = computed.subRelation(factStore2, r2, r1);
//            if (r1_subprop_r2 < Config.THETA && r2_subprop_r1 < Config.THETA) continue;
//            double ifun2 = factStore2.inverseFunctionality(r2, factStore1);
//            double product = 1;
//            if (debug) Announce
//                .debug("  fixed r2", factStore1.lazyToString(r1), factStore2.lazyToString(r2), "r1<r2:", r1_subprop_r2, "ifun:", ifun1);
//
//            // Fix x2
//            for (Object x2 : factStore2.arg1ForRelationAndArg2(r2, y2)) {
//              double xeqv = computed.equality(factStore1, x1, x2);
//              product *= (1 - xeqv);
//              if (product == 0) {
//                if (debug) Announce.debug("  found matched x1/x2", factStore1.lazyToString(x1), factStore2.lazyToString(x2), xeqv);
//                continue nextsubrel;
//              }
//              if (debug) Announce.debug("  found unmatched x1/x2", factStore1.lazyToString(x1), factStore2.lazyToString(x2), xeqv);
//            }
//
//            if(ifun1>Config.THETA && r1_subprop_r2>Config.THETA) y1_eqv_y2 *= 1 - product*ifun1 * r1_subprop_r2;
//            if(Config.subAndSuper && ifun2>Config.THETA && r2_subprop_r1>Config.THETA) y1_eqv_y2 *= 1 - product*ifun2 * r2_subprop_r1;
//            if (debug) Announce.debug("  product=", product);
//            if (debug) Announce.debug("  equivalence after:", y1_eqv_y2, y1_eqv_y2 == 0 ? "next relation" : "");
//            if (y1_eqv_y2 < Config.THETA) break nextr1;
//          }
//        }
//      }
//      equalityProduct.put(y2, y1_eqv_y2);
//    }
//
//    // ---------------------------------
//    //     Set final values
//    // ---------------------------------
//
//    if (Config.takeMaxMax) reduceToMaxMax(equalityProduct);
//    else if (Config.takeMax) reduceToMax(equalityProduct);
//
//    changed = false;
//    for (Integer y2 : equalityProduct.keySet()) {
//      double val = equalityProduct.get(y2);
//      if (val < Config.THETA) continue;
//      changed = true;
//      if (!test) computed.setEquality(factStore1, y1, y2, val);
//      if (debug) Announce.debug("Final:", factStore2.toString(y2), val);
//    }
//    //timeSum += (int) (System.currentTimeMillis() - startTime);
//    boolean isDifferent = !maxEqualities.equals(computed.equalToMax(factStore1, y1));
//    //if (isDifferent) 
//    	//if (setting.nThreads == 1)
//    		//numChanged++;
//    return (changed);
  }

  private static class EntityComputer implements Runnable {
  	AtomicBoolean foundEquality;
  	AtomicBoolean someChanged;
  	int v1;
  	boolean isFirstRun;
  	MemoryEqualityStore equalities;
  	
  	public EntityComputer (AtomicBoolean foundEquality, 
  			AtomicBoolean someChanged, int v1, boolean isFirstRun, MemoryEqualityStore equalities) {
  		this.foundEquality = foundEquality;
  		this.someChanged = someChanged;
  		this.v1 = v1;
  		this.isFirstRun = isFirstRun;
  		this.equalities = equalities;
  	}
  	
  	public void run() {
  		if (findEqualsOf(v1, isFirstRun, someChanged, equalities))
    		foundEquality.set(true);
  	}
  }
  
  /** Runs one whole iteration*/
  public static boolean oneIteration(int run, int startAt) throws IOException {
    if (stop.exists()) return (false);

    // Equality
    statistics.reset();
    int total = factStore1.numEntities() + 1;
    int done = 0;
    //numChanged = 0;
    Announce.progressStart("Computing equality ", total - startAt);
    boolean foundEquality = false;
  	AtomicBoolean aFoundEquality = new AtomicBoolean();
  	AtomicBoolean aSomeChanged = new AtomicBoolean();
  	aFoundEquality.set(false);
  	aSomeChanged.set(false);
    NotifyingBlockingThreadPoolExecutor pool;
    pool = null;
    MemoryEqualityStore equalities = new MemoryEqualityStore();
    long timeStart = System.currentTimeMillis();
    Announce.message("starting equalities at", NumberFormatter.ISOtime());
    PeekIterator<Integer> entities = factStore1.entities();
    if (setting.nThreads != 1) {
      Announce.message("Spawning ", setting.nThreads, " threads");
			pool = new NotifyingBlockingThreadPoolExecutor(
					setting.nThreads, 2*setting.nThreads,
					15, TimeUnit.SECONDS, 60, TimeUnit.MINUTES, null);
    }
    long start = System.currentTimeMillis();
  	for (Integer e1 : entities) {
      if (done++ < startAt) continue;
      Announce.progressStep();
      if (done % reportInterval == 0) {
        //Announce.message("Entities done:", done, "Time per entity:", timeSum / ((float) reportInterval), "ms     Facts per entity:", factSum / ((float) reportInterval),
        //    NumberFormatter.formatMS((long) ((double) (System.currentTimeMillis() - timeStart) / (done - startAt) * (total - done))));
        Announce.message("Entities done:", done, "Time per entity:", (System.currentTimeMillis() - start) / ((float) 1000*1000*reportInterval), "ms",
            NumberFormatter.formatMS((long) ((double) (System.currentTimeMillis() - timeStart) / (done - startAt) * (total - done))));
        if (stop.exists()) break;
      }
      if (factStore1.isClass(e1) || factStore1.isRelation(e1)) continue;
      if (done > total) break;
      if (setting.nThreads == 1)
        foundEquality |= findEqualsOf(e1, run == 0, aSomeChanged, equalities);
      else
      	pool.execute(new EntityComputer(aFoundEquality, aSomeChanged, e1, run == 0, equalities));
    }
  	if (setting.nThreads != 1) {
	    while (true) {
    		try {
					if (pool.await(60, TimeUnit.SECONDS))
						break;
				} catch (InterruptedException e) {
					// continue to wait
				}
    		Announce.message("waiting for task termination...");
	    }
  	}
    entities.close();
    Announce.progressDone();
    Announce.message(foundEquality || aFoundEquality.get() ? "Found equalities" : "Found no equalities");
    Announce.message("reindexing at", NumberFormatter.ISOtime());
    Announce.doing("Reindexing equalities and removing duplicates");
    computed.equalityStore = new MemorySubThingStore(computed.tsvFolder, "eqv_s", run,
    		factStore1, factStore2, equalities);
    Announce.done();
    Announce.message("done equalities at", NumberFormatter.ISOtime());
    if (stop.exists()) return (false);

    // TODO actually the TSV arguments and stuff are useless
    MemorySubThingStore superRelationsOf1 = new MemorySubThingStore(computed.tsvFolder, "superrelations1_s", run);
    MemorySubThingStore superRelationsOf2 = new MemorySubThingStore(computed.tsvFolder, "superrelations2_s", run);
    
    computed.superRelationsOf1 = superRelationsOf1;
    computed.superRelationsOf2 = superRelationsOf2;
    
    // Subrelations 
    int counter = factStore2.numRelations();
    Announce.progressStart("Computing subrelations one direction", counter);
    PeekIterator<Integer> relations = factStore2.relations();
    for (Integer rel : relations) {
      findSuperrelationsOf(rel, factStore2, factStore1);
      if (stop.exists()) break;
      Announce.progressStep();
    }
    relations.close();
    Announce.progressDone();
    if (stop.exists()) return (false);

    counter = factStore1.numRelations();
    Announce.progressStart("Computing subrelations other direction", counter);
    relations = factStore1.relations();
    for (Integer rel : relations) {
      findSuperrelationsOf(rel, factStore1, factStore2);
      if (stop.exists()) break;
      Announce.progressStep();
    }
    relations.close();
    computed.equalityStore.dump(new File(setting.tsvFolder, run + "_eqv.tsv"), factStore1, factStore2);
    computed.superRelationsOf1.dump(new File(setting.tsvFolder, run + "_superrelations1.tsv"), factStore1, factStore2);
    computed.superRelationsOf2.dump(new File(setting.tsvFolder, run + "_superrelations2.tsv"), factStore2, factStore1);
    Announce.progressDone();
    Announce.message("done properties at", NumberFormatter.ISOtime());

    return (aSomeChanged.get());
  }

  /** Computes all subclasses*/
  public static void computeClasses() throws IOException {
    if (stop.exists()) return;
    int counter = factStore2.numClasses();
    Announce.progressStart("Computing subclasses one direction", counter);
    PeekIterator<Integer> classes = factStore2.classes();
    for (Integer cls : classes) {
      if (stop.exists()) break;
      findSuperClassesOf(cls, factStore2, factStore1);
      Announce.progressStep();
    }
    classes.close();
    Announce.progressDone();

    counter = factStore1.numClasses();
    Announce.progressStart("Computing subclasses other direction", counter);
    classes = factStore1.classes();
    for (Integer cls : classes) {
      if (stop.exists()) break;
      findSuperClassesOf(cls, factStore1, factStore2);
      Announce.progressStep();
    }
    classes.close();
    Announce.progressDone();
  }


	private static class FactStoreLoader implements Runnable {
		MemoryFactStore factStore;
		File path;
		String type;

		public FactStoreLoader(File path, String type) {
			this.path = path;
			this.type = type;
			this.factStore = null;
		}

		public void run() {
			try {
				if (type.equals("memory")) {
					FileInputStream f = new FileInputStream(path);
					ObjectInputStream of = new ObjectInputStream(f);
					Object obj = of.readObject();
					of.close();
					if (!(obj instanceof MemoryFactStore)) {
						System.err
								.printf("Serialized object is not a MemoryFactStore, exiting...\n");
						System.exit(2);
					}
					MemoryFactStore result = (MemoryFactStore) obj;
					factStore = result;
					return;
				}
				System.err.printf("Bad targetType %s\n", type);
				assert(false);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				assert(false);
			}
		}
	}
	

	static MemoryFactStore loadFactStore(File factStore, String type) {
		// load with no threading
		FactStoreLoader factStoreLoader = new FactStoreLoader(factStore, type);
		factStoreLoader.run();
		return factStoreLoader.factStore;
	}

  /**Runs the thing*/
  public static void main(String[] args) throws Exception {

  	// Task: load an ontology
    if (args.length == 6) {
	    File sourceFile = new File(args[0]);
	    String parseType = args[1];
	    String target = args[2];
	    String namespace = args[3];
	    String prefix = args[4];
      boolean indexShinglings = Boolean.parseBoolean(args[5]);
	    FactStore factStore = new MemoryFactStore(indexShinglings);
	    FactLoader factLoader = new FactLoader(
		    		sourceFile, parseType, factStore, namespace, prefix);
	    Announce.doing("Loading facts...");
	    factLoader.run();
	    Announce.done();
	    
	    Announce.doing("Saving...");
    	FileOutputStream f = new FileOutputStream(target);
    	ObjectOutputStream of = new ObjectOutputStream(f);
    	of.writeObject(factStore);
    	of.close();
	    
	    System.exit(0);
    }
    
    // Task: dump ontology entities
    if (args.length == 2) {
      File factstore = new File(args[0]);
      Announce.doing("loading fact store");
    	factStore1 = loadFactStore(factstore, "memory");
    	Announce.done();
    	Announce.doing("dumping entities");
    	BufferedWriter w = new BufferedWriter(new FileWriter(args[1]));
    	for (int entity : factStore1.entities()) {
    		if (factStore1.relation(entity) == null) // don't count relations
      		w.write(factStore1.entity(entity).toString() + "\n");
    	}
    	w.close();
    	Announce.done();
	    System.exit(0);
    }
    
    if(args==null || args.length != 1) Announce.help(
    		"Paris <settingFile>\n",
    		"You can specify a file that has no content.",
    		"PARIS will ask for the necessary data and store it in <SettingFile>.\n\n",
    		"Paris <sourceFile> <parseType> <target> <namespace> <prefix> <shinglings>\n",
        "Creates a FactStore from a source ontology.",
        "* <sourceFile> can be an RDF, OWL, NT or TSV file, or a folder containing such files",
        "* <parseType> can be",
        "     yagoNative   (for TSV files of the native YAGO2)",
        "     pierreImdb   (for TSV files in Pierre's format)",
        "     rdf          (for OWL, RDF, or NT files)",
        "     SPO          (for TSV files, where the S is the 1-based column number of",
        "                    the subject, P of the predicate, and O of the object (e.g., 123))",
        "* <target> is the destination where the data will be stored",
        "* <namespace> is the namespace of the ontology you load (e.g., http://www.mpii.de/yago/resource/)",
        "* <prefix> is the prefix by which you want to abbreviate the namespace (e.g., y:)\n",
        "* <shinglings> is true or false to compute the approximate string index or not\n\n",
        "Paris <factstore> <dump>\n",
        "Dump all entities of factstore to dump");
    
    // Task: perform the alignment
    Announce.doing("Starting PARIS");
    Announce.message("Settings:",args[0]);
    setting=new Setting(new File(args[0]));    

    // Prepare the folders
    if (!setting.tsvFolder.exists() && D.readBoolean("Do you want to create the folder " + setting.tsvFolder + "?")) setting.tsvFolder.mkdirs();
    if (!setting.berkeleyFolder.exists() && D.readBoolean("Do you want to create the folder " + setting.berkeleyFolder + "?")) setting.berkeleyFolder
        .mkdirs();
    if(setting.startIteration==0 && setting.startEntity==0) {
      for (File folder : new File[] { setting.berkeleyFolder, setting.tsvFolder }) {
        if (folder.list().length > 0 && D.readBoolean("Do you want to DELETE the files in " + folder + " ?")) {
          Announce.doing("Deleting files in", folder);
          for (File f : folder.listFiles())
            f.delete();
          Announce.done();
        }
      }
    }

    // Set output to the log folder
    Announce.done();
    File logFile=new File(setting.home,"run_"+setting.name+"_"+NumberFormatter.timeStamp()+".txt");
    Announce.message("PARIS is now running.");
    Announce.message("For information about the current state of affairs, look into");
    Announce.message("   ",logFile);    
    Announce.message("To stop this process, create the file");
    Announce.message("   ", stop);    
    if (!test) Announce.setWriter(new FileWriter(logFile)); /**/

    Announce.message("PARIS running at", NumberFormatter.ISOtime());
    Announce.message("@TIME", "startup", System.currentTimeMillis() / 1000L);
    Config.print();

    long startTime = System.currentTimeMillis();

    Announce.doing("Loading fact stores (could take a long time...)");
    FactStoreLoader factStoreLoader1 = new FactStoreLoader(setting.ontology1, setting.ontologyType1);
    FactStoreLoader factStoreLoader2 = new FactStoreLoader(setting.ontology2, setting.ontologyType2);
		Thread factStoreLoaderThread1 = new Thread(factStoreLoader1);
		Thread factStoreLoaderThread2 = new Thread(factStoreLoader2);
    factStoreLoaderThread1.start();
    factStoreLoaderThread2.start();
    factStoreLoaderThread1.join();
    factStoreLoaderThread2.join();
    factStore1 = factStoreLoader1.factStore;
    factStore2 = factStoreLoader2.factStore;
    Announce.done();
    
    computed = new Result(factStore1, factStore2, setting.berkeleyFolder, setting.tsvFolder, true);

    if (debug) Announce.setLevel(Level.DEBUG);
    // ************ If you want to play around, do it in the following {} !
    if (test) {
      computed.startIteration(99);
      Announce.setLevel(Level.DEBUG);
      debug = true;
      AtomicBoolean f = new AtomicBoolean();
      MemoryEqualityStore m = null;
      factStore1.debugEntity("Zac_Alcorn");
      factStore2.debugEntity("dbp:resource/Zac_Alcorn");
      findEqualsOf(factStore1.entity("Zac_Alcorn").id, true, 	f, m);
      factStore1.close();
      factStore2.close();
      computed.close();
      D.exit();
    }
    Announce.message("Factstores loaded at", NumberFormatter.ISOtime());
    Announce.message("To stop this process, create the file");
    Announce.message("   ", stop);
    for (int i = setting.startIteration; i < setting.endIteration; i++) {
    	Announce.message("@TIME", i+1, System.currentTimeMillis() / 1000L);
      if (stop.exists()) break;
      if (!oneIteration(i, setting.startEntity)) break;
      setting.startEntity = 0;
    }
    Announce.message("@TIME", setting.endIteration + 1, System.currentTimeMillis() / 1000L);
    computed.startIteration(setting.endIteration);
    Announce.message("@TIME", "classes", System.currentTimeMillis() / 1000L);
    Announce.message("computing classes at", NumberFormatter.ISOtime());
    computeClasses(); 
    Announce.message("computed classes at", NumberFormatter.ISOtime());
    computed.print(); 
    factStore1.close();
    factStore2.close();
    computed.close();
    if (stop.exists()) Announce.message("Stopped because found", stop);
    System.out.printf("PARIS terminated after %d milliseconds\n",
    		System.currentTimeMillis() - startTime);
    Announce.message("@TIME", "shutdown", System.currentTimeMillis() / 1000L);
    Announce.close();
  }
}

