package randoop.generation.date.runtime;

import java.io.UnsupportedEncodingException;

public class DateRuntimeSupport {

//	private static Random random = new Random();
	
	public static void ExampleTest() {
		int i=0;
		if (i < 100) {
			System.out.println("Heihei, executed!");
		}
	}

	public static Boolean CreateBoolean() {
//		return random.nextBoolean();
		return false;
	}

	public static Boolean not(Boolean b) {
		return !b;
	}

	public static Character CreateCharacter() {
		return ' ';
	}

	public static Character add(Character x, Double delta) {
		return (char) (x + delta.intValue());
	}
	
	public static Byte CreateByte() {
//		byte[] bytes = new byte[1];
//		random.nextBytes(bytes);
//		return (Byte)bytes[0];
		return 0;
	}

	public static Byte add(Byte x, Double delta) {
		return (byte) (x + delta.intValue());
	}
	
	public static Short CreateShort() {
//		return (Short)(short)random.nextInt();
		return 0;
	}

	public static Short add(Short x, Double delta) {
		return (short) (x + delta.intValue());
	}
	
	public static Integer CreateInteger() {
//		return random.nextInt();
		return 0;
	}

	public static Integer add(Integer x, Double delta) {
		return x + delta.intValue();
	}
	
	public static Long CreateLong() {
//		return random.nextLong();
		return 0L;
	}

	public static Long add(Long x, Double delta) {
		return x + delta.longValue();
	}
	
	public static Float CreateFloat() {
//		return random.nextFloat();
		return 0.0f;
	}

	public static Float add(Float x, Double delta) {
		return x + delta.floatValue();
	}
	
	public static Double CreateDouble() {
//		return random.nextDouble();
		return 0.0;
	}

	public static Double add(Double x, Double delta) {
		return x + delta.doubleValue();
	}
	
	public static String CreateString() {
		return "";
	}
	
//	public static String ModifyString(String s, int index, Object delta) {
//		return new StringBuilder(s).replace(index, index+1, (char)(s.charAt(index)+(int)delta) + "").toString();
//	}
	
	public static String ModifyString(String str, Double delta) {
		byte[] arr = str.getBytes();
		arr[arr.length-1] += delta.byteValue();
//		return getRandomCharacter('\u0000','\uFFFF');
//		if (arr[arr.length-1] < '\u0000') {
//			arr[arr.length-1] = '\u0000';
//		}
//		if (arr[arr.length-1] > '\uFFFF') {
//			arr[arr.length-1] = '\uFFFF';
//		}
		try {
			return new String(arr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static String AppendString(String s) {
		return s + CreateCharacter();
	}

//	private static char getRandomCharacter(char ch1, char ch2) {
//		return (char) (ch1 + Math.random() * (ch2 - ch1 + 1));// 因为random<1.0，所以需要+1，才能取到ch2
//	}

}
