package randoop.generation.date.runtime;

import java.util.Random;

public class DateRuntimeSupport {

	private static Random random = new Random();
	
	public static void ExampleTest() {
		int i=0;
		if (i < 100) {
			System.out.println("Heihei, executed!");
		}
	}

	public static Boolean CreateBoolean() {
		return random.nextBoolean();
	}

	public static Boolean not(Boolean b) {
		return !b;
	}

	public static Character CreateCharacter() {
		return getRandomCharacter('\u0000','\uFFFF');
	}

	public static Character add(Character x, Object delta) {
		return (char) (x + (int) delta);
	}
	
	public static Byte CreateByte() {
		byte[] bytes = new byte[1];
		random.nextBytes(bytes);
		return (Byte)bytes[0];
	}

	public static Byte add(Byte x, Object delta) {
		return (byte) (x + (int) delta);
	}
	
	public static Short CreateShort() {
		return (Short)(short)random.nextInt();
	}

	public static Short add(Short x, Object delta) {
		return (short) (x + (int) delta);
	}
	
	public static Integer CreateInteger() {
		return random.nextInt();
	}

	public static Integer add(Integer x, Object delta) {
		return x + (Integer) delta;
	}
	
	public static Long CreateLong() {
		return random.nextLong();
	}

	public static Long add(Long x, Object delta) {
		return x + (Long) delta;
	}
	
	public static Float CreateFloat() {
		return random.nextFloat();
	}

	public static Float add(Float x, Object delta) {
		return x + (Float) delta;
	}
	
	public static Double CreateDouble() {
		return random.nextDouble();
	}

	public static Double add(Double x, Object delta) {
		return x + (Double) delta;
	}
	
	public static String CreateString() {
		return "";
	}
	
	public static String ModifyString(String s, int index, Object delta) {
		return new StringBuilder(s).replace(index, index+1, (char)(s.charAt(index)+(int)delta) + "").toString();
	}
	
	public static String AppendString(String s) {
		return s + CreateCharacter();
	}

	private static char getRandomCharacter(char ch1, char ch2) {
		return (char) (ch1 + Math.random() * (ch2 - ch1 + 1));// 因为random<1.0，所以需要+1，才能取到ch2
	}

}
