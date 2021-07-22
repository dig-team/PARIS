package paris;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import paris.storage.FactStore;


import javatools.datatypes.Pair;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores pairs of entities with a score. It is used for the entity alignment.
 * The first entity will live in one fact store, and the second entity will live in another fact store.
 * If a TSV file is set, every assignment will be printed into that file (in addition to storing it in the data base).
 * The pairs are represented by associating one element to each other element (so we assume takeMaxMax).
 * This class is thread-safe and one instance is shared between all threads in findEqualsOf */


public class EqualityStore extends SubThingStore<Integer> implements Closeable {
  /** Maps an entity from the first factstore to its equality pair*/
  protected int[] subIndexMatch;
  protected double[] subIndexScore;

  /** Maps an entity from the second factstore to its equality pair*/
  protected int[] superIndexMatch;
  protected double[] superIndexScore;

	public EqualityStore(FactStore fs1, FactStore fs2) throws IOException {
  	super(fs1, fs2);
    subIndexMatch = new int[fs1.numEntities() + fs1.numClasses() + 1];
    subIndexScore = new double[fs1.numEntities() + fs1.numClasses() + 1];
    superIndexMatch = new int[fs2.numEntities() + fs2.numClasses() + 1];
    superIndexScore = new double[fs2.numEntities() + fs2.numClasses() + 1];
  }
	
	public String toTsv(SubPair<Integer> p) {
		return fs1.entity(p.sub)+"\t"+fs2.entity(p.supr)+"\t"+p.val+"\n";
	}
	
	public void takeMaxMaxBothWays() {
		// populate superIndex
		for (int i = 0; i < subIndexScore.length; i++) {
			if (superIndexScore[subIndexMatch[i]] < subIndexScore[i]) {
				superIndexScore[subIndexMatch[i]] = subIndexScore[i];
				superIndexMatch[subIndexMatch[i]] = i;
			}
		}
		// reset the sub entities which weren't the MaxMax for their super entities
		for (int i = 0; i < subIndexScore.length; i++) {
			if (superIndexMatch[subIndexMatch[i]] != i) {
				subIndexScore[i] = 0;;
			}
		}
	}

  /* (non-Javadoc)
	 * @see paris.SubThingStore#getValue(java.lang.Integer, java.lang.Integer)
	 */
  @Override
	public double getValue(Integer sub, Integer supr) {
    SubPair<Integer> e = get(sub, supr);
    //Announce.message("@getequality", sub, supr, (e != null ? e.val : 0));
    return (e != null ? e.val : 0);
  }
  
  public double getValueInt(int sub, int supr) {
  	if (subIndexScore[sub] == 0. || subIndexMatch[sub] != supr)
  		return 0.;
  	return subIndexScore[sub];
  }

  /** Returns a pair*/
  protected SubPair<Integer> get(Integer sub, Integer supr) {
  	if (subIndexScore[sub] == 0. || subIndexMatch[sub] != supr)
  		return null;
  	return new SubPair<Integer>(sub, subIndexMatch[sub], subIndexScore[sub]);
  }
  
  @Override
  public void set(Integer sub, Integer supr, double val) {
  	set(new SubPair<Integer>(sub, supr, val));
  }
  
  public void set(SubPair<Integer> e) {
  	if (e.val < subIndexScore[e.sub])
  		return;
  	subIndexMatch[e.sub] = e.supr;
  	subIndexScore[e.sub] = e.val;
  	// because of concurrency, we cannot write superIndex just now
  	// takeMaxMaxBothWays will populate it
  }
  
  /* (non-Javadoc)
	 * @see paris.SubThingStore#pairs(paris.FactStore, paris.FactStore)
	 */
  @Override
	public Collection<SubPair<Integer>> all() {
  	ArrayList<SubPair<Integer>> result = new ArrayList<SubPair<Integer>>();

    for (int i = 0; i < subIndexScore.length; i++) {
    	if (subIndexScore[i] > 0) {
    		result.add(new SubPair<Integer>(i, subIndexMatch[i], subIndexScore[i]));
    	}
    }
    return result;
  }
  

	public Set<Integer> superOf(Integer sub) {
    Set<Integer> result = new TreeSet<Integer>();
  	if (subIndexScore[sub] < Config.THETA)
  		return result;
  	result.add(subIndexMatch[sub]);
  	return (result);
  }

	public Collection<Pair<Object, Double>> superOfScored(Integer sub) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    if (subIndexScore[sub] < Config.THETA)
    	return result;
    result.add(new Pair<Object, Double>(subIndexMatch[sub], subIndexScore[sub]));
    return (result);
  }


	public Set<Integer> subOf(Integer supr) {
    Set<Integer> result = new TreeSet<Integer>();
  	if (superIndexScore[supr] < Config.THETA)
  		return result;
  	result.add(superIndexMatch[supr]);
  	return (result);
  }


	public Collection<Pair<Object, Double>> subOfScored(Integer supr) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    if (superIndexScore[supr] < Config.THETA)
    	return result;
    result.add(new Pair<Object, Double>(superIndexMatch[supr], superIndexScore[supr]));
    return (result);
  }
	
//	/** only keep the n pairs in the store with highest score (and pairs tied with them) */
//	public synchronized void threshold(int n) {
//	  DoubleList scores=new DoubleArrayStack();
//	  for(int i = 0; i < subIndexScore.length; i++) {
//	  	if(subIndexScore[i] == 0.) continue;
//	    DoubleCollection values=((IntKeyDoubleOpenHashMap)(it.getValue())).values();
//	    scores.addAll(values);
//	  }
//	  if(scores.size()<=n) return;
//	  double[] scoresdb=scores.toArray();
//	  Arrays.sort(scoresdb);
//	  double pivot=scoresdb[scoresdb.length-n];
//	  it = index.entries();
//    while(it.hasNext()) {
//      it.next();
//      IntKeyDoubleOpenHashMap values=((IntKeyDoubleOpenHashMap)(it.getValue()));
//      IntKeyDoubleMapIterator it2 = values.entries();
//      while(it2.hasNext()) {
//        it2.next();
//        if(it2.getValue()<pivot) it2.remove();
//      }
//    }
//	}

}
