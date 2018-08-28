package randoop.generation.date.test.function;

import randoop.types.Type;

public class FunctionTest {
	
	public FunctionTest() {
	}
	
	public static void main(String[] args) {
		System.out.println("type int assignable Byte:" + Type.forClass(int.class).isAssignableFrom(Type.forClass(Byte.class)));
		System.out.println("type int assignable Integer:" + Type.forClass(int.class).isAssignableFrom(Type.forClass(Integer.class)));
		System.out.println("type int assignable Long:" + Type.forClass(int.class).isAssignableFrom(Type.forClass(Long.class)));
		System.out.println("type Object assignable int:" + Type.forClass(Object.class).isAssignableFrom(Type.forClass(int.class)));
		System.out.println("type Object assignable Integer:" + Type.forClass(Object.class).isAssignableFrom(Type.forClass(Integer.class)));
		System.out.println("type Integer assignable Object:" + Type.forClass(Integer.class).isAssignableFrom(Type.forClass(Object.class)));
		System.out.println("int assignable Integer:" + int.class.isAssignableFrom(Integer.class));
	}
	
}
