package paris;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import bak.pcj.IntIterator;
import bak.pcj.set.IntOpenHashSet;
import bak.pcj.set.IntSet;

import paris.storage.FactStore;
import paris.storage.FactStore.PredicateAndObject;

import javatools.administrative.Announce;
import javatools.administrative.Announce.Level;
import javatools.administrative.D;
import javatools.datatypes.Pair;
import javatools.parsers.NumberFormatter;

/**
 * This class is part of the PARIS ontology matching project at INRIA
 * Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License by
 * the author Fabian M. Suchanek (http://suchanek.name). For all further
 * information, see http://webdam.inria.fr/paris
 * 
 * This class implements a very cool probabilistic framework for ontology
 * matching
 */
public class Paris {

	/** TRUE for test runs */
	public static boolean test = false;

	/** TRUE for debug on. Make it final to allow conditional compiling. */
	public static boolean debug = false;

	/** First ontology */
	public static FactStore factStore1;

	/** Second ontology */
	public static FactStore factStore2;

	/** Stores the equality */
	public static Result computed;

	/** The setting in which we work */
	public static Setting setting;

	/** Finds the super classes of a class */
	public static void findSuperClassesOf(Integer subclass, FactStore subStore,
			FactStore superStore) {
		// We ignore classes that contain practically all entities
		if (subStore.entity(subclass).startsWith("owl:")
				|| subStore.entity(subclass)
						.equals("http://www.opengis.net/gml/_Feature"))
			return;
		if (debug)
			Announce.debug("Computing superclasses of",
					subclass);

		// maps each superclass d to
		// SUM x such that type(x,c): 1 - PROD y such that type(y,d): 1-P(x=y)
		Map<Integer, Double> superclassDegree = new TreeMap<Integer, Double>();
		// contains the value
		// # x such that type(x,c) // no longer: and exists y: y=x and type(y,some
		// class)
		double normalizer = 0;
		int counter = 0;
		// Don't compute that for classes that are too far up in the hierarchy
		IntSet subInstances = subStore.instancesOf(subclass);
		if (subInstances == null)
			return;
		IntIterator it = subInstances.iterator();
		while (it.hasNext()) {
			int subclassInstance = it.next();
			if (debug && counter++ > 100)
				break;
			if (debug)
				Announce.debug("   Looking at instance",
						subclassInstance);
			// For each instance x of c...
			boolean foundeqv = false;
			Map<Integer, Double> membershipProduct = new TreeMap<Integer, Double>();
			// maps each superclass d to
			// PROD y such that type(y,d): 1-P(x=y)
			for (Pair<Object, Double> superclassInstancePair : computed
					.equalToScored(subStore, subclassInstance)) {
				if (!(superclassInstancePair.first() instanceof Integer))
					continue;
				Integer superclassInstance = (Integer) superclassInstancePair.first();
				double equality = superclassInstancePair.second();
				if (debug)
					Announce.debug("     Is equal to",
							superStore.entity(superclassInstance), equality);

				if (equality < Config.THETA)
					continue;
				IntSet classes = superStore.classesOf(superclassInstance);
				IntIterator it2 = classes.iterator();
				while (it2.hasNext()) {
					int superClass = it2.next();
					assert(superClass > 0);
					double prod = membershipProduct.containsKey(superClass) ? membershipProduct
							.get(superClass) : 1;
					if (debug)
						Announce.debug("        Scoring for",
								superStore.entity(superClass), prod, 1 - equality
										* equality, prod * (1 - equality));
					prod *= 1 - equality;
					membershipProduct.put(superClass, prod);
					foundeqv = true;
				}
			}
			if (foundeqv) {
				for (Integer superclass : membershipProduct.keySet()) {
					D.addKeyValueDbl(superclassDegree, superclass,
							1 - membershipProduct.get(superclass));
				}
			}
			normalizer++;
		}
		// We do not do the domain/range deduction
		// Collect all classes about which we know something in superclassDegree
		// Say that if we have no instances, the superclassDegree is 0
		// for (Integer superclass : domainSuperclassDegree.keySet()) {
		// if (!superclassDegree.containsKey(superclass))
		// superclassDegree.put(superclass, 0.0);
		// }

		// If the normalizer is 0, superclassDegree(x)=0 for all x.
		// So instanceScore will be 0 anyway. Hence, set the normalizer to 1
		// to avoid NAN values when we compute superclassDegree(x)/normalizer.
		if (normalizer == 0)
			normalizer = 1;

		// Set the final values
		for (Integer superclass : superclassDegree.keySet()) {
			double instanceScore = superclassDegree.get(superclass) / normalizer;
			double domainScore = 1.0; // domainSuperclassDegree.containsKey(superclass)
																// ? domainSuperclassDegree.get(superclass) :
																// 1.0;
			if (1 - (1 - instanceScore) * domainScore < Config.THETA)
				continue;
			if (debug)
				Announce.debug("Setting final value:", superStore.entity(superclass),
						superclassDegree.get(superclass), normalizer,
						superclassDegree.get(superclass) / normalizer, 1
								- (1 - instanceScore) * domainScore);
			if (!test)
				computed.setSubclass(subStore, subclass, superclass, 1
						- (1 - instanceScore) * domainScore);
		}
	}

	public static void computeClassesOneWay(FactStore fs1, FactStore fs2) {
		int counter = fs2.numClasses();
		Announce.progressStart("Computing subclasses one direction", counter);
		for (int cls = 0 ; cls < fs2.numEntities(); cls++) {
			if (!fs2.isClass(cls))
				continue;
			findSuperClassesOf(cls, fs2, fs1);
			Announce.progressStep();
		}
		Announce.progressDone();
	}

	/** Reduces a map to one value with the minimum */
	public static <T> void reduceToMinMin(Map<T, Double> map) {
		if (map.isEmpty())
			return;
		double min = 2;
		T key = null;
		for (T v : map.keySet()) {
			if (map.get(v) < min) {
				key = v;
				min = map.get(v);
			}
		}
		map.clear();
		map.put(key, min);
	}

	/** The class for a threaded findEqualsOf computation.
	 *  It reads the entities from the inputs queue, and writes its aggregated result on the target queue (and in equalities)
	 */
	private static class Mapper implements Runnable {
		int run;
		EqualityStore equalities;
		EqualityStoreMultiple equalitiesMultiple;
		BlockingQueue<MapperOutput> target;
		ConcurrentLinkedQueue<Integer> inputs;
		FactStore fs1;
		FactStore fs2;
		int id;
		Map<Integer, Double> equalityProduct;
		Map<Pair<Integer, Pair<Integer, Integer>>, Pair<Pair<Double, Double>, Double>> fullEqualityProduct;
		boolean localDebug;
		int localJoinLengthLimit1;
		int localJoinLengthLimit2;
		// guide to explore only the interesting relations and joins in the first ontology
		Neighborhood relationGuide;
		MapperOutput mapperOutput;
		IntSet visited1;
		IntSet visited2;
		int limit;

		public Mapper(int run, int id, FactStore factStore,
				EqualityStore equalities, EqualityStoreMultiple equalitiesMultiple, MapperOutput mapperOutput, Neighborhood relationGuide,
				BlockingQueue<MapperOutput> target, ConcurrentLinkedQueue<Integer> inputs, int limit) {
			this.run = run;
			this.equalities = equalities;
			this.equalitiesMultiple = equalitiesMultiple;
			this.target = target;
			this.fs1 = factStore;
			this.fs2 = computed.other(fs1);
			this.inputs = inputs;
			this.id = id;
			this.localDebug = debug;
			// this is to be able to reduce the join length limit during the process
			this.localJoinLengthLimit1 = fs1.getJoinLengthLimit();
			this.localJoinLengthLimit2 = fs2.getJoinLengthLimit();
			visited1 = new IntOpenHashSet();
			visited2 = new IntOpenHashSet();
			this.limit = limit;
			this.relationGuide = relationGuide;
			
			if (setting.sampleEntities > 0) {
				// don't do any joins during the few first runs
				if (run < 2) {
					localJoinLengthLimit1 = 1;
					localJoinLengthLimit2 = 1;
				}
			}
			this.mapperOutput = mapperOutput;
		}
		
		/** Explore the second ontology.
		 * @param visited -- hashset of visited relation/object pairs for the fact in the first ontology
		 * @param newNeighborhood -- the neighborhood at the current iteration, that we write
		 * @param x1
		 * @param r1
		 * @param y1 such that x1 -r1-> y1
		 * @param x2
		 * @param r2
		 * @param y2 such that x2 -r2-> y2
		 * @param xeqv the score between x1 and x2 at the previous iteration
		 * @param oldNeighborhood -- the neighborhood at the previous iteration, actually unused
		 */
		public void exploreSecondOntology(Neighborhood newNeighborhood, int x1,
				JoinRelation r1, int y1, int x2,
				JoinRelation r2, int y2, double xeqv, Neighborhood oldNeighborhood) {
			if (r2.isTrivial())
				return;
			if (y2 < fs2.numEntities() && Config.ignoreClasses && fs2.isClass(y2))
				return;
			double yeqv = computed.equality(fs1, y1, y2);

			// we assume that there are no duplicate facts
			// hence, for a join relation length of 1, there is no need to check visited
			// so we save time for the specific case where no joins are made
			//Pair<Integer, Integer> p = new Pair<Integer, Integer>(r2.code(), y2);
			int p = r2.code()*fs2.numEntities() + y2;
			if (r2.length() == 1 || !visited2.contains(p)) {
				if (localDebug) {
					// don't compute the toString's unless running in debug mode, to save time
					// Announce.debug("Mark as visited", r2.toString(), fs2.entity(y2));
					if (xeqv * yeqv > 0)
						Announce.debug("Align", r1.toString(), r2.toString(), "occurrence", xeqv, "score", xeqv*yeqv);
//					Announce.debug("Old contents of visited:");
//					for (Pair<JoinRelation, Integer> pp : visited) {
//						Announce.debug(pp.first.toString(), fs2.entity(pp.second));
//					}
				}
				if (r2.length() > 1) {
				  visited2.add(p);
//				  if (localDebug) {
//				  	Announce.debug("New contents of visited:");
//						for (Pair<JoinRelation, Integer> pp : visited) {
//							Announce.debug(pp.first.toString(), fs2.entity(pp.second));
//						}
//				  }
				}
				newNeighborhood.registerOccurrence(xeqv);
				newNeighborhood.registerScore(xeqv * yeqv);
				if (equalities != null)
					registerEquality(x1, r1, y1, xeqv, x2, r2, y2);
			} else {
				if (localDebug) {
					//Announce.debug("ignore duplicate", r2.toString(), fs2.entity(y2));
				}
			}
			
			if (r2.length() >= localJoinLengthLimit2)
				return;
			if (!setting.allowLoops && x2 == y2)
				return;
			if (relationGuide != null && newNeighborhood.isEmpty())
				return;
			
			List<PredicateAndObject> facts = fs2.factsAbout(y2); 
			for (int i = 0; i < facts.size(); i++) {
				int r2bis = facts.get(i).predicate;
				int ny2 = facts.get(i).object;
//				Neighborhood n2 = oldNeighborhood == null ? null : oldNeighborhood.getChildRO(r2bis);
				Neighborhood n2 = oldNeighborhood;
//				if (!extendNeighborhoods && oldNeighborhood == null) {
//					continue;
//				}
//				JoinRelation nr2 = new JoinRelation(r2);
				Neighborhood nn2 = null;
				if (relationGuide == null) {
					nn2 = newNeighborhood.getChild(run, r2bis);
				} else {
					nn2 = newNeighborhood.getChildRO(r2bis);
				}
				if (nn2 == null) {
					continue;
				}
				if (setting.interestingnessThreshold && run > 0) {
					if (!nn2.worthTrying()) {
						continue;
					}
				}
				r2.push(r2bis);
				exploreSecondOntology(nn2, x1, r1, y1, x2, r2, ny2, xeqv, n2);
				r2.pop();
			}
		}

		/** register evidence for the equality of y1 and y2 from x1 -r1-> y1 and x2 -r2-> y2	*/
		public void registerEquality(
				int x1, JoinRelation r1, int y1, double xeqv,
				int x2, JoinRelation r2, int y2) {
			
			// when using the one pass method, we must use the small initial weights for
			// the two first iterations
			// otherwise nothing can align
			boolean isFirstRun = (run <= 1);
			
//			if (!Config.treatIdAsRelation && fs2.getIdRel() != null
//					&& r2.isSimpleRelation(-fs2.getIdRel().id))
//				return;
			assert(!Config.treatIdAsRelation);

			double subprop = computed.subRelation(fs2, r2, r1);
			if (subprop >= 0)
				subprop /= Config.epsilon;
			double superprop = computed.subRelation(fs1, r1, r2);
			if (superprop >= 0)
				superprop /= Config.epsilon;
			
			if (subprop < Config.THETA && superprop < Config.THETA) {
				if (isFirstRun) {
					double val = Config.IOTA / (1 + Config.iotaDependenceOnLength * ((r1.length() - 1) + (r2.length() - 1)));
					subprop = val;
					superprop = val;
				} else
					return;
			}
			
			double fun1 = fs1.functionality(r1) / Config.epsilon;
			double fun1r = fs1.inverseFunctionality(r1) / Config.epsilon;
			
			double fun2 = fs2.functionality(r2) / Config.epsilon;
			double fun2r = fs2.inverseFunctionality(r2) / Config.epsilon; 

			double factor = 1;
			double factor1 = 1 - xeqv * subprop * fun1 * (Config.bothWayFunctionalities ? fun1r : 1.0);
			double factor2 = 1 - xeqv * superprop * fun2 * (Config.bothWayFunctionalities ? fun2r : 1.0);
			if (subprop >= 0 && fun1 >= 0)
				factor *= factor1;
			if (Config.subAndSuper && superprop >= 0 && fun2 >= 0)
				factor *= factor2;
			
			// with the new method, don't do this for literals
			// also don't do it for very small things
			if (!fs2.isLiteral(y2) && 1 - factor > 0.01) {
				if (!setting.useNewEqualityProduct) {
					// classical equality propagation formula from the PARIS paper
					double val = equalityProduct.containsKey(y2) ? equalityProduct.get(y2)
							: 1.0;
					double oldval = val;
					val *= factor;
					assert(val >= 0 && val <= 1);
					equalityProduct.put((Integer) y2, val);
					if (localDebug) {
						Announce.debug("  Align", fs1.entity(y1), "with", fs2.entity(y2), "for:");
						Announce.debug("    ", fs1.entity(x1),
								r1.toString(), fs1.entity(y1));
						Announce.debug("    ", fs2.entity(x2),
								r2.toString(), fs2.entity(y2));
						Announce.debug("     xeqv=", xeqv, "fun1=", fun1, "fun1r=", fun1r, "fun2=", fun2, "fun2r", fun2r, "r1<r2=", subprop, "r2<r1=", superprop);
						Announce.debug("val=", 1 - val, "oval=", 1 - oldval);
					}
				} else {
					// revised formula from my report
					Pair<Integer, Pair<Integer, Integer>> k = new Pair<Integer, Pair<Integer, Integer>>((Integer) y2, new Pair<Integer, Integer>(
							x1, x2));
				
					if (!fullEqualityProduct.containsKey(k)) {
						fullEqualityProduct.put(k, new Pair<Pair<Double, Double>, Double>(new Pair<Double, Double>(1.0, 1.0), xeqv));
					}
					Pair<Pair<Double, Double>, Double> pval = fullEqualityProduct.get(k);
					if (subprop >= 0 && fun1 >= 0)
						pval.first.first *= 1 - subprop * fun1 * (Config.bothWayFunctionalities ? fun1r : 1.0);
					if (Config.subAndSuper && superprop >= 0 && fun2 >= 0)
						pval.first.second *= 1 - superprop * fun2 * (Config.bothWayFunctionalities ? fun2r : 1.0);
				}
			}
		}
		
		/** register evidence for the equality of y1 and y2 from x1 -r1-> y1 and x2 -r2-> y2
		 * optimized for non-joins	*/
		public void registerEquality(
				int x1, int r1, int y1, double xeqv,
				int x2, int r2, int y2) {
			
			// when using the one pass method, we must use the small initial weights for
			// the two first iterations
			// otherwise nothing can align
			boolean isFirstRun = (run <= 1);
			
//			if (!Config.treatIdAsRelation && fs2.getIdRel() != null
//					&& r2.isSimpleRelation(-fs2.getIdRel().id))
//				return;
			assert(!Config.treatIdAsRelation);

			double subprop = computed.subRelation(fs2, r2, r1);
			if (subprop >= 0)
				subprop /= Config.epsilon;
			double superprop = computed.subRelation(fs1, r1, r2);
			if (superprop >= 0)
				superprop /= Config.epsilon;
			
			if (subprop < Config.THETA && superprop < Config.THETA) {
				if (isFirstRun) {
					double val = Config.IOTA;
					subprop = val;
					superprop = val;
				} else
					return;
			}
			
			double fun1 = fs1.functionality(r1) / Config.epsilon;
			double fun2 = fs2.functionality(r2) / Config.epsilon;
			
			double fun1r = -42;
			double fun2r = -42;
			
			if (Config.bothWayFunctionalities) {
				fun1r = fs1.inverseFunctionality(r1) / Config.epsilon;
				fun2r = fs2.inverseFunctionality(r2) / Config.epsilon;
			}

			double factor = 1;
			double factor1 = 1 - xeqv * subprop * fun1 * (Config.bothWayFunctionalities ? fun1r : 1.0);
			double factor2 = 1 - xeqv * superprop * fun2 * (Config.bothWayFunctionalities ? fun2r : 1.0);
			if (subprop >= 0 && fun1 >= 0)
				factor *= factor1;
			if (Config.subAndSuper && superprop >= 0 && fun2 >= 0)
				factor *= factor2;
			
			// with the new method, don't do this for literals
			// also don't do it for very small things
			if (!fs2.isLiteral(y2) && 1 - factor > 0.01) {
				if (!setting.useNewEqualityProduct) {
					// classical equality propagation formula from the PARIS paper
					double val = equalityProduct.containsKey(y2) ? equalityProduct.get(y2)
							: 1.0;
					double oldval = val;
					val *= factor;
					assert(val >= 0 && val <= 1);
					equalityProduct.put((Integer) y2, val);
					if (localDebug) {
						Announce.debug("  Align", fs1.entity(y1), "with", fs2.entity(y2), "for:");
						Announce.debug("    ", fs1.entity(x1),
								fs1.relation(r1), fs1.entity(y1));
						Announce.debug("    ", fs2.entity(x2),
								fs2.relation(r2), fs2.entity(y2));
						Announce.debug("     xeqv=", xeqv, "fun1=", fun1, "fun1r=", fun1r, "fun2=", fun2, "fun2r", fun2r, "r1<r2=", subprop, "r2<r1=", superprop);
						Announce.debug("val=", 1 - val, "oval=", 1 - oldval);
					}
				} else {
					// revised formula from my report
					Pair<Integer, Pair<Integer, Integer>> k = new Pair<Integer, Pair<Integer, Integer>>((Integer) y2, new Pair<Integer, Integer>(
							x1, x2));
				
					if (!fullEqualityProduct.containsKey(k)) {
						fullEqualityProduct.put(k, new Pair<Pair<Double, Double>, Double>(new Pair<Double, Double>(1.0, 1.0), xeqv));
					}
					Pair<Pair<Double, Double>, Double> pval = fullEqualityProduct.get(k);
					if (subprop >= 0 && fun1 >= 0)
						pval.first.first *= 1 - subprop * fun1 * (Config.bothWayFunctionalities ? fun1r : 1.0);
					if (Config.subAndSuper && superprop >= 0 && fun2 >= 0)
						pval.first.second *= 1 - superprop * fun2 * (Config.bothWayFunctionalities ? fun2r : 1.0);
				}
			}
		}


		/** findEqualsOf for a fixed fact x1 -r1-> y1, other arguments are the normalizer of relations in fs2, the neighborhood for r1, the equality products */
		public void findEqualsOfFact(RelationNormalizer normalizer,
			  Neighborhood neighborhood,
				int x1, JoinRelation r1, int y1) {

			if (localDebug) {
				Announce.debug("run", run, "findEqualsOfFact:", fs1.entity(x1),
					r1.toString(), fs1.entity(y1));
			}
			
			// will only be initialized if there is something to do
			Neighborhood oldNeighborhood = null;

			if (!fs1.isLiteral(x1) && Config.ignoreClasses && fs1.isClass(x1))
			  return;

			// we don't do that anymore because we need to align relations
//			if (fun1 < Config.THETA)
//				return;

			for (Pair<Object, Double> x2pair : computed.equalToScoredId(fs1, x1)) {
				int x2 = (Integer) x2pair.first();
				double xeqv = x2pair.second();
				assert(xeqv >= 0 && xeqv <= 1);

				if (xeqv < Config.THETA)
					continue;

				// for all matching x2, y2's, we need to add weight for the normalizer
				for (Pair<Object, Double> y2pair : computed.equalToScored(fs1, y1)) {
					Double yeqv = y2pair.second();
					if (localDebug) {
						Object y2pf = y2pair.first();
						Announce.debug("Increment normalizer of", r1.toString(), "by", xeqv*yeqv, "for", fs1.entity(x1), r1.toString(),
								fs1.entity(y1), fs2.entity(x2), y2pf instanceof String ? fs2.entity((String) y2pf) : fs2.entity((int) y2pf));
					}
					normalizer.incrementSimpleNormalizer(r1, xeqv * yeqv);
					normalizer.incrementCurrentRealNormalizer(xeqv * yeqv);
				}
				normalizer.addNormalizer(r1);

				visited2.clear();
				
				List<PredicateAndObject> facts = fs2.factsAbout(x2); 
				for (int i = 0; i < facts.size(); i++) {
//					if (oldNeighborhood == null)
//						oldNeighborhood = computed.getNeighborhood(fs1, r1);
					int r2bis = facts.get(i).predicate;
					int ny2 = facts.get(i).object;
					Neighborhood n2 = oldNeighborhood == null ? null : oldNeighborhood.getChildRO(r2bis);
					JoinRelation nr2 = new JoinRelation(fs2, r2bis);
					Neighborhood nn2 = neighborhood.getChild(run, r2bis);
					if (setting.interestingnessThreshold && run > 0) {
						if (!nn2.worthTrying()) {
							continue;
						}
					}
//					Announce.message("@exploreSecondOntology", fs1.toString(x1), r1.toString(), fs1.toString(y1),
//							"and", fs2.toString(x2), nr2.toString(), fs2.toString(ny2));
					
					exploreSecondOntology(nn2, x1, r1, y1, x2, nr2, ny2, xeqv, n2);
				}
			}

			neighborhood.propagateScores();
		}

		// beware, we accumulate in r1 in the REVERSE order
		/** find possible r1's and y1's for a given x1 by exploring recursively around x1, and call findEqualsOfFact for x1 -r1-> y1
		 * 
		 * @param output
		 * @param equalityProduct
		 * @param fullEqualityProduct
		 * @param x1
		 * @param r1
		 * @param y1
		 * @param rg -- the current relation guide
		 * 
		 * Caution: r1 is built in the reverse order and reversed at the end when calling findEqualsOfFact
		 */
		public void exploreFirstOntology(int x1, JoinRelation r1, int y1, Neighborhood rg) {
			
			// we assume that there are no duplicate facts
			// hence, for a join relation length of 1, there is no need to check visited
			// Pair<Integer, Integer> pvisited = new Pair<Integer, Integer>(r1.code(), y1);
			int pvisited = r1.code()*fs1.numEntities() + y1;
					
			if (r1.length() == 1 || !visited1.contains(pvisited)) {
				if (r1.length() > 1)
				  visited1.add(pvisited);
				if (localDebug) {
					// don't compute the toString's unless running in debug mode, to save time
					// Announce.debug("Mark as visited", r1.toString(), fs1.entity(y1));
				}
				
				// TODO2 reverse r1 and reverse it back
				JoinRelation nr1 = new JoinRelation(r1);
				nr1.reverseDirection();
				if (mapperOutput.neighborhoods[nr1.code()] == null) {
					mapperOutput.neighborhoods[nr1.code()] = new HashArrayNeighborhood(fs2, run, true, Math.min(fs2.getJoinLengthLimit(), setting.sumJoinLengthLimit - nr1.length()));
				}
				findEqualsOfFact(mapperOutput.relationNormalizer,
						mapperOutput.neighborhoods[nr1.code()], x1, nr1,
						y1);
			} else {
				if (localDebug) {
					//Announce.debug("ignore duplicate", r1.toString(), fs1.entity(y1));
				}
			}
			
			if (r1.length() >= localJoinLengthLimit1)
				return;
			if (!setting.allowLoops && x1 == y1)
				return;
			if (relationGuide != null && (rg == null || rg.isEmpty()))
				return;
			// we don't consider joins on the first ontology before the second run
			if (run == 0)
				return;
			
			List<PredicateAndObject> facts = fs1.factsAbout(x1); 
			for (int i = 0; i < facts.size(); i++) {
				PredicateAndObject f = facts.get(i);
				if (f.predicate == r1.getLast())
					continue; // relation will be trivial
				int r1bis = FactStore.inverse(f.predicate);
				Neighborhood nrg = null;
				if (relationGuide != null) {
					nrg = rg.getChildRO(f.predicate);
					if (nrg == null)
						continue;
				}
				r1.push(r1bis);
				exploreFirstOntology(f.object, r1, y1, nrg);
				r1.pop();
			}
		}

		/** Find equality candidates for an entity y1 */
		public void findEqualsOf(int y1) {
			//Announce.message("@CALL findEqualsOf", y1, fs1.toString(y1), "");
			// equalityProduct -- maps candidate y2's to their alignment score with y1
			// fullEqualityProduct -- maps candidate y2's and (x1, x2) to their first direction and second direction scores, and to the equiv of x1 and x2
			equalityProduct.clear();
			fullEqualityProduct.clear();

			Announce.debug("run", run, "findEqualsOf:", fs1.entity(y1), "");
			//HashSet<Pair<Integer, Integer>> visited = new HashSet<Pair<Integer, Integer>>();
			visited1.clear();
			// call exploreFirstOntology for all fact about y1
			// (the first recursive call is unrolled to make things run faster)
			
			List<PredicateAndObject> facts = fs1.factsAbout(y1); 
			for (int i = 0; i < facts.size(); i++) {
				PredicateAndObject f = facts.get(i);
				int nx1 = f.object;
				int r1bis = FactStore.inverse(f.predicate);
				JoinRelation nr1 = new JoinRelation(fs1, r1bis);
				Neighborhood rg = null;
				if (relationGuide != null) {
					rg = relationGuide.getChildRO(f.predicate);
					if (rg == null && !Config.allLengthOneAfterSample)
						continue;
				}
				exploreFirstOntology(nx1, nr1, y1, rg);
			}

			assert (!equalityProduct.keySet().contains(null));
			
			if (equalities != null)
				setEqualities(y1);
		}
		
		void setEqualities(int y1) {
		assert(setting.takeMaxMax);

//		double max = 0;
//		Set<Integer> vmax = new TreeSet<Integer>();
		Map<Integer, Double> usefulEqualityProduct;
		if (setting.useNewEqualityProduct) {
			// compute the newEqualityProduct (the one with the revised entity propagation formula) from the fullEqualityProduct
			Map<Integer, Double> newEqualityProduct = new HashMap<Integer, Double>();
			for (Map.Entry<Pair<Integer, Pair<Integer, Integer>>, Pair<Pair<Double, Double>, Double>> e : fullEqualityProduct.entrySet()) {
				int y2 = e.getKey().first;
				double val = newEqualityProduct.containsKey(y2) ? newEqualityProduct.get(y2) : 1.0;
				double xeqv = e.getValue().second;
				if (localDebug) {
					Announce.debug("VAL was", val, "xeqv", xeqv, "score1", e.getValue().first, "score2", e.getValue().second);
					Announce.debug("VAL now", val);
				}
				val *= (1 - xeqv * (1 - e.getValue().first.first)) * (1 - xeqv * (1 - e.getValue().first.second));
				newEqualityProduct.put(y2, val);
			}
			usefulEqualityProduct = newEqualityProduct;
		} else {
			usefulEqualityProduct = equalityProduct;
		}
		if (setting.cleverMatching) {
			equalitiesMultiple.set(y1, usefulEqualityProduct);
			return;
		}
		reduceToMinMin(usefulEqualityProduct);
		for (Integer y2 : usefulEqualityProduct.keySet()) {
			double val = 1 - usefulEqualityProduct.get(y2);
//			if (val < Config.THETA)
//				continue;
			//foundEquality = true;
			equalities.setValue(y1, y2, val);
//			if (val > max) {
//				vmax.clear();
//				vmax.add(y2);
//				max = val;
//			}
//			if (val == max)
//				if (!setting.takeMaxMax || vmax.isEmpty())
//					vmax.add(y2);
			if (localDebug) {
				Announce.debug("Final:", fs2.entity(y2), val);
			}
		}
			
		}
		

		/** Find equality candidates for an entity y1 */
		public void findEqualsOf1(int y1) {
			equalityProduct.clear();
			fullEqualityProduct.clear();

			List<PredicateAndObject> facts = fs1.factsAbout(y1); 
			for (int i = 0; i < facts.size(); i++) {
				int x1 = facts.get(i).object;
				int r1bis = FactStore.inverse(facts.get(i).predicate);
				
				if (!fs1.isLiteral(x1) && Config.ignoreClasses && fs1.isClass(x1))
				  continue;
				
				if (mapperOutput.neighborhoods[r1bis] == null) {
					mapperOutput.neighborhoods[r1bis] = new HashArrayNeighborhood(fs2, run, true, Math.min(fs2.getJoinLengthLimit(), setting.sumJoinLengthLimit - 1));
				}
				Neighborhood currentNeighborhood = mapperOutput.neighborhoods[r1bis];

				for (Pair<Object, Double> x2pair : computed.equalToScoredId(fs1, x1)) {
					int x2 = (Integer) x2pair.first();
					double xeqv = x2pair.second();
					assert(xeqv >= 0 && xeqv <= 1);

					if (xeqv < Config.THETA)
						continue;

					// for all matching x2, y2's, we need to add weight for the normalizer
					for (Pair<Object, Double> y2pair : computed.equalToScored(fs1, y1)) {
						Double yeqv = y2pair.second();
						mapperOutput.relationNormalizer.incrementSimpleNormalizer(r1bis, xeqv * yeqv);
						mapperOutput.relationNormalizer.incrementCurrentRealNormalizer(xeqv * yeqv);
//						if (nr1.toString().startsWith("dbp:infl") || nr1.toString().startsWith("influences"))
//							Announce.message("@@@normalizer", nr1.toString(), fs1.entity(x1), fs1.entity(y1), fs2.entity(x2), fs2.entity((Integer) y2pair.first()), xeqv, yeqv);
					}
					mapperOutput.relationNormalizer.addNormalizer(r1bis);
					
					List<PredicateAndObject> facts2 = fs2.factsAbout(x2); 
					for (int j = 0; j < facts2.size(); j++) {
						int ny2 = facts2.get(j).object;
						double yeqv = computed.equality(fs1, y1, ny2);
						int r2bis = facts2.get(j).predicate;
						Neighborhood nn2 = currentNeighborhood.getChild(run, r2bis);
						nn2.registerOccurrence(xeqv);
						nn2.registerScore(xeqv * yeqv);
//						if (fs2.relation(r2bis).startsWith("dbp:infl") || fs2.relation(r2bis).toString().startsWith("influences"))
//							Announce.message("@@@score", nr1.toString(), fs2.relation(r2bis), fs1.entity(x1), fs1.entity(y1), fs2.entity(x2), fs2.entity(ny2), xeqv, yeqv);
						if (equalities != null)
							registerEquality(x1, r1bis, y1, xeqv, x2, r2bis, ny2);
					}
				}

				currentNeighborhood.propagateScores();
			}
			
			if (equalities != null)
				setEqualities(y1);
}

		/** Run findEqualsOf on entities fetched from inputs */
		public MapperOutput findEqualsOfQueue() {
			
			equalityProduct = new HashMap<Integer, Double>();
			fullEqualityProduct = new HashMap<Pair<Integer, Pair<Integer, Integer>>, Pair<Pair<Double, Double>, Double>>();
			
			int done = 0;
			long start = System.currentTimeMillis();
			long last = start;
			int nManaged = 0;
			while (true) {
				Integer e1;  
				try {
					e1 = inputs.remove();
				} catch (java.util.NoSuchElementException e) {
					// someone took the last item from the queue before we did
					break;
				}
				++done;
				if (done % setting.reportInterval == 0) {
					// Announce.message("Entities done:", done, "Time per entity:", timeSum
					// / ((float) reportInterval), "ms     Facts per entity:", factSum /
					// ((float) reportInterval),
					// NumberFormatter.formatMS((long) ((double) (System.currentTimeMillis()
					// - timeStart) / (done - startAt) * (total - done))));
					long t = System.currentTimeMillis();
					double perEntity = (t - start)
							/ ((float) done);
					Announce.message("(" + id + ") Entities done:", done,
									"Time per entity:", perEntity, "ms");
					Announce.message("(" + id + ") Last time:", t - last);
					Announce.message("(" + id + ") Last entity:", fs1.entity(e1));
					last = t;
				}
				nManaged++;
				
				if (nManaged == limit) {
					break;
				}
				
				if (setting.debugEntity != null) {
					if (fs1.entity(e1).contains(setting.debugEntity)) {
						Announce.message("DEBUGENTITY");
						Announce.setLevel(Level.DEBUG);
						localDebug = true;
					}
				}

				if (localJoinLengthLimit1 == 1 && localJoinLengthLimit2 == 1 && setting.sampleEntities == 0 && setting.optimizeNoJoins)
					findEqualsOf1(e1);
				else
					findEqualsOf(e1);
				
				if (setting.debugEntity != null) {
					if (fs1.entity(e1).contains(setting.debugEntity)) {
						Announce.setLevel(Level.MESSAGES);
						localDebug = false;
					}
				}
			}
			Announce.message("run", run, nManaged, "actually managed");
			return mapperOutput;
		}

		public void run() {
			target.add(findEqualsOfQueue());
		}
	}

	/** limit the mapperOutput to interesting alignments and return the relation guide */
	public static Neighborhood endSampling(int run, MapperOutput mapperOutput) {
	//the current relation normalizer and neighborhoods are the results of exploring without constraints
		Announce.message("End of the sampling phase!");
		if (setting.printNeighborhoodsSampling) {
			Announce.message("BEFORE:");
			mapperOutput.print(computed.other(mapperOutput.fs));
		}
		Neighborhood relationGuide = new HashArrayNeighborhood(mapperOutput.fs, -1, true, mapperOutput.fs.getJoinLengthLimit());
		for (int i = 0; i < mapperOutput.fs.maxJoinRelationCode(); i++) {
			if (mapperOutput.neighborhoods[i] == null)
				continue;
			boolean result;
			JoinRelation jr = mapperOutput.fs.joinRelationByCode(i);
			if (mapperOutput.relationNormalizer.getNormalizer(jr) > setting.joinThreshold) {
				result = mapperOutput.neighborhoods[i].thresholdByNormalizer(mapperOutput.relationNormalizer.getNormalizer(jr), setting.joinThreshold, jr.length() == 1);
			} else {
				mapperOutput.neighborhoods[i] = null;
				result = false;
			}
			if (result) {
				// write in relationGuide that the join relation i in the first ontology should be explored
				Neighborhood cn = relationGuide;
				for (int j = 0; j < jr.length(); j++) {
					assert(jr.get(j) <= mapperOutput.fs.maxRelationId());
					cn = cn.getChild(run, jr.get(j));
				}
				// cn is now the neighborhood representing the join relation i
				// we don't care about the value that it carries, just that it exists
			}
		}
		// relationGuide is now the tree of join relations in onto 1 which align to something in onto 2 (like the statistics module or something)
		// mapperOutput.neighborhoods[i] is now the tree of join relations in onto 2 which align to join relation i in onto 1 
		if (setting.printNeighborhoodsSampling) {
			Announce.message("AFTER:");
			mapperOutput.print(computed.other(mapperOutput.fs));
			Announce.message("GUIDE:");
			((HashArrayNeighborhood) relationGuide).print(new JoinRelation(mapperOutput.fs));
		}
		return relationGuide;
	}
	
	public static MapperOutput aggregateThreads(int run, FactStore factStore, EqualityStore equalities, EqualityStoreMultiple equalitiesMultiple, MapperOutput mapperOutput,
			Neighborhood relationGuide, ConcurrentLinkedQueue<Integer> inputs, int limit) throws InterruptedException {
		Announce.message("Spawning", setting.nThreads, "threads");
		LinkedList<Thread> threads = new LinkedList<Thread>();
		BlockingQueue<MapperOutput> results = new LinkedBlockingQueue<MapperOutput>();
	  for (int i = 0; i < setting.nThreads ; i++) {
	  	MapperOutput myMapperOutput;
	  	if (mapperOutput == null) {
	  		myMapperOutput = new MapperOutput(factStore);
	  	} else {
	  		// If we want to resume from a mapperOutput, we have to create nThreads copies of it and scale them down by this factor
	  		myMapperOutput = new MapperOutput(mapperOutput);
	  		myMapperOutput.scaleDown(setting.nThreads);
	  	}
	  	
	    Mapper mapper = new Mapper(run, i, factStore, equalities, equalitiesMultiple, myMapperOutput, relationGuide, results, inputs, limit);
	    Thread thread = new Thread(mapper);
	    threads.add(thread);
		  thread.start();
	  }
		Announce.message("waiting for thread termination...");
		// wait for termination
		for (Thread thread : threads) {
			thread.join();
			Announce.message("... one thread joined");
		}
		Announce.doing("Aggregating results...");
		// aggregate results in a blank factstore
		mapperOutput = new MapperOutput(factStore);
		for (MapperOutput p : results) {
			Announce.message("Aggregated one result...");
			mapperOutput.reduceWith(p);
		}
		return mapperOutput;
	}
	
	/** Perfom the alignment of one factStore against the other
	 *  equality (initialized by caller) is where entity alignments are stored
	 *  the relation alignment is returned as a MapperOutput */
	public static MapperOutput oneIterationOneWay(int run,
			FactStore factStore, EqualityStore equalities, EqualityStoreMultiple equalitiesMultiple) throws InterruptedException {
		
		MapperOutput mapperOutput = null;
		
		Announce.message("starting equalities at", NumberFormatter.ISOtime());
		List<Integer> entities = factStore.properEntities();
		// initialize a queue with all entities to manage
		ConcurrentLinkedQueue<Integer> inputs = new ConcurrentLinkedQueue<Integer>();
		int nAdded = 0;
		if (setting.shuffleEntities) {
			Collections.shuffle(entities);
		}
		for (int i = 0; i < entities.size(); i++) {
			int e1 = entities.get(i);
			if ((factStore.isClass(e1) && Config.ignoreClasses)
					/*|| fs1.isRelation(e1)*/)
				continue;
			inputs.add(e1);
			nAdded++;
		}
		Announce.message("run", run, nAdded, "added to queue");
	
		int limit = run >= 2 && setting.sampleEntities > 0 ? setting.sampleEntities : 0;
		int tempNThreads = 0;
		if (limit > 0 && setting.debugSampling) {
			Announce.setLevel(Level.DEBUG);
			debug = true;
			tempNThreads = setting.nThreads;
			setting.nThreads = 1;
		}
		if (setting.nThreads == 1) {
			// perform the computation directly
			Mapper mapper = new Mapper(run, -1, factStore, equalities, equalitiesMultiple, new MapperOutput(factStore), null, null, inputs, limit);
			mapperOutput = mapper.findEqualsOfQueue();
		} else {
			// spawn threads to perform the computation
			Announce.message("Will manage", nAdded, "entities");
			mapperOutput = aggregateThreads(run, factStore, equalities, equalitiesMultiple, null, null, inputs, limit);
		}
		
		if (limit > 0) {
			Announce.message("Will end sampling");
			Neighborhood relationGuide = endSampling(run, mapperOutput);
			Announce.message("Will manage the rest now that sampling is done");
			if (setting.debugSampling) {
				Announce.setLevel(Level.MESSAGES);
				debug = false;
				setting.nThreads = tempNThreads;
			}
			if (setting.nThreads == 1) {
				Mapper mapper2 = new Mapper(run, -1, factStore, equalities, equalitiesMultiple, mapperOutput, relationGuide, null, inputs, 0);
				mapperOutput = mapper2.findEqualsOfQueue();
			} else {
				mapperOutput = aggregateThreads(run, factStore, equalities, equalitiesMultiple, mapperOutput, relationGuide, inputs, 0);
			}
			
			Announce.done();
		}
		
		Announce.done();
				
		return mapperOutput;
	}

	/** Runs one whole iteration 
	 * @throws InterruptedException */
	public static void oneIteration(int run) throws IOException, InterruptedException {
		EqualityStore equalities1 = new EqualityStore(factStore1, factStore2);
		//EqualityStore equalities2 = new EqualityStore(factStore2, factStore1);
		EqualityStoreMultiple equalitiesMultiple = null;
		if (setting.cleverMatching) {
			Announce.doing("Performing greedy approximation of the maximum matching...");
			equalitiesMultiple = new EqualityStoreMultiple(factStore1, factStore2);
			Announce.done();
		}

		MapperOutput mapperOutput1 = null;
		MapperOutput mapperOutput2 = null;

		/** We do the computation on the ontologies */
		mapperOutput1 = oneIterationOneWay(run, factStore1, equalities1, equalitiesMultiple);
		if (setting.cleverMatching)
			equalities1 = equalitiesMultiple.takeMaxMaxClever();
		equalities1.dump(new File(setting.tsvFolder, run + "_eqv_full.tsv"));
		equalities1.takeMaxMaxBothWays();
		equalities1.dump(new File(setting.tsvFolder, run + "_eqv.tsv"));
		if (setting.bothWays) {
			mapperOutput2 = oneIterationOneWay(run, factStore2, null, null);
		}
		
		computed.mapperOutput1 = mapperOutput1;
		computed.mapperOutput2 = mapperOutput2;

		computed.equalityStore = equalities1;
		Announce.message("done equalities at", NumberFormatter.ISOtime());

		
		/** Now, we aggregate the relation alignments to use them in the next iteration */
		Announce.message("loading neighborhoods in one direction");
		computed.superRelationsOf1.loadMapperOutput(mapperOutput1);
		Announce.message("loading neighborhoods in other direction");
		if (setting.bothWays) {
			computed.superRelationsOf2.loadMapperOutput(mapperOutput2);
		}

		/** Write the alignments */
		if (setting.bothWays) {
			//equalities2.dump(new File(setting.tsvFolder, run + "_eqv2.tsv"));
			computed.superRelationsOf2.dump(new File(setting.tsvFolder, run
					+ "_superrelations2.tsv"));
		}
		computed.superRelationsOf1.dump(new File(setting.tsvFolder, run
				+ "_superrelations1.tsv"));

		if (setting.printNeighborhoodsSampling)
			computed.printNeighborhoods();
		Announce.progressDone();
		Announce.message("done properties at", NumberFormatter.ISOtime());
	}

	public static FactStore loadFactStore(File path, String prefix, String uri) throws IOException {
		FactStore fs;
		fs = new FactStore(setting, prefix, uri,
				setting.joinLengthLimit, setting.normalizeStrings, setting.normalizeDatesToYears);
		Announce.doing("Loading facts...");
		if (path.isFile()) {
			fs.load(path);
		} else {
			fs.load(path, Pattern.compile(".*"));
		}
		fs.prepare();
		Announce.done();
		return fs;
	}
	
	/** Runs the thing */
	public static void main(String[] args) throws Exception {
		// Load the setting
		if (args == null || args.length < 1) {
			Announce
					.help(
							"PARIS aligns the instances, relations, and classes of two knowledge bases (KBs).\n",
							"java paris.Paris <settingFile>",
							"      You can specify a file that has no content.",
							"      PARIS will then ask for the necessary data and store it in <settingFile>.\n",
							"java paris.Paris <kb1> <kb2> <outputFolder>",
							"      Aligns <kb1> and <kb2>, puts the results into <outputFolder>.\n",
							"java paris.Paris <factstore> <dump>",
							"      Dumps all entities of <factstore> to the file <dump>\n",
							"See http://webdam.inria.fr/paris/ for further information.");
			System.exit(1);
		}
		
		if (args.length == 2) {
	    setting = new Setting("", "", "", "", "", "", null);
			dumpFactStoreEntities(new File(args[0]), args[1]);
			System.exit(0);
		}

		Announce.doing("Starting PARIS");
		if (args.length == 3) {
			Announce.message("Settings specified on command line");
	    setting = new Setting("", ".", args[0], args[1], null, args[2], null);
		} else {
			Announce.message("Settings:", args[0]);
			setting = new Setting(new File(args[0]));			
		}

		// Prepare the folders
		if (!setting.tsvFolder.exists()
				&& D.readBoolean("Do you want to create the folder "
						+ setting.tsvFolder + "?"))
			setting.tsvFolder.mkdirs();
		for (File folder : new File[] { /* setting.berkeleyFolder, */setting.tsvFolder }) {
			if (folder.list().length > 0
					&& D.readBoolean("Do you want to DELETE the files in " + folder
							+ " ?")) {
				Announce.doing("Deleting files in", folder);
				for (File f : folder.listFiles())
					f.delete();
				Announce.done();
			}
		}

		// Set output to the log folder
		Announce.done();
		File logFile = new File(setting.home, "run_" + setting.name + "_"
				+ NumberFormatter.timeStamp() + ".txt");
		Announce.message("PARIS is now running!");
		Announce
				.message("For information about the current state of affairs, look into");
		Announce.message("   ", logFile);
		if (!test)
			Announce.setWriter(new FileWriter(logFile)); /**/

		Announce.message("PARIS running at", NumberFormatter.ISOtime());
		Announce.message("@TIME", "startup", System.currentTimeMillis() / 1000L);
		Config.print();

		long startTime = System.currentTimeMillis();

		Announce.doing("Loading fact stores (could take a long time...)");
		factStore1 = loadFactStore(setting.ontology1, "", "");
		factStore2 = loadFactStore(setting.ontology2, "", "");
		Announce.done();			

		Runtime.getRuntime().gc();
		Announce.message("Total memory used now that fact stores are loaded:",
				(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1000000, "megabytes");
		
		assert(factStore1.getJoinLengthLimit() > 0);
		assert(factStore2.getJoinLengthLimit() > 0);
		Announce.done();

		Announce.message("@TIME", "loaded", System.currentTimeMillis() / 1000L);
		computed = new Result(setting, factStore1, factStore2, setting.tsvFolder);

		if (debug)
			Announce.setLevel(Level.DEBUG);
		if (test) {
			runTest();
		}
		Announce.message("Factstores loaded at", NumberFormatter.ISOtime());
		for (int i = 0; i < setting.endIteration; i++) {
			Announce.message("@TIME", i+1, System.currentTimeMillis() / 1000L);
			// note that we don't check anymore if something has changed...
			oneIteration(i);
		}
		Announce.message("@TIME", setting.endIteration + 1, System.currentTimeMillis() / 1000L);
		computed.startIteration(setting.endIteration);
		if (Config.doComputeClasses) {
			Announce.message("computing classes at", NumberFormatter.ISOtime());
			computeClassesOneWay(factStore1, factStore2);
			computeClassesOneWay(factStore2, factStore1);
			Announce.message("computed classes at", NumberFormatter.ISOtime());
		}
		Announce.message("@TIME", "classes", System.currentTimeMillis() / 1000L);
		computed.print();
		computed.close();
		System.out.printf("PARIS terminated after %d milliseconds\n",
				System.currentTimeMillis() - startTime);
		Announce.message("@TIME", "shutdown", System.currentTimeMillis() / 1000L);
		Announce.close();
	}

	private static void dumpFactStoreEntities(File in, String out)
			throws IOException, ClassNotFoundException {
		Announce.doing("loading fact store");
		factStore1 = loadFactStore(in, "", "");
		Announce.done();
		Announce.doing("dumping entities");
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		for (int entity : factStore1.properEntities()) {
				w.write(factStore1.entity(entity).toString() + "\n");
		}
		w.close();
		Announce.done();
	}

	// ************ If you want to play around, do it in the following {} !
	public static void runTest() throws IOException {
		computed.startIteration(99);
		Announce.setLevel(Level.DEBUG);
		debug = true;
		// test code goes here
		D.p(factStore1.factsAbout("Zhao_Ziyang"));
		D.p(factStore2.factsAbout("p1357789"));
		computed.close();
		D.exit();
	}

}
