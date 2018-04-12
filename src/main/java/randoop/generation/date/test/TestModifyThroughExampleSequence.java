package randoop.generation.date.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.sequence.Sequence;

public class TestModifyThroughExampleSequence {

  @Test
  public void test1() throws NoSuchMethodException, SecurityException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence seq = sg.GenerateExampleSequence();
    MutationAnalyzer analyzer = new MutationAnalyzer(seq);
    List<MutationOperation> all_mutates = new ArrayList<MutationOperation>();
    analyzer.GenerateModifyOperations(all_mutates);
    // apply mutation
    Iterator<MutationOperation> a_itr = all_mutates.iterator();
    while (a_itr.hasNext()) {
      MutationOperation mutate_optr = a_itr.next();
      //      System.out.println(mutate_optr);
      Sequence new_seq = mutate_optr.ApplyMutation();
      System.err.println(new_seq);
    }
  }

  public static void main(String[] args) {
    TestModifyThroughExampleSequence trtes = new TestModifyThroughExampleSequence();
    try {
      trtes.test1();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
  }
}
