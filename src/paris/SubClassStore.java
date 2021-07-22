package paris;

import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an alignment between classes, using hashmaps. */

public class SubClassStore extends HashSubThingStore<Integer> {

	public SubClassStore(FactStore fs1, FactStore fs2) {
		super(fs1, fs2);
	}

	@Override
	public String toTsv(SubPair<Integer> p) {
		return fs1.entity(p.sub)+"\t"+fs2.entity(p.supr)+"\t"+p.val+"\n";
	}
	

}
