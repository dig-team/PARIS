package paris;

import java.io.Serializable;

/** The Relation base class */

@SuppressWarnings("serial")
public abstract class Relation implements Serializable {
	/** corresponds to the entity id unless separateRelationNumbering is set */
	int id;

	public Integer getId() {
		return id;
	}
}
