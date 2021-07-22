package paris;

import java.util.ArrayList;
import java.util.Collection;

import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores the normalizer for relations using a native array.
 * Hence, it uses the int code of join relations internally. */

public class ArrayRelationNormalizer extends RelationNormalizer {
	/** the simpler normalizer value mistakenly used in PARIS 0.1 */
	double simpleNormalizer[];
  /** the normalizer as presented in the PARIS paper */
  double realNormalizer[];

  public ArrayRelationNormalizer(FactStore fs1) {
  	super(fs1);
    simpleNormalizer = new double[fs1.numRelations()];
    realNormalizer = new double[fs1.numRelations()];
  }
  
  /** Add a value to the simple normalizer for a relation */
  public void incrementSimpleNormalizer(JoinRelation r1, double score) {
		incrementSimpleNormalizer(r1.code(), score);
  }
  
  public void incrementSimpleNormalizer(int r1, double score) {
  	simpleNormalizer[r1] += score;
  }
  
  /** Update the current product for the real normalizer */
  public void incrementCurrentRealNormalizer(double score) {
  	// we assume that the current relation is the same relation that was used for previous calls of incrementCurrentRealNormalizer
		currentRealNormalizer *= 1 - score;
  }
  
  /** Add a value to the real normalizer */
  protected void incrementNormalizer(JoinRelation r1, double score) {
  	// we assume that there is no intermediate count going on
  	incrementNormalizer(r1.code(), score);
  }
  protected void incrementNormalizer(int r1, double score) {
  	// we assume that there is no intermediate count going on
  	assert(currentRealNormalizer == 1);
  	realNormalizer[r1] += score;
  }
  
  protected void scaleDownSimpleNormalizer(JoinRelation r1, double score) {
  	simpleNormalizer[r1.code()] /= score;
  }
  public void scaleDownNormalizer(JoinRelation r1, double score) {
  	realNormalizer[r1.code()] /= score;
  }
  
  /** Get the real normalizer for a relation */
  public double getRealNormalizer(JoinRelation r) {
  	return realNormalizer[r.code()];
  }
  
  /** Get the simple normalizer for a relation */
  public double getSimpleNormalizer(JoinRelation r) {
  	return simpleNormalizer[r.code()];
  }

  /** Get all relations for which a normalizer was stored */
  public Iterable<JoinRelation> allRelations() {
  	Collection<JoinRelation> result = new ArrayList<JoinRelation>();
  	for (int i = 0; i < realNormalizer.length; i++)
  		result.add(new JoinRelation(fs1, i));
  	return result;
  }

}

