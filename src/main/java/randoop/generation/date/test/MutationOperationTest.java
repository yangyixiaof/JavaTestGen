package randoop.generation.date.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import randoop.operation.*;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.types.ClassOrInterfaceType;
import randoop.types.RandoopTypeException;
import randoop.types.Type;
import randoop.types.TypeTuple;
import randoop.util.SimpleArrayList;

/** */
public class MutationOperationTest {

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

  // public static TypedOperation methodCall(Type outputType, Type... inputTypesArray) {
  // TypeTuple inputTypes = new TypeTuple(Arrays.asList(inputTypesArray)); // 真的惊了…… [] 和 ...
  // 互转好甜
  // return
  // }

  // 从测试那边的 EnumReflectionTest 类里 copy...
  // 因为 主体代码 不能依赖 测试代码……（就看不到

  /**
   * 新建一个 方法调用 的 TypedTermOperation
   *
   * @param m
   * @param declaringType
   * @return
   */
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

  /**
   * 新建一个 构造函数调用 的 TypedTermOperation
   *
   * @param con
   * @return
   * @throws RandoopTypeException
   */
  // 从测试那边的 EnumReflectionTest 类里 copy...
  public static TypedClassOperation createConstructorCall(Constructor<?> con)
      throws RandoopTypeException {
    ConstructorCall op = new ConstructorCall(con);
    ClassOrInterfaceType declaringType = ClassOrInterfaceType.forClass(con.getDeclaringClass());
    List<Type> paramTypes = new ArrayList<>();
    // paramTypes.add(declaringType);
    for (java.lang.reflect.Type pc : con.getGenericParameterTypes()) {
      paramTypes.add(Type.forType(pc));
    }
    return new TypedClassOperation(op, declaringType, new TypeTuple(paramTypes), declaringType);
  }

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
  @Test
  public void test1() throws NoSuchMethodException, RandoopTypeException {
    SimpleArrayList<Statement> statements = new SimpleArrayList<>();

    Statement s0 = new Statement(intVarDeclAndInit(42));
    statements.add(s0); // add 竟然不是 SimpleList 接口规定的方法，神奇设计

    // 拿到无参构造函数，构造 ConstructorCall // TODO getConstructor
    Statement s1 = new Statement(createConstructorCall(TT.class.getDeclaredConstructors()[0]));

    statements.add(s1);

    //    List<Sequence.RelativeNegativeIndex> args2 = Arrays
    //        .asList(new Sequence.RelativeNegativeIndex(-1), new Sequence.RelativeNegativeIndex(-2));
    //    Statement s2 = new Statement(createMethodCall(TT.class.getMethod("f", Integer.class),
    //        ClassOrInterfaceType.forClass(TT.class)), args2); // 一开始猜的是 Type.forClass, 结果是
    //                                                          // ClassOrInterfaceType.forClass
    //    statements.add(s2);

    Sequence before = new Sequence(statements);

    System.err.println("before:" + before);

    // List<Variable> args3ToInsert =
    // Arrays.asList(new Variable(before, 1), new Variable(before, 0), new Variable(before, 0));
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
