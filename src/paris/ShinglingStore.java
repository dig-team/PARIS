package paris;

import java.util.ArrayList;
import java.util.Collection;

import javatools.administrative.Announce;
import javatools.datatypes.Pair;
import paris.storage.FactStore;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 *
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class stores an alignment between literals, computed by querying a ShinglingTable
 * */


public class ShinglingStore extends SubThingStore<Integer> {

  /** Maps an entity from the first factstore to its equality pair*/
  public int[][] indexMatch;
  public double[][] indexScore;
  Result result;
  Setting setting;
  
  protected void prepareLiteral(int i) {
	  if (!fs1.isLiteral(i))
			return;
		Collection<Pair<Object, Double>> similar = result.literalEqualToScored(fs1, fs1.entity(i));
		indexMatch[i] = new int[similar.size()];
		indexScore[i] = new double[similar.size()];
		int j = 0;
		for (Pair<Object, Double> p : similar) {
			indexMatch[i][j] = (Integer) p.first;
			assert(fs2.isLiteral(indexMatch[i][j]));
			indexScore[i][j] = p.second;
			j++;
		}
	}
		
  protected void prepareOneWay() throws InterruptedException {
  	if (setting.shinglingThreads == 1) {
	  	for (int i = 0; i < fs1.numEntities(); i++) {
	  		prepareLiteral(i);
	  	}
  	} else {
      final int[] running = new int[1];
      Collection<Integer> tids = new ArrayList<Integer>();
      for (int tid = 0; tid < setting.shinglingThreads; tid++)
      	tids.add(tid);
      for (final int tid : tids) {
        running[0]++;
        new Thread() {

          public void run() {
          	for (int i = tid; i < fs1.numEntities(); i+=setting.shinglingThreads) {
      	  		prepareLiteral(i);
      	  	}
            synchronized (running) {
              if (--running[0] == 0) running.notify();
            }
          }
        }.start();
      }
      synchronized (running) {
        running.wait();
      }
  	}
  }
  
	public ShinglingStore(FactStore fs1, FactStore fs2, Result result) throws InterruptedException {
		super(fs1, fs2);
		this.result = result;
		this.setting = fs1.setting;
		Announce.doing("Precomputing shingling results...");
		indexMatch = new int[fs1.numEntities()][];
		indexScore = new double[fs1.numEntities()][];
		prepareOneWay();
		Announce.done();
	}

	@Override
	public double getValue(Integer sub, Integer supr) {
		// Auto-generated method stub
		assert(false);
		return 0;
	}

	@Override
	protected void set(Integer sub, Integer supr, double val) {
		// Auto-generated method stub
		assert(false);
	}

	@Override
	public Iterable<paris.SubThingStore.SubPair<Integer>> all() {
  	ArrayList<SubPair<Integer>> result = new ArrayList<SubPair<Integer>>();

    for (int i = 0; i < indexMatch.length; i++) {
    	if (indexMatch[i] == null)
    		continue;
    	assert(fs1.isLiteral(i));
    	for (int j = 0; j < indexMatch[i].length; j++) {
    		result.add(new SubPair<Integer>(i, indexMatch[i][j], indexScore[i][j]));
    	}
    }
    return result;
  }

	@Override
	public String toTsv(SubPair<Integer> p) {
		return fs1.entity(p.sub)+"\t"+fs2.entity(p.supr)+"\t"+p.val+"\n";
	}


}
