package randoop.generation.date.test;

import java.util.*;
import org.junit.Test;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.types.*;

public class TestMutationThroughExampleSequence {

  public void GenerateTypedOperations(Set<TypedOperation> candidates)
      throws NoSuchMethodException, SecurityException {
    InstantiatedType linkedListType = JDKTypes.LINKED_LIST_TYPE.instantiate(JavaTypes.STRING_TYPE);
    Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
    TypedOperation newLL =
        TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);
    candidates.add(newLL);
    TypedOperation newOb =
        TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");
    candidates.add(newOb);
    TypedOperation addFirst =
        TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
            .apply(substLL);
    candidates.add(addFirst);
    TypedOperation size =
        TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);
    candidates.add(size);
  }

  @Test
  public void test1() {
    try {
      SequenceGenerator sg = new SequenceGenerator();
      TraceableSequence seq = sg.GenerateExampleSequence();
      System.err.println("====== operating sequence begin! ======");
      System.err.println(seq);
      System.err.println("====== operating sequence end! ======");
      Set<TypedOperation> candidates = new HashSet<TypedOperation>();
      GenerateTypedOperations(candidates);
      MutationAnalyzer analyzer = new MutationAnalyzer(seq);
      System.err.println("====== generating mutation begin! ======");
      List<MutationOperation> all_mutates = new ArrayList<MutationOperation>();
      analyzer.GenerateMutationOperations(candidates, all_mutates);
      Iterator<MutationOperation> a_itr = all_mutates.iterator();
      while (a_itr.hasNext()) {
        MutationOperation mutate_optr = a_itr.next();
        Sequence new_seq = mutate_optr.ApplyMutation();
        System.err.println(
            "====== new generated sequence by mutation type:" + mutate_optr.getClass() + " ======");
        System.err.println(new_seq);
      }
      System.err.println("====== generating mutation end! ======");
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    TestMutationThroughExampleSequence tmtes = new TestMutationThroughExampleSequence();
    tmtes.test1();
  }
}
