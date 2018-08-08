package randoop.generation.date.test;

import org.junit.Assert;
import org.junit.Test;

import randoop.generation.date.sequence.LinkedSequence;
import randoop.operation.NonreceiverTerm;
import randoop.operation.TypedOperation;
import randoop.operation.TypedTermOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Variable;
import randoop.types.Type;
import randoop.types.TypeTuple;

/** */
public class MutationOperationLeeTest {

	/**
	 * @param value
	 * @return
	 */
	public static TypedOperation intVarDeclAndInit(Integer value) {
		TypeTuple inputTypes = new TypeTuple();
		Type outputType = Type.forClass(Integer.class);
		return new TypedTermOperation(new NonreceiverTerm(outputType, value), inputTypes, outputType);
	}

	// public static TypedOperation methodCall(Type outputType, Type...
	// inputTypesArray) {
	// TypeTuple inputTypes = new TypeTuple(Arrays.asList(inputTypesArray));
	// return
	// }

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

	// @Test
	// public void test1() {
	// try {
	//
	// SimpleArrayList<Statement> statements = new SimpleArrayList<>();
	//
	// Statement s0 = new Statement(intVarDeclAndInit(42));
	// statements.add(s0);
	//
	// System.out.println(new Sequence(statements));
	//
	// Statement s1 = new
	// Statement(TypedOperation.forConstructor(TT.class.getConstructors()[0]));
	// statements.add(s1);
	//
	// System.out.println(new Sequence(statements));
	//
	// List<Sequence.RelativeNegativeIndex> args2 =
	// Arrays.asList(
	// new Sequence.RelativeNegativeIndex(-1), new
	// Sequence.RelativeNegativeIndex(-2));
	// Statement s2 = null;
	//
	// s2 = new Statement(TypedOperation.forMethod(TT.class.getMethod("f",
	// Integer.class)),
	// args2);
	//
	// statements.add(s2);
	//
	// System.out.println(new Sequence(statements));
	//
	// TraceableSequence before = new TraceableSequence(statements, null, null);
	//
	// List<Variable> args3ToInsert =
	// Arrays.asList(new Variable(before, 1), new Variable(before, 0), new
	// Variable(before,
	// 0));
	// TraceableSequence after = null;
	// after =
	// before.insert(
	// 2,
	// TypedOperation.forMethod(TT.class.getMethod("g", Integer.class, int.class)),
	// args3ToInsert);
	//
	// System.out.println(after.toCodeString());
	//
	// } catch (NoSuchMethodException e) {
	// e.printStackTrace();
	// }
	// }

	@Test
	public void testRemoveBoundary1() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();
		try {
			LinkedSequence after = before.remove(null, -1);
			System.err.println(after);
		} catch (IllegalArgumentException iae) {
			Assert.assertTrue(true);
			return;
		}
		Assert.fail();
	}

	@Test
	public void testRemoveBoundary2() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();
		try {
			LinkedSequence after = before.remove(null, before.size());
			System.err.println(after);
		} catch (IllegalArgumentException iae) {
			Assert.assertTrue(true);
			return;
		}
		Assert.fail();
	}

	@Test
	public void testRemoveFunction1() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();

		System.out.println("yyx:");
		System.out.println("before size: " + before.size());
		before.disableShortForm();
		System.out.println(before);

		for (int toRemove = before.size() - 1; toRemove >= 0; toRemove--) {
			LinkedSequence after = before.remove(null, toRemove);
			System.out.printf("after remove(%d), size: %d\n", toRemove, after.size());
			after.disableShortForm();
			System.out.println(after);
		}
	}

	@Test
	public void testInsert1() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();

		System.out.println("yyx:");
		System.out.println("before size: " + before.size());
		System.out.println(before.toLongFormString());

		for (int toRemove = before.size() - 1; toRemove >= 0; toRemove--) {
			LinkedSequence after = before.remove(null, toRemove);
			System.out.printf("after remove(%d), size: %d\n", toRemove, after.size());
			System.out.println(after.toLongFormString());
		}
	}

	@Test
	public void testModifyReference1() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequenceForModifyBoolean();

		before.disableShortForm();
		System.out.println(before);
		System.out.println();

		LinkedSequence afterInsert = before.insert(null, 2, TypedOperation
				.createNonreceiverInitialization(new NonreceiverTerm(Type.forClass(Boolean.class), false)));
		afterInsert.disableShortForm();
		System.out.println(afterInsert);
		System.out.println();

		LinkedSequence afterModify = afterInsert.modifyReference(null, 3, 1, new Variable(afterInsert, 2));
		afterModify.disableShortForm();
		System.out.println(afterModify);

		Assert.assertEquals(new Sequence.RelativeNegativeIndex(-1), afterModify.getStatement(3).getInputs().get(1));
	}

	@Test
	public void testModifyBooleanUnboxed() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyBoolean(false);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyBoolean(null, 2, 1);
		after.disableShortForm();
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Boolean> booleanList0 = new java.util.LinkedList<java.lang.Boolean>();\n"
						+ "boolean boolean1 = true;\n"
						+ "boolean boolean2 = randoop.generation.date.runtime.DateRuntime.not(boolean1);\n"
						+ "booleanList0.addFirst((java.lang.Boolean)boolean2);\n" + "int int4 = booleanList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyBooleanBoxed() throws NoSuchMethodException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyBoolean(true);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyBoolean(null, 2, 1);
		after.disableShortForm();
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Boolean> booleanList0 = new java.util.LinkedList<java.lang.Boolean>();\n"
						+ "java.lang.Boolean boolean1 = true;\n"
						+ "java.lang.Boolean boolean2 = randoop.generation.date.runtime.DateRuntime.not(boolean1);\n"
						+ "booleanList0.addFirst(boolean2);\n" + "int int4 = booleanList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyRealfloat() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(float.class, 1.2f);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyReal(null, 2, 1, -0.1f);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Float> floatList0 = new java.util.LinkedList<java.lang.Float>();\n"
						+ "float float1 = 1.2f;\n" + "float float2 = (-0.1f);\n"
						+ "float float3 = randoop.generation.date.runtime.DateRuntime.add(float1, (java.lang.Object)float2);\n"
						+ "floatList0.addFirst((java.lang.Float)float3);\n" + "int int5 = floatList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyRealFloat() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Float.class, 1.2f);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyReal(null, 2, 1, -0.1f);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Float> floatList0 = new java.util.LinkedList<java.lang.Float>();\n"
						+ "java.lang.Float float1 = 1.2f;\n" + "java.lang.Float float2 = (-0.1f);\n"
						+ "java.lang.Float float3 = randoop.generation.date.runtime.DateRuntime.add(float1, (java.lang.Object)float2);\n"
						+ "floatList0.addFirst(float3);\n" + "int int5 = floatList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyRealdouble() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(double.class, 1.2);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyReal(null, 2, 1, -0.1);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Double> doubleList0 = new java.util.LinkedList<java.lang.Double>();\n"
						+ "double double1 = 1.2d;\n" + "double double2 = (-0.1d);\n"
						+ "double double3 = randoop.generation.date.runtime.DateRuntime.add(double1, (java.lang.Object)double2);\n"
						+ "doubleList0.addFirst((java.lang.Double)double3);\n" + "int int5 = doubleList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyRealDouble() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Double.class, 1.2);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyReal(null, 2, 1, -0.1);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Double> doubleList0 = new java.util.LinkedList<java.lang.Double>();\n"
						+ "java.lang.Double double1 = 1.2d;\n" + "java.lang.Double double2 = (-0.1d);\n"
						+ "java.lang.Double double3 = randoop.generation.date.runtime.DateRuntime.add(double1, (java.lang.Object)double2);\n"
						+ "doubleList0.addFirst(double3);\n" + "int int5 = doubleList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralint() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(int.class, 42);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, -233);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Integer> intList0 = new java.util.LinkedList<java.lang.Integer>();\n"
						+ "int int1 = 42;\n" + "int int2 = (-233);\n"
						+ "int int3 = randoop.generation.date.runtime.DateRuntime.add(int1, (java.lang.Object)int2);\n"
						+ "intList0.addFirst((java.lang.Integer)int3);\n" + "int int5 = intList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralInteger() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Integer.class, 42);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, -233);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Integer> intList0 = new java.util.LinkedList<java.lang.Integer>();\n"
						+ "java.lang.Integer int1 = 42;\n" + "java.lang.Integer int2 = (-233);\n"
						+ "java.lang.Integer int3 = randoop.generation.date.runtime.DateRuntime.add(int1, (java.lang.Object)int2);\n"
						+ "intList0.addFirst(int3);\n" + "int int5 = intList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegrallong() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(long.class, 42L);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, -233L);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Long> longList0 = new java.util.LinkedList<java.lang.Long>();\n"
						+ "long long1 = 42L;\n" + "long long2 = (-233L);\n"
						+ "long long3 = randoop.generation.date.runtime.DateRuntime.add(long1, (java.lang.Object)long2);\n"
						+ "longList0.addFirst((java.lang.Long)long3);\n" + "int int5 = longList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralLong() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Long.class, 42L);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, -233L);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Long> longList0 = new java.util.LinkedList<java.lang.Long>();\n"
						+ "java.lang.Long long1 = 42L;\n" + "java.lang.Long long2 = (-233L);\n"
						+ "java.lang.Long long3 = randoop.generation.date.runtime.DateRuntime.add(long1, (java.lang.Object)long2);\n"
						+ "longList0.addFirst(long3);\n" + "int int5 = longList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralshort() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(short.class, (short) 42);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, (short) -233);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Short> shortList0 = new java.util.LinkedList<java.lang.Short>();\n"
						+ "short short1 = (short)42;\n" + "short short2 = (short)-233;\n"
						+ "short short3 = randoop.generation.date.runtime.DateRuntime.add(short1, (java.lang.Object)short2);\n"
						+ "shortList0.addFirst((java.lang.Short)short3);\n" + "int int5 = shortList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralShort() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Short.class, (short) 42);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, (short) -233);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Short> shortList0 = new java.util.LinkedList<java.lang.Short>();\n"
						+ "java.lang.Short short1 = (short)42;\n" + "java.lang.Short short2 = (short)-233;\n"
						+ "java.lang.Short short3 = randoop.generation.date.runtime.DateRuntime.add(short1, (java.lang.Object)short2);\n"
						+ "shortList0.addFirst(short3);\n" + "int int5 = shortList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralbyte() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(byte.class, (byte) 42);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, (byte) -23);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Byte> byteList0 = new java.util.LinkedList<java.lang.Byte>();\n"
						+ "byte byte1 = (byte)42;\n" + "byte byte2 = (byte)-23;\n"
						+ "byte byte3 = randoop.generation.date.runtime.DateRuntime.add(byte1, (java.lang.Object)byte2);\n"
						+ "byteList0.addFirst((java.lang.Byte)byte3);\n" + "int int5 = byteList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralByte() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Byte.class, (byte) 42L);

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, (byte) -23L);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Byte> byteList0 = new java.util.LinkedList<java.lang.Byte>();\n"
						+ "java.lang.Byte byte1 = (byte)42;\n" + "java.lang.Byte byte2 = (byte)-23;\n"
						+ "java.lang.Byte byte3 = randoop.generation.date.runtime.DateRuntime.add(byte1, (java.lang.Object)byte2);\n"
						+ "byteList0.addFirst(byte3);\n" + "int int5 = byteList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralchar() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(char.class, 'c');

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, (char) 5);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Character> charList0 = new java.util.LinkedList<java.lang.Character>();\n"
						+ "char char1 = 'c';\n" + "char char2 = '\\u0005';\n"
						+ "char char3 = randoop.generation.date.runtime.DateRuntime.add(char1, (java.lang.Object)char2);\n"
						+ "charList0.addFirst((java.lang.Character)char3);\n" + "int int5 = charList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyIntegralCharacter() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Character.class, 'c');

		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyIntegral(null, 2, 1, (char) 5);
		System.out.println("after - shortform");
		System.out.println(after);
		after.disableShortForm();
		System.out.println("after - longform");
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.Character> charList0 = new java.util.LinkedList<java.lang.Character>();\n"
						+ "java.lang.Character char1 = 'c';\n" + "java.lang.Character char2 = '\\u0005';\n"
						+ "java.lang.Character char3 = randoop.generation.date.runtime.DateRuntime.add(char1, (java.lang.Object)char2);\n"
						+ "charList0.addFirst(char3);\n" + "int int5 = charList0.size();\n",
				after.toString());
	}

	@Test
	public void testModifyStringInsert() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();

		System.out.println("before size: " + before.size());
		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyStringInsert(null, 2, 1, 1); // "hi!" -> "h i!"
		after.disableShortForm();
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
						+ "java.lang.String str1 = \"hi!\";\n" + "int int2 = 1;\n"
						+ "java.lang.String str3 = randoop.generation.date.runtime.DateRuntime.insert(str1, int2);\n"
						+ "strList0.addFirst(str3);\n" + "int int5 = strList0.size();\n"
						+ "java.util.TreeSet<java.lang.String> strSet6 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
						+ "java.util.Set<java.lang.String> strSet7 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet6);\n",
				after.toString());
	}

	@Test
	public void testModifyStringRemove() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();

		System.out.println("before size: " + before.size());
		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyStringRemove(null, 2, 1, 1); // "hi!" -> "h!"
		after.disableShortForm();
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
						+ "java.lang.String str1 = \"hi!\";\n" + "int int2 = 1;\n"
						+ "java.lang.String str3 = randoop.generation.date.runtime.DateRuntime.remove(str1, int2);\n"
						+ "strList0.addFirst(str3);\n" + "int int5 = strList0.size();\n"
						+ "java.util.TreeSet<java.lang.String> strSet6 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
						+ "java.util.Set<java.lang.String> strSet7 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet6);\n",
				after.toString());
	}

	@Test
	public void testModifyStringModify() throws NoSuchMethodException, ClassNotFoundException {
		SequenceGenerator sg = new SequenceGenerator();
		LinkedSequence before = sg.GenerateExampleSequence();

		System.out.println("before size: " + before.size());
		before.disableShortForm();
		System.out.println(before);

		LinkedSequence after = before.modifyStringModify(null, 2, 1, 1, 10); // "hi!" -> "hs!"
		after.disableShortForm();
		System.out.println(after);

		Assert.assertEquals(
				"java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
						+ "java.lang.String str1 = \"hi!\";\n" + "int int2 = 1;\n" + "int int3 = 10;\n"
						+ "java.lang.String str4 = randoop.generation.date.runtime.DateRuntime.modify(str1, int2, int3);\n"
						+ "strList0.addFirst(str4);\n" + "int int6 = strList0.size();\n"
						+ "java.util.TreeSet<java.lang.String> strSet7 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
						+ "java.util.Set<java.lang.String> strSet8 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet7);\n",
				after.toString());
	}
}
