package randoop.generation.date.test.function;

import java.util.ArrayList;
import java.util.LinkedList;

import randoop.types.Type;

public class WildCardTypeTest <K> {
	
	private K k;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
//		LinkedList<Object> ll = new LinkedList<Object>();
//		LinkedList<? extends Integer> ll2 = new LinkedList<Integer>();
////		ll2 = ll;
//		System.out.println(LinkedList<Object>.class);
//		System.out.println(ll.getClass());
//		ParameterizedType pt = (ParameterizedType) ll.getClass().getGenericSuperclass();
		ArrayList al = new ArrayList();
		al.add(new Integer(1));
		TestWC(al);
		System.out.println("al.getClass():" + al.getClass());
		LinkedList ll = new LinkedList<Object>();
		System.out.println("ll:" + ll);
		Type ll_type = Type.forClass(LinkedList.class);
		System.out.println("Type.forClass(LinkedList.class):" + ll_type);
		System.out.println("ll_type.getClass():" + ll_type.getClass());
		WildCardTypeTest.haha(new String());
		WildCardTypeTest wctt = new WildCardTypeTest();
		wctt.haha2(new String());
	}
	
	public static void TestWC(ArrayList<String> al) {
		for (Object s : al) {
			System.out.println(s);
		}
	}
	
	public static <T> void haha(T t) {
		System.out.println(t);
	}
	
	public void haha2(K k2) {
		k = k2;
		System.out.println(k);
	}
	
}
