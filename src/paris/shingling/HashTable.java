package paris.shingling;

import java.io.Serializable;

import bak.pcj.list.IntArrayList;
import bak.pcj.list.IntList;

public class HashTable implements Serializable {
	private static final long serialVersionUID = -3179275834529793369L;
	IntList[] table;
	
	public HashTable(int s) {
		table=new IntList[s];
	}

	public void add(int i, int j) {
		if(table[i]==null)
			table[i]=new IntArrayList();
		table[i].add(j);
	}

	public IntList get(int i) {
		if(table[i]==null)
			return new IntArrayList();
		else
			return table[i];
	}
}
