package paris;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javatools.datatypes.CombinedIterable;
import javatools.datatypes.IterableForIterator;
import javatools.datatypes.MappedIterator;
import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;
import paris.Config.EntityType;
import paris.shingling.QueryResult;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an ontology. It is the parent class of a database implementation
 * (BerkeleyFactStore) and an in-memory implementation (MemoryFactStore).
 * In general, all entities are represented by integer codes. 
 * This includes relations. 
 * A negative code means the inverse of the relation (and is not stored as an entity).
 * */


public abstract class FactStore {

	public interface ObjectWithId<IdType> {
		public IdType getId();
	}

	/** Represents a fact. The second argument is either an entity (given by its integer code) 
	 * or a literal (string). Exactly one of them is set.*/
	public static class Fact implements ObjectWithId<Long>, Serializable {

		/** long id of this fact, used as a key in the Berkeley DB*/
		long id;

		public Long getId() {
			return id;
		}

		/** first argument of the fact*/
		int arg1;

		/** relation of the fact*/
		int relation;

		/** second argument of the fact, if it is an entity*/
		int arg2;

		/** second argument of the fact, if it is a literal*/
		String arg2String;

		/** Constructs a fact */
		public Fact() {

		}

		/** Constructs a fact */
		public Fact(int a1, int r, int a2) {
			arg1 = a1;
			relation = r;
			arg2 = a2;
		}

		/** Constructs a fact */
		public Fact(int a1, int r, String a2) {
			arg1 = a1;
			relation = r;
			arg2String = a2;
		}

		@Override
		public String toString() {
			return "(" + id + ") " + arg1 + ", " + relation + ", " + arg2();
		}

		/** Returns the second argument as a string or int */
		public Object arg2() {
			if (arg2 != 0)
				return (arg2);
			return (arg2String);
		}

		/** Returns the Config.EntityType of the second argument */
		public EntityType targetType() {
			if (arg2 != 0)
				return (EntityType.RESOURCE);
			return (Config.entityType(arg2String));
		}

		public Pair<Integer, Object> getPair() {
			return new Pair<Integer, Object>(relation, arg2);
		}
	}

	/** Represents a relation. The id is the same as the id of the corresponding entity. 
	 * Inverse relations have a negative id and are not stored.*/
	public static class TypedRelation implements ObjectWithId<Integer>, Serializable {

		/** corresponds to the entity id */
		int id;

		public Integer getId() {
			return id;
		}

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
		public TypedRelation() {

		}

		/** Constructs a relation */
		public TypedRelation(String n) {
			name = n;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/** Represents an entity*/
	public static class Entity implements ObjectWithId<Integer>, Serializable {

		public int id;

		public Integer getId() {
			return id;
		}

		/** Entity name*/
		String name;

		/** TRUE if this is a class*/
		boolean isClass;

		/** Constructs an entity */
		public Entity(String arg1, boolean iAmAClass) {
			name = arg1;
			isClass = iAmAClass;
		}

		/** Constructs an entity */
		public Entity() {
		}

		@Override
		public String toString() {
			return name + (isClass ? "(class)" : "");
		}
	}

	/** Empty list */
	public static final List<Object> emptyObjectList = new ArrayList<Object>();
	/** Empty list */
	public static final List<Integer> emptyIntegerList = new ArrayList<Integer>();

	/** Returns the relations of this fact store (without inverse) */
	public abstract PeekIterator<Integer> relations();

	/** TRUE if that entity is a relation */
	public abstract boolean isRelation(Integer entity);

	/** Returns all classes */
	public abstract PeekIterator<Integer> classes();

	/** Returns all entities */
	public abstract PeekIterator<Integer> entities();

	/** Returns a relation for an id */
	public abstract TypedRelation relation(Integer id);

	/** Returns a relation for a name */
	public abstract TypedRelation relation(String name);

	/** Returns an entity for an id */
	public abstract Entity entity(Integer id);

	/** Returns an entity for a name */
	public abstract Entity entity(String name);

	/** TRUE if that entity is a class */
	public abstract boolean isClass(Integer entity);

	/** TRUE if that entity is a class */
	public abstract boolean isClass(Object entity);

	/** Returns number of classes */
	public abstract int numClasses();

	/** Returns number of classes */
	public abstract int numEntities();

	/** Returns all relations that have a given target type */
	public abstract Iterable<Integer> relationsWithTargetType(EntityType type);

	/** Returns all relations that have a given source type */
	public abstract Iterable<Integer> relationsWithSourceType(EntityType type);

	/** Returns the target type of a relation */
	public abstract EntityType targetType(Integer relation);

	/** Returns the source type of a relation */
	public abstract EntityType sourceType(Integer relation);

	/** Returns the number of facts */
	public abstract long numFacts();

	/** Prints information about this store */
	public abstract void print();

	public abstract void close() throws IOException;

	/** Adds a relation */
	public abstract TypedRelation addRelation(String relation);

	/** Adds an entity */
	public abstract Entity addEntity(String entity);

	/** Adds a class */
	public abstract void addClass(String c);

	/** Adds a fact */
	public abstract void add(String arg1, String relation, String arg2,
			EntityType arg2Type);

	/** Returns subjects */
	public abstract Iterable<Integer> subjects(Iterator<Fact> it);

	/** Returns relations */
	public abstract Iterable<Integer> relations(Iterator<Fact> it);

	/** Returns relations, inverted */
	public abstract Iterable<Integer> invertedRelations(Iterator<Fact> it);

	/** Returns objects */
	public abstract Iterable<Object> objects(Iterator<Fact> it);

	/** Returns arg1 and rel */
	public abstract Iterable<Pair<Integer, Object>> arg1andRelation(
			Iterator<Fact> it);

	/** Returns all facts with the given arg as first arg */
	public abstract Iterable<Fact> factsForSubject(Integer arg1);

	/** Returns arg2 and rel */
	public abstract Iterable<Pair<Integer, Object>> arg2andInvertedRelation(
			Iterator<Fact> it);

	/** Iterates over all facts, without inverse relations */
	public abstract Iterable<Fact> facts();

	/**
	 * returns all facts with that relation.
	 */
	public abstract MappedIterator<?, Pair<Object, Object>> factsForRelation(
			final int rel);

	/** returns the second argument for a relation with given first arg */
	public abstract Collection<Object> arg2ForRelationAndArg1(Integer rel,
			Object arg1);

	/** returns all relations where this entity appears a second arg */
	public abstract Iterator<Integer> relationsForArg2(Object arg2);

	/** returns all facts where this entity appears a second arg */
	public abstract Iterable<Pair<Integer, Object>> factsForArg2(Object arg2);

	/** returns all facts where this entity appears a second arg */
	public abstract Iterable<Pair<Integer, Object>> factsForArg1(Object arg1);

	/**
	 * returns all relations where these entities appear as first arg and second
	 * arg
	 */
	public abstract Iterator<Integer> relationsForArg1Arg2(Object arg1,
			Object arg2);

	/** returns all first arguments for this relation and given second arg */
	public abstract Iterable<? extends Object> arg1ForRelationAndArg2(
			Integer rel, Object arg2);

	/** Returns the direct instances of a class */
	public abstract Iterable<Integer> directInstancesOf(Integer clss, int max);

	/**
	 * Returns the instances of a class. Returns NULL in case there are more than
	 * 1000 subclasses. Returns maximally MAX elements per class
	 */
	public abstract CombinedIterable<Integer> instancesOf(Integer clss, int max);

	/**
	 * TRUE if the fact holds. This is used nowhere.
	 * 
	 * Currently implements completeness assumption on known relations. Does
	 * chaining for TYPE
	 */
	// public abstract int fact(Integer rel, Object arg1, Object arg2);

	/** TRUE if this entity is of that type */
	public abstract boolean type(Object entity, Object clss);

	/** Returns direct and indirect classes of an instance */
	public abstract Set<Integer> classesOf(Integer inst);

	/** Returns all superclasses of this class */
	public abstract Set<Integer> superclasses(Integer clss);

	/**
	 * Returns all subclasses of this class. Stops at 1000 subclasses, returns
	 * NULL
	 */
	public abstract Set<Integer> subclassesOf(Integer clss);

	/** Returns all facts about an entity */
	public abstract Iterable<Fact> factsAbout(Integer entity);

	/** TRUE if the entity appears somewhere */
	public abstract boolean exists(Integer e);

	/** TRUE if the entity appears somewhere */
	public abstract boolean exists(Object e);

	/** returns the (harmonic) functionality */
	public abstract double functionality(Integer rel);

	/** returns the (harmonic) functionality */
	public abstract double functionality(Integer rel, FactStore other);

	/** Returns the inverse (harmonic) functionality */
	public abstract double inverseFunctionality(Integer rel);

	/** Returns the inverse (harmonic) functionality with another fact store */
	public abstract double inverseFunctionality(Integer rel, FactStore other);

	/** Prints facts about an entity */
	public abstract void printFactsAbout(String name);

	/** Prints facts about an entity */
	public abstract void printFactsAbout(Integer entity);

	/** Prints facts with a relation */
	public abstract void printFactsWith(String relation);

	/** Prints facts about an entity */
	public abstract void printFactsWith(Integer relation);

	/** Returns a toString that gets evaluated only if needed */
	public abstract Object lazyToString(final Object entity);

	/** returns a string rep for an entity */
	public abstract String toString(Object entity);

	/** returns a string rep for an entity */
	public abstract String toString(Integer entity);

	/** returns a string rep for a fact */
	public abstract String toString(Long factId);

	/** returns a string rep for a fact */
	public abstract String toString(Fact fact);

	public abstract String toString();

	public abstract TypedRelation getIdRel();

	public abstract int numRelations();

	/** Indicate that the subject is a relation */
	protected static List<String> propertyTypes = Arrays.asList("rdf:Property",
			"owl:DatatypeProperty", "owl:DeprecatedProperty",
			"owl:FunctionalProperty", "owl:InverseFunctionalProperty",
			"owl:ObjectProperty");

	/** Indicate that the subject is a class */
	protected static List<String> classTypes = Arrays.asList("rdfs:Class",
			"owl:Class");

	/** Indicate that the object is a class */
	protected static List<String> classIndicatorsSecondArg = Arrays.asList(
			"rdfs:domain", "rdfs:range", "rdfs:subClassOf", "rdf:type");// ,"http://reliant.teknowledge.com/DAML/Mid-level-ontology.owl#economyType","http://reliant.teknowledge.com/DAML/SUMO.owl#subAttribute","http://reliant.teknowledge.com/DAML/SUMO.owl#successorAttribute");

	/** Indicate that the subject is a class */
	protected static List<String> classIndicatorsFirstArg = Arrays
			.asList("rdfs:subClassOf");// ,"http://reliant.teknowledge.com/DAML/SUMO.owl#subAttribute","http://reliant.teknowledge.com/DAML/SUMO.owl#successorAttribute");

	public abstract void registerPrefixNamespace(String prefix, String namespace);

	// initialize the store
	public abstract void init();

	public abstract void debugEntity(String string);

	public abstract Iterable<QueryResult> similarLiterals(String query, double threshold);
  
  /** Inverts all throughcoming relations */
  public static Collection<Integer> invertRelations(Collection<Integer> i) {
    List<Integer> result = new ArrayList<Integer>(i.size());
    for (Integer k : i)
      result.add(-k);
    return (result);
  }

}