package paris;

import java.io.Closeable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import paris.storage.FactStore;

import javatools.administrative.Announce;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an alignment between relations, using a matrix (native arrays)
 * Hence, it is faster than HashSubRelationStore but not suitable for large numbers of relations (especially joins) */


public class MatrixSubRelationStore extends SubRelationStore implements Closeable {
  double matrix[][];
  int size1, size2;

  /** The log writer */
  protected Writer tsvWriter;
  
  public MatrixSubRelationStore(FactStore fs1, FactStore fs2) {
  	super(fs1, fs2);
  	size1 = fs1.maxJoinRelationCode();
  	size2 = fs2.maxJoinRelationCode();
  	Announce.message("about to allocate matrix of", size1, "times", size2);
  	matrix = new double[size1][size2];
  }
  
  public void clear() {
  	for (int i = 0; i < size1; i++)
  		for (int j = 0; j < size2; j++)
  			matrix[i][j] = 0;
  }


  /* (non-Javadoc)
	 * @see paris.SubThingStore#getValue(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public double getValue(JoinRelation sub, JoinRelation supr) {
		return getValueCode(sub.code(), supr.code());
	}
	
	public double getValueCode(int sub, int supr) {
		return matrix[sub][supr];
	}

	@Override
  public void set(JoinRelation sub, JoinRelation supr, double val) {
//  	long myId = freshPairId++;
//  	e.id = myId;
  	matrix[sub.code()][supr.code()] = val;
  	matrix[sub.code(true)][supr.code(true)] = val;
  }

	@Override
  public Collection<SubPair<JoinRelation>> all() {
  	Collection<SubPair<JoinRelation>> result = new ArrayList<SubPair<JoinRelation>>();
    for (int i = 0; i < size1; i++)
    	for (int j = 0; j < size2; j++)
    		if (matrix[i][j] > 0) {
    			SubPair<JoinRelation> item = new SubPair<JoinRelation>(fs1.joinRelationByCode(i), fs2.joinRelationByCode(j), matrix[i][j]);
        	if (item.sub.isTrivial() || item.supr.isTrivial()) continue;
        	result.add(item);    			
    		}
    return result;	
  }
  
}
