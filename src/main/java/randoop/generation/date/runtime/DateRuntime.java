package randoop.generation.date.runtime;

import java.util.HashMap;
import java.util.Map;

import randoop.types.PrimitiveType;
import randoop.types.Type;

public class DateRuntime {

	public static Map<Type, Class<?>> booleanTypeToClass = new HashMap<>();
	public static Map<Type, Class<?>> integralTypeToClass = new HashMap<>();
	public static Map<Type, Class<?>> realTypeToClass = new HashMap<>();

	static {
		booleanTypeToClass.put(PrimitiveType.forClass(boolean.class), boolean.class);
		booleanTypeToClass.put(PrimitiveType.forClass(Boolean.class), Boolean.class);

		realTypeToClass.put(PrimitiveType.forClass(float.class), float.class);
		realTypeToClass.put(PrimitiveType.forClass(Float.class), Float.class);
		realTypeToClass.put(PrimitiveType.forClass(double.class), double.class);
		realTypeToClass.put(PrimitiveType.forClass(Double.class), Double.class);

		integralTypeToClass.put(PrimitiveType.forClass(byte.class), byte.class);
		integralTypeToClass.put(PrimitiveType.forClass(Byte.class), Byte.class);
		integralTypeToClass.put(PrimitiveType.forClass(short.class), short.class);
		integralTypeToClass.put(PrimitiveType.forClass(Short.class), Short.class);
		integralTypeToClass.put(PrimitiveType.forClass(int.class), int.class);
		integralTypeToClass.put(PrimitiveType.forClass(Integer.class), Integer.class);
		integralTypeToClass.put(PrimitiveType.forClass(long.class), long.class);
		integralTypeToClass.put(PrimitiveType.forClass(Long.class), Long.class);
		integralTypeToClass.put(PrimitiveType.forClass(char.class), char.class);
		integralTypeToClass.put(PrimitiveType.forClass(Character.class), Character.class);
	}

	// integral values (including char)
	public static int add(int x, Object delta) {
		return x + (int) delta;
	}

	public static Integer add(Integer x, Object delta) {
		return x + (Integer) delta;
	}

	public static long add(long x, Object delta) {
		return x + (long) delta;
	}

	public static Long add(Long x, Object delta) {
		return x + (Long) delta;
	}

	public static short add(short x, Object delta) {
		return (short) (x + (int) delta);
	}

	public static Short add(Short x, Object delta) {
		return (short) (x + (int) delta);
	}

	public static byte add(byte x, Object delta) {
		return (byte) (x + (int) delta);
	}

	public static Byte add(Byte x, Object delta) {
		return (byte) (x + (int) delta);
	}

	public static char add(char x, Object delta) {
		return (char) (x + (int) delta);
	}

	public static Character add(Character x, Object delta) {
		return (char) (x + (int) delta);
	}

	// real values
	public static float add(float x, Object delta) {
		return x + (float) delta;
	}

	public static double add(double x, Object delta) {
		return x + (double) delta;
	}

	public static Float add(Float x, Object delta) {
		return x + (Float) delta;
	}

	public static Double add(Double x, Object delta) {
		return x + (Double) delta;
	}

	// boolean values
	public static boolean not(boolean b) {
		return !b;
	}

	public static Boolean not(Boolean b) {
		return !b;
	}

	// string values
	public static String insert(String str, int index) {
		return insert(str, index, ' ');
	}

	private static String insert(String str, int index, char c) {
		char[] oldS = str.toCharArray();
		char[] newS = new char[str.length() + 1];
		System.arraycopy(oldS, 0, newS, 0, index);
		newS[index] = c;
		System.arraycopy(oldS, index, newS, index + 1, str.length() - index);
		return new String(newS);
	}

	public static String remove(String str, int index) {
		char[] oldS = str.toCharArray();
		char[] newS = new char[str.length() - 1];
		System.arraycopy(oldS, 0, newS, 0, index);
		System.arraycopy(oldS, index + 1, newS, index, str.length() - index - 1);
		return new String(newS);
	}

	public static String modify(String str, int index, int delta) {
		char[] arr = str.toCharArray();
		arr[index] += delta;
		return new String(arr);
	}
}
