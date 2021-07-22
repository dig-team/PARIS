package paris.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import paris.Config;
import paris.Setting;
import paris.JoinRelation;
import paris.shingling.QueryResult;
import paris.shingling.ShinglingTable;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.parsers.NumberFormatter;
import bak.pcj.IntIterator;
import bak.pcj.map.ObjectKeyIntMap;
import bak.pcj.map.ObjectKeyIntOpenHashMap;
import bak.pcj.set.IntOpenHashSet;
import bak.pcj.set.IntSet;

/** Represents a set of facts */
public class FactStore {

  /** Approximate string index */
  ShinglingTable literalIndex;

  /** Maps entity names and literals to their ids */
  protected ObjectKeyIntMap entityNames = new ObjectKeyIntOpenHashMap();

  /**
   * Maps relation names to their ids, everyone followed by its inverse relation
   */
  protected ObjectKeyIntMap relationNames = new ObjectKeyIntOpenHashMap();

  /** Maps ids to entity names */
  protected ArrayList<String> entities = new ArrayList<>();

  /** Maps ids to relation names */
  protected ArrayList<String> relations = new ArrayList<>();

  /** Maps to the functionalities */
  protected double[] functionalities;

  /** Holds the prefix of the fact store */
  public final String prefix;

  /** Holds the prefix expansion of the fact store */
  public final String uri;

  /** Formatters of this fact store */
  public final LiteralFormatter[] formatters;

  /** Store if something is a class */
  protected boolean isClass[];
  protected boolean isLiteral[];

  protected int joinLengthLimit;

  /**
   * True when the factStore has been loaded and should not be modified any more
   */
  public boolean finalized = false;

  int cachedNumLiterals;

  int cachedNumClasses;

  public Setting setting;

  /** Constant for rdf:type*/
  public int TYPE;

  /** Constant for rdfs:subclassOf*/
  public int SUBCLASSOF;

  /** Constructor */
  public FactStore(Setting setting, String prefix, String uri, int joinLengthLimit, LiteralFormatter... formis) {
    this.prefix = prefix;
    this.uri = uri;
    this.joinLengthLimit = joinLengthLimit;
    this.setting = setting;
    formatters = formis;
    addRelation("<xxx-unused>");
    addEntity("<xxx-unused>");
  }

  public static LiteralFormatter[] getArgs(boolean normalizeStrings, boolean normalizeDatesToYears) {
    Collection<LiteralFormatter> formis = new LinkedList<LiteralFormatter>();
    formis.add(LiteralFormatter.CUT_DATATYPE);
    if (normalizeStrings) formis.add(LiteralFormatter.NORMALIZE);
    if (normalizeDatesToYears) formis.add(LiteralFormatter.TRIM_TO_YEAR);
    return (LiteralFormatter[]) formis.toArray(new LiteralFormatter[formis.size()]);
  }

  /** Constructor */
  public FactStore(Setting setting, String prefix, String uri, int joinLengthLimit, boolean normalizeStrings, boolean normalizeDatesToYears) {
    this(setting, prefix, uri, joinLengthLimit, getArgs(normalizeStrings, normalizeDatesToYears));
  }

  /** holds a predicate and an object */
  public class PredicateAndObject {

    public int predicate;

    public int object;

    public PredicateAndObject(int predicate2, int object2) {
      predicate = predicate2;
      object = object2;
    }

    @Override
    public String toString() {
      return relation(predicate) + " " + entity(object);
    }
  }

  /** Maps a subject to a list of predicates and objects */
  protected ArrayList<ArrayList<PredicateAndObject>> facts = new ArrayList<ArrayList<PredicateAndObject>>();

  /** Adds a fact */
  public void add(int subject, int predicate, int object) {
    synchronized (facts) {
      facts.ensureCapacity(subject);
      while (facts.size() <= subject)
        facts.add(null);
      PredicateAndObject predAndObj = new PredicateAndObject(predicate, object);
      ArrayList<PredicateAndObject> factsAboutSubject = facts.get(subject);
      if (factsAboutSubject == null) facts.set(subject, factsAboutSubject = new ArrayList<PredicateAndObject>());
      factsAboutSubject.add(predAndObj);
    }
    if (!isInverse(predicate)) add(object, inverse(predicate), subject);
  }

  /** Returns number of entities */
  public int numEntities() {
    return (entities.size());
  }

  /** Returns number of classes */
  public int numClasses() {
    if (cachedNumClasses > 0) return cachedNumClasses;
    int n = 0;
    for (int i = 0; i < numEntities(); i++)
      if (isClass(i)) n++;
    if (finalized) cachedNumClasses = n;
    return n;
  }

  /** Returns number of literals */
  public int numLiterals() {
    if (cachedNumLiterals > 0) return cachedNumLiterals;
    int n = 0;
    for (int i = 0; i < numEntities(); i++)
      if (isLiteral(i)) n++;
    if (finalized) cachedNumLiterals = n;
    return n;
  }

  /** Returns number of relations */
  public int numRelations() {
    return (relations.size());
  }

//  /** returns the number of facts */
//  public int size() {
//    return (facts.size());
//  }

  /** returns the functionality of a relation */
  public double functionality(int relation) {
    return (functionalities[relation]);
  }

  /** returns the inverse functionality of a relation */
  public double inverseFunctionality(int relation) {
    return (functionality(inverse(relation)));
  }

  public double functionality(JoinRelation r) {
    double res = 2;
    for (int i = 0; i < r.length(); i++)
      res = Math.min(res, functionality(r.get(i)));
    return res;
  }

  public double inverseFunctionality(JoinRelation r) {
    return functionality(r.reversed());
  }

  /** Check if entity is a class */
  public boolean isClass(int e) {
    if (this.finalized) {
      // cache is ready
      return isClass[e];
    }
    if (isLiteral(e)) return false;
    ArrayList<PredicateAndObject> myFacts = facts.get(e);
    if (myFacts == null) return (false);
    int type = relation("rdf:type");
    int subClass = relation("rdfs:subclassOf");
    for (int i = 0; i < myFacts.size(); i++) {
      PredicateAndObject po = myFacts.get(i);
      if (po.predicate == inverse(type)) return true;
      if (po.predicate == subClass || po.predicate == inverse(subClass))
      	return true;
    }
    return false;
  }

  /** Check if an entity is a literal */
  public boolean isLiteral(int e) {
    if (this.finalized) {
      // cache is ready
      return isLiteral[e];
    }
    return isLiteral(entities.get(e));
  }

  /** Check if an entity is a literal */
  public boolean isLiteral(String e) {
    return e.startsWith("\"");
  }

  /** Populate caches */
  protected void populateCaches() {
  	isClass = new boolean[numEntities()];
  	isLiteral = new boolean[numEntities()];
    for (int i = 0; i < numEntities(); i++) {
      isClass[i] = isClass(i);
    }
    for (int i = 0; i < numEntities(); i++) {
      isLiteral[i] = isLiteral(i);
    }
    // System.out.println(numEntities());
  }

  /** Computes the functionalities */
  protected void computeFunctionalities() {
    int[] numOccurrences = new int[numRelations()];
    functionalities = new double[numRelations()];
    int[] numSubjectsPerRelation = new int[numRelations()];
    Announce.progressStart("Computing functionalities in " + uri, numEntities());
    int[] lastSubject = new int[numRelations()];
    for (int subject = 0; subject < facts.size(); subject++) {
      Announce.progressStep();
      ArrayList<PredicateAndObject> myFacts = facts.get(subject);
      if (myFacts == null) continue;
      for (int fact = 0; fact < myFacts.size(); fact++) {
        PredicateAndObject po = myFacts.get(fact);
        numOccurrences[po.predicate]++;
        if (lastSubject[po.predicate] != subject) {
        	lastSubject[po.predicate] = subject;
        	numSubjectsPerRelation[po.predicate]++;
        }
      }
    }
    Announce.progressDone();
    Announce.doing("Functionalities");
    for (int relation = 0; relation < numRelations(); relation++) {
      functionalities[relation] = ((double) numSubjectsPerRelation[relation]) / numOccurrences[relation];
      Announce.message("functionality:", relations.get(relation), functionalities[relation]);
    }
    Announce.message("Number of literals:", numLiterals());
    Announce.done();
  }

  /** Returns the instances of a class */
  public IntSet instancesOf(int clss) {
    IntSet result = new IntOpenHashSet();
    ArrayList<PredicateAndObject> myFacts = facts.get(clss);
    if (myFacts == null) return (result);
    int type = inverse(TYPE);
    for (int i = 0; i < myFacts.size(); i++) {
      PredicateAndObject po = myFacts.get(i);
      if (po.predicate == type) result.add(po.object);
    }
    return (result);
  }

  /** Returns the classes of an instance */
  public IntSet classesOf(int instance) {
    IntSet result = new IntOpenHashSet();
    ArrayList<PredicateAndObject> myFacts = facts.get(instance);
    if (myFacts == null) return (result);
    for (int i = 0; i < myFacts.size(); i++) {
      PredicateAndObject po = myFacts.get(i);
      if (po.predicate == TYPE) result.add(po.object);
    }
    return (result);
  }

  /** returns the classes of an entity */
  public IntSet classesOf(String string) {
    return classesOf(entity(string));
  }

  /** returns the classes and superclasses of an entity */
  public IntSet classesAndSuperClassesOf(String string) {
    return classesAndSuperClassesOf(entity(string));
  }

  /** returns the classes and superclasses of an entity */
  public IntSet classesAndSuperClassesOf(int instance) {
    IntSet result = new IntOpenHashSet();
    IntIterator it=classesOf(instance).iterator();
    while(it.hasNext()) {
      addSuperClassesOf(it.next(), result);
    }
    return (result);
  }

  /** Adds the superclasses of a class*/
  protected void addSuperClassesOf(int c, IntSet result) {
    if(result.contains(c)) return;
    result.add(c);
    ArrayList<PredicateAndObject> myFacts = facts.get(c);
    if (myFacts == null) return;
    for (int i = 0; i < myFacts.size(); i++) {
      PredicateAndObject po = myFacts.get(i);
      if (po.predicate == SUBCLASSOF && !result.contains(po.object)) {
        addSuperClassesOf(po.object, result);
      }
    }
  }

  /** returns the instances of an entity */
  public IntSet instancesOf(String string) {
    return instancesOf(entity(string));
  }

  /** Returns the relation id of a relation name */
  public int relation(String relation) {
    return relationNames.get(relation);
  }

  /** Returns the entity id of an entity name */
  public int entity(String entity) {
    return entityNames.get(entity);
  }

  /** Returns the relation of a relation id */
  public String relation(int relation) {
    return relations.get(relation);
  }

  /** Returns the entity an entity id */
  public String entity(int entity) {
    return entities.get(entity);
  }

  /** Trims everything to size, computes functionalities */
  public void prepare() {
    Announce.doing("Preparing", uri);
    TYPE = relation("rdf:type");
    SUBCLASSOF = relation("rdfs:subClassOf");
    Announce.doing("Trimming to size");
    this.entities.trimToSize();
    this.relations.trimToSize();
    this.entityNames.trimToSize();
    this.relationNames.trimToSize();
    assert (numEntities() == facts.size());
    Announce.message("done loading", entities.size(), "entities");
    populateCaches();
    for (int i = 0; i < facts.size(); i++) {
      if (facts.get(i) != null) facts.get(i).trimToSize();
    }
    Announce.done();
    computeFunctionalities();
    Announce.done();
    if (setting.literalDistance == Setting.LiteralDistance.SHINGLING || setting.literalDistance == Setting.LiteralDistance.SHINGLINGLEVENSHTEIN) {
      Announce.doing("indexing literals...");
      this.literalIndex = new ShinglingTable(setting.shinglingSize, setting.shinglingFunctions, setting.shinglingTableSize);
      Set<String> indexed = new HashSet<String>();
      for (int i = 0; i < numEntities(); i++) {
        if (!isLiteral(i)) continue;
        if (indexed.contains(entity(i))) continue;
        indexed.add(entity(i));
        this.literalIndex.index(entity(i));
      }
      Announce.done();
    }
    if (setting.debugEntity != null) {
    	for (int i = 0 ; i < numEntities(); i++) {
    		if (entity(i).contains(setting.debugEntity)) {
    			Announce.message("DEBUGENTITY");
    			Announce.message(factsAbout(i));
    		}
    	}
    }
    this.finalized = true;
  }

  /** returns the inverse of a relation */
  public static String inverse(String relation) {
    if (relation.endsWith("-")) return (relation.substring(0, relation.length() - 1));
    return (relation + "-");
  }

  /** returns the inverse of a relation */
  public static int inverse(int relation) {
    if (isInverse(relation)) return (relation - 1);
    return (relation + 1);
  }

  /** True for inverse relations */
  public static boolean isInverse(int relation) {
    return (relation & 1) == 1;
  }

  /** add a relation */
  public synchronized int addRelation(String relation) {
    int id = relationNames.size();
    relationNames.put(relation, id);
    relations.add(relation);
    relationNames.put(inverse(relation), inverse(id));
    relations.add(inverse(relation));
    return (id);
  }

  /** returns a relation id (or adds it) */
  public synchronized int getOrAddRelation(String relation) {
    // Don't use lget() here, it's not thread-safe
    if (finalized || relationNames.containsKey(relation)) return (relationNames.get(relation));
    // synchronized (relationNames) {
    return addRelation(relation);
    // }
  }

  /** add an entity */
  public synchronized int addEntity(String entity) {
    int id = entityNames.size();
    entityNames.put(entity, id);
    entities.add(entity);
    return (id);
  }

  /** returns an entity id (or adds it) */
  public synchronized int getOrAddEntity(String entity) {
    // Don't use lget() here, it's not thread-safe
    if (entityNames.containsKey(entity))
    	return (entityNames.get(entity));
    // synchronized (entityNames) {
    assert(!finalized);
    return addEntity(entity);
    // }
  }

  /** returns facts about the subject */
  public List<PredicateAndObject> factsAbout(int subject) {
    return (facts.get(subject));
  }

  /** returns facts about the subject */
  public List<PredicateAndObject> factsAbout(String subject) {
    return (facts.get(getOrAddEntity(subject)));
  }

  /** Pattern for prefix*/
  protected static final Pattern prefixPattern = Pattern.compile("[a-z0-9]{1,5}:.*");

  /** Adds the standard prefix if necessary*/
  public String addPrefix(String uri) {
    if (isLiteral(uri)) return (uri);
    if (uri.startsWith(this.uri)) return (prefix + uri.substring(this.uri.length()));
    if (prefixPattern.matcher(uri).matches()) return (uri);
    return (prefix + uri);
  }

  /** Adds a fact. removes data types. Adds quotes for numbers */
  public void add(String subject, String predicate, String object) {
    object = addPrefix(LiteralFormatter.format(object, formatters));
    // For old YAGO files that can have a literal as the subject
    subject = addPrefix(LiteralFormatter.format(subject, formatters));
    predicate = addPrefix(predicate);
    int predicateId = getOrAddRelation(predicate);
    assert (!isInverse(predicateId));
    int objectId = getOrAddEntity(object);
    int subjectId = getOrAddEntity(subject);
    add(subjectId, predicateId, objectId);
    assert (facts.size() >= subjectId);
    assert (facts.size() >= objectId);
    assert (subjectId <= numEntities());
    assert (objectId <= numEntities());
  }

  /**
   * Loads a file
   * 
   * @throws IOException
   */
  public void load(File f) throws IOException {
    if (f.isDirectory()) {
      load(f.listFiles());
    } else {
      if (functionalities != null) {
        Announce.warning("First load files, then call prepare()!");
        System.exit(2);
      }
      for (String[] fact : Parser.forFile(f)) {
      	try {
//      		Announce.message(fact);
      		add(fact[0], fact[1], fact[2]);
      	} catch (ArrayIndexOutOfBoundsException e) {
      		Announce.message("bad fact:");
      		for (int i = 0; i < fact.length; i++)
      			Announce.message(fact[i]);
      		System.exit(2);
      	}
      }
    }
  }

  /** Loads a files in the folder that match the regex pattern */
  public void load(File folder, Pattern namePattern) throws IOException {
    List<File> files = new ArrayList<File>();
    for (File file : folder.listFiles())
      if (namePattern.matcher(file.getName()).matches()) files.add(file);
    load(files);
  }

  /** Loads files in parallel */
  public void load(File... files) throws IOException {
    load(Arrays.asList(files));
  }

  /** Loads the files */
  public void load(List<File> files) throws IOException {
    int size = numEntities();
    long time = System.currentTimeMillis();
    long memory = Runtime.getRuntime().freeMemory();
    Announce.doing("Loading files");
    final int[] running = new int[1];
    if (setting.parallelFileLoad) {
	    for (final File file : files) {
	      running[0]++;
	      new Thread() {
	
	        public void run() {
	          try {
	            synchronized (Announce.blanks) {
	              Announce.message("Starting " + file.getName());
	            }
	            load(file);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          synchronized (Announce.blanks) {
	            Announce.message("Finished " + file.getName() + ", still running: " + (running[0] - 1));
	            synchronized (running) {
	              if (--running[0] == 0) running.notify();
	            }
	          }
	        }
	      }.start();
	    }
	    try {
	      synchronized (running) {
	        running.wait();
	      }
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
    } else {
    	for (final File file : files) {
    		load(file);
    	}
    }
    Announce.done("Loaded facts about " + (numEntities() - size) + " entities in "
    		+ NumberFormatter.formatMS(System.currentTimeMillis() - time) + " using "
        + ((Runtime.getRuntime().freeMemory() - memory) / 1000000) + " MB");
  }

  /** Return a number larger than the largest join relation code allocated */
  public int maxJoinRelationCode() {
    return (int) Math.pow(maxRelationId() * 2, joinLengthLimit);
  }

  public int maxRelationId() {
    return relations.size();
  }

  public int getJoinLengthLimit() {
    return joinLengthLimit;
  }

  public void reduceJoinLengthLimit(int newLimit) {
    joinLengthLimit = Math.min(joinLengthLimit, newLimit);
  }

  /** Return a join relation from a join relation code */
  public JoinRelation joinRelationByCode(int code) {
    JoinRelation r = new JoinRelation(this);
    int max = maxRelationId();
    while (code != 0) {
      r.push(code % max);
      code /= max;
    }
    r.reverseDirection();
    return r;
  }

  public Collection<QueryResult> similarLiterals(String query, double threshold) {
    return this.literalIndex.query(Config.stripQuotes(query), threshold);
  }

  public List<Integer> properEntities() {
    List<Integer> result = new ArrayList<Integer>();
    for (int i = 1; i < numEntities(); i++)
      if (!isLiteral(i) && !isClass(i)) result.add(i);
    return result;
  }

  public static void main(String[] args) throws Exception {
    Setting setting = new Setting("", "", "", "", "", "", null);
    //new File("/home/a3nm/documents/stage/paris/dummy_conf"));
    FactStore f = new FactStore(setting, "imdb:", "http://imdb/", 1, LiteralFormatter.CUT_DATATYPE, LiteralFormatter.NORMALIZE);
    f.load(new File("/home/a3nm/documents/stage/paris/ontologies/imdb_small"), Pattern.compile(".*"));
    // Pattern.compile("happenedIn.tsv"));
    // f.load(new
    // File("/home/a3nm/DOCUMENTS/stage/paris/ontologies/dbpedia_small_uniq.nt"));
    //f.load(new File("/home/a3nm/documents/stage/paris/ontologies/yagodebug"));
    f.prepare();
    D.p(f.isClass(1));
    D.p(f.entity(1));
    D.p(f.factsAbout(1));
    D.p(f.addPrefix("rdfs:label"));
    String[] examples = new String[] {
    		"p1550813", "p826266", "p868780", "p2210300", "p2407572", "p207566", "p2967074", "p37159", "p2524953",
    		"tt1350852", "p1779811", "p1340570", "tt0659432", "p2608974", "p1601915", "l11224", "p1725119", "tt0796418", "p2174336", "tt1181151", "p2449951", "p465644",
    		"p1357789"};
    for (String example : examples)
    	D.p(f.factsAbout("imdb:" + example));
//    D.p(f.classesOf("y2:Ulm"));
//    IntIterator it=f.classesAndSuperClassesOf("y2:Ulm").iterator();
//    while(it.hasNext()) {
//      D.p(f.entity(it.next()));
//    }
  }
}
