package randoop.generation.date.test;

import org.junit.Assert;
import org.junit.Test;
import randoop.sequence.Sequence;

public class SequenceGeneratorTest {

  @Test
  public void test1() {
    SequenceGenerator sg = new SequenceGenerator();
    Sequence example = null;
    try {
      example = sg.GenerateExampleSequence();
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
    System.out.println("example:");
    System.out.println(example);
    Assert.assertEquals(
        example.toString(),
        ""
            + "java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
            + "strList0.addFirst(\"hi!\");\n"
            + "int int3 = strList0.size();\n"
            + "java.util.TreeSet<java.lang.String> strSet4 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
            + "java.util.Set<java.lang.String> strSet5 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet4);\n");
  }
}
