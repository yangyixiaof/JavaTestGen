package randoop.generation.date.util;

import java.util.ArrayList;

public class ArrayUtil {
	
	public static <T> void InsertAtSpecifiedPositionOfArray(ArrayList<T> seqs, T ele, int pos) {
		int null_times = pos + 1 - seqs.size();
		for (int i=0;i<null_times;i++) {
			seqs.add(null);
		}
		seqs.set(pos, ele);
	}
	
}
