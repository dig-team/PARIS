package paris;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.naming.NameParser;

import paris.Config.EntityType;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.CombinedIterable;
import javatools.datatypes.CombinedIterator;
import javatools.datatypes.FinalMap;
import javatools.datatypes.FinalSet;
import javatools.datatypes.IterableForIterator;
import javatools.datatypes.MappedIterator;
import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;
import javatools.filehandlers.FileLines;
import javatools.parsers.DateParser;
import javatools.parsers.NumberParser;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an ontology using the Berkeley DB. 
 * In general, all entities are represented by integer codes. 
 * This includes relations. 
 * A negative code means the inverse of the relation (and is not stored as an entity).
 * 
 * This class provides code to read from 
 * - an RDF/N3 file
 * - an OWL file
 * - an RT file
 * - YAGO (TSV file + YAGO-specific translations)
 * - IMDB (TSV file + IMDB-specific translations)
 * The code does type detection on its own and 
 * ignores all type information that the file might contain. */
public class FactStore implements Closeable {

  // ---------------------------------------------------------------
  // Global values (specific to this instance of the fact store)
  // ---------------------------------------------------------------

  /** The relation between an entity and its id, if Config.treatIdAsRelation=TRUE */
  public TypedRelation idRel;

  /** The RDF:TYPE relation */
  public TypedRelation type;

  /** The RDFS:SUBCLASSOF relation */
  public TypedRelation subclassof;

  /** Administrational info for the fact store*/
  @com.sleepycat.persist.model.Entity
  public static class Configuration {

    @PrimaryKey
    int id = 1;

    /** Holds the name space prefix of this ontology. Needed for Config.treatIdAsRelation */
    public String myNameSpacePrefix;

  }

  /** Administrational info for the fact store*/
  public Configuration config;

  @com.sleepycat.persist.model.Entity
  /** Represents a fact. The second argument is either an entity (given by its integer code) 
   * or a literal (string). Exactly one of them is set.*/
  public static class Fact {

    @PrimaryKey(sequence = "ID")
    /** long id of this fact, used as a key in the Berkeley DB*/
    long id;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** first argument of the fact*/
    int arg1;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** relation of the fact*/
    int relation;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** second argument of the fact, if it is an entity*/
    int arg2;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** second argument of the fact, if it is a literal*/
    String arg2String;

    /** Constructs a fact*/
    public Fact() {

    }

    /** Constructs a fact*/
    public Fact(int a1, int r, int a2) {
      arg1 = a1;
      relation = r;
      arg2 = a2;
    }

    /** Constructs a fact*/
    public Fact(int a1, int r, String a2) {
      arg1 = a1;
      relation = r;
      arg2String = a2;
    }

    @Override
    public String toString() {
      return "(" + id + ") " + arg1 + ", " + relation + ", " + arg2();
    }

    /** Returns the second argument as a string or int*/
    public Object arg2() {
      if (arg2 != 0) return (arg2);
      return (arg2String);
    }

    /** Returns the Config.EntityType of the second argument*/
    public EntityType targetType() {
      if (arg2 != 0) return (EntityType.RESOURCE);
      return (Config.entityType(arg2String));
    }

  }

  @com.sleepycat.persist.model.Entity
  /** Represents a relation. The id is the same as the id of the corresponding entity. 
   * Inverse relations have a negative id and are not stored.*/
  public static class TypedRelation {

    /** corresponds to the entity id*/
    @PrimaryKey
    int id;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    /** Relation name*/
    String name;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** Range of the relation (in terms of Config.EntityType)*/
    EntityType targetType;

    /** Number of facts of this relation in the ontology*/
    long numPairs;

    /** Number of first args of this relation in the ontology*/
    int numArg1;

    /** Number of second args of this relation in the ontology*/
    int numArg2;

    /** Constructs a relation*/
    public TypedRelation() {

    }

    /** Constructs a relation*/
    public TypedRelation(String n) {
      name = n;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  @com.sleepycat.persist.model.Entity
  /** Represents an entity*/
  public static class Entity {

    @PrimaryKey(sequence = "ID")
    public int id;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    /** Entity name*/
    String name;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** TRUE if this is a class*/
    boolean isClass;

    /** Constructs an entity*/
    public Entity(String arg1, boolean iAmAClass) {
      name = arg1;
      isClass = iAmAClass;
    }

    /** Constructs an entity*/
    public Entity() {
    }

    @Override
    public String toString() {
      return name + (isClass ? "(class)" : "");
    }
  }

  /** Empty list*/
  public static final List<Object> emptyObjectList = new ArrayList<Object>();

  /** Empty list*/
  public static final List<Integer> emptyIntegerList = new ArrayList<Integer>();

  // ---------------------------------------------------------------
  // Properties and Accessor methods
  // ---------------------------------------------------------------

  /** Holds the store */
  protected EntityStore store;

  /** Holds all facts */
  protected PrimaryIndex<Long, Fact> factsById;

  /** Holds all facts by first arg */
  protected SecondaryIndex<Integer, Long, Fact> factsByArg1;

  /** Holds all facts by second arg */
  protected SecondaryIndex<Integer, Long, Fact> factsByArg2;

  /** Holds all facts by second arg */
  protected SecondaryIndex<String, Long, Fact> factsByArg2String;

  /** Holds all facts by relation */
  protected SecondaryIndex<Integer, Long, Fact> factsByRelation;

  /** Holds all relations */
  protected PrimaryIndex<Integer, TypedRelation> relationsById;

  /** Holds all relations */
  protected SecondaryIndex<String, Integer, TypedRelation> relationsByName;

  /** Holds all typed relations by type */
  protected SecondaryIndex<EntityType, Integer, TypedRelation> relationsByType;

  /** Returns the relations of this fact store (without inverse)*/
  public PeekIterator<Integer> relations() {
    return iterableForCursor(relationsById.keys());
  }

  /** returns the number of relations */
  protected int numRelations() {
    return (int) relationsByName.count();
  }

  /** TRUE if that entity is a relation */
  public boolean isRelation(Integer entity) {
    if (entity < 0) return (isRelation(-entity));
    return (relationsById.contains(entity));
  }

  /** Holds all the entities */
  protected PrimaryIndex<Integer, Entity> entitiesById;

  /** Holds all the entities */
  protected SecondaryIndex<String, Integer, Entity> entitiesByName;

  /** Holds all the entities by class/individual*/
  protected SecondaryIndex<Boolean, Integer, Entity> entitiesByNature;

  /** Returns all classes */
  public PeekIterator<Integer> classes() {
    return (primaryKeysForSecondaryKey(entitiesByNature, true));
  }

  /** Returns all entities */
  public PeekIterator<Integer> entities() {
    return (primaryKeysForSecondaryKey(entitiesByNature, false));
  }

  /** Returns a relation for an id*/
  public TypedRelation relation(Integer id) {
    return (relationsById.get(id));
  }

  /** Returns a relation for a name*/
  public TypedRelation relation(String name) {
    return (relationsByName.get(name));
  }

  /** Returns an entity for an id*/
  public Entity entity(Integer id) {
    return (entitiesById.get(id));
  }

  /** Returns an entity for a name*/
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
    if (numClasses == -1) numClasses = entitiesByNature.subIndex(true).count();
    return ((int) numClasses);
  }

  /** Holds number of entities */
  protected long numEntities = -1;

  /** Returns number of classes */
  public int numEntities() {
    if (numEntities == -1) numEntities = entitiesByNature.subIndex(false).count();
    return ((int) numEntities);
  }

  /** Returns all relations that have a given target type*/
  public Iterable<Integer> relationsWithTargetType(EntityType type) {
    return (primaryKeysForSecondaryKey(relationsByType, type));
  }

  /** Returns all relations that have a given source type*/
  public Iterable<Integer> relationsWithSourceType(EntityType type) {
    return (invertRelations(primaryKeysForSecondaryKey(relationsByType, type)));
  }

  /** Returns the target type of a relation*/
  public EntityType targetType(Integer relation) {
    if (relation < 0) return (EntityType.RESOURCE);
    TypedRelation rel = relationsById.get(relation);
    if (rel == null) return (null);
    return (rel.targetType);
  }

  /** Returns the source type of a relation*/
  public EntityType sourceType(Integer relation) {
    return (targetType(-relation));
  }

  /** Holds the number of facts */
  protected long numFacts = -1;

  /** Returns the number of facts */
  public long numFacts() {
    if (numFacts == -1) numFacts = factsById.count();
    return (numFacts);
  }

  // --------------------------------------------------------------
  // Administrational methods
  // --------------------------------------------------------------

  /** Prints information about this store*/
  public void print() {
    Announce.message("Ontology");
    Announce.message("  Namespace prefix:", config.myNameSpacePrefix);
    //Announce.message("  Facts:", numFacts()); // Takes too long
    Announce.message("  Relations:", numRelations());
    //Announce.message("  Classes:", numClasses()); // Takes too long
    Announce.message("  Entities:", numEntities(), ", for example");
    int c = 10;
    EntityCursor<Entity> e = entitiesById.entities();
    for (Entity x : e) {
      Announce.message("      ", x);
      if (!(c-- > 0)) break;
    }
    e.close();
    Announce.message("  Sample facts:");
    c = 10;
    Iterable<Fact> f = facts();
    for (Fact x : f) {
      Announce.message("      ", toString(x));
      if (!(c-- > 0)) break;
    }
    try {
      ((Closeable) f).close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    Announce.message("  Memory (Mb):");
    Announce.message("     Java Free: " + Runtime.getRuntime().freeMemory() / 1000 / 1000);
    Announce.message("     Java Max: " + Runtime.getRuntime().maxMemory() / 1000 / 1000);
    Announce.message("     Java Total: " + Runtime.getRuntime().totalMemory() / 1000 / 1000);
    Announce.message("     BDB cache percent: " + store.getEnvironment().getConfig().getCachePercent());
    Announce.message("     BDB cache size: " + store.getEnvironment().getConfig().getCacheSize() / 1000 / 1000);
  }

  @Override
  public void close() throws IOException {
    Announce.doing("Closing fact store");
    Environment e = store.getEnvironment();
    store.close();
    /*
    // This takes up to 6h without any evident advantage
    try {
      e.cleanLog();
    } catch (Exception ex) {
      // Gets thrown if the environment was read only. Nobody cares.
    }*/
    e.close();
    Announce.done();
  }

  // --------------------------------------------------------------
  // Loading
  // --------------------------------------------------------------

  /** Indicate that the subject is a relation*/
  protected static List<String> propertyTypes = Arrays.asList("rdf:Property", "owl:DatatypeProperty", "owl:DeprecatedProperty",
      "owl:FunctionalProperty", "owl:InverseFunctionalProperty", "owl:ObjectProperty");

  /** Indicate that the subject is a class*/
  protected static List<String> classTypes = Arrays.asList("rdfs:Class", "owl:Class");

  /** Indicate that the object is a class*/
  protected static List<String> classIndicatorsSecondArg = Arrays.asList("rdfs:domain", "rdfs:range", "rdfs:subClassOf", "rdf:type");// ,"http://reliant.teknowledge.com/DAML/Mid-level-ontology.owl#economyType","http://reliant.teknowledge.com/DAML/SUMO.owl#subAttribute","http://reliant.teknowledge.com/DAML/SUMO.owl#successorAttribute");

  /** Indicate that the subject is a class*/
  protected static List<String> classIndicatorsFirstArg = Arrays.asList("rdfs:subClassOf");// ,"http://reliant.teknowledge.com/DAML/SUMO.owl#subAttribute","http://reliant.teknowledge.com/DAML/SUMO.owl#successorAttribute");

  /** Adds a relation*/
  public TypedRelation addRelation(String relation) {
    TypedRelation result = relationsByName.get(relation);
    if (result == null) {
      result = new TypedRelation(relation);
      Entity relent = entitiesByName.get(relation);
      if (relent == null) {
        entitiesById.put(new Entity(relation, false));
        relent = entitiesByName.get(relation);
      }
      result.id = relent.id;
      relationsById.putNoReturn(result);
    }
    return (result);
  }

  /** Adds an entity*/
  public Entity addEntity(String entity) {
    Entity e = entitiesByName.get(entity);
    if (e == null) {
      entitiesById.putNoReturn(new Entity(entity, false));
      e = entitiesByName.get(entity); // Put returns NULL
    }
    return (e);
  }

  /** Adds a class*/
  public void addClass(String c) {
    Entity e = entitiesByName.get(c);
    if (e == null) e = new Entity(c, true);
    else if (e.isClass) return;
    else e.isClass = true;
    entitiesById.putNoReturn(e);
  }

  /** Adds a fact*/
  public void add(String arg1, String relation, String arg2, EntityType arg2Type) {
    arg1 = Config.format(arg1, EntityType.RESOURCE);
    relation = Config.format(relation, EntityType.RESOURCE);
    TypedRelation rel = addRelation(relation);
    Entity entity1 = addEntity(arg1);
    Entity entity2 = null;
    arg2 = Config.format(arg2, arg2Type);
    if (arg2Type == EntityType.RESOURCE) {
      entity2 = addEntity(arg2);
    }
    if (relation.equals("rdf:type")) {
      if (propertyTypes.contains(arg2)) addRelation(arg1);
      if (classTypes.contains(arg2)) addClass(arg1);
    }
    if (classIndicatorsFirstArg.contains(relation)) addClass(arg1);
    if (classIndicatorsSecondArg.contains(relation)) addClass(arg2);
    factsById.putNoReturn(entity2 == null ? new Fact(entity1.id, rel.id, arg2) : new Fact(entity1.id, rel.id, entity2.id));
  }

  /** Maps Yago relation names to their RDFS equivalent */
  public static FinalMap<String, String> yagoMap = new FinalMap<String, String>("type", "rdf:type", "subclassOf", "rdfs:subclassOf", "subpropertyOf",
      "rdfs:subPropertyOf", "hasDomain", "rdfs:domain", "hasRange", "rdfs:range", "means", "rdfs:label");

  /** Relations to exclude from YAGO*/
  public static final Set<String> yagoExcludeRelations = new FinalSet<String>("type_star", "isCalled", "hasWikipediaUrl", "hasPreferredMeaning",
      "hasPreferredName", "hasGeoCoordinates", "hasWebsite", "hasGloss", "hasValue", "hasGeonamesId", "hasSynsetId", "hasUTCOffset", "hasDuration",
      "endedOnDate", "startedOnDate", "wasDestroyedOnDate", "hasHeight", "partOf", "hasWeight", "hasPopulationDensity", "hasMusicalRole", "hasPages",
      "hasPredecessor", "hasRevenue");

  /** How to treat IMDB relations*/
  public enum IMDBType {
    RESOURCE(Config.EntityType.RESOURCE), NUMBER(Config.EntityType.NUMBER), DATE(Config.EntityType.DATE), STRING(Config.EntityType.STRING), STRINGASRESOURCE(
        Config.EntityType.RESOURCE), META(null);

    public Config.EntityType entityType;

    IMDBType(Config.EntityType e) {
      entityType = e;
    }
  };

  /** Maps IMDB relations to their target type*/
  public static final Map<String, IMDBType> imdbRelations = new FinalMap<String, IMDBType>("actedIn", IMDBType.RESOURCE,
      "bornIn",
      IMDBType.RESOURCE,
      "bornOn",
      IMDBType.DATE,
      //"character",IMDBType.META,
      "cinematographerOf",
      IMDBType.RESOURCE,
      "composerOf",
      IMDBType.RESOURCE,
      //"constumeDesignerOf",IMDBType.RESOURCE,
      //"creditPosition",IMDBType.NUMBER,
      //"deceasedFrom",IMDBType.STRING,
      "deceasedIn", IMDBType.RESOURCE, "deceasedOn", IMDBType.DATE, "directorOf", IMDBType.RESOURCE, "editorOf", IMDBType.RESOURCE, "episodeOf",
      IMDBType.RESOURCE, "firstName", IMDBType.STRING,
      //"gender",IMDBType.RESOURCE,
      "hasHeight", IMDBType.NUMBER, "hasLanguage", IMDBType.STRINGASRESOURCE,
      //"inCountry",IMDBType.META,
      "label", IMDBType.STRING, "lastName", IMDBType.STRING, "locatedIn", IMDBType.RESOURCE, "nickName", IMDBType.STRING, "producedIn",
      IMDBType.STRINGASRESOURCE, "producerOf", IMDBType.RESOURCE,
      //"productionDesignerOf",IMDBType.RESOURCE,
      "releasedOn", IMDBType.DATE, "type", IMDBType.STRINGASRESOURCE, "writerOf", IMDBType.RESOURCE);

  /** TRUE for YAGO fact identifiers*/
  public static boolean isYagoFactId(String s) {
    return (s.matches("#\\d+"));
  }

  /** Reads into a fact store */
  public void add(File file, String parseType) throws IOException {
    boolean test = false;

    // Recursive case
    if (file.isDirectory()) {
      Announce.doing("Loading", file);
      for (File f : file.listFiles())
        add(f, parseType);
      Announce.done();
      return;
    }

    // This is the special fact adder for IMDB
    // This will not necessarily work any more
    if (parseType.equalsIgnoreCase("pierreimdb")) {
      String relation = file.getName().substring(0, file.getName().length() - 4);
      IMDBType targetType = imdbRelations.get(relation);
      if (targetType == null) return;
      Set<String> stringsThatAreEntities = new TreeSet<String>();
      relation = relation.equals("type") ? "rdf:type" : relation.equals("label") ? "rdfs:label" : "imdb:" + relation;
      FileLines lines = new FileLines(new InputStreamReader(new FileInputStream(file), "UTF8"));
      int counter = 0;
      for (String line : lines) {
        if (test && counter++ > 10) {
          lines.close();
          break;
        }
        String[] split = line.split("\t");
        if (split.length < 3) continue;
        String arg1 = split[1];
        String arg2 = split[2];
        arg1 = "imdb:" + arg1;
        switch (targetType) {
          case DATE:
            arg2 = DateParser.normalize(arg2);
            break;
          case META:
            Announce.warning("Don't know what to do with meta facts");
            return;
          case NUMBER:
            if (arg2.contains("1/2\"")) continue;
            arg2 = arg2.replaceAll("(\\d+)' (\\d+)\"", "\\1 feet \\2 inches");
            arg2 = arg2.replaceAll("(\\d+)'", "\\1 feet");
            arg2 = NumberParser.normalize(arg2).trim();
            break;
          case STRING:
            if (arg2.contains("$") || arg2.length() < 3 || (!Character.isLetter(arg2.charAt(0)) && !Character.isDigit(arg2.charAt(0)))
                || arg2.equals("Too")) continue;
            break;
          case RESOURCE:
            arg2 = "imdb:" + arg2;
            break;
          case STRINGASRESOURCE:
            stringsThatAreEntities.add(arg2);
            arg2 = "imdb:" + arg2.replace(' ', '_');
        }
        add(arg1, relation, arg2, targetType.entityType);
      }
      for (String entity : stringsThatAreEntities)
        add("imdb:" + entity.replace(' ', '_'), "rdfs:label", entity, EntityType.RESOURCE);
      return;
    }

    // This is the special fact adder for YAGO
    if (parseType.equalsIgnoreCase("yagonative")) {
      String relation = file.getName().substring(0, file.getName().length() - 4);
      if (relation.startsWith("_") || yagoExcludeRelations.contains(relation)) return;
      boolean swapArgs = relation.equals("means");
      if (yagoMap.containsKey(relation)) relation = yagoMap.get(relation);
      else relation = "y:" + relation;
      FileLines lines = new FileLines(file, "Loading " + relation);
      int counter = 0;
      for (String line : lines) {
        if (test && counter++ > 10) {
          lines.close();
          break;
        }
        String[] split = line.split("\t");
        if (isYagoFactId(split[1]) || isYagoFactId(split[2])) {
          lines.close();
          Announce.message("  Meta facts only");
          break;
        }
        String arg1 = split[1];
        String arg2 = split[2];
        if (Config.entityType(arg1) == EntityType.RESOURCE) arg1 = "y:" + arg1;
        if (Config.entityType(arg2) == EntityType.RESOURCE) arg2 = "y:" + arg2;
        if (swapArgs) {
          String t = arg1;
          arg1 = arg2;
          arg2 = t;
        }
        add(arg1, relation, arg2, Config.entityType(arg2));
      }
      return;
    }

    // This is the generic fact TSV fact loader
    if (file.getName().endsWith(".tsv")) {
      if (!NumberParser.isInt(parseType) || parseType.length() != 3) {
        Announce.error("For TSV files, the parseType has to be 3 digits, and not", parseType);
        return;
      }
      int subjpos = parseType.charAt(0) - '1';
      if (subjpos < 0 || subjpos > 9) Announce.error("Invalid subject position identifier in parseType", parseType);
      int verbpos = parseType.charAt(1) - '1';
      if (verbpos < 0 || verbpos > 9) Announce.error("Invalid predicate position identifier in parseType", parseType);
      int objpos = parseType.charAt(2) - '1';
      if (objpos < 0 || objpos > 9) Announce.error("Invalid object position identifier in parseType", parseType);
      Map<String, Integer> entityTypes = new TreeMap<String, Integer>();
      Set<String> inverse = new TreeSet<String>();
      Map<String, Integer> counter = new TreeMap<String, Integer>();
      Announce.doing("Loading " + file.getName());
      FileLines lines = new FileLines(file);
      for (String line : lines) {
        if (line.trim().length() == 0) continue;
        String[] split = line.split("\t");
        if (split.length <= objpos || split.length <= subjpos || split.length <= verbpos) {
          Announce.warning("Not enough tab-separated elements in line\n" + line);
          continue;
        }
        String arg1 = split[subjpos];
        String relation = split[verbpos];
        String arg2 = split[objpos];
        if (test) {
          if (D.getOrZero(counter, relation) > 10) continue;
          D.addKeyValue(counter, relation, 1);
        }
        
        // Hacks for Konstantina
        if(file.getName().startsWith("imdb")) {
          if(relation.equals("actedIn")) {
            int pos=arg1.indexOf(',');
            if(pos>0) arg1=arg1.substring(pos+2)+' '+arg1.substring(0,pos);
          }
          if(arg2.matches(".+19..")) {
            arg2=arg2.substring(0,arg2.length()-5);
          }
          if(arg1.matches(".+19..")) {
            arg1=arg1.substring(0,arg1.length()-5);
          }
        }
        
        if (!entityTypes.containsKey(relation)) {
          Announce.message();
          D.p("Please choose among the following options:");
          D.p("Exclude the relationship", relation, "                 (1)");
          D.p("Facts are of the form    ENTITY", relation, "ENTITY    (2)");
          D.p("Facts are of the form    LITERAL", relation, "ENTITY   (3)");
          D.p("Facts are of the form    ENTITY", relation, "LITERAL   (4)");
          D.p("Facts are of the form    LITERAL", relation, "LITERAL  (5)");
          switch ((int) D.readLong("Your choice:")) {
            case 2:
              entityTypes.put(relation, 2);
              break;
            case 3:
              entityTypes.put(relation, 1);
              inverse.add(relation);
              break;
            case 4:
              entityTypes.put(relation, 1);
              break;
            case 5:
              D.println("Such relations cannot be converted to RDF. Skipping.");
              entityTypes.put(relation, 0);
              continue;
            default:
              D.println("Skipping.");
              entityTypes.put(relation, 0);
              continue;
          }
        }
        int type = entityTypes.get(relation);
        if (inverse.contains(relation)) {
          String a = arg1;
          arg1 = arg2;
          arg2 = a;
          relation += "INV";
        }
        if (!Config.hasPrefix(arg1)) arg1 = config.myNameSpacePrefix + Config.stripQuotes(arg1);
        if (!Config.hasPrefix(relation)) relation = config.myNameSpacePrefix + Config.stripQuotes(relation);
        switch (type) {
          case 0:
            // Exclude
            continue;
          case 1:
            // For literals            
            add(arg1, relation, arg2, Config.literalType(arg2));
            break;
          case 2:
            // For entities
            if (!Config.hasPrefix(arg2)) arg2 = config.myNameSpacePrefix + Config.stripQuotes(arg2);
            add(arg1, relation, arg2, EntityType.RESOURCE);
            break;
        }
      }
      Announce.done();
      return;
    }

    // OWL and RDF
    if (file.getName().endsWith(".owl") || file.getName().endsWith(".rdf")) {
      Announce.doing("Loading", file);
      Announce.doing("Creating model");
      Model model = ModelFactory.createDefaultModel();
      InputStream in = new FileInputStream(file);
      model.read(in, null);
      in.close();
      Announce.done();
      Announce.progressStart("Storing model", model.size());
      int counter = 0;
      for (Statement s : new IterableForIterator<Statement>(model.listStatements())) {
        if (test && counter++ > 10) break;
        if (s.getObject().isLiteral()) {
          add(s.getSubject().toString(), s.getPredicate().toString(), s.getObject().toString(), Config.literalType(s.getObject().toString()));
        } else {
          add(s.getSubject().toString(), s.getPredicate().toString(), s.getObject().toString(), EntityType.RESOURCE);
        }
      }
      Announce.progressDone();
      model.close();
      Announce.done();
      return;
    }

    // NT
    if (file.getName().endsWith(".nt")) {
      FileLines lines = new FileLines(file, "Loading " + file);
      int counter = 0;
      for (String line : lines) {
        if (test && counter++ > 100) break;
        line = line.trim();
        if (line.length() == 0) continue;
        try {
          String subj = line.substring(1, line.indexOf('>'));
          line = line.substring(subj.length() + 4);
          String pred = line.substring(0, line.indexOf('>'));
          line = line.substring(pred.length() + 2);
          String obj = line.substring(0, line.length() - 2);
          if (obj.startsWith("<")) {
            add(subj, pred, obj.substring(1, obj.length() - 1), EntityType.RESOURCE);
          } else if (obj.startsWith("\"")) {
            obj = Config.stripQuotes(obj);
            add(subj, pred, obj, Config.literalType(obj));
          }
        } catch (Exception e) {
          D.p(line);
          D.p(e.getMessage());
        }
      }
      lines.close();
      return;
    }

    Announce.warning("Unknown file type", file.getName(), "for parse type", parseType);
  }

  /** Computes functionalities*/
  protected void init() {
    Announce.doing("Computing functionalities");
    EntityCursor<TypedRelation> cursor = relationsById.entities();
    TypedRelation rel;
    while ((rel = cursor.next()) != null) {
      if (rel.id < 0) continue;
      if (rel.id == idRel.id) {
        rel.numArg1 = 1000;
        rel.numArg2 = 1000;
        rel.numPairs = 1000;
        rel.targetType = Config.EntityType.STRING;
        Announce.message("Computing functionality of " + rel);
        Announce.message("     numArg1=", rel.numArg1, "numArg2=", rel.numArg2, "numPairs=", rel.numPairs, "target=", rel.targetType);
        cursor.update(rel);
        continue;
      }
      int max = 100000;
      Announce.progressStart("Computing functionality of " + rel, max);
      int numPairs = 0;
      TreeSet<Integer> arg1 = new TreeSet<Integer>();
      TreeSet<Integer> arg2 = new TreeSet<Integer>();
      TreeMap<EntityType, Integer> types = new TreeMap<EntityType, Integer>();
      EntityCursor<Fact> facts = factsByRelation.subIndex(rel.id).entities();
      Fact fact;
      while (max-- > 0 && (fact = facts.next()) != null) {
        arg1.add(fact.arg1);
        arg2.add(fact.arg2().hashCode());
        D.addKeyValue(types, fact.targetType(), 1);
        Announce.progressStep();
        numPairs++;
      }
      Announce.progressDone();
      facts.close();
      if (numPairs == 0) {
        Announce.message("    no relation instances");
        continue;
      }
      rel.numArg1 = arg1.size();
      rel.numArg2 = arg2.size();
      rel.numPairs = numPairs;
      rel.targetType = null;
      for (EntityType t : types.keySet()) {
        if (rel.targetType == null || types.get(t) > types.get(rel.targetType)) rel.targetType = t;
      }
      Announce.message("     numArg1=", rel.numArg1, "numArg2=", rel.numArg2, "numPairs=", rel.numPairs, "target=", rel.targetType);
      cursor.update(rel);
    }
    cursor.close();
    Announce.done();
  }

  /** Creates a berkeley fact store from RDF files*/
  public static void createFactStore(File sourceFolder, String parseType, File targetFolder, String namespace, String prefix) throws IOException {
    Config.print();
    Announce.doing("Creating fact store from", sourceFolder);
    if (!targetFolder.exists()) targetFolder.mkdir();
    File[] files = targetFolder.listFiles();
    if (files != null && files.length > 0) {
      Announce.message("Do you want to delete the files in?", targetFolder);
      if (!D.r().equals("yes")) return;
      Announce.progressStart("Deleting existing files in " + targetFolder, files.length);
      for (File f : files) {
        f.delete();
        Announce.progressStep();
      }
      Announce.progressDone();
    }
    if (!prefix.endsWith(":")) prefix = prefix + ":";
    Config.prefixes.put(prefix, namespace);
    FactStore f = new FactStore(targetFolder, true);
    f.config.myNameSpacePrefix = prefix;
    f.updateConfig();
    f.add(sourceFolder, parseType);
    f.numEntities = -1;
    f.init();
    f.print();
    f.close();
    Announce.done();
  }

  /** Constructor*/
  public FactStore(File folder) {
    this(folder, false);
  }

  /** Constructor*/
  public FactStore(File folder, boolean allowWrite) {
    Announce.doing("Connecting to fact store", folder);
    Announce.doing("Building environment (this can take some time)");
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setCachePercent(20);
    envConfig.setReadOnly(!allowWrite);
    envConfig.setAllowCreate(allowWrite);
    Environment myDbEnvironment = new Environment(folder, envConfig);
    StoreConfig storeconf = new StoreConfig();
    storeconf.setReadOnly(!allowWrite);
    storeconf.setAllowCreate(allowWrite);
    storeconf.setDeferredWrite(true);
    storeconf.setTransactional(false);
    Announce.doneDoing("Loading data");
    store = new EntityStore(myDbEnvironment, "FactStore", storeconf);
    this.entitiesById = store.getPrimaryIndex(Integer.class, Entity.class);
    this.entitiesByName = store.getSecondaryIndex(entitiesById, String.class, "name");
    this.entitiesByNature = store.getSecondaryIndex(entitiesById, Boolean.class, "isClass");
    this.factsById = store.getPrimaryIndex(Long.class, Fact.class);
    this.factsByArg1 = store.getSecondaryIndex(factsById, Integer.class, "arg1");
    this.factsByArg2 = store.getSecondaryIndex(factsById, Integer.class, "arg2");
    this.factsByArg2String = store.getSecondaryIndex(factsById, String.class, "arg2String");
    this.factsByRelation = store.getSecondaryIndex(factsById, Integer.class, "relation");
    this.relationsById = store.getPrimaryIndex(Integer.class, TypedRelation.class);
    this.relationsByType = store.getSecondaryIndex(relationsById, EntityType.class, "targetType");
    this.relationsByName = store.getSecondaryIndex(relationsById, String.class, "name");
    this.config = getConfig();
    idRel = addRelation("@id");
    type = addRelation("rdf:type");
    subclassof = addRelation("rdfs:subclassOf");
    Announce.done();
    print();
    Announce.done();
  }

  /** Returns the configuration*/
  protected Configuration getConfig() {
    Configuration config = store.getPrimaryIndex(Integer.class, Configuration.class).get(1);
    if (config == null) config = new Configuration();
    return (config);
  }

  /** Permanently stores the configuration*/
  protected void updateConfig() {
    store.getPrimaryIndex(Integer.class, Configuration.class).putNoReturn(config);
  }

  // --------------------------------------------------------------
  // Mappers
  // --------------------------------------------------------------

  /** Returns subjects*/
  public Set<Object> subjects(ForwardCursor<Fact> it) {
    Set<Object> result = new HashSet<Object>();
    Fact f;
    while ((f = it.next()) != null)
      result.add(f.arg1);
    it.close();
    return (result);
  }

  /** Returns objects*/
  public Set<Object> objects(ForwardCursor<Fact> it) {
    Set<Object> result = new HashSet<Object>();
    Fact f;
    while ((f = it.next()) != null)
      result.add(f.arg2());
    it.close();
    return (result);
  }

  /** Returns relations*/
  public Set<Integer> relations(ForwardCursor<Fact> it) {
    Set<Integer> result = new TreeSet<Integer>();
    Fact f;
    while ((f = it.next()) != null)
      result.add(f.relation);
    it.close();
    return (result);
  }

  /** Returns subjects*/
  public Iterable<Integer> subjects(Iterator<Fact> it) {
    Iterator<Integer> it2 = new MappedIterator<Fact, Integer>(it, new MappedIterator.Map<Fact, Integer>() {

      @Override
      public Integer map(Fact a) {
        return a.arg1;
      }
    });
    return (new IterableForIterator<Integer>(it2));
  }

  /** Returns relations*/
  public Iterable<Integer> relations(Iterator<Fact> it) {
    return (new IterableForIterator<Integer>(new MappedIterator<Fact, Integer>(it, new MappedIterator.Map<Fact, Integer>() {

      @Override
      public Integer map(Fact a) {
        return a.relation;
      }
    })));
  }

  /** Returns relations, inverted*/
  public Iterable<Integer> invertedRelations(Iterator<Fact> it) {
    return (new IterableForIterator<Integer>(new MappedIterator<Fact, Integer>(it, new MappedIterator.Map<Fact, Integer>() {

      @Override
      public Integer map(Fact a) {
        return -a.relation;
      }
    })));
  }

  /** Returns objects*/
  public Iterable<Object> objects(Iterator<Fact> it) {
    Iterator<Object> it2 = new MappedIterator<Fact, Object>(it, new MappedIterator.Map<Fact, Object>() {

      @Override
      public Object map(Fact a) {
        return a.arg2();
      }
    });
    return (new IterableForIterator<Object>(it2));
  }

  /** Returns arg1 and rel*/
  public Iterable<Pair<Integer, Object>> arg1andRelation(Iterator<Fact> it) {
    Iterator<Pair<Integer, Object>> it2 = new MappedIterator<Fact, Pair<Integer, Object>>(it, new MappedIterator.Map<Fact, Pair<Integer, Object>>() {

      @Override
      public Pair<Integer, Object> map(Fact a) {
        return new Pair<Integer, Object>(a.relation, a.arg1);
      }
    });
    return (new IterableForIterator<Pair<Integer, Object>>(it2));
  }

  /** Returns all facts with the given arg as first arg*/
  public Iterable<Fact> factsForSubject(Integer arg1) {
    return (iterableForCursor(factsByArg1.subIndex(arg1).entities()));
  }

  /** Returns arg2 and rel*/
  public Iterable<Pair<Integer, Object>> arg2andInvertedRelation(Iterator<Fact> it) {
    Iterator<Pair<Integer, Object>> it2 = new MappedIterator<Fact, Pair<Integer, Object>>(it, new MappedIterator.Map<Fact, Pair<Integer, Object>>() {

      @Override
      public Pair<Integer, Object> map(Fact a) {
        return new Pair<Integer, Object>(-a.relation, a.arg2());
      }
    });
    return (new IterableForIterator<Pair<Integer, Object>>(it2));
  }

  /** Returns an iterable for an entity cursor. The iterable must be run until termination to ensure closing!*/
  public static <T> PeekIterator<T> iterableForCursorUnique(final EntityCursor<T> e) {
    return new PeekIterator<T>() {

      @Override
      protected T internalNext() throws Exception {
        T result = e.nextNoDup();
        if (result == null) e.close();
        return (result);
      }

      @Override
      public void close() {
        e.close();
      }
    };
  }

  /** Returns an iterable for an entity cursor. The iterable must be run until termination to ensure closing!*/
  public static <T> PeekIterator<T> iterableForCursor(final ForwardCursor<T> e) {
    return new PeekIterator<T>() {

      @Override
      protected T internalNext() throws Exception {
        T result = e.next();
        if (result == null) e.close();
        return (result);
      }

      @Override
      public void close() {
        e.close();
      }
    };
  }

  /** Returns an iterable for an entity cursor. The iterable must be run until termination to ensure closing!*/
  public static <T> PeekIterator<T> iterableForCursor(final ForwardCursor<T> e, final int max) {
    return new PeekIterator<T>() {

      int count = max;

      @Override
      protected T internalNext() throws Exception {
        T result = (count-- == 0) ? null : e.next();
        if (result == null) e.close();
        return (result);
      }

      @Override
      public void close() {
        e.close();
      }
    };
  }

  /** Returns an iterable for an entity cursor. The iterable must be run until termination to ensure closing!*/
  public static <PK, E> PeekIterator<E> iterableForIndex(final EntityIndex<PK, E> e) {
    return (iterableForCursor(e.entities()));
  }

  /** Returns an iterable for an entity cursor. The iterable must be run until termination to ensure closing!*/
  public static <PK, E> PeekIterator<E> iterableForIndexUnique(final EntityIndex<PK, E> e) {
    return (iterableForCursorUnique(e.entities()));
  }

  /** Returns an iterable for an entity cursor. The iterable must be run until termination to ensure closing!*/
  public static <PK, SK, E> PeekIterator<PK> primaryKeysForSecondaryKey(final SecondaryIndex<SK, PK, E> index, SK secondaryKey) {
    return (iterableForCursor(index.subIndex(secondaryKey).keys()));
  }

  /** Inverts all throughcoming relations */
  public static Iterable<Integer> invertRelations(Iterable<Integer> i) {
    return (new IterableForIterator<Integer>(new MappedIterator<Integer, Integer>(i.iterator(), new MappedIterator.Map<Integer, Integer>() {

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
    return (iterableForIndex(factsById));
  }

  /**
   * returns all facts with that relation.
   */
  public MappedIterator<?, Pair<Object, Object>> factsForRelation(final int rel) {
    if (Config.treatIdAsRelation && (rel == idRel.id || rel == -idRel.id)) {
      return (new MappedIterator<Entity, Pair<Object, Object>>(iterableForIndex(entitiesById),
          new MappedIterator.Map<Entity, Pair<Object, Object>>() {

            @Override
            public Pair<Object, Object> map(Entity a) {
              if (rel == idRel.id) return (new Pair<Object, Object>(a.id, entityName(a.name)));
              return (new Pair<Object, Object>(entityName(a.name), a.id));
            }
          }));
    }

    if (rel < 0) {
      return (new MappedIterator<Fact, Pair<Object, Object>>(iterableForIndex(factsByRelation.subIndex(-rel)),
          new MappedIterator.Map<Fact, Pair<Object, Object>>() {

            @Override
            public Pair<Object, Object> map(Fact a) {
              return (new Pair<Object, Object>(a.arg2(), a.arg1));
            }
          }));
    }
    return (new MappedIterator<Fact, Pair<Object, Object>>(iterableForIndex(factsByRelation.subIndex(rel)),
        new MappedIterator.Map<Fact, Pair<Object, Object>>() {

          @Override
          public Pair<Object, Object> map(Fact a) {
            return (new Pair<Object, Object>(a.arg1, a.arg2()));
          }
        }));
  }

  /** returns the second argument for a relation with given first arg */
  public Collection<Object> arg2ForRelationAndArg1(Integer rel, Object arg1) {
    if (Config.treatIdAsRelation && rel == idRel.id) {
      if (!(arg1 instanceof Integer)) return (emptyObjectList);
      return (Arrays.asList((Object) entityName((Integer) arg1)));
    }
    if (Config.treatIdAsRelation && rel == -idRel.id) {
      if (!(arg1 instanceof String)) return (emptyObjectList);
      String entity = config.myNameSpacePrefix + Config.stripQuotes(arg1.toString());
      Entity e = entitiesByName.get(Config.compress(entity));
      if (e != null) return (Arrays.asList((Object) e.id));
      return (emptyObjectList);
    }
    // Hack for dbpedia. DBpedia does not contain '#' in numbers/dates, so return immediately instead of trying to find matches.
    if (config.myNameSpacePrefix.equals("dbp:") && arg1.toString().contains("#")) return (emptyObjectList);
    EntityJoin<Long, Fact> join = new EntityJoin<Long, Fact>(factsById);
    if (rel < 0) {
      if (arg1 instanceof Integer) join.addCondition(factsByArg2, (Integer) arg1);
      else join.addCondition(factsByArg2String, arg1.toString());
      join.addCondition(factsByRelation, -rel);
      return (subjects(join.entities()));
    } else {
      if (!(arg1 instanceof Integer)) return (emptyObjectList);
      join.addCondition(factsByArg1, (Integer) arg1);
      join.addCondition(factsByRelation, rel);
      return (objects(join.entities()));
    }
  }

  /** returns all relations where this entity appears a second arg */
  public Iterable<Integer> relationsForArg2(Object arg2) {
    Set<Integer> result = new TreeSet<Integer>();
    if (arg2 instanceof Integer) {
      for (Fact f : iterableForIndexUnique(factsByArg2.subIndex((Integer) arg2))) {
        result.add(f.relation);
      }
      for (Fact f : iterableForIndexUnique(factsByArg1.subIndex((Integer) arg2))) {
        result.add(-f.relation);
      }
    } else {
      for (Fact f : iterableForIndex(factsByArg2String.subIndex(arg2.toString()))) {
        result.add(f.relation);
      }
    }
    return (result);
  }

  /** returns all facts where this entity appears a second arg */
  public Iterable<Pair<Integer, Object>> factsForArg2(Object arg2) {
    if (arg2 instanceof Integer) {
      Iterable<Pair<Integer, Object>> it1 = arg1andRelation(iterableForIndex(factsByArg2.subIndex((Integer) arg2)));
      Iterable<Pair<Integer, Object>> it2 = arg2andInvertedRelation(iterableForIndex(factsByArg1.subIndex((Integer) arg2)));
      CombinedIterable<Pair<Integer, Object>> combo = new CombinedIterable<Pair<Integer, Object>>(it1, it2);
      if (Config.treatIdAsRelation) {
        combo.add(new Pair<Integer, Object>(-idRel.id, entityName((Integer) arg2)));
      }
      return (combo);
    }
    return (arg1andRelation(iterableForIndex(factsByArg2String.subIndex(arg2.toString()))));
  }

  /** returns all facts where this entity appears a second arg */
  public Iterable<Pair<Integer, Object>> factsForArg1(Object arg1) {
    return (new MappedIterator<Pair<Integer, Object>, Pair<Integer, Object>>(factsForArg2(arg1).iterator(),
        new MappedIterator.Map<Pair<Integer, Object>, Pair<Integer, Object>>() {

          @Override
          public Pair<Integer, Object> map(Pair<Integer, Object> a) {
            a.first = -a.first;
            return a;
          }
        }));
  }

  /** returns all relations where these entities appear as first arg and second arg*/
  public Iterable<Integer> relationsForArg1Arg2(Object arg1, Object arg2) {
    EntityJoin<Long, Fact> join = new EntityJoin<Long, Fact>(factsById);
    Iterable<Integer> it1 = emptyIntegerList;
    if (arg1 instanceof Integer) {
      join.addCondition(factsByArg1, (Integer) arg1);
      if (arg2 instanceof Integer) join.addCondition(factsByArg2, (Integer) arg2);
      else join.addCondition(factsByArg2String, arg2.toString());
      it1 = relations(iterableForCursor(join.entities()));
    }
    if (arg2 instanceof Integer) {
      join = new EntityJoin<Long, Fact>(factsById);
      join.addCondition(factsByArg1, (Integer) arg2);
      if (arg1 instanceof Integer) join.addCondition(factsByArg2, (Integer) arg1);
      else join.addCondition(factsByArg2String, arg1.toString());
      it1 = new CombinedIterable<Integer>(it1, invertedRelations(iterableForCursor(join.entities())));
    }
    return (it1);
  }

  /** returns all relations where this entity appears a first arg */
  public Set<Integer> relationsForArg1(Object arg1) {
    Set<Integer> result = new TreeSet<Integer>();
    if (arg1 instanceof Integer) {
      for (Fact f : iterableForIndexUnique(factsByArg1.subIndex((Integer) arg1))) {
        result.add(f.relation);
      }
      for (Fact f : iterableForIndexUnique(factsByArg2.subIndex((Integer) arg1))) {
        result.add(-f.relation);
      }
    } else {
      for (Fact f : iterableForIndex(factsByArg2String.subIndex(arg1.toString()))) {
        result.add(-f.relation);
      }
    }
    return (result);
  }

  /** returns all first arguments for this relation and given second arg */
  public Iterable<? extends Object> arg1ForRelationAndArg2(Integer rel, Object arg2) {
    return (arg2ForRelationAndArg1(-rel, arg2));
  }

  /** Returns the direct instances of a class */
  public Iterable<Integer> directInstancesOf(Integer clss, int max) {
    EntityJoin<Long, Fact> join = new EntityJoin<Long, Fact>(factsById);
    join.addCondition(factsByArg2, clss);
    join.addCondition(factsByRelation, type.id);
    return (subjects(iterableForCursor(join.entities(), max)));
  }

  /** Returns the instances of a class. Returns NULL in case there are more than 1000 subclasses. Returns maximally MAX elements per class */
  public CombinedIterable<Integer> instancesOf(Integer clss, int max) {
    CombinedIterable<Integer> result = new CombinedIterable<Integer>();
    Set<Integer> classes = subclassesOf(clss);
    if (classes == null) return (null);
    for (Integer c : classes) {
      result.add(directInstancesOf(c, max));
    }
    return (result);
  }

  // --------------------------------------------------------------
  // Checking facts
  // --------------------------------------------------------------

  /**
   * TRUE if the fact holds. This is used nowhere.
   * 
   * Currently implements completeness assumption on known relations. Does
   * chaining for TYPE
   */
  public int fact(Integer rel, Object arg1, Object arg2) {
    if (rel < 0) return (fact(-rel, arg2, arg1));
    if (!(arg1 instanceof Integer)) return (0);
    if (rel == idRel.id) return (arg2.equals(entityName((Integer) arg1)) ? 1 : 0);
    if (rel == type.id) return (type(arg1, arg2) ? 1 : 0);
    EntityJoin<Long, Fact> join = new EntityJoin<Long, Fact>(factsById);
    join.addCondition(factsByArg1, (Integer) arg1);
    join.addCondition(factsByRelation, rel);
    if (arg2 instanceof Integer) join.addCondition(factsByArg2, (Integer) arg2);
    else join.addCondition(factsByArg2String, arg2.toString());
    ForwardCursor<Long> c = join.keys();
    boolean result = c.next() != null;
    c.close();
    return (result ? 1 : 0);
  }

  /** TRUE if this entity is of that type */
  public boolean type(Object entity, Object clss) {
    return (entity instanceof Integer && clss instanceof Integer && classesOf((Integer) entity).contains((Integer) clss));
  }

  /** Returns direct and indirect classes of an instance */
  public Set<Integer> classesOf(Integer inst) {
    Set<Integer> result = new TreeSet<Integer>();
    for (Object clss : arg2ForRelationAndArg1(type.id, inst)) {
      addSuperclasses((Integer) clss, result);
    }
    return (result);
  }

  /** Adds the superclasses to the set*/
  protected void addSuperclasses(Integer clss, Set<Integer> superclasses) {
    if (superclasses.contains(clss)) return;
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

  /** Adds the subclasses to the set. Stops at 1000 subclasses*/
  protected void addSubclasses(Integer clss, Set<Integer> subclasses) {
    if (subclasses.contains(clss) || subclasses.size() > 1000) return;
    subclasses.add(clss);
    for (Object sc : arg1ForRelationAndArg2(subclassof.id, clss)) {
      addSubclasses((Integer) sc, subclasses);
    }
  }

  /** Returns all subclasses of this class. Stops at 1000 subclasses, returns NULL */
  public Set<Integer> subclassesOf(Integer clss) {
    Set<Integer> subclasses = new TreeSet<Integer>();
    addSubclasses(clss, subclasses);
    if (subclasses.size() == 1000) return (null);
    return (subclasses);
  }

  /** Returns all facts about an entity*/
  public Iterable<Fact> factsAbout(Integer entity) {
    Iterable<Fact> it1 = iterableForCursorUnique(factsByArg1.subIndex(entity).entities());
    Iterable<Fact> it2 = iterableForCursorUnique(factsByArg2.subIndex(entity).entities());
    return (new CombinedIterable<Fact>(it1, it2));
  }

  /** TRUE if the entity appears somewhere*/
  public boolean exists(Integer e) {
    return (entitiesById.contains(e));
  }

  /** TRUE if the entity appears somewhere*/
  public boolean exists(Object e) {
    if (e instanceof Integer) return (exists((Integer) e));
    if (Config.treatIdAsRelation && e instanceof String) return (true);
    return (factsByArg2String.contains(e.toString()));
  }

  // --------------------------------------------------------------
  // Harmonic Functionality
  // --------------------------------------------------------------

  /** returns the (harmonic) functionality */
  public double functionality(Integer rel) {
    if (rel < 0) {
      rel = -rel;
      TypedRelation r = relationsById.get(rel);
      if (r == null) return (-1);
      return (r.numArg2 / (double) r.numPairs);
    } else {
      TypedRelation r = relationsById.get(rel);
      if (r == null) return (-1);
      return (r.numArg1 / (double) r.numPairs);
    }
  }

  /** returns the (harmonic) functionality */
  public double functionality(Integer rel, FactStore other) {
    if (other.functionality(rel) == -1) return (functionality(rel));
    if (rel < 0) {
      rel = -rel;
      TypedRelation r = relationsById.get(rel);
      if (r == null) return (other.inverseFunctionality(rel));
      TypedRelation r2 = other.relationsById.get(rel);
      return ((r.numArg2 + r2.numArg2) / (double) (r.numPairs + r2.numPairs));
    } else {
      TypedRelation r = relationsById.get(rel);
      if (r == null) return (other.functionality(rel));
      TypedRelation r2 = other.relationsById.get(rel);
      return ((r.numArg1 + r2.numArg1) / (double) (r.numPairs + r2.numPairs));
    }
  }

  /** Returns the inverse (harmonic) functionality */
  public double inverseFunctionality(Integer rel) {
    return (functionality(-rel));
  }

  /** Returns the inverse (harmonic) functionality with another fact store*/
  public double inverseFunctionality(Integer rel, FactStore other) {
    return (functionality(-rel, other));
  }

  // --------------------------------------------------------------
  // Main
  // --------------------------------------------------------------

  /** Prints facts about an entity*/
  public void printFactsAbout(String name) {
    Entity ent = entity(name);
    if (ent == null) System.out.println("No entity named " + name + " in " + this);
    else printFactsAbout(ent.id);
  }

  /** Prints facts about an entity*/
  public void printFactsAbout(Integer entity) {
    for (Fact f : factsAbout(entity))
      System.out.println(toString(f));
  }

  /** Prints facts with a relation */
  public void printFactsWith(String relation) {
    printFactsWith(relation(relation).id);
  }

  /** Prints facts about an entity*/
  public void printFactsWith(Integer relation) {
    System.out.println("Facts with " + toString(relation));
    int counter = 0;
    for (Pair<Object, Object> f : factsForRelation(relation)) {
      if (++counter == 20) System.out.print("  and...");
      if (counter >= 20) continue;
      System.out.println(" " + toString(f.first()) + " " + toString(relation) + " " + toString(f.second()));
    }
    if (counter >= 20) System.out.println((counter - 19) + " more");
  }

  /** Returns a toString that gets evaluated only if needed*/
  public Object lazyToString(final Object entity) {
    return (new Object() {

      @Override
      public String toString() {
        return FactStore.this.toString(entity);
      }
    });
  }

  /** returns a string rep for an entity*/
  public String toString(Object entity) {
    if (entity instanceof Integer) return (toString((Integer) entity));
    if (entity instanceof Long) return (toString((Long) entity));
    return (entity.toString());
  }

  /** returns a string rep for an entity*/
  public String toString(Integer entity) {
    if (entity < 0) {
      TypedRelation rel = relationsById.get(-entity);
      if (rel != null) return (Config.invert(rel.name));
      return ("UNKNOWN_RELATION");
    }
    TypedRelation rel = relationsById.get(entity);
    if (rel != null) return (rel.name);
    Entity e = entitiesById.get(entity);
    if (e == null) return ("UNKNOWN_ENTITY");
    return (e.name);
  }

  /** returns a string rep for a fact*/
  public String toString(Long factId) {
    Fact fact = factsById.get(factId);
    if (fact == null) return ("UNKNOWN_FACT");
    return (toString(fact));
  }

  /** returns a string rep for a fact*/
  public String toString(Fact fact) {
    return (toString(fact.arg1) + ", " + toString(fact.relation) + ", " + (fact.arg2 == 0 ? fact.arg2String : toString(fact.arg2)));
  }

  @Override
  public String toString() {
    return ("ontology " + config.myNameSpacePrefix + " with " + numRelations() + " relations");
  }

  /** For creating a fact store */
  public static void main(String[] args) throws Exception {
    if (args == null || args.length < 5) Announce.help("FactStore <sourceFile> <parseType> <targetFolder> <nameSpace> <prefix>\n",
        "Creates a FactStore from a source ontology.", "* <sourceFile> can be an RDF, OWL, NT or TSV file, or a folder containing such files",
        "* <parseType> can be", "     yagoNative   (for TSV files of the native YAGO2)", "     pierreImdb   (for TSV files in Pierre's format)",
        "     rdf          (for OWL, RDF, or NT files)", "     SPO          (for TSV files, where the S is the 1-based column number of",
        "                    the subject, P of the predicate, and O of the object (e.g., 123))",
        "* <targetFolder> has to be an empty folder where the factstore will live",
        "* <nameSpace> is the namespace of the ontology you load (e.g., http://www.mpii.de/yago/resource/)",
        "* <prefix> is the prefix by which you want to abbreviate the namespace (e.g., y:)");
    createFactStore(new File(args[0]), args[1], new File(args[2]), args[3], args[4]);
    //createFactStore(new File("c:\\Fabian\\data\\person\\rdf\\person11.rdf"), new File("c:\\Fabian\\data\\person\\11"), "o1:");
    //createFactStore(new File("c:\\Fabian\\data\\person\\rdf\\person12.rdf"), new File("c:\\Fabian\\data\\person\\12"), "o2:");
    //createFactStore(new File("c:\\Fabian\\data\\person\\rdf\\person21.rdf"), new File("c:\\Fabian\\data\\person\\21"), "o1:");
    //createFactStore(new File("c:\\Fabian\\data\\person\\rdf\\person22.rdf"), new File("c:\\Fabian\\data\\person\\22"), "o2:");
    //createFactStore(new File("c:\\Fabian\\data\\person\\rdf\\restaurant2.rdf"), new File("c:\\Fabian\\data\\person\\restaurant2"), "okkam2:");

    //createFactStore(new File("/home/pierre/imdb/ontology"), new File("/media/ssd/fabian/data/imdb/berkeleydb"), "imdb:");
    /*FactStore fs = new FactStore(new File("/media/ssd/fabian/data/imdb/berkeley"), true);
    fs.add(new File("/home/pierre/imdb/ontology/label.tsv"));
    fs.numEntities = -1;
    fs.numClasses=-1;
    fs.numFacts=-1;
    fs.init();
    fs.close();*/
    //createFactStore(new File("c:\\Fabian\\data\\dbpedia\\n3"), new File("c:\\Fabian\\data\\dbpedia\\berkeley"), "dbp:");
    //createFactStore(new File("c:\\Fabian\\data\\yago\\data\\fact"), new File("c:\\Fabian\\data\\yago\\data\\berkeley"), "y:");
    //FactStore fs = new FactStore(new File("c:\\Fabian\\data\\yago\\data\\berkeley"));
    //fs.close();
    /*for (Fact fact : fs.facts())
      System.out.println(fs.toString(fact));
    fs.print();
    fs.close();*/
  }

}
