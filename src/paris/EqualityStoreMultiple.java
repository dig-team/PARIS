package paris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import paris.SubThingStore.SubPair;
import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores pairs of entities with a score. It is used for the entity alignment.
 * The first entity will live in one fact store, and the second entity will live in another fact store.
 * If a TSV file is set, every assignment will be printed into that file (in addition to storing it in the data base).
 * This class keeps all alignments and enforces takeMaxMax later by a simple heuristic of maximum weighted bipartite matching.
 * This class is thread-safe and one instance is shared between all threads in findEqualsOf. */

public class EqualityStoreMultiple {
	
  /** only remember at most that many candidates per entity */
  public static int maxMatches = 5;
 
	FactStore fs1;
	FactStore fs2;
	protected SubPair<Integer> subIndex[][];

	@SuppressWarnings("unchecked")
	public EqualityStoreMultiple(FactStore fs1, FactStore fs2) {
		this.fs1 = fs1;
		this.fs2 = fs2;
		subIndex = new SubPair[fs1.numEntities() + fs1.numClasses() + 1][];
	}

	@SuppressWarnings("unchecked")
	public void set(int sub, Map<Integer, Double> equalities) {
		if (EqualityStoreMultiple.maxMatches > 0) {
			ArrayList<SubPair<Integer>> array = new ArrayList<SubPair<Integer>>();
			for (Map.Entry<Integer, Double> e : equalities.entrySet()) {
				array.add(new SubPair<Integer>(sub, e.getKey(), 1 - e.getValue()));
			}
			Collections.sort(array);
			Collections.reverse(array);
			int mx = array.size();
			if (EqualityStoreMultiple.maxMatches < mx)
				mx = EqualityStoreMultiple.maxMatches;
			subIndex[sub] = new SubPair[mx];
			for (int j = 0 ; j < mx; j++) {
				subIndex[sub][j] = array.get(j);
			}
			return;
		}
		subIndex[sub] = new SubPair[equalities.size()];
		int i = 0;
		for (Map.Entry<Integer, Double> e : equalities.entrySet()) {
			subIndex[sub][i] = new SubPair<Integer>(sub, e.getKey(), 1 - e.getValue());
			i++;
		}
	}
	
	/** Approximate weighted bipartite maximum matching to ensure that each entity is
	 * mapped to at most one other entity */
	public EqualityStore takeMaxMaxClever() throws IOException {
		EqualityStore eq = new EqualityStore(fs1, fs2);
		boolean[] matched1 = new boolean[fs1.numEntities() + fs1.numClasses() + 1];
		boolean[] matched2 = new boolean[fs2.numEntities() + fs2.numClasses() + 1];
		ArrayList<SubPair<Integer>> matches = new ArrayList<SubPair<Integer>>();
		for (int i = 0; i < subIndex.length; i++) {
			if (subIndex[i] == null) continue;
			for (int j = 0; j < subIndex[i].length; j++) {
				matches.add(subIndex[i][j]);
			}
		}
		Collections.sort(matches);
		Collections.reverse(matches);
		for (int i = 0; i < matches.size(); i++) {
			int sub = matches.get(i).sub;
			int supr = matches.get(i).supr;
			if (matched1[sub] || matched2[supr])
				continue;
			matched1[sub] = true;
			matched2[supr] = true;
			eq.set(matches.get(i));
		}
		eq.takeMaxMaxBothWays();
		return eq;
	}
}
