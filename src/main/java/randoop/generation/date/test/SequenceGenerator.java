package randoop.generation.date.test;

import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.types.*;

import java.util.*;

public class SequenceGenerator {

  public TraceableSequence GenerateExampleSequence()
      throws NoSuchMethodException, SecurityException {
    InstantiatedType linkedListType = JDKTypes.LINKED_LIST_TYPE.instantiate(JavaTypes.STRING_TYPE);
    Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
    TypedOperation newLL =
        TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

    TypedOperation newOb =
        TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");

    TypedOperation addFirst =
        TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
            .apply(substLL);

    TypedOperation size =
        TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

    InstantiatedType treeSetType = JDKTypes.TREE_SET_TYPE.instantiate(JavaTypes.STRING_TYPE);
    Substitution<ReferenceType> substTS = treeSetType.getTypeSubstitution();
    TypedOperation wcTS =
        TypedOperation.forConstructor(TreeSet.class.getConstructor(Collection.class))
            .apply(substTS)
            .applyCaptureConversion();
    Substitution<ReferenceType> substWC =
        Substitution.forArgs(wcTS.getTypeParameters(), (ReferenceType) JavaTypes.STRING_TYPE);
    TypedOperation newTS = wcTS.apply(substWC);

    TypedOperation syncA =
        TypedOperation.forMethod(Collections.class.getMethod("synchronizedSet", Set.class));
    Substitution<ReferenceType> substA =
        Substitution.forArgs(syncA.getTypeParameters(), (ReferenceType) JavaTypes.STRING_TYPE);
    TypedOperation syncS = syncA.apply(substA);

    TraceableSequence s = new TraceableSequence();
    s = new TraceableSequence(s.extend(newLL).statements, null, s);
    s = new TraceableSequence(s.extend(newOb).statements, null, s);
    s =
        new TraceableSequence(
            s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, null, s);
    s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, null, s);
    s = new TraceableSequence(s.extend(newTS, s.getVariable(0)).statements, null, s);
    s = new TraceableSequence(s.extend(syncS, s.getVariable(4)).statements, null, s);
    return s;
  }

  public TraceableSequence GenerateExampleSequenceForModifyBoolean()
      throws NoSuchMethodException, SecurityException {
    InstantiatedType linkedListType =
        JDKTypes.LINKED_LIST_TYPE.instantiate(ReferenceType.forClass(Boolean.class));
    Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
    TypedOperation newLL =
        TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

    TypedOperation newOb =
        TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);

    TypedOperation addFirst =
        TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
            .apply(substLL);

    TypedOperation size =
        TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

    InstantiatedType treeSetType =
        JDKTypes.TREE_SET_TYPE.instantiate(ReferenceType.forClass(Boolean.class));
    Substitution<ReferenceType> substTS = treeSetType.getTypeSubstitution();
    TypedOperation wcTS =
        TypedOperation.forConstructor(TreeSet.class.getConstructor(Collection.class))
            .apply(substTS)
            .applyCaptureConversion();
    Substitution<ReferenceType> substWC =
        Substitution.forArgs(wcTS.getTypeParameters(), ReferenceType.forClass(Boolean.class));
    TypedOperation newTS = wcTS.apply(substWC);

    TypedOperation syncA =
        TypedOperation.forMethod(Collections.class.getMethod("synchronizedSet", Set.class));
    Substitution<ReferenceType> substA =
        Substitution.forArgs(syncA.getTypeParameters(), ReferenceType.forClass(Boolean.class));
    TypedOperation syncS = syncA.apply(substA);

    TraceableSequence s = new TraceableSequence();
    s = new TraceableSequence(s.extend(newLL).statements, null, s);
    s = new TraceableSequence(s.extend(newOb).statements, null, s);
    s =
        new TraceableSequence(
            s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, null, s);
    s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, null, s);
    s = new TraceableSequence(s.extend(newTS, s.getVariable(0)).statements, null, s);
    s = new TraceableSequence(s.extend(syncS, s.getVariable(4)).statements, null, s);
    return s;
  }

  public TraceableSequence GenerateShortExampleSequenceForModifyBoolean(boolean boxed)
      throws NoSuchMethodException, SecurityException {
    InstantiatedType linkedListType =
        JDKTypes.LINKED_LIST_TYPE.instantiate(ReferenceType.forClass(Boolean.class));
    Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
    TypedOperation newLL =
        TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

    TypedOperation newOb;
    if (!boxed) {
      newOb = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);

    } else {
      newOb =
          TypedOperation.createPrimitiveInitialization(ReferenceType.forClass(Boolean.class), true);
    }

    TypedOperation addFirst =
        TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
            .apply(substLL);

    TypedOperation size =
        TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

    TraceableSequence s = new TraceableSequence();
    s = new TraceableSequence(s.extend(newLL).statements, null, s);
    s = new TraceableSequence(s.extend(newOb).statements, null, s);
    s =
        new TraceableSequence(
            s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, null, s);
    s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, null, s);

    return s;
  }

  public TraceableSequence GenerateShortExampleSequenceForModifyNumber(
      Class<?> type, Object initValue)
      throws NoSuchMethodException, SecurityException, ClassNotFoundException {
    Class<?> ensureBoxedType =
        type.isPrimitive() ? PrimitiveTypes.primitiveToBoxed.get(type) : type;

    InstantiatedType linkedListType =
        JDKTypes.LINKED_LIST_TYPE.instantiate(ReferenceType.forClass(ensureBoxedType));
    Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
    TypedOperation newLL =
        TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

    TypedOperation newOb;
    newOb = TypedOperation.createPrimitiveInitialization(Type.forClass(type), initValue);

    TypedOperation addFirst =
        TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
            .apply(substLL);

    TypedOperation size =
        TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

    TraceableSequence s = new TraceableSequence();
    s = new TraceableSequence(s.extend(newLL).statements, null, s);
    s = new TraceableSequence(s.extend(newOb).statements, null, s);
    s =
        new TraceableSequence(
            s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, null, s);
    s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, null, s);

    return s;
  }
}
