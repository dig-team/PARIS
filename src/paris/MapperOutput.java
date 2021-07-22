package paris;

import javatools.administrative.Announce;
import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores the result of one pass by one thread (i.e., the result of the map operation, hence the name).
 * It can be reduced with another instance of the same object to get the merged results.
 * Note that this does not store computed entity alignments: they are stored in an EqualityStore which
 * is shared between threads, because the concurrent writes are not a problem in this case. */


public class MapperOutput {
	/** Relation alignments */
  HashArrayNeighborhood[] neighborhoods;
  /** Relation normalizers */
  RelationNormalizer relationNormalizer;
  FactStore fs;
  
  public MapperOutput(MapperOutput other) {
  	this(other.fs);
  	reduceWith(other);
  }
  
  public MapperOutput(FactStore fs) {
  	this.fs = fs;
  	this.neighborhoods = new HashArrayNeighborhood[fs.maxJoinRelationCode()];
  	if (fs.setting.optimizeNoJoins && fs.getJoinLengthLimit() == 1)
  		this.relationNormalizer = new ArrayRelationNormalizer(fs);
  	else
  		this.relationNormalizer = new HashRelationNormalizer(fs);
  }
  
  /** Reduce this result with another result */
  public void reduceWith(MapperOutput mo) {
  	for (int i = 0; i < fs.maxJoinRelationCode(); i++) {
  		Neighborhood other = mo.neighborhoods[i];
  		if (other == null)
  			continue;
  		if (neighborhoods[i] == null) {
  			neighborhoods[i] = new HashArrayNeighborhood(other.fs, other.run, true, other.maxDepth);
  		}
			neighborhoods[i].reduceWith(mo.neighborhoods[i]);
  	}
  	relationNormalizer.reduceWith(mo.relationNormalizer);
  }
  
  public void printNeighborhoodsForFactStore(FactStore fs) {
  	
  }
  
  public void scaleDown(int n) {
  	for (int i = 0; i < fs.maxJoinRelationCode(); i++) {
  		if (this.neighborhoods[i] == null)
  			continue;
  		this.neighborhoods[i].scaleDown(n);
  	}
  	relationNormalizer.scaleDown(n);
  }
  public void print(FactStore other) {
  	for (int i = 0; i < fs.maxJoinRelationCode(); i++) {
  		if (neighborhoods[i] == null) continue;
  		JoinRelation r = fs.joinRelationByCode(i);
  		Announce.message("== normalizer for", r, ": ", relationNormalizer.getNormalizer(r), "==");
  		Announce.message("== neighborhood for", r, "==");
  		neighborhoods[i].print(new JoinRelation(other));
  	}
  }
}
