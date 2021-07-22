package paris;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javatools.administrative.Announce;
import javatools.datatypes.MappedIterator;
import javatools.datatypes.Pair;
import javatools.datatypes.Triple;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores pairs of entities with a score. It is used for equalities, subrelations and superrelations.
 * The first entity will live in one fact store, and the second entity will live in another fact store.
 * If a TSV file is set, every assignment will be printed into that file (in addition to storing it in the data base).*/
public class SubThingStore implements Closeable {

  @Entity
  /** Represents a pair of a sub-entity and a super-entity with a degree*/
  public static class SubPair {

    @PrimaryKey(sequence = "ID")
    long id;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** Holds the first entity*/
    public int sub;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    /** Holds the second entity*/
    public int supr;

    /** Holds the score*/
    public double val;

    /** Constructs a pair*/
    public SubPair() {

    }

    /** Constructs a pair*/
    public SubPair(int s1, int s2, double val2) {
      sub = s1;
      supr = s2;
      val = val2;
    }

    /** Returns this as a triple*/
    public Triple<Integer, Integer, Double> toTriple() {
      return (new Triple<Integer, Integer, Double>(sub, supr, val));
    }

    @Override
    public String toString() {
      return sub + "/" + supr + "/" + val + "/" + id;
    }
  }

  /** Maps subrelations to superrelations*/
  protected EntityStore store;

  /** Maps ids to RDFRelations */
  protected PrimaryIndex<Long, SubPair> primaryIndex;

  /** Maps subrel to equality pairs*/
  protected SecondaryIndex<Integer, Long, SubPair> subIndex;

  /** Maps second arg to equality pairs*/
  protected SecondaryIndex<Integer, Long, SubPair> superIndex;

  /** The log writer */
  protected Writer tsvWriter;

  /** Constructor*/
  public SubThingStore(Environment environment, String tablename, boolean allowWrite) {
    StoreConfig storeconf = new StoreConfig();
    storeconf.setAllowCreate(allowWrite);
    storeconf.setDeferredWrite(true);
    storeconf.setTransactional(false);
    storeconf.setReadOnly(!allowWrite);
    store = new EntityStore(environment, tablename, storeconf);
    primaryIndex = store.getPrimaryIndex(Long.class, SubPair.class);
    subIndex = store.getSecondaryIndex(primaryIndex, Integer.class, "sub");
    superIndex = store.getSecondaryIndex(primaryIndex, Integer.class, "supr");
  }
  
  /** Shut down*/
  @Override
  public void close() {
    store.close();
    if (tsvWriter != null) try {
      tsvWriter.close();
    } catch (IOException e) {
    }
  }

  /** Sets the writer
   * @throws IOException */
  public void setTSVfile(File file) throws IOException {
    if (tsvWriter != null) try {
      tsvWriter.close();
    } catch (IOException e) {
    }
    tsvWriter = new FileWriter(file);
  }

  /** Closes the TSV writer, dumps the whole set to file */
  public void dump(File file, FactStore subStore, FactStore superStore) throws IOException {
    if (tsvWriter != null) try {
      tsvWriter.close();
    } catch (IOException e) {
    }
    Announce.doing("Saving TSV");
    BufferedWriter w = new BufferedWriter(new FileWriter(file));

    EntityCursor<SubPair> cursor = primaryIndex.entities();
    while(cursor.next() != null) {
      w.write(subStore.toString(cursor.current().sub)+"\t"+superStore.toString(cursor.current().supr)+"\t"+cursor.current().val+"\n");
    }
    cursor.close();
    w.close();
    Announce.done();
  }
  
  /** Returns the probability that s1 is a sub-entity of s2*/
  public double getValue(Integer sub, Integer supr) {
    SubPair e = get(sub, supr);
    return (e == null ? -1 : e.val);
  }

  /** Clears all super classes*/
  public void clearSuperOf(Integer subclass) {
    subIndex.delete(subclass);
  }

  /** Clears all sub classes*/
  public void clearSubOf(Integer superclass) {
    superIndex.delete(superclass);
  }

  /** Returns a pair*/
  protected SubPair get(Integer sub, Integer supr) {
    EntityJoin<Long, SubPair> join = new EntityJoin<Long, SubPair>(primaryIndex);
    join.addCondition(subIndex, sub);
    join.addCondition(superIndex, supr);
    ForwardCursor<SubPair> c = join.entities();
    SubPair e = c.next();
    c.close();
    return (e);
  }

  /** Sets the probability that s1 is a subclass of s2*/
  public void setValue(FactStore substore, Integer sub, FactStore superstore, Integer supr, double val) {
    if (val == -1 || Double.isNaN(val)) return;
    if (val > 1) val = 1;
    SubPair e = get(sub, supr);
    if (e == null) e = new SubPair(sub, supr, val);
    else e.val = val;
    if(val<Config.THETA) primaryIndex.delete(e.id);
    else primaryIndex.putNoReturn(e);
    if (tsvWriter != null) try {
      tsvWriter.write(substore.toString(sub) + "\t" + superstore.toString(supr) + "\t" + val + "\n");
    } catch (IOException e1) {
      Announce.warning("TSV Writer failed", e1.getMessage());
      tsvWriter = null;
    }
  }

  /** Returns superclasses*/
  public Set<Integer> superOf(Integer sub) {
    Set<Integer> result = new TreeSet<Integer>();
    for (SubPair a : FactStore.iterableForIndex(subIndex.subIndex(sub))) {
      result.add(a.supr);
    }
    return (result);
  }

  /** Returns superclasses, scored*/
  public Collection<Pair<Object, Double>> superOfScored(Integer sub) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    for (SubPair a : FactStore.iterableForIndex(subIndex.subIndex(sub))) {
      result.add(new Pair<Object, Double>(a.supr, a.val));
    }
    return (result);
  }

  /** Returns subclasses*/
  public Set<Integer> subOf(Integer supr) {
    Set<Integer> result = new TreeSet<Integer>();
    for (SubPair a : FactStore.iterableForIndex(superIndex.subIndex(supr))) {
      result.add(a.sub);
    }
    return (result);
  }

  /** Returns subclasses with score*/
  public Collection<Pair<Object, Double>> subOfScored(Integer supr) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    for (SubPair a : FactStore.iterableForIndex(superIndex.subIndex(supr))) {
      result.add(new Pair<Object, Double>(a.sub, a.val));
    }
    return (result);
  }

  /** Prints information*/
  public String toString() {
    return ("Sub/super: stored in Berkeley store " + store + ": " + primaryIndex.count());
  }

  /** Returns number of entries*/
  public long size() {
    return (primaryIndex.count());
  }

  /** Returns all sub's */
  public Iterable<Integer> subs() {
    return (new MappedIterator<SubPair, Integer>(FactStore.iterableForIndexUnique(subIndex), new MappedIterator.Map<SubPair, Integer>() {

      @Override
      public Integer map(SubPair a) {
        return a.sub;
      }
    }));
  }

  /** Returns all sub-super-pairs */
  public Iterable<SubPair> all() {
    return (FactStore.iterableForIndexUnique(subIndex));
  }

  /** Save the contents to a file*/
  public void saveTo(File file, FactStore subStore, FactStore superStore) throws IOException {
    BufferedWriter w = new BufferedWriter(new FileWriter(file));

    /* 
     // The following is the solution is if the index is corrupted
     
    EntityCursor<SubPair> cursor = primaryIndex.entities();
    while(cursor.next() != null) {
      w.write(subStore.toString(cursor.current().sub)+"\t"+superStore.toString(cursor.current().supr)+"\t"+cursor.current().val+"\n");
    }
    cursor.close();
    */
    for (Integer sub : subs()) {
      String subName = subStore.toString(sub);
      final Map<Integer, Double> targets = new TreeMap<Integer, Double>();
      for (SubPair pair : FactStore.iterableForIndex(subIndex.subIndex(sub))) {
        targets.put(pair.supr, pair.val);
      }
      List<Integer> targetsSorted = new ArrayList<Integer>(targets.keySet());
      Collections.sort(targetsSorted, new Comparator<Integer>() {

        @Override
        public int compare(Integer o1, Integer o2) {
          return (targets.get(o2).compareTo(targets.get(o1)));
        }
      });
      for (Integer target : targetsSorted) {
        w.write(subName + "\t" + superStore.toString(target) + "\t" + targets.get(target) + "\n");
      }
    }

    w.close();
  }

  /** Returns 10 sample pairs*/
  public List<Triple<Integer, Integer, Double>> sample() {
    List<Triple<Integer, Integer, Double>> result = new ArrayList<Triple<Integer, Integer, Double>>(10);
    EntityCursor<SubPair> cursor = primaryIndex.entities();
    for (int i = 0; i < 10 && cursor.next() != null; i++) {
      result.add(cursor.current().toTriple());
    }
    cursor.close();
    return (result);
  }
}
