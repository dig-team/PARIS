package paris.shingling;

import java.io.Serializable;

import bak.pcj.list.ByteArrayList;
import bak.pcj.list.IntArrayList;

public class StringVector implements Serializable {
	private static final long serialVersionUID = 5219995193764885392L;
	private int size;
	ByteArrayList bytes=new ByteArrayList();
	IntArrayList offsets=new IntArrayList();

	public int size() {
		return size;
	}

	public void add(String s) {
		offsets.add(bytes.size());
		for(byte b:s.getBytes())
			bytes.add(b);
		++size;
	}

	public String get(int i) {
		int bl=getByteLength(i);
		byte b[]=new byte[bl];
		int offset=offsets.get(i);
		for(int j=0;j<bl;++j)
			b[j]=bytes.get(offset+j);
		return new String(b);
	}

	public int getByteLength(int i)
	{
		if(i==size-1) {
			return bytes.size()-offsets.get(i);
		} else {
			return offsets.get(i+1)-offsets.get(i);
		}
	}
}
