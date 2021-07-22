package paris.shingling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import bak.pcj.IntIterator;
import bak.pcj.map.IntKeyIntChainedHashMap;
import bak.pcj.map.IntKeyIntMap;
import bak.pcj.map.IntKeyIntMapIterator;

/**
 * This class is part of the PARIS ontology matching project at INRIA
 * Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * 
 * by the author Mayur Garg. For all further information, see
 * 
 * http://webdam.inria.fr/paris
 */

public class ShinglingTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7160689534294511873L;
	/** random seed to ensure determinism */
	private static Random rnd = new Random(42);
	private final int noHashFunctions;
	private final int shinglingLength;
	private final int hashTableSize;

	private HashTable[] hashtables;
	private int[][] hashValues;
	private StringVector indexed;

	public ShinglingTable(int shinglingLength, int noHashFunctions,
			int hashTableSize) {
		this.noHashFunctions = noHashFunctions;
		this.shinglingLength = shinglingLength;
		this.hashTableSize = hashTableSize;
		createHashFunctions(noHashFunctions, hashTableSize);
		hashtables = new HashTable[noHashFunctions];
		for(int i=0;i<noHashFunctions;++i)
			hashtables[i]=new HashTable(hashTableSize);
		indexed = new StringVector();
	}

	public void createHashFunctions(int n, int hs) {
		hashValues = new int[n][hs];

		for (int i = 0; i < n; i++) {
			List<Integer> list = new ArrayList<Integer>();

			for (int j = 0; j < hs; j++) {
				list.add(j);
			}
			java.util.Collections.shuffle(list, rnd);
			for (int j = 0; j < hs; j++) {
				hashValues[i][j] = (list.get(j)).intValue();
			}
		}
	}

	public static double goldStandard(String a, String b, int k) {
		int res = 0;

		HashMap<Integer, Integer> hm1 = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();

		byte[] a1 = a.getBytes();
		byte[] b1 = b.getBytes();

		int l1 = a1.length;
		int l2 = b1.length;

		for (int c1 = 0; c1 < (l1 - k + 1); c1++) {

			int rep1 = 0;

			for (int iks = 0; iks < k; iks++) {
				assert(a1[c1 + iks] == (((int) (a1[c1 + iks])) % 256));
				rep1 = rep1 * 256 + a1[c1 + iks];

			}
			
			if (hm1.containsKey(rep1)) {
				hm1.put(rep1, hm1.get(rep1) + 1);
			} else {
				hm1.put(rep1, 1);
			}
		}

		for (int c1 = 0; c1 < (l2 - k + 1); c1++) {

			int rep2 = 0;

			for (int iks = 0; iks < k; iks++) {

				rep2 = rep2 * 256 + (((int) (b1[c1 + iks])) % 256);
			}
			if (hm2.containsKey(rep2)) {
				int val = hm2.get(rep2) + 1;
				hm2.put(rep2, val);

				if (hm1.containsKey(rep2)) {
					if (val <= (hm1.get(rep2))) {
						res++;
					}
				}
			} else {
				hm2.put(rep2, 1);
				if (hm1.containsKey(rep2)) {
					res++;

				}
			}
		}
		int nKgrams = Math.max(0, a1.length - k + 1) + Math.max(0, b1.length - k + 1);
		if (nKgrams > 0) {
			double result = (2. * res / nKgrams);;
			assert(result >= 0 && result <= 1);
			return result;
		} else {
			return a.equals(b) ? 1. : 0.;
		}
	}

	public void index(String strTmp) {
		indexed.add(strTmp);
		
		int min[] = new int[noHashFunctions];
		for (int i = 0; i < noHashFunctions; i++) {
			min[i] = hashTableSize - 1;
		}
		
		int[] hash = new int[noHashFunctions];

		byte ba[] = strTmp.getBytes();

		int l = ba.length;
		for (int i = 0; i < (l - shinglingLength + 1); i++) {
			int rep = 0;
			for (int j = 0; j < shinglingLength; j++) {

				int tmpry = ((int) (ba[i + j])) % 256;
				if (tmpry < 0) {
					tmpry = tmpry + 256;
				}

				rep = (rep * 256) + tmpry;
			}
			
			rep = (rep) % (hashTableSize);
			if (rep < 0) {
				rep = hashTableSize + rep;
			}

			for (int k = 0; k < noHashFunctions; k++) {
				hash[k] = (hashValues[k][rep]);
				if (hash[k] < min[k])
					min[k] = hash[k];
			}
		}

		for (int i = 0; i < noHashFunctions; i++) {
			hashtables[i].add(min[i],indexed.size()-1);
		}
	}

	public Collection<QueryResult> query(String str1Tmp, double threshold) {
		int tmpry;
		byte[] ba;

		int[] min = new int[noHashFunctions];
		int l;
		int rep;
		int cnt2;
		int c1;
		int iks;
		int cnt3;
		int cnt4;
		double score;
		double goldScore;

		int[] hash = new int[noHashFunctions];

		Comparator<QueryResult> comp = new Comparator<QueryResult>() {

			@Override
			public int compare(QueryResult arg0, QueryResult arg1) {
				if (arg0.trueScore < arg1.trueScore) {
					return 1;
				} else if (arg0.trueScore > arg1.trueScore) {
					return -1;
				} else {
					return 0;
				}
			};
		};

		List<QueryResult> resultList = new ArrayList<QueryResult>();
		QueryResult res;

		ba = str1Tmp.getBytes();

		for (cnt2 = 0; cnt2 < noHashFunctions; cnt2++) {
			min[cnt2] = hashTableSize - 1;
		}

		l = ba.length;
		for (c1 = 0; c1 < (l - shinglingLength + 1); c1++) {
			rep = 0;
			for (iks = 0; iks < shinglingLength; iks++) {
				tmpry = ((int) (ba[c1 + iks])) % 256;
				if (tmpry < 0) {
					tmpry = tmpry + 256;
				}
				rep = (rep * 256) + tmpry;
			}
			rep = (rep) % (hashTableSize);
			if (rep < 0) {
				rep = hashTableSize + rep;
			}
			for (cnt3 = 0; cnt3 < noHashFunctions; cnt3++) {
				hash[cnt3] = (hashValues[cnt3][rep]);
				if (hash[cnt3] < min[cnt3])
					min[cnt3] = hash[cnt3];
			}
		}

		IntKeyIntMap hm = new IntKeyIntChainedHashMap();
		for (cnt4 = 0; cnt4 < noHashFunctions; cnt4++) {

			IntIterator i = hashtables[cnt4].get(min[cnt4]).iterator();
			while (i.hasNext()) {
				int stmp = i.next();
				hm.put(stmp,hm.get(stmp)+1);
			}
		}

		IntKeyIntMapIterator iterator = hm.entries();
		while(iterator.hasNext()) {
			iterator.next();
			score = iterator.getValue() * (1. / noHashFunctions);
			if (score >= threshold) {
				goldScore = goldStandard(str1Tmp, indexed.get(iterator.getKey()),
						shinglingLength);

				if (goldScore >= threshold) {
					res = new QueryResult(indexed.get(iterator.getKey()), score, goldScore);
					resultList.add(res);

				}

			}
		}

		Collections.sort(resultList, comp);

		return resultList;
	}

	public static void main(String args[]) {
		testMemory();
		simpleTest();
	}

	private static void testMemory() {
		long start = System.currentTimeMillis();
		Runtime.getRuntime().gc();
		double initial = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.err.println(initial / 1e6 + " MB initial memory");
		ShinglingTable st = new ShinglingTable(2, 10, 65536);
		Runtime.getRuntime().gc();
		initial = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.err.println(initial / 1e6
				+ " MB memory after hash table construction");

		final int nb_strings = 2000000;
		for (int i = 0; i < nb_strings; ++i) {
			st.index(Integer.toString(1000000000 + i));
		}

		Runtime.getRuntime().gc();
		double memory = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.err.println((memory - initial) / nb_strings
				+ " bytes per string");
		System.err.println((System.currentTimeMillis() - start) * 1. / 1000
				+ " seconds for indexing " + nb_strings + " strings");

		// So that st is not GC'ed
		st.index("");
	}

	private static void simpleTest() {
		ShinglingTable st1 = new ShinglingTable(2, 10, 65536);
		st1.index("David Copperfield");
		st1.index("Two cities");
		st1.index("The story of troy");
		st1.index("Othello");
		st1.index("Copper Field");
		st1.index("Copperfield");

		System.out.println("Query : " + "D. Copperfield");
		Iterator<QueryResult> i1 = st1.query("D. Copperfield", 0.4).iterator();

		while (i1.hasNext()) {
			QueryResult qr = i1.next();
			System.out.println(qr.result + "\t" + qr.estimatedScore + "\t"
					+ qr.trueScore);
		}

		System.out.println("Query : " + "Troy story");
		Iterator<QueryResult> i3 = st1.query("Troy story", 0.4).iterator();

		while (i3.hasNext()) {
			QueryResult qr = i3.next();
			System.out.println(qr.result + "\t" + qr.estimatedScore + "\t"
					+ qr.trueScore);
		}

		System.out.println("Query : " + "2 cities");
		Iterator<QueryResult> i2 = st1.query("2 cities", 0.4).iterator();

		while (i2.hasNext()) {
			QueryResult qr = i2.next();
			System.out.println(qr.result + "\t" + qr.estimatedScore + "\t"
					+ qr.trueScore);
		}

		System.out.println("Query : " + "The Othello");
		Iterator<QueryResult> i4 = st1.query("The Othello", 0.4).iterator();

		while (i4.hasNext()) {
			QueryResult qr = i4.next();
			System.out.println(qr.result + "\t" + qr.estimatedScore + "\t"
					+ qr.trueScore);
		}
	}
}
