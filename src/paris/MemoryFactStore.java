package paris;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import paris.Config.EntityType;
import paris.shingling.QueryResult;
import paris.shingling.ShinglingTable;
import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.CombinedIterable;
import javatools.datatypes.FilteredIterator;
import javatools.datatypes.IterableForIterator;
import javatools.datatypes.MappedIterator;
import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;

/**
 * This class is part of the PARIS ontology matching project at INRIA
 * Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further
 * information, see http://webdam.inria.fr/paris
 * 
 * This class stores an ontology in memory. In general, all entities are
 * represented by integer codes. This includes relations. A negative code means
 * the inverse of the relation (and is not stored as an entity).
 */

public class MemoryFactStore extends FactStore implements Closeable,
		Serializable {

	// ---------------------------------------------------------------
	// Global values (specific to this instance of the fact store)
	// ---------------------------------------------------------------

	/**
	 * The relation between an entity and its id, if Config.treatIdAsRelation=TRUE
	 */
	public TypedRelation idRel;

	/** The RDF:TYPE relation */
	public TypedRelation type;

	/** The RDFS:SUBCLASSOF relation */
	public TypedRelation subclassof;

	/** Administrational info for the fact store */
	public static class Configuration implements Serializable {

		/**
		 * Holds the name space prefix of this ontology. Needed for
		 * Config.treatIdAsRelation
		 */
		public String myNameSpacePrefix;

	}

	/** Administrational info for the fact store */
	public Configuration config;

	/** Empty list */
	public static final List<Object> emptyObjectList = new ArrayList<Object>();

	/** Empty list */
	public static final List<Integer> emptyIntegerList = new ArrayList<Integer>();

	
	// ---------------------------------------------------------------
	// Properties and Accessor methods
	// ---------------------------------------------------------------

	/** Holds all facts */
	protected Map<Long, Fact> factsById;
	private long freshFactId;

	/** Holds all facts by first arg */
	protected MultiMap<Integer, Fact> factsByArg1;

	/** Holds all facts by second arg */
	protected MultiMap<Integer, Fact> factsByArg2;

	/** Holds all facts by second arg */
	protected MultiMap<String, Fact> factsByArg2String;

	/** Holds all facts by relation */
	protected MultiMap<Integer, Fact> factsByRelation;

	/** Holds all relations */
	protected Map<Integer, TypedRelation> relationsById;

	/** Holds all relations */
	protected Map<String, TypedRelation> relationsByName;

	/** Holds all typed relations by type */
	protected MultiMap<EntityType, TypedRelation> relationsByType;

	/**
	 * Hold the size of the *domain* of all join relations (with all orientations)
	 */
	int domain[];
	/** Holds the number of occurrences */
	int occurrences[];
	
	/** should we do approximate indexing of the strings? */
	public boolean withShinglings = true;
	
	/** size of k-grams to index */
	public static int shinglingSize = 3;
	/** number of hash functions */
	public static int shinglingFunctions = 30;
	/** hash table size */
//	public static int shinglingTableSize = 1048576;
	public static int shinglingTableSize = 65536;
	
	ShinglingTable literalIndex;

	// /** Returns the relations of this fact store (without inverse) */
	// public PeekIterator<Integer> relations() {
	// return new PeekIterator.SimplePeekIterator<Integer>(relationsById.keySet()
	// .iterator());
	// }

	public Iterable<Integer> simpleRelations() {
		return relationsById.keySet();
	}

	/** Returns the relations of this fact store (without inverse) */
	public PeekIterator<Integer> relations() {
		return new PeekIterator.SimplePeekIterator<Integer>(relationsById.keySet()
				.iterator());
	}

	/** returns the number of relations */
	public int numRelations() {
		return (int) relationsByName.size();
	}

	/** TRUE if that entity is a relation */
	public boolean isRelation(Integer entity) {
		if (entity < 0)
			return (isRelation(-entity));
		return (relationsById.containsKey(entity));
	}

	/** Holds all the entities */
	protected Map<Integer, Entity> entitiesById;
	private int freshEntityId;

	/** Holds all the entities */
	protected Map<String, Entity> entitiesByName;

	/** Holds all the entities by class/individual */
	protected MultiMap<Boolean, Entity> entitiesByNature;

	/* sadly, generics are not covariant, so we cannot use that :( */
	/*public static <PK, SK> PeekIterator<PK> primaryKeysForSecondaryKey(
			final MultiMap<SK, ObjectWithId<PK>> index, SK secondaryKey) {
		return new PeekIterator.SimplePeekIterator<PK>(
				(Iterator<PK>) new MappedIterator<ObjectWithId<PK>, PK>(index.get(
						secondaryKey).iterator(),
						new MappedIterator.Map<ObjectWithId<PK>, PK>() {
							@Override
							public PK map(ObjectWithId<PK> e) {
								return e.getId();
							}
						}));
	}*/

	// some entities may be erroneously registered in entitiesByNature, so we
	// filter
	private static PeekIterator<Integer> primaryKeysForSecondaryKeyNature(
			final MultiMap<Boolean, Entity> index, final Boolean secondaryKey) {
		return new PeekIterator.SimplePeekIterator<Integer>(
				(Iterator) new MappedIterator(new FilteredIterator(index.getOrEmpty(
						secondaryKey).iterator(), new FilteredIterator.If<Entity>() {
					@Override
					public boolean condition(Entity e) {
						return e.isClass == secondaryKey;
					}
				}), new MappedIterator.Map<Entity, Integer>() {
					@Override
					public Integer map(Entity e) {
						return e.getId();
					}
				}));
	}

	private static PeekIterator<Integer> primaryKeysForSecondaryKeyType(
			final MultiMap<EntityType, TypedRelation> index, EntityType secondaryKey) {
		return new PeekIterator.SimplePeekIterator<Integer>(
				(Iterator) new MappedIterator(
						index.getOrEmpty(secondaryKey).iterator(),
						new MappedIterator.Map<TypedRelation, Integer>() {
							@Override
							public Integer map(TypedRelation e) {
								return e.getId();
							}
						}));
	}

	public PeekIterator<Integer> getByNature(boolean nature) {
		return primaryKeysForSecondaryKeyNature(entitiesByNature, nature);
	}

	/** Returns all classes */
	public PeekIterator<Integer> classes() {
		return getByNature(true);
	}

	/** Returns all entities */
	public PeekIterator<Integer> entities() {
		return getByNature(false);
	}

	/** Returns a relation for an id */
	public TypedRelation relation(Integer id) {
		return (relationsById.get(id));
	}

	/** Returns a relation for a name */
	public TypedRelation relation(String name) {
		return (relationsByName.get(name));
	}

	/** Returns an entity for an id */
	public Entity entity(Integer id) {
		return (entitiesById.get(id));
	}

	/** Returns an entity for a name */
	public Entity entity(String name) {
		return (entitiesByName.get(name));
	}

	/** TRUE if that entity is a class */
	public boolean isClass(Integer entity) {
		Entity e = entitiesById.get(entity);
		return (e != null && e.isClass);
	}

	/** TRUE if that entity is a class */
	public boolean isClass(Object entity) {
		return (entity instanceof Integer && isClass((Integer) entity));
	}

	/** Holds number of classes */
	protected long numClasses = -1;

	/** Returns number of classes */
	public int numClasses() {
		if (numClasses == -1)
			numClasses = entitiesByNature.getOrEmpty(true).size();
		return ((int) numClasses);
	}

	/** Holds number of entities */
	protected long numEntities = -1;

	/** Returns number of classes */
	public int numEntities() {
		if (numEntities == -1)
			numEntities = entitiesByNature.getOrEmpty(false).size();
		return ((int) numEntities);
	}

	/** Returns all relations that have a given target type */
	public Iterable<Integer> relationsWithTargetType(EntityType type) {
		return (primaryKeysForSecondaryKeyType(relationsByType, type));
	}

	/** Returns all relations that have a given source type */
	public Iterable<Integer> relationsWithSourceType(EntityType type) {
		return (invertRelations((PeekIterator<Integer>) primaryKeysForSecondaryKeyType(
				relationsByType, type)));
	}

	/** Returns the target type of a relation */
	public EntityType targetType(Integer relation) {
		if (relation < 0)
			return (EntityType.RESOURCE);
		TypedRelation rel = relationsById.get(relation);
		if (rel == null)
			return (null);
		return (rel.targetType);
	}

	/** Returns the source type of a relation */
	public EntityType sourceType(Integer relation) {
		return (targetType(-relation));
	}

	/** Holds the number of facts */
	protected long numFacts = -1;

	/** Returns the number of facts */
	public long numFacts() {
		if (numFacts == -1)
			numFacts = factsById.size();
		return (numFacts);
	}

	// --------------------------------------------------------------
	// Administrational methods
	// --------------------------------------------------------------

	/** Prints information about this store */
	public void print() {
		Announce.message("Ontology");
		Announce.message("  Namespace prefix:", config.myNameSpacePrefix);
		// Announce.message("  Facts:", numFacts()); // Takes too long
		Announce.message("  Relations:", numRelations());
		// Announce.message("  Classes:", numClasses()); // Takes too long
		Announce.message("  Entities:", numEntities(), ", for example");
		int c = 10;
		Iterable<Entity> e = entitiesById.values();
		for (Entity x : e) {
			Announce.message("      ", x);
			if (!(c-- > 0))
				break;
		}
		Announce.message("  Sample facts:");
		c = 10;
		Iterable<Fact> f = facts();
		for (Fact x : f) {
			Announce.message("      ", toString(x));
			if (!(c-- > 0))
				break;
		}
		// try {
		// ((Closeable) f).close();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		Announce.message("  Memory (Mb):");
		Announce.message("     Java Free: " + Runtime.getRuntime().freeMemory()
				/ 1000 / 1000);
		Announce.message("     Java Max: " + Runtime.getRuntime().maxMemory()
				/ 1000 / 1000);
		Announce.message("     Java Total: " + Runtime.getRuntime().totalMemory()
				/ 1000 / 1000);
	}

	@Override
	public void close() throws IOException {
		Announce.doing("Nothing to close");
		/*
		 * // This takes up to 6h without any evident advantage try { e.cleanLog();
		 * } catch (Exception ex) { // Gets thrown if the environment was read only.
		 * Nobody cares. }
		 */
		Announce.done();
	}

	// --------------------------------------------------------------
	// Loading
	// --------------------------------------------------------------

	/** Adds a relation */
	public TypedRelation addRelation(String relation) {
		return addRelation(relation, EntityType.STRING);
	}

	/** Adds a relation */
	public TypedRelation addRelation(String relation, EntityType t) {
		TypedRelation result = relationsByName.get(relation);
		if (result == null) {
			result = new TypedRelation(relation);
			result.targetType = t;
			// a relation can be the subject or object of a statement, so we add it as
			// an entity
			Entity entity = addEntity(relation);
			result.id = entity.id;
			relationsById.put(result.id, result);
			relationsByName.put(result.name, result);
			// relationsByType.put(result.targetType, result);
		}
		return (result);
	}

	/** Adds an entity */
	public Entity addEntity(String entity) {
		return addEntity(entity, false);
	}

	public Entity addEntity(String entity, boolean isClass) {
		Entity e = entitiesByName.get(entity);
		if (e == null) {
			int myId = freshEntityId++;
			e = new Entity(entity, isClass);
			e.id = myId;
			entitiesById.put(e.id, e);
			entitiesByName.put(e.name, e);
			entitiesByNature.put(e.isClass, e);
			e = entitiesByName.get(entity); // Put returns NULL
		}
		return (e);
	}

	/** Adds a class */
	public void addClass(String c) {
		Entity e = addEntity(c, true);
		if (!e.isClass) {
			e.isClass = true;
			entitiesByNature.put(e.isClass, e);
		}
	}

	/** Adds a fact */
	public void add(String arg1, String relation, String arg2, EntityType arg2Type) {
		arg1 = Config.format(arg1, EntityType.RESOURCE);
		relation = Config.format(relation, EntityType.RESOURCE);
		TypedRelation rel = addRelation(relation, arg2Type);
		Entity entity1 = addEntity(arg1);
		Entity entity2 = null;
		arg2 = Config.format(arg2, arg2Type);
		if (arg2Type == EntityType.RESOURCE) {
			entity2 = addEntity(arg2);
		}
		if (relation.equals("rdf:type")) {
			if (propertyTypes.contains(arg2))
				addRelation(arg1);
			if (classTypes.contains(arg2))
				addClass(arg1);
		}
		if (classIndicatorsFirstArg.contains(relation))
			addClass(arg1);
		if (classIndicatorsSecondArg.contains(relation))
			addClass(arg2);
		long myId = freshFactId++;
		Fact fact = (entity2 == null ? new Fact(entity1.id, rel.id, arg2)
				: new Fact(entity1.id, rel.id, entity2.id));
		fact.id = myId;
		factsById.put(myId, fact);
		factsByArg1.put(fact.arg1, fact);
		// if (fact.arg2String != null && fact.arg2String.equals("\"valentino\"")) {
		// System.out.println("aha!");
		// }
		if (rel.id == type.id)
			assert entity2 != null;
		if (entity2 == null)
			factsByArg2String.put(fact.arg2String, fact);
		else
			factsByArg2.put(fact.arg2, fact);
		factsByRelation.put(fact.relation, fact);
	}

	/** Computes functionalities */
	@Override
	public void init() {
		Announce.doing("Computing functionalities");
		Iterator<TypedRelation> cursor = relationsById.values().iterator();
		TypedRelation rel;
		while (cursor.hasNext()) {
			rel = cursor.next();
			if (rel.id < 0)
				continue;
			if (rel.id == idRel.id) {
				rel.numArg1 = 1000;
				rel.numArg2 = 1000;
				rel.numPairs = 1000;
				rel.targetType = Config.EntityType.STRING;
				Announce.message("Computing functionality of " + rel);
				Announce.message("     numArg1=", rel.numArg1, "numArg2=", rel.numArg2,
						"numPairs=", rel.numPairs, "target=", rel.targetType);
				continue;
			}
			int max = 100000;
			Announce.progressStart("Computing functionality of " + rel, max);
			int numPairs = 0;
			TreeSet<Integer> arg1 = new TreeSet<Integer>();
			TreeSet<Integer> arg2 = new TreeSet<Integer>();
			TreeMap<EntityType, Integer> types = new TreeMap<EntityType, Integer>();
			Iterator<Fact> facts = factsByRelation.getOrEmpty(rel.id).iterator();
			Fact fact;
			while (max-- > 0 && facts.hasNext()) {
				fact = facts.next();
				arg1.add(fact.arg1);
				arg2.add(fact.arg2().hashCode());
				D.addKeyValue(types, fact.targetType(), 1);
				Announce.progressStep();
				numPairs++;
			}
			Announce.progressDone();
			if (numPairs == 0) {
				Announce.message("    no relation instances");
				continue;
			}
			rel.numArg1 = arg1.size();
			rel.numArg2 = arg2.size();
			rel.numPairs = numPairs;
			rel.targetType = null;
			for (EntityType t : types.keySet()) {
				if (rel.targetType == null || types.get(t) > types.get(rel.targetType))
					rel.targetType = t;
			}
			// this is where we populate relationsByType
			relationsByType.put(rel.targetType, rel);
			Announce.message("     numArg1=", rel.numArg1, "numArg2=", rel.numArg2,
					"numPairs=", rel.numPairs, "target=", rel.targetType);
		}
		Announce.done();
		if (withShinglings) {
			Announce.doing("indexing literals...");
			this.literalIndex = new ShinglingTable(shinglingSize, shinglingFunctions, shinglingTableSize);
			Set<String> indexed = new HashSet<String>();
			for (Fact f : factsById.values()) {
				if (f.arg2String == null)
					continue;
				if (indexed.contains(f.arg2String))
					continue;
				indexed.add(f.arg2String);
				this.literalIndex.index(Config.stripQuotes(f.arg2String));
			}
			Announce.done();
		} else {
			this.literalIndex = null;
		}
	}

	public MemoryFactStore(boolean withShinglings) {
		numEntities = -1;
		freshFactId = 0;
		freshEntityId = 0;
		this.entitiesById = new HashMap<Integer, Entity>(Config.initialSize);
		this.entitiesByName = new HashMap<String, Entity>(Config.initialSize);
		this.entitiesByNature = new MultiMap<Boolean, Entity>(2, Config.initialSize);
		this.factsById = new HashMap<Long, Fact>(Config.initialSize);
		this.factsByArg1 = new MultiMap<Integer, Fact>(Config.initialSize);
		this.factsByArg2 = new MultiMap<Integer, Fact>(Config.initialSize);
		this.factsByArg2String = new MultiMap<String, Fact>(Config.initialSize);
		this.factsByRelation = new MultiMap<Integer, Fact>();
		this.relationsById = new HashMap<Integer, TypedRelation>();
		this.relationsByName = new HashMap<String, TypedRelation>();
		this.relationsByType = new MultiMap<EntityType, TypedRelation>();
		this.config = new Configuration();
		idRel = addRelation("@id");
		type = addRelation("rdf:type");
		subclassof = addRelation("rdfs:subclassOf");
		this.withShinglings = withShinglings;
	}

	// --------------------------------------------------------------
	// Mappers
	// --------------------------------------------------------------

	/** Returns subjects */
	public Iterable<Integer> subjects(Iterator<Fact> it) {
		Iterator<Integer> it2 = new MappedIterator<Fact, Integer>(it,
				new MappedIterator.Map<Fact, Integer>() {

					@Override
					public Integer map(Fact a) {
						return a.arg1;
					}
				});
		return (new IterableForIterator<Integer>(it2));
	}

	/** Returns relations */
	public Iterable<Integer> relations(Iterator<Fact> it) {
		return (new IterableForIterator<Integer>(new MappedIterator<Fact, Integer>(
				it, new MappedIterator.Map<Fact, Integer>() {

					@Override
					public Integer map(Fact a) {
						return a.relation;
					}
				})));
	}

	/** Returns relations, inverted */
	public Iterable<Integer> invertedRelations(Iterator<Fact> it) {
		return (new IterableForIterator<Integer>(new MappedIterator<Fact, Integer>(
				it, new MappedIterator.Map<Fact, Integer>() {

					@Override
					public Integer map(Fact a) {
						return -a.relation;
					}
				})));
	}

	/** Returns objects */
	public Iterable<Object> objects(Iterator<Fact> it) {
		Iterator<Object> it2 = new MappedIterator<Fact, Object>(it,
				new MappedIterator.Map<Fact, Object>() {

					@Override
					public Object map(Fact a) {
						return a.arg2();
					}
				});
		return (new IterableForIterator<Object>(it2));
	}

	/** Returns arg1 and rel */
	public Iterable<Pair<Integer, Object>> arg1andRelation(Iterator<Fact> it) {
		Iterator<Pair<Integer, Object>> it2 = new MappedIterator<Fact, Pair<Integer, Object>>(
				it, new MappedIterator.Map<Fact, Pair<Integer, Object>>() {

					@Override
					public Pair<Integer, Object> map(Fact a) {
						return new Pair<Integer, Object>(a.relation, a.arg1);
					}
				});
		return (new IterableForIterator<Pair<Integer, Object>>(it2));
	}

	/** Returns all facts with the given arg as first arg */
	public Iterable<Fact> factsForSubject(Integer arg1) {
		return factsByArg1.getOrEmpty(arg1);
	}

	/** Returns arg2 and rel */
	public Iterable<Pair<Integer, Object>> arg2andInvertedRelation(
			Iterator<Fact> it) {
		Iterator<Pair<Integer, Object>> it2 = new MappedIterator<Fact, Pair<Integer, Object>>(
				it, new MappedIterator.Map<Fact, Pair<Integer, Object>>() {

					@Override
					public Pair<Integer, Object> map(Fact a) {
						return new Pair<Integer, Object>(-a.relation, a.arg2());
					}
				});
		return (new IterableForIterator<Pair<Integer, Object>>(it2));
	}

	/** Inverts all throughcoming relations */
	public static Iterable<Integer> invertRelations(Iterable<Integer> i) {
		return (new IterableForIterator<Integer>(
				new MappedIterator<Integer, Integer>(i.iterator(),
						new MappedIterator.Map<Integer, Integer>() {

							public Integer map(Integer a) {
								return -a;
							}
						})));
	}

	/** Inverts all throughcoming relations */
	public static Collection<Integer> invertRelations(Collection<Integer> i) {
		List<Integer> result = new ArrayList<Integer>(i.size());
		for (Integer k : i)
			result.add(-k);
		return (result);
	}

	/** Returns the entity name as a string */
	protected String entityName(String n) {
		return ('"' + Config.entityName(n) + '"');
	}

	/** Returns the entity name as a string */
	protected String entityName(Integer n) {
		return (entityName(entitiesById.get(n)));
	}

	/** Returns the entity name as a string */
	protected String entityName(Entity n) {
		return (entityName(n.name));
	}

	// --------------------------------------------------------------
	// Accessor methods
	// --------------------------------------------------------------

	/** Iterates over all facts, without inverse relations */
	public Iterable<Fact> facts() {
		return (factsById.values());
	}

	/**
	 * returns all facts with that relation.
	 */
	public MappedIterator<?, Pair<Object, Object>> factsForRelation(final int rel) {
		if (Config.treatIdAsRelation && (rel == idRel.id || rel == -idRel.id)) {
			return (new MappedIterator<Entity, Pair<Object, Object>>(entitiesById
					.values().iterator(),
					new MappedIterator.Map<Entity, Pair<Object, Object>>() {

						@Override
						public Pair<Object, Object> map(Entity a) {
							if (rel == idRel.id)
								return (new Pair<Object, Object>(a.id, entityName(a.name)));
							return (new Pair<Object, Object>(entityName(a.name), a.id));
						}
					}));
		}

		if (rel < 0) {
			return (new MappedIterator<Fact, Pair<Object, Object>>(factsByRelation
					.getOrEmpty(-rel).iterator(),
					new MappedIterator.Map<Fact, Pair<Object, Object>>() {

						@Override
						public Pair<Object, Object> map(Fact a) {
							return (new Pair<Object, Object>(a.arg2(), a.arg1));
						}
					}));
		}
		return (new MappedIterator<Fact, Pair<Object, Object>>(factsByRelation
				.getOrEmpty(rel).iterator(),
				new MappedIterator.Map<Fact, Pair<Object, Object>>() {

					@Override
					public Pair<Object, Object> map(Fact a) {
						return (new Pair<Object, Object>(a.arg1, a.arg2()));
					}
				}));
	}

	/** returns the elements of s with a relation equal to rel */
	public Iterator<Fact> filterOnRelation(Set<Fact> s, final int rel) {
		return (new FilteredIterator<Fact>(s.iterator(),
				new FilteredIterator.If<Fact>() {
					@Override
						public boolean condition(Fact f) {
							return f.relation == rel;
						}
				}
		));
	}

	public Collection<Object> iterableToCollection(Iterable<?> iter) {
		Collection<Object> c = new ArrayList<Object>();
		for (Object o : iter) {
			c.add(o);
		}
		return c;
	}

	/** returns the second argument for a relation with given first arg */
	public Collection<Object> arg2ForRelationAndArg1(Integer rel, Object arg1) {
		if (Config.treatIdAsRelation && rel == idRel.id) {
			if (!(arg1 instanceof Integer))
				return (emptyObjectList);
			return (Arrays.asList((Object) entityName((Integer) arg1)));
		}
		if (Config.treatIdAsRelation && rel == -idRel.id) {
			if (!(arg1 instanceof String))
				return (emptyObjectList);
			String entity = config.myNameSpacePrefix
					+ Config.stripQuotes(arg1.toString());
			Entity e = entitiesByName.get(Config.compress(entity));
			if (e != null)
				return (Arrays.asList((Object) e.id));
			return (emptyObjectList);
		}
		// Hack for dbpedia. DBpedia does not contain '#' in numbers/dates, so
		// return immediately instead of trying to find matches.
		if (config.myNameSpacePrefix.equals("dbp:")
				&& arg1.toString().contains("#"))
			return (emptyObjectList);
		if (rel < 0) {
			if (arg1 instanceof Integer)
				return iterableToCollection(subjects(filterOnRelation(
						factsByArg2.getOrEmpty((Integer) arg1), -rel)));
			else
				return iterableToCollection(subjects(filterOnRelation(
						factsByArg2String.getOrEmpty(arg1.toString()), -rel)));
		} else {
			if (!(arg1 instanceof Integer))
				return (emptyObjectList);
			return iterableToCollection(objects(filterOnRelation(
					factsByArg1.getOrEmpty((Integer) arg1), rel)));
		}
	}

	public <T1, T2> Iterator<T1> first(Iterator<Pair<T1, T2>> l) {
		return (new MappedIterator<Pair<T1, T2>, T1>(
				l,
				new MappedIterator.Map<Pair<T1, T2>, T1>() {

					@Override
					public T1 map(Pair<T1, T2> a) {
						return a.first;
					}
				}));
	}
	
	public <T1, T2> Iterator<T1> first(Iterable<Pair<T1, T2>> l) {
		return first(l.iterator());
	}

	
	public <T1, T2> Iterator<T2> second(Iterator<Pair<T1, T2>> l) {
		return (new MappedIterator<Pair<T1, T2>, T2>(
				l,
				new MappedIterator.Map<Pair<T1, T2>, T2>() {

					@Override
					public T2 map(Pair<T1, T2> a) {
						return a.second;
					}
				}));
	}
	
	public <T1, T2> Iterator<T2> second(Iterable<Pair<T1, T2>> l) {
		return second(l.iterator());
	}

	
	/** returns all relations where this entity appears a second arg */
	public Iterator<Integer> relationsForArg2(Object arg2) {
		return first(factsForArg2(arg2));
	}
	
	
	/** returns all facts where this entity appears as second arg */
	public Iterable<Pair<Integer, Object>> factsForArg2(Object arg2) {
		if (arg2 instanceof Integer) {
			Iterable<Pair<Integer, Object>> it1 = arg1andRelation(factsByArg2
					.getOrEmpty((Integer) arg2).iterator());
			Iterable<Pair<Integer, Object>> it2 = arg2andInvertedRelation(factsByArg1
					.getOrEmpty((Integer) arg2).iterator());
			CombinedIterable<Pair<Integer, Object>> combo = new CombinedIterable<Pair<Integer, Object>>(
					it1, it2);
			if (Config.treatIdAsRelation) {
				combo.add(new Pair<Integer, Object>(-idRel.id,
						entityName((Integer) arg2)));
			}
			return (combo);
		}
		return (arg1andRelation(factsByArg2String.getOrEmpty(arg2.toString())
				.iterator()));
	}


	/** returns all facts where this entity appears as first arg */
	public Iterable<Pair<Integer, Object>> factsForArg1(Object arg1) {
		return (new MappedIterator<Pair<Integer, Object>, Pair<Integer, Object>>(
				factsForArg2(arg1).iterator(),
				new MappedIterator.Map<Pair<Integer, Object>, Pair<Integer, Object>>() {

					@Override
					public Pair<Integer, Object> map(Pair<Integer, Object> a) {
						a.first = -a.first;
						return a;
					}
				}));
	}

	/** returns the elements of s with a relation equal to rel */
	public Iterator<Fact> filterOnArg1(Set<Fact> s, final int arg1) {
		return (new FilteredIterator<Fact>(s.iterator(),
				new FilteredIterator.If<Fact>() {
					@Override
						public boolean condition(Fact f) {
							return f.arg1 == arg1;
						}
				}
		));
	}
	
	/** returns the elements of s with a relation equal to rel */
	public <T1, T2> Iterator<Pair<T1, T2>> filterOnSecond(Iterable<Pair<T1, T2>> l, final T2 arg2) {
		return (new FilteredIterator<Pair<T1, T2>>(l.iterator(),
				new FilteredIterator.If<Pair<T1, T2>>() {
					@Override
						public boolean condition(Pair<T1, T2> p) {
							return p.second.equals(arg2);
						}
				}
		));
	}

	public Iterator<Integer> inverted(Iterator<Integer> l) {
		return new MappedIterator<Integer, Integer>(
				l,
				new MappedIterator.Map<Integer, Integer>() {

					@Override
					public Integer map(Integer a) {
						return -a;
					}
				});
	}
	
	/**
	 * returns all relations where these entities appear as first arg and second
	 * arg
	 */
	public Iterator<Integer> relationsForArg1Arg2(Object arg1, Object arg2) {
		if (arg1 instanceof Integer) {
			return first(filterOnSecond(factsForArg2(arg2), (Integer) arg1));
		} else {
			if (arg2 instanceof Integer) {
				return first(filterOnSecond(factsForArg1(arg1), (Integer) arg2));
			} else {
				return emptyIntegerList.iterator();
			}
		}
	}

	/** returns all first arguments for this relation and given second arg */
	public Iterable<? extends Object> arg1ForRelationAndArg2(Integer rel,
			Object arg2) {
		return (arg2ForRelationAndArg1(-rel, arg2));
	}

	/** Returns the direct instances of a class */
	public Iterable<Integer> directInstancesOf(Integer clss, int max) {
		return subjects(filterOnRelation(factsByArg2.getOrEmpty(clss), type.id));
	}

	/**
	 * Returns the instances of a class. Returns NULL in case there are more than
	 * 1000 subclasses. Returns maximally MAX elements per class
	 */
	public CombinedIterable<Integer> instancesOf(Integer clss, int max) {
		CombinedIterable<Integer> result = new CombinedIterable<Integer>();
		Set<Integer> classes = subclassesOf(clss);
		if (classes == null)
			return (null);
		for (Integer c : classes) {
			result.add(directInstancesOf(c, max));
		}
		return (result);
	}

	// --------------------------------------------------------------
	// Checking facts
	// --------------------------------------------------------------

	/** TRUE if this entity is of that type */
	public boolean type(Object entity, Object clss) {
		return (entity instanceof Integer && clss instanceof Integer && classesOf(
				(Integer) entity).contains((Integer) clss));
	}

	/** Returns direct and indirect classes of an instance */
	public Set<Integer> classesOf(Integer inst) {
		Set<Integer> result = new TreeSet<Integer>();
		for (Object clss : arg2ForRelationAndArg1(type.id, inst)) {
			addSuperclasses((Integer) clss, result);
		}
		return (result);
	}

	/** Adds the superclasses to the set */
	protected void addSuperclasses(Integer clss, Set<Integer> superclasses) {
		if (superclasses.contains(clss))
			return;
		superclasses.add(clss);
		for (Object sc : arg2ForRelationAndArg1(subclassof.id, clss)) {
			addSuperclasses((Integer) sc, superclasses);
		}
	}

	/** Returns all superclasses of this class */
	public Set<Integer> superclasses(Integer clss) {
		Set<Integer> superclasses = new TreeSet<Integer>();
		addSuperclasses(clss, superclasses);
		return (superclasses);
	}

	/** Adds the subclasses to the set. Stops at 1000 subclasses */
	protected void addSubclasses(Integer clss, Set<Integer> subclasses) {
		if (subclasses.contains(clss) || subclasses.size() > 1000)
			return;
		subclasses.add(clss);
		for (Object sc : arg1ForRelationAndArg2(subclassof.id, clss)) {
			addSubclasses((Integer) sc, subclasses);
		}
	}

	/**
	 * Returns all subclasses of this class. Stops at 1000 subclasses, returns
	 * NULL
	 */
	public Set<Integer> subclassesOf(Integer clss) {
		Set<Integer> subclasses = new TreeSet<Integer>();
		addSubclasses(clss, subclasses);
		if (subclasses.size() == 1000)
			return (null);
		return (subclasses);
	}

	/** Returns all facts about an entity */
	public Iterable<Fact> factsAbout(Integer entity) {
		Iterable<Fact> it1 = factsByArg1.getOrEmpty(entity);
		Iterable<Fact> it2 = factsByArg2.getOrEmpty(entity);
		return (new CombinedIterable<Fact>(it1, it2));
	}

	/** TRUE if the entity appears somewhere */
	public boolean exists(Integer e) {
		return (entitiesById.containsKey(e));
	}

	/** TRUE if the entity appears somewhere */
	public boolean exists(Object e) {
		if (e instanceof Integer)
			return (exists((Integer) e));
		if (Config.treatIdAsRelation && e instanceof String)
			return (true);
		return (factsByArg2String.containsKey(e.toString()));
	}

	// --------------------------------------------------------------
	// Harmonic Functionality
	// --------------------------------------------------------------

	/** returns the (harmonic) functionality */
	public double functionality(Integer rel) {
		if (rel < 0) {
			rel = -rel;
			TypedRelation r = relationsById.get(rel);
			if (r == null)
				return (-1);
			return (r.numArg2 / (double) r.numPairs);
		} else {
			TypedRelation r = relationsById.get(rel);
			if (r == null)
				return (-1);
			return (r.numArg1 / (double) r.numPairs);
		}
	}

	/** returns the (harmonic) functionality */
	public double functionality(Integer rel, FactStore other) {
		if (other.functionality(rel) == -1)
			return (functionality(rel));
		if (rel < 0) {
			rel = -rel;
			TypedRelation r = relationsById.get(rel);
			if (r == null)
				return (other.inverseFunctionality(rel));
			TypedRelation r2 = other.relation(rel);
			return ((r.numArg2 + r2.numArg2) / (double) (r.numPairs + r2.numPairs));
		} else {
			TypedRelation r = relationsById.get(rel);
			if (r == null)
				return (other.functionality(rel));
			TypedRelation r2 = other.relation(rel);
			return ((r.numArg1 + r2.numArg1) / (double) (r.numPairs + r2.numPairs));
		}
	}

	/** Returns the inverse (harmonic) functionality */
	public double inverseFunctionality(Integer rel) {
		return (functionality(-rel));
	}

	/** Returns the inverse (harmonic) functionality with another fact store */
	public double inverseFunctionality(Integer rel, FactStore other) {
		return (functionality(-rel, other));
	}

	// --------------------------------------------------------------
	// Main
	// --------------------------------------------------------------

	/** Prints facts about an entity */
	public void printFactsAbout(String name) {
		Entity ent = entity(name);
		if (ent == null)
			System.out.println("No entity named " + name + " in " + this);
		else
			printFactsAbout(ent.id);
	}

	/** Prints facts about an entity */
	public void printFactsAbout(Integer entity) {
		for (Fact f : factsAbout(entity))
			System.out.println(toString(f));
	}

	/** Prints facts with a relation */
	public void printFactsWith(String relation) {
		printFactsWith(relation(relation).id);
	}

	/** Prints facts about an entity */
	public void printFactsWith(Integer relation) {
		System.out.println("Facts with " + toString(relation));
		int counter = 0;
		for (Pair<Object, Object> f : factsForRelation(relation)) {
			if (++counter == 20)
				System.out.print("  and...");
			if (counter >= 20)
				continue;
			System.out.println(" " + toString(f.first()) + " " + toString(relation)
					+ " " + toString(f.second()));
		}
		if (counter >= 20)
			System.out.println((counter - 19) + " more");
	}

	/** Returns a toString that gets evaluated only if needed */
	public Object lazyToString(final Object entity) {
		return (new Object() {

			@Override
			public String toString() {
				return MemoryFactStore.this.toString(entity);
			}
		});
	}

	/** returns a string rep for an entity */
	public String toString(Object entity) {
		if (entity instanceof Integer)
			return (toString((Integer) entity));
		if (entity instanceof Long)
			return (toString((Long) entity));
		return (entity.toString());
	}

	/** returns a string rep for an entity */
	public String toString(Integer entity) {
		if (entity < 0) {
			TypedRelation rel = relationsById.get(-entity);
			if (rel != null)
				return (Config.invert(rel.name));
			assert (false);
			return ("UNKNOWN_RELATION");
		}
		TypedRelation rel = relationsById.get(entity);
		if (rel != null)
			return (rel.name);
		Entity e = entitiesById.get(entity);
		if (e != null)
			return (e.name);
		assert (false);
		return ("UNKNOWN_ENTITY");
	}

	/** returns a string rep for a fact */
	public String toString(Long factId) {
		Fact fact = factsById.get(factId);
		if (fact != null)
			return (toString(fact));
		assert (false);
		return ("UNKNOWN_FACT");
	}

	/** returns a string rep for a fact */
	public String toString(Fact fact) {
		return (toString(fact.arg1) + ", " + toString(fact.relation) + ", " + (fact.arg2 == 0 ? fact.arg2String
				: toString(fact.arg2)));
	}

	@Override
	public String toString() {
		return ("ontology " + config.myNameSpacePrefix + " with " + numRelations() + " relations");
	}

	public TypedRelation getIdRel() {
		return idRel;
	}

	public void registerPrefixNamespace(String prefix, String namespace) {
		config.myNameSpacePrefix = prefix;
	}

	public void debugEntity(String e) {
		Entity entity = entity(e);
		Announce.message(entity);
		for (Fact f : factsByArg1.getOrEmpty(entity.id)) {
			Announce.message(f);
		}
		Announce.message("FOR ARG 2");
		for (Fact f : factsByArg2.getOrEmpty(entity.id)) {
			Announce.message(f);
		}
		Announce.message("facts for arg2");
		for (Pair<Integer, Object> p : factsForArg2(entity.id)) {
			Announce.message(p.first());
			Announce.message(p.second());
		}
	}
	
	@Override
	public Iterable<QueryResult> similarLiterals(String query, double threshold) {
		// if we are trying to query literalIndex, then it must have been generated when creating the FactStore
		// otherwise, we fail
		assert(this.withShinglings);
		return this.literalIndex.query(Config.stripQuotes(query), threshold);
	}

}
