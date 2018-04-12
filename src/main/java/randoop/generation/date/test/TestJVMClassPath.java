package randoop.generation.date.test;

public class TestJVMClassPath {

  public static void main(String[] args) {
    String path = System.getProperty("java.class.path");
    System.err.println(path);
  }
}
