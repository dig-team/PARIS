package paris;

import java.util.HashMap;
import java.util.Map;

import paris.storage.FactStore;

import javatools.administrative.D;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores the normalizer for relations using a HashMap
 * Hence, it uses the join relations themselves internally.
 */


public class HashRelationNormalizer extends RelationNormalizer {
	/** the simpler normalizer value mistakenly used in PARIS 0.1 */
  Map<JoinRelation, Double> simpleNormalizer;
  /** the normalizer as presented in the PARIS paper */
  Map<JoinRelation, Double> realNormalizer;

  public HashRelationNormalizer(FactStore fs1) {
  	super(fs1);
    simpleNormalizer = new HashMap<JoinRelation, Double> ();
    realNormalizer = new HashMap<JoinRelation, Double> ();
  }
  
  /** Add a value to the simple normalizer for a relation */
  public void incrementSimpleNormalizer(JoinRelation r1, double score) {
		D.addKeyValueDbl(simpleNormalizer, r1, score);
  }
  
  public void incrementSimpleNormalizer(int r1, double score) {
  	incrementSimpleNormalizer(new JoinRelation(fs1, r1), score);
  }
  
  /** Update the current product for the real normalizer */
  public void incrementCurrentRealNormalizer(double score) {
  	// we assume that the current relation is the same relation that was used for previous calls of incrementCurrentRealNormalizer
		currentRealNormalizer *= 1 - score;
  }
  
  /** Add a value to the real normalizer */
  protected void incrementNormalizer(JoinRelation r1, double score) {
  	// we assume that there is no intermediate count going on
  	assert(currentRealNormalizer == 1);
  	D.addKeyValueDbl(realNormalizer, r1, score);
  }
  
  protected void incrementNormalizer(int r1, double score) {
  	// we assume that there is no intermediate count going on
  	incrementNormalizer(new JoinRelation(fs1, r1), score);
  }
  
  /** Get the normalizer for a relation and a certain normalizer table */
  private double getNormalizer(JoinRelation r, Map<JoinRelation, Double> n) {
  	return n.containsKey(r) ? n.get(r) : 0.0;
  }
  
  /** Get the real normalizer for a relation */
  public double getRealNormalizer(JoinRelation r) {
  	return getNormalizer(r, realNormalizer);
  }
  
  /** Get the simple normalizer for a relation */
  public double getSimpleNormalizer(JoinRelation r) {
  	return getNormalizer(r, simpleNormalizer);
  }
  
  /** Get all relations for which a normalizer was stored */
  public Iterable<JoinRelation> allRelations() {
  	return simpleNormalizer.keySet();
  }
  
  protected void scaleDownSimpleNormalizer(JoinRelation r1, double score) {
  	double val = simpleNormalizer.get(r1);
  	simpleNormalizer.put(r1, val/score);
  }
  public void scaleDownNormalizer(JoinRelation r1, double score) {
  	double val = realNormalizer.get(r1);
  	realNormalizer.put(r1, val/score);
  }
}

