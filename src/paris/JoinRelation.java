package paris;

import paris.storage.FactStore;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
*
* It is licensed under a Creative Commons Attribution Non-Commercial License
* by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
* see http://webdam.inria.fr/paris
*
* This class describes a join relation, ie. a join of a sequence of oriented elementary relations represented by signed integers */

@SuppressWarnings("serial")
public class JoinRelation extends Relation implements Comparable<JoinRelation> {
	/** The elementary relations */
	int relations[];
	/** The number of elementary relations */
	int nrelations;
	/** The fact store this relation lives in */
	FactStore factStore;

	public JoinRelation(FactStore f) {
		assert(f != null);
		this.relations = new int[f.getJoinLengthLimit()];
		assert(f.getJoinLengthLimit() > 0);
		this.nrelations = 0;
		this.factStore = f;
	}

	public JoinRelation(JoinRelation r) {
		this(r.factStore);
		nrelations = r.length();
		for (int i = 0; i < nrelations; i++)
			relations[i] = r.get(i);
	}

	public JoinRelation(FactStore f, int relation) {
		this(f);
		push(relation);
	}

	/** Join this relation with a simple relation, in-place */
	public void push(int relation) {
		relations[nrelations++] = relation;
	}

	/** Remove the last simple relation joined on, in-place */
	public void pop() {
		nrelations--;
	}

	/** Reverse this relation in-place */
	public void reverse() {
		// we must reverse individual elementary relations
		for (int i = 0; i < nrelations; i++) {
			relations[i] = FactStore.inverse(relations[i]);
		}
		// we must also reverse the order of the elementary relations
		reverseDirection();
	}
	
	/** Reverse the order of the elementary relations */
	public void reverseDirection() {
		int t;
		for (int i = 0; i < nrelations/2; i++) {
			t = relations[i];
			relations[i] = relations[nrelations - i - 1];
			relations[nrelations - i - 1] = t;
		}
	}

	/** Return the reversed relation */
	public JoinRelation reversed() {
		JoinRelation r = new JoinRelation(this);
		r.reverse();
		return r;
	}

	/** Get the number of elementary relations */
	public int length() {
		return nrelations;
	}

	/** Get an elementary relation by position */
	public int get(int pos) {
		return relations[pos];
	}

	/** Get the last elementary relation */
	public int getLast() {
		return relations[nrelations - 1];
	}

	/** Get a copy of this join without the last element */
	public JoinRelation allButLast() {
		JoinRelation r = new JoinRelation(this);
		r.pop();
		return r;
	}

	/** Check if we are exactly the simple relation r */
	public boolean isSimpleRelation(int r) {
		return (length() == 1) && (getLast() == r);
	}

	/** Compare to another join relation, using the lexicographic order */
	@Override
	public int compareTo(JoinRelation o) {
		for (int i = 0; i < Math.min(length(), o.length()); i++) {
			int x = get(i) - o.get(i);
			if (x != 0)
				return x;
		}
		return length() - o.length();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof JoinRelation))
			return false;
		return compareTo((JoinRelation) o) == 0;
	}

	@Override
	public int hashCode() {
		return java.util.Arrays.hashCode(relations);
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < nrelations; i++) {
			if (i > 0)
				result += " -> ";
			int relation = relations[i];
			String suffix = "";
			if (FactStore.isInverse(relation)) {
				suffix = "-";
				relation = FactStore.inverse(relation);
			}
			result += factStore.relation(relation).toString();
			result += suffix;
		}
		return result;
	}

	/** Check that the relation contains no occurrence of x joined with -x */
	public boolean isTrivial() {
                if (nrelations < 2) return false;
                if (nrelations == 2) return relations[1] == FactStore.inverse(relations[0]);
		int last = relations[0];
		for (int i = 1; i < nrelations; i++) {
			if (relations[i] == FactStore.inverse(last)) return true;
			last = relations[i];
		}
		return false;
	}
	
	/** Return a unique numeric code for this relation, ensuring that the codes are contiguous
	 * This is used as a position in arrays of join relations to speed things up
	 * reversed can be used to get the code of the reversed relation quickly */
	public int code(boolean reversed) {
		int code = 0;
		int max = factStore.maxRelationId();
		for (int i = 0; i < nrelations; i++) {
			code *= max;
			int toadd;
			if (reversed) {
				toadd = (FactStore.inverse(relations[nrelations - 1 - i]));
			} else {
				toadd = relations[i];
			}
			assert(toadd < max);
			code += toadd;
		}
		return code;
	}
	
	public int code() {
		return code(false);
	}
}
