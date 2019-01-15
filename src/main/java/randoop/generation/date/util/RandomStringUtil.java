package randoop.generation.date.util;

import java.util.Random;

public class RandomStringUtil {
	
	public static final String SOURCES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
	private static Random random = new Random();

	/**
	 * Generate a random string.
	 *
	 * @param random
	 *            the random number generator.
	 * @param characters
	 *            the characters for generating string.
	 * @param length
	 *            the length of the generated string.
	 * @return
	 */
	public static String GenerateString(String characters, int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(random.nextInt(characters.length()));
		}
		return new String(text);
	}
	
	public static String GenerateStringByDefaultChars(int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = SOURCES.charAt(random.nextInt(SOURCES.length()));
		}
		return new String(text);
	}

	public static void main(String[] args) {
        System.out.println(RandomStringUtil.GenerateStringByDefaultChars(10));
        System.out.println(RandomStringUtil.GenerateStringByDefaultChars(10));
        System.out.println(RandomStringUtil.GenerateStringByDefaultChars(15));
        System.out.println(RandomStringUtil.GenerateStringByDefaultChars(15));
    }
	
}
