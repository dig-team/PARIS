package paris;

import java.io.IOException;

import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class is a SubThingStore<JoinRelation> with specialized methods to store the relation alignment
 */

public abstract class SubRelationStore extends SubThingStore<JoinRelation> {  
  public SubRelationStore(FactStore fs1, FactStore fs2) {
		super(fs1, fs2);
	}

	/** Load the alignments from the output of a mapping step */
  public void loadMapperOutput(MapperOutput output) throws IOException {
  	clear();
  	for (int i = 0; i < fs1.maxJoinRelationCode(); i++) {
  		if (output.neighborhoods[i] == null)
  			continue;
  		JoinRelation r1 = fs1.joinRelationByCode(i);
  		HashArrayNeighborhood n = output.neighborhoods[i];
  		double normalizer = output.relationNormalizer.getNormalizer(r1);
  		if (normalizer == 0) continue; // r1 has occurrences but no alignment candidates
			populate(r1, normalizer, new JoinRelation(fs2), n, false);
  	}
  }

  public String toTsv(SubPair<JoinRelation> p) {
  	return p.sub.toString()+"\t"+p.supr.toString()+"\t"+p.val+"\n";
  }
  public void setValueReverse(JoinRelation r1, JoinRelation r2, double val, boolean opposite) {
    //Announce.message("requested to set " + r1 + " as subrel of " + r2 + " with score " + val);
  	assert(!Double.isInfinite(val) && !Double.isNaN(val));
  	// a possible reason for this assert to fail is when you have duplicate facts in your fact store
  	assert(val >= 0 && val <= 1.01);
  	if (!opposite) { 
      setValue(r1, r2, val);
  	} else {
  		//double oval = getValueRaw(r1, r2.reversed());
  		double oval = getValue(r1, r2.reversed());
  		setValue(r1, r2.reversed(), val + oval);
  	}
    //Announce.message("Setting " + r1 + " as subrel of " + r2 + " with score " + val);
  }
  
  protected void populate(JoinRelation r1, double normalizer, JoinRelation r2, HashArrayNeighborhood n, boolean opposite) {
  	if (fs1.setting.interestingnessThreshold && !n.worthTrying())
  		return;
  	double val = (n.score + fs1.setting.smoothNumerator) / (normalizer + fs1.setting.smoothDenominator);
  	//Announce.message("about to set " + r1 + " as subrel of " + r2 + " fullscore " + n.score + " normalizer " + normalizer);
  	if (r2.length() > 0) {
  	  setValueReverse(r1, r2, val, opposite);
  	}
		for (Integer relation : n.children.keySet()) {
			JoinRelation nr2 = new JoinRelation(r2);
			nr2.push(relation);
			assert(nr2.length() == r2.length() + 1);
			populate(r1, normalizer, nr2, n.children.get(relation), opposite);
		}
  }

	public abstract double getValueCode(int sub, int supr);
	public abstract void clear();
  
}
