package paris;

import paris.storage.FactStore;

/**
 * This class is part of the PARIS ontology matching project at INRIA
 * Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License by
 * the author Fabian M. Suchanek (http://suchanek.name). For all further
 * information, see http://webdam.inria.fr/paris
 * 
 * This class stores the normalizer of relations in a FactStore
 */

public abstract class RelationNormalizer {

	FactStore fs1;
	
  /** intermediate count for the normalizer, for one relation */
  protected double currentRealNormalizer = 1;
  
	public RelationNormalizer(FactStore fs1) {
		this.fs1 = fs1;
	}
	
	public abstract void incrementSimpleNormalizer(JoinRelation r1, double score);
	public abstract void incrementSimpleNormalizer(int r1, double score);
	
	protected abstract void incrementNormalizer(JoinRelation r1, double score);
	protected abstract void incrementNormalizer(int r1, double score);
	
	public abstract void scaleDownNormalizer(JoinRelation r1, double score);
	protected abstract void scaleDownSimpleNormalizer(JoinRelation r1, double score);
	
	public abstract void incrementCurrentRealNormalizer(double score);
	
	public abstract Iterable<JoinRelation> allRelations();
	public abstract double getSimpleNormalizer(JoinRelation r);
	public abstract double getRealNormalizer(JoinRelation r);
	
  /** Get the normalizer for a relation, according to the configuration */
  public double getNormalizer(JoinRelation r) {
  	return Config.realNormalizer ? getRealNormalizer(r) : getSimpleNormalizer(r);
  }
  
  public void addNormalizer(int r1) {
  	// we assume that r1 is the same relation that was used for previous calls of incrementCurrentRealNormalizer
  	double t = 1 - currentRealNormalizer;
  	// reset the current product
  	currentRealNormalizer = 1;
  	// take into account the current product
  	incrementNormalizer(r1, t);
  }
  
  /** Take into account the current product for the real normalizer */
  public void addNormalizer(JoinRelation r1) {
  	// we assume that r1 is the same relation that was used for previous calls of incrementCurrentRealNormalizer
  	double t = 1 - currentRealNormalizer;
  	// reset the current product
  	currentRealNormalizer = 1;
  	// take into account the current product
  	incrementNormalizer(r1, t);
  }
  
  /** Merge another RelationNormalizer into the current one (reduce operation) */
  public void reduceWith(RelationNormalizer ra) {
  	for (JoinRelation r1 : ra.allRelations()) {
  		incrementSimpleNormalizer(r1, ra.getSimpleNormalizer(r1));
  		incrementNormalizer(r1, ra.getRealNormalizer(r1));
  	}  	
  	
  }
  
  public void scaleDown(int n) {
  	for (JoinRelation r1 : allRelations()) {
  		scaleDownSimpleNormalizer(r1, n);
  		scaleDownNormalizer(r1, n);
  	}
  }
}
