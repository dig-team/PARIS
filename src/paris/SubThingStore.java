package paris;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javatools.datatypes.Pair;
import javatools.datatypes.PeekIterator;
import javatools.datatypes.Triple;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an alignment between relations or instances.
 * */

public abstract class SubThingStore {


  /** Represents a pair of a sub-entity and a super-entity with a degree*/
  public static class SubPair {

    long id;

    /** Holds the first entity*/
    public int sub;

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
    
    public String toTsv(FactStore subStore, FactStore superStore) {
    	return subStore.toString(sub)+"\t"+superStore.toString(supr)+"\t"+val+"\n";
    }
  }

	/** Shut down*/
	public abstract void close();

	/** Sets the writer
	 * @throws IOException */
	public abstract void setTSVfile(File file) throws IOException;

	/** Closes the TSV writer, dumps the whole set to file */
	public abstract void dump(File file, FactStore subStore, FactStore superStore)
			throws IOException;

	/** Returns the probability that s1 is a sub-entity of s2*/
	public abstract double getValue(Integer sub, Integer supr);

	/** Sets the probability that s1 is a subclass of s2*/
	public abstract void setValue(FactStore substore, Integer sub,
			FactStore superstore, Integer supr, double val);

	/** Returns superclasses*/
	public abstract Set<Integer> superOf(Integer sub);

	/** Returns superclasses, scored*/
	public abstract Collection<Pair<Object, Double>> superOfScored(Integer sub);

	/** Returns subclasses*/
	public abstract Set<Integer> subOf(Integer supr);

	/** Returns subclasses with score*/
	public abstract Collection<Pair<Object, Double>> subOfScored(Integer supr);

	/** Prints information*/
	public abstract String toString();

	/** Returns number of entries*/
	public abstract long size();

	/** Returns all sub's */
	public abstract Iterable<Integer> subs();

	/** Returns all sub-super-pairs */
	public abstract Iterable<SubPair> all();

	/** Save the contents to a file*/
	public abstract void saveTo(File file, FactStore subStore,
			FactStore superStore) throws IOException;

	/** Returns 10 sample pairs*/
	public abstract List<Triple<Integer, Integer, Double>> sample();
		
	public abstract PeekIterator<SubPair> getSuperEntities(int supr);

}