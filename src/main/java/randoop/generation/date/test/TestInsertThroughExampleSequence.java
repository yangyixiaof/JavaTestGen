package randoop.generation.date.test;

import org.junit.Test;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.types.*;

import java.util.*;

public class TestInsertThroughExampleSequence {

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
  public void test1() throws NoSuchMethodException, SecurityException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence seq = sg.GenerateExampleSequence();
    System.err.println("===== original sequence =====");
    System.err.println(seq.toLongFormString());
    Set<TypedOperation> candidates = new HashSet<TypedOperation>();
    GenerateTypedOperations(candidates);
    MutationAnalyzer analyzer = new MutationAnalyzer(seq);
    List<MutationOperation> all_mutates = new ArrayList<MutationOperation>();
    analyzer.GenerateInsertOperations(all_mutates, candidates);
    // apply mutation
    Iterator<MutationOperation> a_itr = all_mutates.iterator();
    while (a_itr.hasNext()) {
      MutationOperation mutate_optr = a_itr.next();
      TraceableSequence new_seq = mutate_optr.ApplyMutation();
      System.err.println("===== mutation: =====");
      System.err.println(mutate_optr);
      System.err.println("===== new sequence =====");
      System.err.println(new_seq.toLongFormString());
    }
  }

  public static void main(String[] args) {
    TestInsertThroughExampleSequence tites = new TestInsertThroughExampleSequence();
    try {
      tites.test1();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
  }
}
