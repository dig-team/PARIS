package paris;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import paris.SubThingStore.SubPair;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores alignments between pairs of entities, it is thread-safe for writes */


public class MemoryEqualityStore {
	ConcurrentHashMap<Integer, Set<SubPair>> index;
	
	public MemoryEqualityStore() {
		index = new ConcurrentHashMap<Integer, Set<SubPair>>();
	}
	
	public void setValue(FactStore substore, Integer sub, FactStore superstore, Integer supr, double val) {
  	if (val < Config.THETA) return;
  	if (val == -1 || Double.isNaN(val)) return;
    if (val > 1) val = 1;
    
    Set<SubPair> s = null;
		if (!index.keySet().contains(sub)) {
			s = new HashSet<SubPair>();
			index.put(sub, s);
		} else {
			s = index.get(sub);
		}
		s.add(new SubPair(sub, supr, val));
  }
}
