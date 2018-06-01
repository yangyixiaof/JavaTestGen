package randoop.generation.date.tensorflow.test;

import cern.colt.matrix.ObjectFactory1D;

public class TestColt {
	
	public static void main(String[] args) {
		Object[] arr = ObjectFactory1D.dense.make(2, 1).toArray();
		System.err.println("arr.length:" + arr.length);
		for (Object obj : arr) {
			System.err.println(obj);
		}
	}
	
}
