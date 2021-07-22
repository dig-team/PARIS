package paris;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import paris.storage.FactStore;

import javatools.administrative.Announce;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
*
* It is licensed under a Creative Commons Attribution Non-Commercial License
* by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
* see http://webdam.inria.fr/paris
*
* This class is an implementation of Neighborhood using HashMaps and arrays
* Arrays are used for faster lookups, HashMaps are used for faster sparse iteration
* */


public class HashArrayNeighborhood extends Neighborhood {	
	/** child neighborhoods for a simple relation */
	HashMap<Integer, HashArrayNeighborhood> children;
	HashArrayNeighborhood[] childrenArray;
	int maxRelationId;
		
	HashArrayNeighborhood(FactStore fs, int run, boolean root, int maxDepth) {
		this.children = new HashMap<Integer, HashArrayNeighborhood>();
		this.maxRelationId = fs.maxRelationId();
		this.childrenArray = new HashArrayNeighborhood[2*maxRelationId];
		this.run = run;
		this.father = null;
		this.depth = 0;
		this.maxDepth = maxDepth;
		this.fs = fs;
		assert(maxRelationId > 0);
		resetOccurrenceScore();
	}
	
	HashArrayNeighborhood(HashArrayNeighborhood father, int run) {
		this(father.fs, run, false, father.maxDepth);
		this.father = father;
		this.depth = father.depth + 1;
		if (maxDepth == depth) {
			this.childrenArray = null; // we won't need children, spare the memory
//		this.children = null; <- could also be done, but we would need to tweak accessors
		}
	}
	
	HashArrayNeighborhood addChild(int run, int relation) {
		if (maxDepth == depth)
			return null;
		HashArrayNeighborhood n = new HashArrayNeighborhood(this, run);
		children.put(relation, n);
		childrenArray[relation+maxRelationId] = n;
		return n;
	}
	
	@Override
	public Neighborhood getChild(int run, int relation) {
		if (maxDepth == depth)
			return null;
		HashArrayNeighborhood n = childrenArray[relation + maxRelationId];
		if (n == null) {
			n = addChild(run, relation);
		}
		return n;
	}
	
	@Override
	public HashArrayNeighborhood getChildRO(int relation) {
		return childrenArray[relation + maxRelationId];
	}
	
	boolean isLeaf() {
		return children.keySet().isEmpty();
	}
	
	/** reset occurrence and score */
	void reset() {
		resetOccurrenceScore();
		for (HashArrayNeighborhood n : children.values())
			n.reset();
	}
	
	/** prune neighborhoods which do not occur sufficiently often */
	void prune(double threshold) {
		for (Iterator<Map.Entry<Integer, HashArrayNeighborhood>> i = children.entrySet().iterator(); i.hasNext(); )  
		{  
		    Map.Entry<Integer, HashArrayNeighborhood> entry = i.next();  
		    if (this.occurrence * threshold >= entry.getValue().occurrence)
		    {  
		        i.remove();  
		    } else {
		    	entry.getValue().prune(threshold);
		    }
		}
	}
	
	public void print(JoinRelation prefix) {
		Announce.message(prefix, "occurrence ", occurrence, "score", score, "ongoingScore", ongoingScore);
		for (Integer relation : children.keySet()) {
			JoinRelation prefix2 = new JoinRelation(prefix);
			prefix2.push(relation);
			children.get(relation).print(prefix2);
		}
	}
	
	@Override
	public Iterator<Map.Entry<Integer, HashArrayNeighborhood>> childrenEntrySet() {
		return children.entrySet().iterator();
	}
	
	@Override
	public Collection<? extends Neighborhood> children() {
		return children.values();
	}
	
	@Override
	public Collection<Integer> keys() {
		return children.keySet();
	}
	
	@Override
	public Set<? extends Map.Entry<Integer, ? extends Neighborhood>> entries() {
		return children.entrySet();
	}
	
	@Override
	public void removeChild(int relation) {
		children.remove(relation);
		childrenArray[relation+maxRelationId] = null;
	}
	
	@Override
	public void reduceWith(Neighborhood n) {
		if (n == null)
			return;
		this.occurrence += n.occurrence;
		this.score += n.score;
		this.ongoingScore *= n.ongoingScore;
		this.run = Math.min(this.run, n.run);
		
		for (Iterator<? extends Map.Entry<Integer, ? extends Neighborhood>> i = n.childrenEntrySet(); i.hasNext();)  
		{
			Map.Entry<Integer, ? extends Neighborhood> e = i.next();
			Neighborhood n2 = getChild(e.getValue().run, e.getKey());
			n2.reduceWith(e.getValue());
		}
	}
	
	@Override
	public void scaleDown(double val) {
		this.occurrence /= val;
		this.score /= val;
		assert(this.ongoingScore == 1.);
		
		for (Neighborhood child : children())  
		{
			child.scaleDown(val);
		}
	}
}
