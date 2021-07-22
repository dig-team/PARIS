package paris;

import paris.Config.EntityType;

/** Represents a relation. The id is the same as the id of the corresponding entity. 
 * Inverse relations have a negative id and are not stored.*/	
@SuppressWarnings("serial")
public class SimpleRelation extends Relation {
	/** Relation name*/
	String name;
	
	/** Range of the relation (in terms of Config.EntityType)*/
	EntityType targetType;

	/** Number of facts of this relation in the ontology */
	long numPairs;

	/** Number of first args of this relation in the ontology */
	int numArg1;

	/** Number of second args of this relation in the ontology */
	int numArg2;

	/** Constructs a relation */
	public SimpleRelation() {

	}

	/** Constructs a relation */
	public SimpleRelation(String n) {
		name = n;
	}

	@Override
	public String toString() {
		if (name != null)
			return name;
		else
			return Integer.toString(id);
	}
	

}

