package randoop.generation.date.test.function;

public class RunExceptionTest {
	
	@SuppressWarnings({ "unused", "rawtypes" })
	public static void main(String[] args) {
		java.util.LinkedList linkedList0 = new java.util.LinkedList();
		int int1 = linkedList0.size();
		Object e2 = linkedList0.peekFirst();
		System.out.println(e2);
		
		try {
			java.util.LinkedList linkedList10 = new java.util.LinkedList();
			java.lang.Object etv1 = linkedList10.element();
		} catch (Exception e) {
			System.err.println("An exception occurred!");
		}
	}
	
}
