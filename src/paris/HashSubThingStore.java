package paris;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import paris.storage.FactStore;

import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an alignment between things, using hashmaps. */

// TODO code duplication with HashSubRelationStore

public abstract class HashSubThingStore<T> extends SubThingStore<T> implements Closeable {

  /** Maps ids to pairs */
  protected Map<Long, SubPair<T>> primaryIndex;
  private long freshPairId;
  
  /** Maps subrel to pairs*/
  protected MultiMap<T, SubPair<T>> subIndex;

  /** Maps second arg to pairs*/
  protected MultiMap<T, SubPair<T>> superIndex;
  
  /** Maps first and second arg to pairs */
  protected Map<T, Map<T, SubPair<T>>> cross;
  

  public HashSubThingStore(FactStore fs1, FactStore fs2) {
  	super(fs1, fs2);
  	primaryIndex = new HashMap<Long, SubPair<T>>();
  	subIndex = new MultiMap<T, SubPair<T>>(new MultiMap.HashSetFactory<SubPair<T>>());
  	superIndex = new MultiMap<T, SubPair<T>>(new MultiMap.HashSetFactory<SubPair<T>>());
  	cross = new HashMap<T, Map<T, SubPair<T>>>();
  	freshPairId = 0;
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#getValue(java.lang.Integer, java.lang.Integer)
	 */
  @Override
	public double getValue(T sub, T supr) {
    SubPair<T> e = get(sub, supr);
    return (e != null ? e.val : 0);
  }

  /** Returns a pair*/
  protected SubPair<T> get(T sub, T supr) {
  	if (!cross.keySet().contains(sub))
  		return null;
  	return cross.get(sub).get(supr);
  }

  public void set(SubPair<T> e) {
  	long myId = freshPairId++;
  	e.id = myId;
  	primaryIndex.put(myId, e);
  	subIndex.put(e.sub, e);
  	superIndex.put(e.supr, e);
  	Map <T, SubPair<T>> m;
  	if (!cross.keySet().contains(e.sub)) {
  		m = new HashMap<T, SubPair<T>>();
  		cross.put(e.sub, m);
  	} else {
  		m = cross.get(e.sub);
  	}
  	m.put(e.supr, e);
  }
  
  public void set(T sub, T supr, double val) {
  	set(new SubPair<T>(sub, supr, val));
  }

  /* (non-Javadoc)
	 * @see paris.SubThingStore#all()
	 */
	public Iterable<SubPair<T>> all() {
    return (primaryIndex.values());
  }

	public PeekIterator<SubPair<T>> getSuperEntities(T supr) {
		return new PeekIterator.SimplePeekIterator<SubPair<T>>(superIndex.get(supr).iterator());
	}

	public Set<T> superOf(T sub) {
    Set<T> result = new TreeSet<T>();
    for (SubPair<T> a : subIndex.getOrEmpty(sub)) {
      result.add(a.supr);
    }
    return (result);
  }

	public Collection<Pair<Object, Double>> superOfScored(T sub) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    for (SubPair<T> a : subIndex.getOrEmpty(sub)) {
      result.add(new Pair<Object, Double>(a.supr, a.val));
    }
    return (result);
  }

	public Set<T> subOf(T supr) {
    Set<T> result = new TreeSet<T>();
    for (SubPair<T> a : superIndex.getOrEmpty(supr)) {
      result.add(a.sub);
    }
    return (result);
  }

	public Collection<Pair<Object, Double>> subOfScored(T supr) {
    List<Pair<Object, Double>> result = new ArrayList<Pair<Object, Double>>();
    for (SubPair<T> a : superIndex.getOrEmpty(supr)) {
      result.add(new Pair<Object, Double>(a.sub, a.val));
    }
    return (result);
  }
	
	
}
