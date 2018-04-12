package randoop.generation.date.test.resource;

/**
 * https://github.com/randoop/randoop/issues/178
 *
 * @param <T>
 */
public class Outer<T> {
  public class Inner {
    public String msg() {
      return "test";
    }
  }

  //	public static void main(String[] args) {
  //		Outer<Object> outer = new Outer<>();
  //		Outer<Object>.Inner inner = outer.new Inner();
  //		System.out.println(inner.msg());
  //	}
}
