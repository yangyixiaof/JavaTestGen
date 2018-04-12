package randoop.generation.date.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.sequence.Sequence;

public class TestRemoveThroughExampleSequence {

  @Test
  public void test1() throws NoSuchMethodException, SecurityException {
    String file_seperator = System.getProperty("line.separator");
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence seq = sg.GenerateExampleSequence();
    System.err.println(file_seperator + "original sequence:" + file_seperator + seq.toCodeString());
    MutationAnalyzer analyzer = new MutationAnalyzer(seq);
    List<MutationOperation> all_mutates = new ArrayList<MutationOperation>();
    analyzer.GenerateRemoveOperations(all_mutates);
    // apply mutation
    Iterator<MutationOperation> a_itr = all_mutates.iterator();
    while (a_itr.hasNext()) {
      MutationOperation mutate_optr = a_itr.next();
      System.err.println("mutation:" + mutate_optr.toString());
      Sequence new_seq = mutate_optr.ApplyMutation();
      System.err.println(
          file_seperator + "new sequence:" + file_seperator + new_seq.toCodeString());
    }
  }

  public static void main(String[] args) {
    TestRemoveThroughExampleSequence trtes = new TestRemoveThroughExampleSequence();
    try {
      trtes.test1();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
  }
}
