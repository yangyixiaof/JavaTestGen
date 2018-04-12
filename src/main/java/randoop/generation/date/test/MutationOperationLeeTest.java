package randoop.generation.date.test;

import org.junit.Assert;
import org.junit.Test;
import randoop.generation.date.sequence.TraceableSequence;
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
   * 工具方法，新建一个「声明并初始化 Integer」的 TypedTermOperation。
   *
   * <p>用于构造形如 Integer var0 = 1; 的语句。
   *
   * <p>TODO 肯定有轮子了吧！找找
   *
   * @param value
   * @return
   */
  public static TypedOperation intVarDeclAndInit(Integer value) {
    TypeTuple inputTypes = new TypeTuple();
    Type outputType = Type.forClass(Integer.class);
    return new TypedTermOperation(new NonreceiverTerm(outputType, value), inputTypes, outputType);
  }

  //  public static TypedOperation methodCall(Type outputType, Type... inputTypesArray) {
  //    TypeTuple inputTypes = new TypeTuple(Arrays.asList(inputTypesArray)); // 真的惊了…… [] 和 ...
  // 互转好甜
  //      return
  //  }

  //    从测试那边的 EnumReflectionTest 类里 copy...
  // 因为 主体代码 不能依赖 测试代码……（就看不到

  /** 还是写出来，用 Java 反射的常用 API 来拿类和方法好一点…… 手动构造太惊悚 */
  class TT {
    public TT() {}

    public boolean f(Integer i) {
      return true;
    }

    public char g(Integer i, int j) {
      return '@';
    }
  }

  /**
   * 单元测试，展示 1. 如何构造 Sequence 2. 如何构造 insert 操作所需的参数
   *
   * <p>噗，构造 Sequence 没写对。不写了吧 TODO
   *
   * <pre>
   * 构造一个这样的 Sequence，命名为 before
   * Integer var0 = 42;
   * TT var1 = new T();
   * Boolean var2 = var1.f(var0);
   *
   * 然后做一个 insert 操作，期望能把
   * Character var3 = var1.g(var0,var0);
   * 插到 Boolean var2... 之前，
   * 形成新 Sequence，命名为 after
   * </pre>
   */
  //  @Test
  //  public void test1() {
  //    try {
  //
  //      SimpleArrayList<Statement> statements = new SimpleArrayList<>();
  //
  //      Statement s0 = new Statement(intVarDeclAndInit(42));
  //      statements.add(s0); // add 竟然不是 SimpleList 接口规定的方法，神奇设计
  //
  //      System.out.println(new Sequence(statements));
  //
  //      // 拿到无参构造函数，构造 ConstructorCall // TODO getConstructor
  //      Statement s1 = new
  // Statement(TypedOperation.forConstructor(TT.class.getConstructors()[0]));
  //      statements.add(s1);
  //
  //      System.out.println(new Sequence(statements));
  //
  //      List<Sequence.RelativeNegativeIndex> args2 =
  //          Arrays.asList(
  //              new Sequence.RelativeNegativeIndex(-1), new Sequence.RelativeNegativeIndex(-2));
  //      Statement s2 = null; // 一开始猜的是 Type.forClass, 结果是 ClassOrInterfaceType.forClass
  //
  //      s2 = new Statement(TypedOperation.forMethod(TT.class.getMethod("f", Integer.class)),
  // args2);
  //
  //      statements.add(s2);
  //
  //      System.out.println(new Sequence(statements));
  //
  //      TraceableSequence before = new TraceableSequence(statements, null, null);
  //
  //      List<Variable> args3ToInsert =
  //          Arrays.asList(new Variable(before, 1), new Variable(before, 0), new Variable(before,
  // 0));
  //      TraceableSequence after = null;
  //      after =
  //          before.insert(
  //              2,
  //              TypedOperation.forMethod(TT.class.getMethod("g", Integer.class, int.class)),
  //              args3ToInsert);
  //
  //      System.out.println(after.toCodeString());
  //
  //    } catch (NoSuchMethodException e) {
  //      e.printStackTrace();
  //    }
  //  }

  @Test
  public void testRemoveBoundary1() throws NoSuchMethodException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateExampleSequence();
    try {
      TraceableSequence after = before.remove(-1);
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
    TraceableSequence before = sg.GenerateExampleSequence();
    try {
      TraceableSequence after = before.remove(before.size());
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
    TraceableSequence before = sg.GenerateExampleSequence();

    System.out.println("yyx 实现了 toLongFormString！人间之鉴:");
    System.out.println("before size: " + before.size());
    before.disableShortForm();
    System.out.println(before);

    for (int toRemove = before.size() - 1; toRemove >= 0; toRemove--) {
      TraceableSequence after = before.remove(toRemove);
      System.out.printf("after remove(%d), size: %d\n", toRemove, after.size());
      after.disableShortForm();
      System.out.println(after);
      // 不崩 就差不多了
    }
  }

  @Test
  public void testInsert1() throws NoSuchMethodException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateExampleSequence();

    System.out.println("yyx 实现了 toLongFormString！人间之鉴（诶，还是内联了，只是没有压缩？）:");
    System.out.println("before size: " + before.size());
    System.out.println(before.toLongFormString());

    for (int toRemove = before.size() - 1; toRemove >= 0; toRemove--) {
      TraceableSequence after = before.remove(toRemove);
      System.out.printf("after remove(%d), size: %d\n", toRemove, after.size());
      System.out.println(after.toLongFormString());
      // 不崩 就差不多了
    }
  }

  @Test
  public void testModifyReference1() throws NoSuchMethodException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateExampleSequenceForModifyBoolean();

    before.disableShortForm();
    System.out.println(before);
    System.out.println();

    TraceableSequence afterInsert =
        before.insert(
            2,
            TypedOperation.createNonreceiverInitialization(
                new NonreceiverTerm(Type.forClass(Boolean.class), false)));
    afterInsert.disableShortForm();
    System.out.println(afterInsert);
    System.out.println();

    TraceableSequence afterModify = afterInsert.modifyReference(3, 1, new Variable(afterInsert, 2));
    afterModify.disableShortForm();
    System.out.println(afterModify);

    Assert.assertEquals(
        new Sequence.RelativeNegativeIndex(-1), afterModify.getStatement(3).getInputs().get(1));
  }

  @Test
  public void testModifyBooleanUnboxed() throws NoSuchMethodException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyBoolean(false);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyBoolean(2, 1);
    after.disableShortForm();
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Boolean> booleanList0 = new java.util.LinkedList<java.lang.Boolean>();\n"
            + "boolean boolean1 = true;\n"
            + "boolean boolean2 = randoop.generation.date.runtime.DateRuntime.not(boolean1);\n"
            + "booleanList0.addFirst((java.lang.Boolean)boolean2);\n"
            + "int int4 = booleanList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyBooleanBoxed() throws NoSuchMethodException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyBoolean(true);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyBoolean(2, 1);
    after.disableShortForm();
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Boolean> booleanList0 = new java.util.LinkedList<java.lang.Boolean>();\n"
            + "java.lang.Boolean boolean1 = true;\n"
            + "java.lang.Boolean boolean2 = randoop.generation.date.runtime.DateRuntime.not(boolean1);\n"
            + "booleanList0.addFirst(boolean2);\n"
            + "int int4 = booleanList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyRealfloat() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(float.class, 1.2f);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyReal(2, 1, -0.1f);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Float> floatList0 = new java.util.LinkedList<java.lang.Float>();\n"
            + "float float1 = 1.2f;\n"
            + "float float2 = (-0.1f);\n"
            + "float float3 = randoop.generation.date.runtime.DateRuntime.add(float1, (java.lang.Object)float2);\n"
            + "floatList0.addFirst((java.lang.Float)float3);\n"
            + "int int5 = floatList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyRealFloat() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Float.class, 1.2f);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyReal(2, 1, -0.1f);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Float> floatList0 = new java.util.LinkedList<java.lang.Float>();\n"
            + "java.lang.Float float1 = 1.2f;\n"
            + "java.lang.Float float2 = (-0.1f);\n"
            + "java.lang.Float float3 = randoop.generation.date.runtime.DateRuntime.add(float1, (java.lang.Object)float2);\n"
            + "floatList0.addFirst(float3);\n"
            + "int int5 = floatList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyRealdouble() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(double.class, 1.2);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyReal(2, 1, -0.1);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Double> doubleList0 = new java.util.LinkedList<java.lang.Double>();\n"
            + "double double1 = 1.2d;\n"
            + "double double2 = (-0.1d);\n"
            + "double double3 = randoop.generation.date.runtime.DateRuntime.add(double1, (java.lang.Object)double2);\n"
            + "doubleList0.addFirst((java.lang.Double)double3);\n"
            + "int int5 = doubleList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyRealDouble() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Double.class, 1.2);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyReal(2, 1, -0.1);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Double> doubleList0 = new java.util.LinkedList<java.lang.Double>();\n"
            + "java.lang.Double double1 = 1.2d;\n"
            + "java.lang.Double double2 = (-0.1d);\n"
            + "java.lang.Double double3 = randoop.generation.date.runtime.DateRuntime.add(double1, (java.lang.Object)double2);\n"
            + "doubleList0.addFirst(double3);\n"
            + "int int5 = doubleList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralint() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(int.class, 42);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, -233);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Integer> intList0 = new java.util.LinkedList<java.lang.Integer>();\n"
            + "int int1 = 42;\n"
            + "int int2 = (-233);\n"
            + "int int3 = randoop.generation.date.runtime.DateRuntime.add(int1, (java.lang.Object)int2);\n"
            + "intList0.addFirst((java.lang.Integer)int3);\n"
            + "int int5 = intList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralInteger() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Integer.class, 42);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, -233);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Integer> intList0 = new java.util.LinkedList<java.lang.Integer>();\n"
            + "java.lang.Integer int1 = 42;\n"
            + "java.lang.Integer int2 = (-233);\n"
            + "java.lang.Integer int3 = randoop.generation.date.runtime.DateRuntime.add(int1, (java.lang.Object)int2);\n"
            + "intList0.addFirst(int3);\n"
            + "int int5 = intList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegrallong() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(long.class, 42L);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, -233L);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Long> longList0 = new java.util.LinkedList<java.lang.Long>();\n"
            + "long long1 = 42L;\n"
            + "long long2 = (-233L);\n"
            + "long long3 = randoop.generation.date.runtime.DateRuntime.add(long1, (java.lang.Object)long2);\n"
            + "longList0.addFirst((java.lang.Long)long3);\n"
            + "int int5 = longList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralLong() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Long.class, 42L);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, -233L);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Long> longList0 = new java.util.LinkedList<java.lang.Long>();\n"
            + "java.lang.Long long1 = 42L;\n"
            + "java.lang.Long long2 = (-233L);\n"
            + "java.lang.Long long3 = randoop.generation.date.runtime.DateRuntime.add(long1, (java.lang.Object)long2);\n"
            + "longList0.addFirst(long3);\n"
            + "int int5 = longList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralshort() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before =
        sg.GenerateShortExampleSequenceForModifyNumber(short.class, (short) 42);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, (short) -233); // 这里必须传 short 类型
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Short> shortList0 = new java.util.LinkedList<java.lang.Short>();\n"
            + "short short1 = (short)42;\n"
            + "short short2 = (short)-233;\n"
            + "short short3 = randoop.generation.date.runtime.DateRuntime.add(short1, (java.lang.Object)short2);\n"
            + "shortList0.addFirst((java.lang.Short)short3);\n"
            + "int int5 = shortList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralShort() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before =
        sg.GenerateShortExampleSequenceForModifyNumber(Short.class, (short) 42);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, (short) -233);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Short> shortList0 = new java.util.LinkedList<java.lang.Short>();\n"
            + "java.lang.Short short1 = (short)42;\n"
            + "java.lang.Short short2 = (short)-233;\n"
            + "java.lang.Short short3 = randoop.generation.date.runtime.DateRuntime.add(short1, (java.lang.Object)short2);\n"
            + "shortList0.addFirst(short3);\n"
            + "int int5 = shortList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralbyte() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before =
        sg.GenerateShortExampleSequenceForModifyNumber(byte.class, (byte) 42);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, (byte) -23);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Byte> byteList0 = new java.util.LinkedList<java.lang.Byte>();\n"
            + "byte byte1 = (byte)42;\n"
            + "byte byte2 = (byte)-23;\n"
            + "byte byte3 = randoop.generation.date.runtime.DateRuntime.add(byte1, (java.lang.Object)byte2);\n"
            + "byteList0.addFirst((java.lang.Byte)byte3);\n"
            + "int int5 = byteList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralByte() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before =
        sg.GenerateShortExampleSequenceForModifyNumber(Byte.class, (byte) 42L);

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, (byte) -23L);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Byte> byteList0 = new java.util.LinkedList<java.lang.Byte>();\n"
            + "java.lang.Byte byte1 = (byte)42;\n"
            + "java.lang.Byte byte2 = (byte)-23;\n"
            + "java.lang.Byte byte3 = randoop.generation.date.runtime.DateRuntime.add(byte1, (java.lang.Object)byte2);\n"
            + "byteList0.addFirst(byte3);\n"
            + "int int5 = byteList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralchar() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(char.class, 'c');

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, (char) 5);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Character> charList0 = new java.util.LinkedList<java.lang.Character>();\n"
            + "char char1 = 'c';\n"
            + "char char2 = '\\u0005';\n"
            + "char char3 = randoop.generation.date.runtime.DateRuntime.add(char1, (java.lang.Object)char2);\n"
            + "charList0.addFirst((java.lang.Character)char3);\n"
            + "int int5 = charList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyIntegralCharacter() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateShortExampleSequenceForModifyNumber(Character.class, 'c');

    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyIntegral(2, 1, (char) 5);
    System.out.println("after - shortform"); // TODO shortform 使用 DateRuntime 里的小函数的语义！
    System.out.println(after);
    after.disableShortForm();
    System.out.println("after - longform");
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.Character> charList0 = new java.util.LinkedList<java.lang.Character>();\n"
            + "java.lang.Character char1 = 'c';\n"
            + "java.lang.Character char2 = '\\u0005';\n"
            + "java.lang.Character char3 = randoop.generation.date.runtime.DateRuntime.add(char1, (java.lang.Object)char2);\n"
            + "charList0.addFirst(char3);\n"
            + "int int5 = charList0.size();\n",
        after.toString());
  }

  @Test
  public void testModifyStringInsert() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateExampleSequence();

    System.out.println("before size: " + before.size());
    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyStringInsert(2, 1, 1); // "hi!" -> "h i!"
    after.disableShortForm();
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
            + "java.lang.String str1 = \"hi!\";\n"
            + "int int2 = 1;\n"
            + "java.lang.String str3 = randoop.generation.date.runtime.DateRuntime.insert(str1, int2);\n"
            + "strList0.addFirst(str3);\n"
            + "int int5 = strList0.size();\n"
            + "java.util.TreeSet<java.lang.String> strSet6 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
            + "java.util.Set<java.lang.String> strSet7 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet6);\n",
        after.toString());
  }

  @Test
  public void testModifyStringRemove() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateExampleSequence();

    System.out.println("before size: " + before.size());
    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyStringRemove(2, 1, 1); // "hi!" -> "h!"
    after.disableShortForm();
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
            + "java.lang.String str1 = \"hi!\";\n"
            + "int int2 = 1;\n"
            + "java.lang.String str3 = randoop.generation.date.runtime.DateRuntime.remove(str1, int2);\n"
            + "strList0.addFirst(str3);\n"
            + "int int5 = strList0.size();\n"
            + "java.util.TreeSet<java.lang.String> strSet6 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
            + "java.util.Set<java.lang.String> strSet7 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet6);\n",
        after.toString());
  }

  @Test
  public void testModifyStringModify() throws NoSuchMethodException, ClassNotFoundException {
    SequenceGenerator sg = new SequenceGenerator();
    TraceableSequence before = sg.GenerateExampleSequence();

    System.out.println("before size: " + before.size());
    before.disableShortForm();
    System.out.println(before);

    TraceableSequence after = before.modifyStringModify(2, 1, 1, 10); // "hi!" -> "hs!"
    after.disableShortForm();
    System.out.println(after);

    Assert.assertEquals(
        "java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();\n"
            + "java.lang.String str1 = \"hi!\";\n"
            + "int int2 = 1;\n"
            + "int int3 = 10;\n"
            + "java.lang.String str4 = randoop.generation.date.runtime.DateRuntime.modify(str1, int2, int3);\n"
            + "strList0.addFirst(str4);\n"
            + "int int6 = strList0.size();\n"
            + "java.util.TreeSet<java.lang.String> strSet7 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);\n"
            + "java.util.Set<java.lang.String> strSet8 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet7);\n",
        after.toString());
  }
}
