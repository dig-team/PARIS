package paris;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javatools.administrative.D;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class serves to optimize the matching process. It stores which relations found counterparts in the first iteration.
 * When we know that, we can stop trying the other relations over and over again in vain.*/
public class Statistics {

  /** Number of trials per relation, after which we will freeze which relations to consider*/
  public int TRIEDOFTENENOUGH = 100;

  /** Minimum number of matches with a foreign relation that will make the relation considered*/
  public int MINHITS = 2;

  /** Number of hits of one relation in the other store*/
  protected Map<Integer, Map<Integer, Integer>> hits = new TreeMap<Integer, Map<Integer, Integer>>();

  /** Number of total trials*/
  protected Map<Integer, Integer> trialsPerRelation = new TreeMap<Integer, Integer>();

  /** Resets the statistics*/
  public void reset() {
    hits = new TreeMap<Integer, Map<Integer, Integer>>();
    trialsPerRelation = new TreeMap<Integer, Integer>();
  }
  
  /** register that relation r1 has found a match with a foreign relation r2*/
  public void hit(Integer p1, Integer p2) {
    Map<Integer, Integer> myMap = hits.get(p1);
    if (myMap == null) hits.put(p1, myMap = new TreeMap<Integer, Integer>());
    D.addKeyValue(myMap, p2, 1);
  }

  /** Register that it has been tried to find a match for relation r1*/
  public void trial(Integer p1, FactStore factStore1, FactStore factStore2) {
    D.addKeyValue(trialsPerRelation, p1, 1);
    // Remove all those that are not worth trying
    if (trialsPerRelation.get(p1) == TRIEDOFTENENOUGH) {
      if(hits.get(p1)==null) hits.put(p1,new TreeMap<Integer, Integer>());
      Iterator<Integer> p2it=hits.get(p1).keySet().iterator();
      StringBuilder b = new StringBuilder();
      while(p2it.hasNext()) {
        Integer p2=p2it.next();
        if (hits.get(p1).get(p2)>=MINHITS) {
          b.append(factStore2.toString(p2)).append(" ");
        } else {
          p2it.remove();
        }
      }
      //Announce.message("Will try", factStore1.toString(p1), "only with", b);
    }
  }

  /** TRUE if this relation has any matching foreign relations*/
  public boolean isWorthTrying(Integer p1) {
    if (D.getOrZero(trialsPerRelation, p1) < TRIEDOFTENENOUGH) return (true);
    return(!hits.get(p1).isEmpty());
    //return(true);
  }
  
  /** TRUE if this pair is worth trying*/
  public boolean isWorthTrying(Integer p1, Integer p2) {    
    //return(true);
    if (D.getOrZero(trialsPerRelation, p1) < TRIEDOFTENENOUGH) return (true);
    return(hits.get(p1).containsKey(p2));
  }
  
  /** Returns all foreign relations that are worth trying (or NULL for "all")*/
  public Set<Integer> worthTrying(Integer p1) {
    if (D.getOrZero(trialsPerRelation, p1) < TRIEDOFTENENOUGH) return (null);
    return(hits.get(p1).keySet());
  }
}
