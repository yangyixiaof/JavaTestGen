package randoop.generation.date.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import randoop.operation.ConstructorCall;
import randoop.operation.MethodCall;
import randoop.operation.NonreceiverTerm;
import randoop.operation.TypedClassOperation;
import randoop.operation.TypedOperation;
import randoop.operation.TypedTermOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.types.ClassOrInterfaceType;
import randoop.types.RandoopTypeException;
import randoop.types.Type;
import randoop.types.TypeTuple;
import randoop.util.SimpleArrayList;

public class MutationOperationTest {

	public static TypedOperation intVarDeclAndInit(Integer value) {
		TypeTuple inputTypes = new TypeTuple();
		Type outputType = Type.forClass(Integer.class);
		return new TypedTermOperation(new NonreceiverTerm(outputType, value), inputTypes, outputType);
	}

	public static TypedClassOperation createMethodCall(Method m, ClassOrInterfaceType declaringType) {
		MethodCall op = new MethodCall(m);
		List<Type> paramTypes = new ArrayList<>();
		paramTypes.add(declaringType);
		for (java.lang.reflect.Type t : m.getGenericParameterTypes()) {
			paramTypes.add(Type.forType(t));
		}
		Type outputType = Type.forType(m.getGenericReturnType());
		return new TypedClassOperation(op, declaringType, new TypeTuple(paramTypes), outputType);
	}

	public static TypedClassOperation createConstructorCall(Constructor<?> con) throws RandoopTypeException {
		ConstructorCall op = new ConstructorCall(con);
		ClassOrInterfaceType declaringType = ClassOrInterfaceType.forClass(con.getDeclaringClass());
		List<Type> paramTypes = new ArrayList<>();
		// paramTypes.add(declaringType);
		for (java.lang.reflect.Type pc : con.getGenericParameterTypes()) {
			paramTypes.add(Type.forType(pc));
		}
		return new TypedClassOperation(op, declaringType, new TypeTuple(paramTypes), declaringType);
	}

	class TT {
		public TT() {
		}

		public boolean f(Integer i) {
			return true;
		}

		public char g(Integer i, int j) {
			return '@';
		}
	}

	@Test
	public void test1() throws NoSuchMethodException, RandoopTypeException {
		SimpleArrayList<Statement> statements = new SimpleArrayList<>();

		Statement s0 = new Statement(intVarDeclAndInit(42));
		statements.add(s0);

		Statement s1 = new Statement(createConstructorCall(TT.class.getDeclaredConstructors()[0]));

		statements.add(s1);

		// List<Sequence.RelativeNegativeIndex> args2 = Arrays
		// .asList(new Sequence.RelativeNegativeIndex(-1), new
		// Sequence.RelativeNegativeIndex(-2));
		// Statement s2 = new Statement(createMethodCall(TT.class.getMethod("f",
		// Integer.class),
		// ClassOrInterfaceType.forClass(TT.class)), args2);
		// statements.add(s2);

		Sequence before = new Sequence(statements);

		System.err.println("before:" + before);

		// List<Variable> args3ToInsert =
		// Arrays.asList(new Variable(before, 1), new Variable(before, 0), new
		// Variable(before, 0));
		// Sequence after =
		// before.insert(
		// 2,
		// createMethodCall(
		// TT.class.getMethod("g", Integer.class, int.class),
		// ClassOrInterfaceType.forClass(TT.class)),
		// args3ToInsert);
		//
		// System.err.println("after:" + after);
	}
}
