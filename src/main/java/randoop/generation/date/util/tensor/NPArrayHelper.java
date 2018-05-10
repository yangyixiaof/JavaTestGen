package randoop.generation.date.util.tensor;

public class NPArrayHelper {
	
	public static void CopyArray(int[][] source, int[][] target) {
		int length = source.length;
	    for (int i = 0; i < length; i++) {
	        System.arraycopy(source[i], 0, target[i], 0, source[i].length);
	    }
	}
	
}
