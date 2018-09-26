package randoop.generation.date.test.function;

import java.util.ArrayList;

import randoop.types.Type;

public class ClassTest {
	
	public static void main(String[] args) {
		System.out.println(Object.class.isAssignableFrom(Integer.class));
		System.out.println(int.class.isAssignableFrom(Integer.class));
		System.out.println(Type.forClass(int.class).isAssignableFrom(Type.forClass(Integer.class)));
		System.out.println(Type.forClass(int.class).isBoxedPrimitive());
		System.out.println(Type.forClass(int.class).isPrimitive());
		System.out.println(Type.forClass(Integer.class).isBoxedPrimitive());
		System.out.println(Type.forClass(Integer.class).getRuntimeClass());
		System.out.println(int.class.isAssignableFrom(int.class));
		System.out.println(long.class.isAssignableFrom(byte.class));
		System.out.println(long.class.isAssignableFrom(Byte.class));
		System.out.println(Type.forClass(int.class).getName());
		System.out.println(Type.forClass(Integer.class).getName());
		System.out.println(Type.forClass(Integer.class).getCanonicalName());
		System.out.println(Type.forClass(ArrayList.class).getName());
	}
	
}
