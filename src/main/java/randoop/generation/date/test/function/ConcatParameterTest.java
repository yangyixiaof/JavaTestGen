package randoop.generation.date.test.function;

import org.apache.commons.lang3.ArrayUtils;

public class ConcatParameterTest {
	
	public static void HHC(String... ss) {
		for (String s : ss) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
		String[] s1 = new String[] {"asd", "ncx"};
		String s2 = "lk";
		String[] both = ArrayUtils.addAll(s1, s2);
		ConcatParameterTest.HHC(both);
	}
	
}
