package randoop.generation.date.test.function;

import java.util.ArrayList;

public class WildCardTypeTest {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
//		LinkedList<Object> ll = new LinkedList<Object>();
//		LinkedList<? extends Integer> ll2 = new LinkedList<Integer>();
////		ll2 = ll;
//		System.out.println(LinkedList<Object>.class);
//		System.out.println(ll.getClass());
//		ParameterizedType pt = (ParameterizedType) ll.getClass().getGenericSuperclass();
		ArrayList al = new ArrayList<>();
		al.add(new Object());
		TestWC(al);
	}
	
	public static void TestWC(ArrayList<String> al) {
		for (Object s : al) {
			System.out.println(s);
		}
	}
	
}
