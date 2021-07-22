package paris;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/** 
This class is inspired by class MultiMap of the Java Tools (see http://mpii.de/yago-naga/javatools).
It is licensed under the Creative Commons Attribution License 
(see http://creativecommons.org/licenses/by/3.0) by 
the YAGO-NAGA team (see http://mpii.de/yago-naga).

This class uses a HashMap to map A's to sets of B's: the implementation of sets of B's is defined when calling the constructor.
*/


@SuppressWarnings("serial")
public class MultiMap<A, B> implements Iterable<Entry<A,B>>, Serializable {

	public interface Factory<T> extends Serializable {
	  T newInstance();
	  T newInstance(Integer num);
	}
	
	public static class HashSetFactory<T> implements Factory<HashSet<T>>{
		public HashSet<T> newInstance() {
			return new HashSet<T>();
		}
		public HashSet<T> newInstance(Integer num) {
			return new HashSet<T>(num);
		}
	}
	
	public static class VectorFactory<T> implements Factory<Vector<T>>{
		public Vector<T> newInstance() {
			return new Vector<T>();
		}
		public Vector<T> newInstance(Integer num) {
			return new Vector<T>(num);
		}
	}
	
	private Factory<? extends Collection<B>> factory;
  private Map<A, Collection<B>> relation;
  int initialHashSetSize = 0;
  
  public MultiMap(Factory<? extends Collection<B>> factory) {
  	this.factory = factory;
    relation = new HashMap<A, Collection<B>>();
  }
  
  public MultiMap(Factory<? extends Collection<B>> factory, int initialSize) {
  	this.factory = factory;
    relation = new HashMap<A, Collection<B>>(initialSize);
  }
  
  public MultiMap(Factory<? extends Collection<B>> clazz, int initialSize, int initialHashSetSize) {
    this(clazz, initialSize);
    this.initialHashSetSize = initialHashSetSize;
  }
  
  public void put(A a, B b) {
    Collection<B> bs = relation.get(a);
    
    if (bs == null) {
    	if (initialHashSetSize == 0)
        bs = factory.newInstance();
    	else
    		bs = factory.newInstance(initialHashSetSize);
      relation.put(a, bs);
    }
    
    bs.add(b);
  }
  
  public void putAll(MultiMap<A, B> otherMap) {
    for (Entry<A, B> e : otherMap) {
      put(e.getKey(), e.getValue());
    }
  }
  
  public Collection<B> get(A a) {
    return relation.get(a);
  }
  
  public boolean contains(Entry<A, B> e) {
    Collection<B> test = relation.get(e.getKey());
    
    if (test == null) {
      return false;
    } else {
      return test.contains(e.getValue());
    }
  }
  
  public boolean isEmpty() {
    return relation.isEmpty();
  }

  @Override
  public Iterator<Entry<A,B>> iterator() {
    return new MultiMapIterator();
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    for (Entry<A, B> e: this) {
      sb.append(e.getKey() + " - " + e.getValue() + ", ");
    }
    
    return sb.toString();
  }
  
  private class MultiMapIterator implements Iterator<Entry<A,B>> {

    private Iterator<A> aIterator;
    private A currentA;
    
    private Iterator<B> bIterator;
        
    public MultiMapIterator() {
      aIterator = relation.keySet().iterator();
    }
    
    @Override
    public boolean hasNext() {
      if (aIterator.hasNext()) {
        if (bIterator == null) {
          currentA = aIterator.next();
          bIterator = relation.get(currentA).iterator();
        }

        return true;
      } else if (bIterator != null) {
        return bIterator.hasNext();
      } else {
        return false;
      }
    }

    @Override
    public Entry<A,B> next() {      
      // there is at least 1 element in bIterator
      B currentB = bIterator.next();
      
      if (!bIterator.hasNext()) {
        bIterator = null;
      }
      
      return new AbstractMap.SimpleEntry<A,B>(currentA, currentB);
    }

    @Override
    public void remove() {
      // not supported
    	assert(false);
    }
  }
  
  public boolean containsKey(A key) {
 		Collection<B> values = get(key);
 		if (values == null) return false;
 		return !values.isEmpty();
	}
  
  public Collection<B> getOrEmpty(A key) {
  	Collection<B> result = get(key);
  	if (result == null) {
  		return factory.newInstance();
  	}
  	return result;
  }
  
  public Set<A> keySet() {
  	return relation.keySet();
  }

	public void clear() {
		relation.clear();
	}
}
