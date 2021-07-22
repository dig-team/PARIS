package paris;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
*
* It is licensed under a Creative Commons Attribution Non-Commercial License
* by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
* see http://webdam.inria.fr/paris
*
* This class stores an alignment between one join relations and other join relations.
* It is a tree node, with a pointer to its father (representing the current relation without its last element)
* and children (representing the current relation with one more relation joined)
* */


public abstract class Neighborhood {
	/** Stores at which iteration we created this neighborhood? */
	int run;
	/** Stores if we have values to propagate */
	boolean dirty;

	/** attempt threshold: after that many occurrences, enforce that score/occurrence > interestigness threshold
	 *  This is only used if setting.interestingnessThreshold is set */
	public static double attemptThreshold = 1;
	
	/** interestingness threshold
	 *  This is only used if setting.interestingnessThreshold is set */
	public static double interestingnessThreshold = Config.epsilon;
	
	/** how often does this neighborhood occurs? */ 
	double occurrence;
	
	/** score for the alignment of the reference relation to the current node */
	double score;
	
	/** ongoing score */
	double ongoingScore;
	
	HashArrayNeighborhood father;
	
	int depth;
	
	public FactStore fs;
	int maxDepth;

	/** Register one occurrence of the current relation */
	void registerOccurrence(double occurrence) {
		this.occurrence += occurrence;
	}
	
	/** Mark that we are dirty */
	void markDirty() {
		if (dirty) return;
		dirty = true;
		if (father != null)
			father.markDirty();
	}
	
	/** Register alignment score for the current relation */
	void registerScore(double score) {
		ongoingScore *= 1 - score;
		markDirty();
	}
	
	/** Test if we are older than some run */
	boolean olderThan(int run) {
		return this.run < run;
	}
	
	/** Reset occurrence scores */
	void resetOccurrenceScore() {
		this.occurrence = 0;
		this.score = 0;
		this.ongoingScore = 1;
		dirty = false;
	}
	
	/** Get the child node obtained by joining the current relation with newRelation */ 
	public abstract Neighborhood getChild(int run, int newRelation);
	
	/** Get the child node obtained by joining the current relation with newRelation, don't create a new one if it doesn't exist */ 
	public abstract Neighborhood getChildRO(int newRelation);
	
	/** Get an iterator on the entries of all children */
	public abstract Iterator<? extends Map.Entry<Integer, ? extends Neighborhood>> childrenEntrySet();
	
	/** Get an iterator on all children */
	public abstract Collection<? extends Neighborhood> children();
	
	/** Get an iterator on all keys */
	public abstract Collection<Integer> keys();

	/** Get an iterator on all entries */
	public abstract Set<? extends Map.Entry<Integer, ? extends Neighborhood>> entries();

	/** Reduce this result with another result */
	public abstract void reduceWith(Neighborhood n);
	
	public abstract void scaleDown(double val);

	
	public boolean worthTrying() {
		if (this.depth <= 1)
			return true;
		if (this.occurrence < Neighborhood.attemptThreshold)
			return true;
		return (this.score / this.occurrence >= Neighborhood.interestingnessThreshold);
	}
	
	/** Propagate the ongoing scores throughout the tree */
	void propagateScores() {
		if (!dirty) return;
		score += 1 - ongoingScore;
		ongoingScore = 1;
		dirty = false;
		for (Neighborhood n : children())
			n.propagateScores();
	}

	public abstract void removeChild(int relation);
	
	/** Remove nodes with score below a threshold, using a certain normalizer */
	boolean thresholdByNormalizer(double norm, double threshold, boolean toplevel) {
		boolean interesting = false;
		interesting = interesting || ((score + fs.setting.smoothNumeratorSampling) / (norm + fs.setting.smoothDenominatorSampling) >= threshold);
		Iterator<? extends Map.Entry<Integer, ? extends Neighborhood>> it = entries().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, ? extends Neighborhood> n = it.next();
			boolean result = n.getValue().thresholdByNormalizer(norm, threshold, false);
			if (toplevel && Config.allLengthOneAfterSample)
				result = true; // don't remove this child and say the toplevel node is interesting
			if (!result)
				it.remove();
			interesting = interesting || result;
		}
		return interesting;
	}
	
	public boolean isEmpty() {
		return children().isEmpty();
	}
}