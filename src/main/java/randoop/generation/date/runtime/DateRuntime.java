package randoop.generation.date.runtime;

import randoop.types.PrimitiveType;
import randoop.types.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成的测试用例执行时需要依赖 DateRuntime。
 *
 * <p>按照约定，本类需要写在 randoop.generation.date 及其子包内。
 *
 * <p>因为 Mutation 分析时 ({@link randoop.generation.date.mutation.MutationAnalyzer})，跳过
 * randoop.generation.date 开头的类的方法调用。不会被 remove 或 modify。
 */
public class DateRuntime {

  // 啊 似乎可以 Type#getRuntimeClass() ...... TODO
  // 利用了 1. 取值 2. 用 Map#getOrDefault 来判定 membership
  public static Map<Type, Class<?>> booleanTypeToClass = new HashMap<>();
  public static Map<Type, Class<?>> integralTypeToClass = new HashMap<>();
  public static Map<Type, Class<?>> realTypeToClass = new HashMap<>();

  static {
    // TODO 在 modifyBoolean 用起
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
    // 字符也算 integral 先。
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

  /**
   * 在 str 的 index 处插入一个半角空格字符，其他字符顺延。
   *
   * <p>返回新字符串，不影响参数 str（也没法影响）。
   *
   * <p>TODO 参数不合法时返回什么？看需求
   *
   * <p>是否允许插到 length() 处？（相当于 extend，蛤
   *
   * @param str
   * @param index
   * @return
   */
  public static String insert(String str, int index) {
    return insert(str, index, ' ');
  }

  /**
   * 这个先不暴露。
   *
   * @param str
   * @param index
   * @param c
   * @return
   */
  private static String insert(String str, int index, char c) {
    char[] oldS = str.toCharArray();
    char[] newS = new char[str.length() + 1];
    System.arraycopy(oldS, 0, newS, 0, index); // [0,index-1]长度为index
    newS[index] = c; // 插入的
    System.arraycopy(oldS, index, newS, index + 1, str.length() - index); // 剩下的
    return new String(newS);
  }

  /**
   * 移除 str 在 index 处的字符。其他字符向前补上。
   *
   * <p>返回新字符串，不影响参数 str（也没法影响）。
   *
   * @param str
   * @param index
   * @return
   */
  public static String remove(String str, int index) {
    char[] oldS = str.toCharArray();
    char[] newS = new char[str.length() - 1];
    System.arraycopy(oldS, 0, newS, 0, index); // [0,index-1]长度为index
    System.arraycopy(oldS, index + 1, newS, index, str.length() - index - 1); // 跳过index处，剩下的
    return new String(newS);
  }

  /**
   * 把 str 在 index 处的字符 x，变成新字符 y，其中 y 的编码比 x 的编码大 delta。
   *
   * <p>TODO Java char 的字符编码…… ascii vs unicode codepoint... 非法字符检查？
   *
   * <p>返回新字符串，不影响参数 str（也没法影响）。
   *
   * @param str
   * @param index
   * @param delta
   * @return
   */
  public static String modify(String str, int index, int delta) {
    char[] arr = str.toCharArray();
    arr[index] += delta;
    return new String(arr);
  }
}
