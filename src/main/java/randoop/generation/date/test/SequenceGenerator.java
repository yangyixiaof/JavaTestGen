package randoop.generation.date.test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import cn.yyx.labtask.test_agent_trace_reader.TraceInfo;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.types.InstantiatedType;
import randoop.types.JDKTypes;
import randoop.types.JavaTypes;
import randoop.types.PrimitiveTypes;
import randoop.types.ReferenceType;
import randoop.types.Substitution;
import randoop.types.Type;

public class SequenceGenerator {

	public TraceableSequence GenerateExampleSequence() throws NoSuchMethodException, SecurityException {
		InstantiatedType linkedListType = JDKTypes.LINKED_LIST_TYPE.instantiate(JavaTypes.STRING_TYPE);
		Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
		TypedOperation newLL = TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

		TypedOperation newOb = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");

		TypedOperation addFirst = TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
				.apply(substLL);

		TypedOperation size = TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

		InstantiatedType treeSetType = JDKTypes.TREE_SET_TYPE.instantiate(JavaTypes.STRING_TYPE);
		Substitution<ReferenceType> substTS = treeSetType.getTypeSubstitution();
		TypedOperation wcTS = TypedOperation.forConstructor(TreeSet.class.getConstructor(Collection.class))
				.apply(substTS).applyCaptureConversion();
		Substitution<ReferenceType> substWC = Substitution.forArgs(wcTS.getTypeParameters(),
				(ReferenceType) JavaTypes.STRING_TYPE);
		TypedOperation newTS = wcTS.apply(substWC);

		TypedOperation syncA = TypedOperation.forMethod(Collections.class.getMethod("synchronizedSet", Set.class));
		Substitution<ReferenceType> substA = Substitution.forArgs(syncA.getTypeParameters(),
				(ReferenceType) JavaTypes.STRING_TYPE);
		TypedOperation syncS = syncA.apply(substA);

		TraceableSequence s = new TraceableSequence();
		s = new TraceableSequence(s.extend(newLL).statements, s, null);
		s = new TraceableSequence(s.extend(newOb).statements, s, null);
		s = new TraceableSequence(s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, s, null);
		s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, s, null);
		s = new TraceableSequence(s.extend(newTS, s.getVariable(0)).statements, s, null);
		s = new TraceableSequence(s.extend(syncS, s.getVariable(4)).statements, s, null);
		return s;
	}

	public TraceableSequence GenerateExampleSequenceForModifyBoolean() throws NoSuchMethodException, SecurityException {
		InstantiatedType linkedListType = JDKTypes.LINKED_LIST_TYPE.instantiate(ReferenceType.forClass(Boolean.class));
		Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
		TypedOperation newLL = TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

		TypedOperation newOb = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);

		TypedOperation addFirst = TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
				.apply(substLL);

		TypedOperation size = TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

		InstantiatedType treeSetType = JDKTypes.TREE_SET_TYPE.instantiate(ReferenceType.forClass(Boolean.class));
		Substitution<ReferenceType> substTS = treeSetType.getTypeSubstitution();
		TypedOperation wcTS = TypedOperation.forConstructor(TreeSet.class.getConstructor(Collection.class))
				.apply(substTS).applyCaptureConversion();
		Substitution<ReferenceType> substWC = Substitution.forArgs(wcTS.getTypeParameters(),
				ReferenceType.forClass(Boolean.class));
		TypedOperation newTS = wcTS.apply(substWC);

		TypedOperation syncA = TypedOperation.forMethod(Collections.class.getMethod("synchronizedSet", Set.class));
		Substitution<ReferenceType> substA = Substitution.forArgs(syncA.getTypeParameters(),
				ReferenceType.forClass(Boolean.class));
		TypedOperation syncS = syncA.apply(substA);

		TraceableSequence s = new TraceableSequence();
		s = new TraceableSequence(s.extend(newLL).statements, s, null);
		s = new TraceableSequence(s.extend(newOb).statements, s, null);
		s = new TraceableSequence(s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, s, null);
		s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, s, null);
		s = new TraceableSequence(s.extend(newTS, s.getVariable(0)).statements, s, null);
		s = new TraceableSequence(s.extend(syncS, s.getVariable(4)).statements, s, null);
		return s;
	}

	public TraceableSequence GenerateShortExampleSequenceForModifyBoolean(boolean boxed)
			throws NoSuchMethodException, SecurityException {
		InstantiatedType linkedListType = JDKTypes.LINKED_LIST_TYPE.instantiate(ReferenceType.forClass(Boolean.class));
		Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
		TypedOperation newLL = TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

		TypedOperation newOb;
		if (!boxed) {
			newOb = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);

		} else {
			newOb = TypedOperation.createPrimitiveInitialization(ReferenceType.forClass(Boolean.class), true);
		}

		TypedOperation addFirst = TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
				.apply(substLL);

		TypedOperation size = TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

		TraceableSequence s = new TraceableSequence();
		s = new TraceableSequence(s.extend(newLL).statements, s, null);
		s = new TraceableSequence(s.extend(newOb).statements, s, null);
		s = new TraceableSequence(s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, s, null);
		s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, s, null);

		return s;
	}

	public TraceableSequence GenerateShortExampleSequenceForModifyNumber(Class<?> type, Object initValue)
			throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<?> ensureBoxedType = type.isPrimitive() ? PrimitiveTypes.primitiveToBoxed.get(type) : type;

		InstantiatedType linkedListType = JDKTypes.LINKED_LIST_TYPE
				.instantiate(ReferenceType.forClass(ensureBoxedType));
		Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
		TypedOperation newLL = TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

		TypedOperation newOb;
		newOb = TypedOperation.createPrimitiveInitialization(Type.forClass(type), initValue);

		TypedOperation addFirst = TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
				.apply(substLL);

		TypedOperation size = TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

		TraceableSequence s = new TraceableSequence();
		s = new TraceableSequence(s.extend(newLL).statements, s, null);
		s = new TraceableSequence(s.extend(newOb).statements, s, null);
		s = new TraceableSequence(s.extend(addFirst, s.getVariable(0), s.getVariable(1)).statements, s, null);
		s = new TraceableSequence(s.extend(size, s.getVariable(0)).statements, s, null);

		return s;
	}

	public static TraceableSequence GenerateTraceTestExampleSequence() {
		TraceableSequence ts = null;
		try {
			TypedOperation test_trace_simple_branch_invoke = TypedOperation
					.forMethod(StaticSimpleBranchFile.class.getMethod("TestStaticSimpleBranch"));
			Sequence s = new Sequence();
			ts = new TraceableSequence(s.extend(test_trace_simple_branch_invoke).statements, new TraceableSequence(s),
					null);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		TraceInfo ti = new TraceInfo();
		ts.SetExecutionTrace(ti);
		return ts;
	}

}
