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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


import javatools.administrative.Announce;
import javatools.datatypes.MappedIterator;
import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;
import javatools.datatypes.Triple;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores pairs of entities with a score. It is used for subrelations and superrelations.
 * The first entity will live in one fact store, and the second entity will live in another fact store.
 * If a TSV file is set, every assignment will be printed into that file (in addition to storing it in the data base).
 * beware, this class is NOT thread-safe for writes */


public class MemorySubThingStore extends SubThingStore implements Closeable {

  /** Maps ids to RDFRelations */
  protected Map<Long, SubPair> primaryIndex;
  private long freshPairId;
  
  /** Maps subrel to equality pairs*/
  protected MultiMap<Integer, SubPair> subIndex;

  /** Maps second arg to equality pairs*/
  protected MultiMap<Integer, SubPair> superIndex;
  
  protected Map<Integer, Map<Integer, SubPair>> cross;

  /** The log writer */
  protected Writer tsvWriter;

  
  public MemorySubThingStore(File tsvFolder, String name, int iteration) throws IOException {
  	this();
    this.setTSVfile(new File(tsvFolder,iteration+"_" + name + ".tsv"));
  }
  
  public MemorySubThingStore(File tsvFolder, String name, int iteration, FactStore fs1, FactStore fs2,
  		MemoryEqualityStore equalities) throws IOException {
  	this(tsvFolder, name, iteration);

  	// for each y in fs2, we must keep the best matching x in fs1
  	// fill a reverse mapping
  	Map<Integer, Map<Integer, Double>> reverseProduct = new TreeMap<Integer, Map<Integer, Double>>();
  	for (int key : equalities.index.keySet())
  		for (SubPair p : equalities.index.get(key)) {
  			Map<Integer, Double> t = reverseProduct.get(p.supr);
  			if (t == null) {
  				t = new TreeMap<Integer, Double>();
  				reverseProduct.put(p.supr, t);
  			}
  			t.put(p.sub, p.val);
  		}
  	
  	for (int supr : reverseProduct.keySet()) {
  		// now, reduce it
  		Map<Integer, Double> t = reverseProduct.get(supr);
      if (Config.takeMaxMax) Paris.reduceToMaxMax(t);
      else if (Config.takeMax) Paris.reduceToMax(t);
      // and insert what remains
      for (int sub : t.keySet()) {
      	double val = t.get(sub);
	      if (Config.takeMaxMax)
	      	assert(superIndex.get(supr) == null || superIndex.get(supr).size() == 0);
	      SubPair p = new SubPair(sub, supr, val);
//				if (fs1.toString(p.sub).equals("Zambia") || fs1.toString(p.sub).equals("Lusaka")
//							|| fs2.toString(p.supr).equals("dbp:resource/Zambia") || fs2.toString(p.supr).equals("dbp:resource/Lusaka"))
//					Announce.message(p);
	      setValueMax(fs1, fs2, p);
      }
  	}
  	
//  	// insert, without allowing two y1's to map to the same y2
//  	for (int supr : reverseProduct.keyS)
//  		for (SubPair p : equalities.index.get(key)) {
//  			if (fs1.toString(p.sub).equals("Zambia") || fs1.toString(p.sub).equals("Lusaka")
//  					|| fs2.toString(p.supr).equals("dbp:resource/Zambia") || fs2.toString(p.supr).equals("dbp:resource/Lusaka"))
//  				Announce.message(p);
//  			if (acceptDupes || )
//  			  setValueMax(fs1, fs2, p);
//  		}
  }
  public MemorySubThingStore() {
  	primaryIndex = new HashMap<Long, SubPair>();
  	subIndex = new MultiMap<Integer, SubPair>();
  	superIndex = new MultiMap<Integer, SubPair>();
  	cross = new HashMap<Integer, Map<Integer, SubPair>>();
  	freshPairId = 0;
  }
  
  /* (non-Javadoc)
	 * @see paris.SubThingStore#close()
	 */
	@Override
  public void close() {
    // store.close();
    if (tsvWriter != null) try {
      tsvWriter.close();
    } catch (IOException e) {
    }
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#setTSVfile(java.io.File)
	 */
  @Override
	public void setTSVfile(File file) throws IOException {
    if (tsvWriter != null) try {
      tsvWriter.close();
    } catch (IOException e) {
    }
    tsvWriter = new FileWriter(file);
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#dump(java.io.File, paris.FactStore, paris.FactStore)
	 */
  @Override
	public void dump(File file, FactStore subStore, FactStore superStore) throws IOException {
    if (tsvWriter != null) try {
      tsvWriter.close();
    } catch (IOException e) {
    }
    Announce.doing("Saving TSV");
    BufferedWriter w = new BufferedWriter(new FileWriter(file));

    Iterator<SubPair> cursor = primaryIndex.values().iterator();
    while(cursor.hasNext()) {
    	SubPair item = cursor.next();
      w.write(item.toTsv(subStore, superStore));
    }
    // cursor.close();
    w.close();
    Announce.done();
  }
  
  /* (non-Javadoc)
	 * @see paris.SubThingStore#getValue(java.lang.Integer, java.lang.Integer)
	 */
  @Override
	public double getValue(Integer sub, Integer supr) {
    SubPair e = get(sub, supr);
    return (e == null ? -1 : e.val);
  }

  /** Returns a pair*/
  protected SubPair get(Integer sub, Integer supr) {
  	if (!cross.keySet().contains(sub))
  		return null;
  	return cross.get(sub).get(supr);
//  	for (SubPair p : subIndex.getOrEmpty(sub)) {
//  		if (p.supr == supr)
//  			return p;
//  	}
//  	return null;
  }

  public void setValue(FactStore substore, FactStore superstore, SubPair e) {
  	long myId = freshPairId++;
  	e.id = myId;
  	primaryIndex.put(myId, e);
  	subIndex.put(e.sub, e);
  	superIndex.put(e.supr, e);
  	Map <Integer, SubPair> m;
  	if (!cross.keySet().contains(e.sub)) {
  		m = new HashMap<Integer, SubPair>();
  		cross.put(e.sub, m);
  	} else {
  		m = cross.get(e.sub);
  	}
  	m.put(e.supr, e);
  }
  
  public void setValueMax(FactStore substore, FactStore superstore, SubPair e) {
  	SubPair old = get(e.sub, e.supr);
  	if (old == null)
  		setValue(substore, superstore, e);
  	else
  		e.val = Math.max(e.val, old.val);
  }
  
  /* (non-Javadoc)
	 * @see paris.SubThingStore#setValue(paris.FactStore, java.lang.Integer, paris.FactStore, java.lang.Integer, double)
	 */
  @Override
	public void setValue(FactStore substore, Integer sub, FactStore superstore, Integer supr, double val) {
  	if (val < Config.THETA) return;
    if (val == -1 || Double.isNaN(val)) return;
    if (val > 1) val = 1;
    SubPair e = get(sub, supr);
    if (e == null) {
    	e = new SubPair(sub, supr, val);
    	setValue(substore, superstore, e);
    }
    else e.val = val;
    //if(val<Config.THETA) primaryIndex.delete(e.id);
    //assert (tsvWriter != null);
    if (tsvWriter != null) try {
      tsvWriter.write(substore.toString(sub) + "\t" + superstore.toString(supr) + "\t" + val + "\n");
    } catch (IOException e1) {
      Announce.warning("TSV Writer failed", e1.getMessage());
      tsvWriter = null;
    }
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#superOf(java.lang.Integer)
	 */
  @Override
	public Set<Integer> superOf(Integer sub) {
    Set<Integer> result = new TreeSet<Integer>();
    for (SubPair a : subIndex.getOrEmpty(sub)) {
      result.add(a.supr);
    }
    return (result);
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#superOfScored(java.lang.Integer)
	 */
  @Override
	public Collection<Pair<Object, Double>> superOfScored(Integer sub) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    for (SubPair a : subIndex.getOrEmpty(sub)) {
      result.add(new Pair<Object, Double>(a.supr, a.val));
    }
    return (result);
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#subOf(java.lang.Integer)
	 */
  @Override
	public Set<Integer> subOf(Integer supr) {
    Set<Integer> result = new TreeSet<Integer>();
    for (SubPair a : superIndex.getOrEmpty(supr)) {
      result.add(a.sub);
    }
    return (result);
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#subOfScored(java.lang.Integer)
	 */
  @Override
	public Collection<Pair<Object, Double>> subOfScored(Integer supr) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    for (SubPair a : superIndex.getOrEmpty(supr)) {
      result.add(new Pair<Object, Double>(a.sub, a.val));
    }
    return (result);
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#toString()
	 */
  @Override
	public String toString() {
    return ("Sub/super: stored in memory store: " + primaryIndex.size());
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#size()
	 */
  @Override
	public long size() {
    return (primaryIndex.size());
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#subs()
	 */
  @Override
	public Iterable<Integer> subs() {
    return (new MappedIterator<SubPair, Integer>(primaryIndex.values().iterator(), new MappedIterator.Map<SubPair, Integer>() {

      @Override
      public Integer map(SubPair a) {
        return a.sub;
      }
    }));
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#all()
	 */
  @Override
	public Iterable<SubPair> all() {
    return (primaryIndex.values());
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#saveTo(java.io.File, paris.FactStore, paris.FactStore)
	 */
  @Override
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
      for (SubPair pair : subIndex.get(sub)) {
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

  /* (non-Javadoc)
	 * @see paris.SubThingStore#sample()
	 */
  @Override
	public List<Triple<Integer, Integer, Double>> sample() {
    List<Triple<Integer, Integer, Double>> result = new ArrayList<Triple<Integer, Integer, Double>>(10);
    Iterator<SubPair> cursor = primaryIndex.values().iterator();
    for (int i = 0; i < 10 && cursor.hasNext(); i++) {
      result.add(cursor.next().toTriple());
    }
    //cursor.close();
    return (result);
  }

	@Override
	public PeekIterator<SubPair> getSuperEntities(int supr) {
		return new PeekIterator.SimplePeekIterator<SubPair>(superIndex.get(supr).iterator());
	}
}
