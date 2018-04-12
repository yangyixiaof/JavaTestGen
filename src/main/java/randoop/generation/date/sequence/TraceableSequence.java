package randoop.generation.date.sequence;

import randoop.Globals;
import randoop.generation.date.runtime.DateRuntime;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.sequence.Variable;
import randoop.types.PrimitiveType;
import randoop.types.Type;
import randoop.util.SimpleArrayList;
import randoop.util.SimpleList;

import java.util.*;

public class TraceableSequence extends Sequence {

  final TraceableSequence last_sequence;
  final Map<Statement, Integer> curr_statement_in_last_sequence_index_map = new HashMap<>();

  // public TraceableSequence(Sequence curr_sequence, Sequence last_sequence) {
  // this.curr_sequence = curr_sequence;
  // this.last_sequence = last_sequence;
  // }

  public TraceableSequence() {
    super();
    last_sequence = null;
  }

  public TraceableSequence(Sequence sequence) {
    super(
        sequence.statements,
        computeHashcode(sequence.statements),
        computeNetSize(sequence.statements));
    this.last_sequence = null;
  }

  public TraceableSequence(
      SimpleList<Statement> statements,
      Map<Statement, Integer> curr_statement_in_last_sequence_index_map,
      TraceableSequence last_sequence) {
    super(statements, computeHashcode(statements), computeNetSize(statements));
    this.last_sequence = last_sequence;
    if (curr_statement_in_last_sequence_index_map != null) {
      this.curr_statement_in_last_sequence_index_map.putAll(
          curr_statement_in_last_sequence_index_map);
    }
  }

  /**
   * 在 this Sequence 中插入一个 Statement，使这个 Statement 成为该 Sequence 的第 index 个语句，其他语句后推。0 起计数。
   *
   * <p>相比之下，extend 是在末尾插入一个 Statement。当 index==this.size() 时，insert 退化为 extend。
   *
   * @param index 要把语句插到哪儿
   * @param operation
   * @param inputVariables
   * @return 插入语句后的新 sequence
   */
  public final TraceableSequence insert(
		  int index, TypedOperation operation, List<Variable> inputVariables) {
    // 1 检查参数
    if (index < 0 || this.size() < index) {
      String msg = "this.size():" + this.size() + " but index:" + index;
      throw new IllegalArgumentException(msg);
    }
    checkInputs(operation, inputVariables);

    // 1.1 确保只引用 index 之前的变量。
    // TODO 只在这里确保是不够的。可以在这儿过滤，但要保证效率还得在调用处从源头就只选前面的变量。（但是我们先"完成目标" ：）
    for (Variable v : inputVariables) {
      if (v.index >= index) {
        // 不能抛 RE。因为在目前实现方式下这是一个「业务分支」而非异常。
        return this; // TODO 或许导致多处引用同一 Sequence，这会出事吗？
      }
    }

    // 2 构造要插入的 statement
    List<Sequence.RelativeNegativeIndex> indexListOfNewStatment = new ArrayList<>(1);
    for (Variable v : inputVariables) {
      indexListOfNewStatment.add(getRelativeIndexForVariable(index, v));
    }
    Statement newStatement = new Statement(operation, indexListOfNewStatment);

    HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();

    // 3 构造新 sequence。依次添加：插入点之前的语句，插入的语句，插入点之后的语句
    SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
    for (int i = 0; i < index; i++) {
      Statement stmt = this.getStatement(i);
      curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
      newStatements.add(stmt);
    }
    newStatements.add(newStatement);
    // 3.1 构造时要修改被推后的 statements 的 RelativeNegativeIndex
    /*
     * 这是一个 RelativeNegativeIndex 调整示例。 我们给出每个语句的 序号 和 List<RelativeNegativeIndex>
     *
     * 插之前： 0 {} 1 {-1} 2 {-1} 3 {-3,-1}
     *
     * insert(2,...) 之后： 0 {} 1 {-1} 2 {...} // 新插入的 statement 3 {-2} // -1 受影响变成 -2 4 {-4,-1} // -3
     * 受影响变成 -4，-1 不受影响
     */
    for (int i = index; i < this.size(); i++) {
      Statement stmt = this.getStatement(i);
      curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
      List<Sequence.RelativeNegativeIndex> newInputs = new ArrayList<>();
      for (Sequence.RelativeNegativeIndex rni : stmt.getInputs()) {
        if (rni.index + i < index) {
          newInputs.add(new Sequence.RelativeNegativeIndex(rni.index - 1));
        } else {
          newInputs.add(new Sequence.RelativeNegativeIndex(rni.index));
        }
      }
      newStatements.add(new Statement(stmt.getOperation(), newInputs)); // 旧操作，新参数（只有 RNI 是新的= =、）
    }

    return new TraceableSequence(
        newStatements,
        curr_statement_in_last_sequence_index_map_inner,
        this); // 用这个构造函数，让 hashcode 和 netsize 被重新计算
  }

  /**
   * @param index
   * @param operation
   * @return
   */
  public final TraceableSequence insert(int index, TypedOperation operation) {
    return insert(index, operation, new ArrayList<Variable>());
  }

  /**
   * 把 this Sequence 的第 stmtIndex 句的第 varIndex 个输入变量改为 targetVariable。index 均从 0 起计数。
   *
   * @param stmtIndex
   * @param varIndex
   * @param targetVariable
   * @return
   */
  public final TraceableSequence modifyReference(
      int stmtIndex, int varIndex, Variable targetVariable) {
    // checkInputsForModifyReference
    if (stmtIndex < 0 || this.size() < stmtIndex) {
      String msg = "this.size():" + this.size() + " but stmtIndex:" + stmtIndex;
      throw new IllegalArgumentException(msg);
    }
    Statement statementToModify = statements.get(stmtIndex);
    TypedOperation opOfTheModified = statementToModify.getOperation();
    if (varIndex < 0 || opOfTheModified.getInputTypes().size() < varIndex) {
      String msg =
          "opOfTheModified.getInputTypes().size():"
              + opOfTheModified.getInputTypes().size()
              + " but varIndex:"
              + varIndex;
      throw new IllegalArgumentException(msg);
    }
    if (targetVariable.sequence != this) {
      String msg =
          "targetVariable.owner != this for"
              + Globals.lineSep
              + "sequence: "
              + toString()
              + Globals.lineSep
              + "targetVariable:"
              + targetVariable;
      throw new IllegalArgumentException(msg);
    }

    Type targetVarType = statements.get(targetVariable.index).getOutputType();
    if (targetVarType == null) {
      String msg =
          "targetVarType == null for"
              + Globals.lineSep
              + "sequence: "
              + toString()
              + Globals.lineSep
              + "targetVariable:"
              + targetVariable;
      throw new IllegalArgumentException(msg);
    }

    // 过滤。TODO 从源头处过滤
    if (!opOfTheModified.getInputTypes().get(varIndex).isAssignableFrom(targetVarType)) {
      return this; // TODO 或许导致多处引用同一 Sequence，这会出事吗？ fail loudly!
    }
    // 确保只引用 index 之前的变量。TODO 从源头处过滤
    if (targetVariable.index >= stmtIndex) {
      // 不能抛 RE。因为在目前实现方式下这是一个「业务分支」而非异常。
      return this; // TODO 或许导致多处引用同一 Sequence，这会出事吗？
    }

    // 构造
    HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();
    SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
    for (int i = 0; i < this.size(); i++) {
      if (i != stmtIndex) {
        Statement stmt = this.getStatement(i);
        curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
        newStatements.add(stmt);
      } else {
        List<Sequence.RelativeNegativeIndex> newInputs = new ArrayList<>();
        for (int j = 0; j < statementToModify.getInputs().size(); j++) {
          if (j != varIndex) {
            Sequence.RelativeNegativeIndex rni = statementToModify.getInputs().get(j);
            newInputs.add(new Sequence.RelativeNegativeIndex(rni.index)); // wanna
            // RelativeNegativeIndex#clone()
            // TODO
          } else {
            newInputs.add(new Sequence.RelativeNegativeIndex(targetVariable.index - stmtIndex));
          }
        }
        // 旧操作，新参数（只有一个 RNI 是新的= =、）
        Statement modifiedStatement = new Statement(statementToModify.getOperation(), newInputs);
        newStatements.add(modifiedStatement);
      }
    }
    return new TraceableSequence(
        newStatements,
        curr_statement_in_last_sequence_index_map_inner,
        this); // 用这个构造函数，让 hashcode 和 netsize 被重新计算
  }

  /**
   * @param stmtIndex
   * @param varIndex
   * @return
   */
  public final TraceableSequence modifyBoolean(int stmtIndex, int varIndex) {
    Statement stmtToModify = this.getStatement(stmtIndex); // 是否要把越界 Exception 包成更业务的 Exception？
    //    Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); // 有强转时，鞋子形状！=脚的形状
    // 找到 产生出被改变量 的那一句
    int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
    Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

    Boolean isBool = false, isBoxed = null;
    if (typeOfVarToModify.equals(PrimitiveType.forClass(boolean.class))) {
      isBool = true;
      isBoxed = false;
    } else if (typeOfVarToModify.equals(PrimitiveType.forClass(Boolean.class))) {
      isBool = true;
      isBoxed = true;
    }

    if (!isBool) {
      throw new IllegalArgumentException(
          String.format(
              "modifyBoolean-ing a non-boolean variable. statement index: %d, input index: %d",
              stmtIndex, varIndex));
    }

    try {
      /*
      (以小 boolean 为例。大 Boolean 一样。)

      变异前：
      boolean a = ...
      ...
      o.f(a);

      1. 插入 boolean a1 = DateRuntime.not(a) 在 a 声明后
      boolean a = ...
      boolean a1 = DateRuntime.not(a);
      ...
      o.f(a);

      2. 把 o.f(a) 给 modifyReference 成 o.f(a1)
      boolean a = ...
      boolean a1 = DateRuntime.not(a);
      ...
      o.f(a1);
       */
      TypedOperation methodCallNot;
      if (isBoxed) {
        methodCallNot = TypedOperation.forMethod(DateRuntime.class.getMethod("not", Boolean.class));
      } else {
        methodCallNot = TypedOperation.forMethod(DateRuntime.class.getMethod("not", boolean.class));
      }

      TraceableSequence insertedFlip =
          this.insert(
              varSourceStmtIndex + 1,
              methodCallNot,
              Arrays.asList(new Variable(this, varSourceStmtIndex)));

      // 要修改的那句已经被挤下来 1 行了
      return insertedFlip.modifyReference(
          stmtIndex + 1, varIndex, new Variable(insertedFlip, varSourceStmtIndex + 1));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
      // TODO 确认停下
    }
  }

  /**
   * 让调用者方便：deltaValue 可以是 Float 或 Double（以及 float 或 double，感谢自动 boxing）。
   *
   * <p>现在仍然需要在传参时区分 float 和 double，如 1.2f 和 1.2 TODO 抹平gap
   *
   * <p>被改的变量，类型修旧如旧
   *
   * @param stmtIndex
   * @param varIndex
   * @return
   */
  public final TraceableSequence modifyReal(int stmtIndex, int varIndex, Object deltaValue) {
    Statement stmtToModify = this.getStatement(stmtIndex); // 是否要把越界 Exception 包成更业务的 Exception？
    // Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); // 有强转时，鞋子形状 != 脚的形状
    // 找到 产生出被改变量 的那一句
    int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
    Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

    Class<?> classOfVarToModify = DateRuntime.realTypeToClass.getOrDefault(typeOfVarToModify, null);
    if (classOfVarToModify == null) {
      throw new IllegalArgumentException(
          String.format(
              "modifyReal-ing a non-(float, Float, double, Doublle) variable. statement index: %d, input index: %d",
              stmtIndex, varIndex));
    }
    //    Class<?> ensurePrimitive = classOfVarToModify.isPrimitive()?classOfVarToModify:
    // PrimitiveTypes.boxedToPrimitive.get(classOfVarToModify);

    // TODO 怎么检查 delta 好？

    try {
      /*
      原来是：
      float a = ...
      ...
      o.f(a);

      1. 插入 float delta = 2.0 这种
      2. 插入 float a1 = DateRuntime.add(a, delta) 这种
      3. 把 o.f(a) 给 modifyReference 成 o.f(a1)

      float a = ...
      float delta = 2.0
      float a1 = DateRuntime.add(a, delta)
      ...
      o.f(a1);
       */

      TypedOperation deltaInit =
          TypedOperation.createPrimitiveInitialization(
              Type.forClass(classOfVarToModify), deltaValue
              //              ensurePrimitive.cast(deltaValue)
              ); // cast 目的：让传入的 delta 强转成被改变量的类型，以便调用合适的 add 函数。 TODO Float 不能强转 Double 只能构造函数……
      TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, deltaInit);

      TypedOperation addMethodCall =
          TypedOperation.forMethod(
              DateRuntime.class.getMethod("add", classOfVarToModify, Object.class));
      TraceableSequence insertedAdd =
          insertedDelta.insert(
              varSourceStmtIndex + 2,
              addMethodCall,
              Arrays.asList(
                  new Variable(insertedDelta, varSourceStmtIndex),
                  new Variable(insertedDelta, varSourceStmtIndex + 1)));

      // 要修改的那句已经被挤下来两行了……
      return insertedAdd.modifyReference(
          stmtIndex + 2, varIndex, new Variable(insertedAdd, varSourceStmtIndex + 2));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
      // TODO 确认能 fail
    }
  }

  /**
   * 效果实测：
   *
   * <p>给int integer long传 42
   *
   * <p>给Long传 42L
   *
   * <p>缺陷：现在要针对不同类型的被改变量，传不同类型的 deltaValue。具体是 1,1L,(short)1,(byte)1,(char)1 五种。
   *
   * <p>也是有道理的吧：不同的数值类型有不同的范围，当然有不同的可用 delta 范围。能按类型去区分 delta
   * 范围，也就知道了类型，也就不妨传不同类型的。不过，这也没法静态完全分析出。Sad
   *
   * @param stmtIndex
   * @param varIndex
   * @param deltaValue
   * @return
   */
  public final TraceableSequence modifyIntegral(int stmtIndex, int varIndex, Object deltaValue) {
    Statement stmtToModify = this.getStatement(stmtIndex); // 是否要把越界 Exception 包成更业务的 Exception？
    // Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); // 有强转时，鞋子形状 != 脚的形状
    // 找到 产生出被改变量 的那一句
    int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
    Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

    Class<?> classOfVarToModify =
        DateRuntime.integralTypeToClass.getOrDefault(typeOfVarToModify, null);
    if (classOfVarToModify == null) {
      throw new IllegalArgumentException(
          String.format(
              "modifyIntegral-ing a non-(byte, Byte, short, Short, int, Integer, long, Long) variable. statement index: %d, input index: %d",
              stmtIndex, varIndex));
    }
    //    Class<?> ensurePrimitive = classOfVarToModify.isPrimitive()?classOfVarToModify:
    // PrimitiveTypes.boxedToPrimitive.get(classOfVarToModify);

    // TODO 怎么检查 delta 好？

    try {
      /*
      原来是：
      float a = ...
      ...
      o.f(a);

      1. 插入 float delta = 2.0 这种
      2. 插入 float a1 = DateRuntime.add(a, delta) 这种
      3. 把 o.f(a) 给 modifyReference 成 o.f(a1)

      float a = ...
      float delta = 2.0
      float a1 = DateRuntime.add(a, delta)
      ...
      o.f(a1);
       */

      TypedOperation deltaInit =
          TypedOperation.createPrimitiveInitialization(
              Type.forClass(classOfVarToModify), deltaValue
              //              ensurePrimitive.cast(deltaValue)
              ); // cast 目的：让传入的 delta 强转成被改变量的类型，以便调用合适的 add 函数。 TODO Float 不能强转 Double 只能构造函数……
      TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, deltaInit);

      TypedOperation addMethodCall =
          TypedOperation.forMethod(
              DateRuntime.class.getMethod("add", classOfVarToModify, Object.class));
      TraceableSequence insertedAdd =
          insertedDelta.insert(
              varSourceStmtIndex + 2,
              addMethodCall,
              Arrays.asList(
                  new Variable(insertedDelta, varSourceStmtIndex),
                  new Variable(insertedDelta, varSourceStmtIndex + 1)));

      // 要修改的那句已经被挤下来两行了……
      return insertedAdd.modifyReference(
          stmtIndex + 2, varIndex, new Variable(insertedAdd, varSourceStmtIndex + 2));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
      // TODO 确认能 fail
    }
  }

  //  public final TraceableSequence modifyIntegral(int stmtIndex, int varIndex, long deltaValue) {
  //    return modifyIntegral(stmtIndex, varIndex, Long.valueOf(deltaValue));
  //  }
  //
  //  public final TraceableSequence modifyIntegral(int stmtIndex, int varIndex, int deltaValue) {
  //    return modifyIntegral(stmtIndex, varIndex, Integer.valueOf(deltaValue));
  //  }

  public final TraceableSequence modifyStringInsert(int stmtIndex, int varIndex, int charIndex) {
    /*
    String s = ...;
    ...
    o.f(s);

    ↓

    String s = ...;
    int charIndex=...;
    String s1 = DateRuntime.insert(s,charIndex);
    ...
    o.f(s1);

    跟 modifyReal 和 modifyIntegral 差不多
     */
    try {
      Statement stmtToModify = this.getStatement(stmtIndex);
      // 找到 产生出被改变量 的那一句
      int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
      Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

      if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
        throw new IllegalArgumentException(
            String.format(
                "modifyStringInsert-ing a non-Sting variable. statement index: %d, input index: %d",
                stmtIndex, varIndex));
      }

      TypedOperation charIndexInit =
          TypedOperation.createPrimitiveInitialization(
              PrimitiveType.forClass(int.class), charIndex);
      TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, charIndexInit);

      TypedOperation insertMethodCall =
          TypedOperation.forMethod(DateRuntime.class.getMethod("insert", String.class, int.class));
      TraceableSequence insertedInsert =
          insertedDelta.insert(
              varSourceStmtIndex + 2,
              insertMethodCall,
              Arrays.asList(
                  new Variable(insertedDelta, varSourceStmtIndex),
                  new Variable(insertedDelta, varSourceStmtIndex + 1)));

      // 要修改的那句已经被挤下来两行了……
      return insertedInsert.modifyReference(
          stmtIndex + 2, varIndex, new Variable(insertedInsert, varSourceStmtIndex + 2));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
      // TODO 确认能 fail
    }
  }

  public final TraceableSequence modifyStringRemove(int stmtIndex, int varIndex, int charIndex) {
    /*
    String s = ...;
    ...
    o.f(s);

    ↓

    String s = ...;
    int charIndex=...;
    String s1 = DateRuntime.remove(s,charIndex);
    ...
    o.f(s1);

    跟 modifyReal 和 modifyIntegral 差不多
     */
    try {
      Statement stmtToModify = this.getStatement(stmtIndex);
      // 找到 产生出被改变量 的那一句
      int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
      Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

      if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
        throw new IllegalArgumentException(
            String.format(
                "modifyStringRemove-ing a non-Sting variable. statement index: %d, input index: %d",
                stmtIndex, varIndex));
      }

      TypedOperation charIndexInit =
          TypedOperation.createPrimitiveInitialization(
              PrimitiveType.forClass(int.class), charIndex);
      TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, charIndexInit);

      TypedOperation removeMethodCall =
          TypedOperation.forMethod(DateRuntime.class.getMethod("remove", String.class, int.class));
      TraceableSequence insertedInsert =
          insertedDelta.insert(
              varSourceStmtIndex + 2,
              removeMethodCall,
              Arrays.asList(
                  new Variable(insertedDelta, varSourceStmtIndex),
                  new Variable(insertedDelta, varSourceStmtIndex + 1)));

      // 要修改的那句已经被挤下来两行了……
      return insertedInsert.modifyReference(
          stmtIndex + 2, varIndex, new Variable(insertedInsert, varSourceStmtIndex + 2));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
      // TODO 确认能 fail
    }
  }

  public final TraceableSequence modifyStringModify(
      int stmtIndex, int varIndex, int charIndex, int deltaValue) {
    /*
    String s = ...;
    ...
    o.f(s);

    ↓

    String s = ...;
    int charIndex=...;
    int deltaValue=...;
    String s1 = DateRuntime.modify(s,charIndex,deltaValue);
    ...
    o.f(s1);

    比 modifyReal 和 modifyIntegral 要多挤下来一行！！！
     */
    try {
      Statement stmtToModify = this.getStatement(stmtIndex);
      // 找到 产生出被改变量 的那一句
      int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
      Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

      if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
        throw new IllegalArgumentException(
            String.format(
                "modifyStringModify-ing a non-Sting variable. statement index: %d, input index: %d",
                stmtIndex, varIndex));
      }

      TypedOperation charIndexInit =
          TypedOperation.createPrimitiveInitialization(
              PrimitiveType.forClass(int.class), charIndex);
      TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, charIndexInit);

      TypedOperation deltaValueInit =
          TypedOperation.createPrimitiveInitialization(
              PrimitiveType.forClass(int.class), deltaValue);
      TraceableSequence insertedDelta2 =
          insertedDelta.insert(varSourceStmtIndex + 2, deltaValueInit);

      TypedOperation modifyMethodCall =
          TypedOperation.forMethod(
              DateRuntime.class.getMethod("modify", String.class, int.class, int.class));
      TraceableSequence insertedModify =
          insertedDelta2.insert(
              varSourceStmtIndex + 3,
              modifyMethodCall,
              Arrays.asList(
                  new Variable(insertedDelta2, varSourceStmtIndex),
                  new Variable(insertedDelta2, varSourceStmtIndex + 1),
                  new Variable(insertedDelta2, varSourceStmtIndex + 2)));

      // 要修改的那句已经被挤下来 3 行了……
      return insertedModify.modifyReference(
          stmtIndex + 3, varIndex, new Variable(insertedModify, varSourceStmtIndex + 3));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      return null;
      // TODO 确认能 fail
    }
  }

  /**
   * TODO 废除 把 this Sequence 的第 stmtIndex 句的第 varIndex 个输入变量"的值"根据 deltaValue 做修改，改法见 deltaValue
   * 参数说明。index 均从 0 起计数。
   *
   * <p>（产生若干临时 Sequence。TODO？性能优化点）
   *
   * <p>ModifyPrimitive 残留文档： // // TODO 确认常数不被直接用于 input // NO！有产生直接传字面量的用例（经过cast） // —— 怀疑是经过
   * shorten 的？原来是 NonreceiverTerm + UncheckedCast 语句？。 // 直接传字面量这种是 ShortForm！大喜
   *
   * @param stmtIndex
   * @param varIndex
   * @param deltaValue 变更量。对于数值，总是传来 Double，变这个量。对于 Boolean，不管传来什么，总 not
   * @return
   */
  @Deprecated
  public final TraceableSequence modifyPrimitive(int stmtIndex, int varIndex, Object deltaValue) {
    return null;
  }

  /**
   * 删除 this Sequence 的第 stmtIndex 句。级联删除依赖本句的语句；调整剩下语句的输入的 RelativeNegativeIndex。
   *
   * <pre>
   * 这是一个 RelativeNegativeIndex 调整示例。
   * 我们给出每个语句的 序号 和 List&lt;RelativeNegativeIndex&gt;
   *
   * 删之前：
   * 0 {}
   * 1 {-1}
   * 2 {...} // 要删除的
   * 3 {-1,-2} // 因 -1 级联删除
   * 4 {-4,-3} // 不被级联删除
   * 5 {-2} // 3 被删除后，级联删除
   *
   * remove(2) 之后：
   * 0 {}
   * 1 {-1}
   * 2 {-2,-1} // 不被级联删除，但要改 RNI！
   * </pre>
   *
   * <p>实现：
   *
   * <p>构造以 Statement 为点，以「被依赖」关系为边的有向无环简单图。
   *
   * <p>以 stmtIndex 为起点 traverse 该图（就 dfs 吧），记下能访问到的 Statements，作为枪毙名单。
   *
   * <p>按枪毙名单调整剩下 Statements 的 input（的 RelativeNegativeIndex）。构造新 Sequence。
   *
   * @param stmtIndex
   * @return
   */
  public final TraceableSequence remove(int stmtIndex) {
    // 参数检查, easy peasy
    if (stmtIndex < 0 || this.size() <= stmtIndex) {
      String msg =
          "this.size():"
              + this.size()
              + " but stmtIndex:"
              + stmtIndex
              + ". Expected 0 <= stmtIndex < this.size().";
      throw new IllegalArgumentException(msg);
    }

    // 被依赖关系图 的 邻接表
    ArrayList<ArrayList<Integer>> dependedRelationAdj = calculateDependedRelationAdjacencyList();
    // 不想泄露到 class member 那里…… 就繁琐传参吧。就以改参代返回吧。QAQ
    int stmtNum = dependedRelationAdj.size();
    boolean[] visited = new boolean[stmtNum];
    Arrays.fill(visited, false);
    boolean[] toRemove = new boolean[stmtNum];
    Arrays.fill(toRemove, false);
    dfsForRemoval(dependedRelationAdj, stmtIndex, visited, toRemove);

    // 根据枪毙名单（toRemove）构造新 TraceableSequence
    // TODO 用 HashMap，Statement#equals 是否足够不重不漏判等？#hashcode 效率（小事）
    HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();
    SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
    // 用于计算区间内被删语句数，继而用于计算新的诸 RelativeNegativeIndex
    int[] howManyRemovedBetween0And = new int[stmtNum];
    howManyRemovedBetween0And[0] = toRemove[0] ? 1 : 0;
    for (int i = 1; i < stmtNum; i++) {
      howManyRemovedBetween0And[i] = howManyRemovedBetween0And[i - 1] + (toRemove[i] ? 1 : 0);
    }

    for (int i = 0; i < size(); ++i) {
      if (toRemove[i]) {
        //        howManyRemovedBeforeMe++;
      } else {
        Statement stmt = this.getStatement(i);
        curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
        List<Sequence.RelativeNegativeIndex> newInputs = new ArrayList<>();
        for (Sequence.RelativeNegativeIndex rni : stmt.getInputs()) {
          int argStmtIndex = i + rni.index; // 该输入是第几句产生的
          int howManyRemovedBetweenArgAndCall =
              argStmtIndex == 0
                  ? howManyRemovedBetween0And[i]
                  : howManyRemovedBetween0And[i] - howManyRemovedBetween0And[argStmtIndex - 1];
          newInputs.add(new Sequence.RelativeNegativeIndex(rni.index + howManyRemovedBetweenArgAndCall));
        }
        newStatements.add(new Statement(stmt.getOperation(), newInputs));
      }
    }
    return new TraceableSequence(
        newStatements, curr_statement_in_last_sequence_index_map_inner, this);
  }

  /**
   * Calculate "depended" relation adjacency list for this Sequence.
   *
   * <p>复杂度稍高？线性时间去重
   *
   * @return
   */
  final ArrayList<ArrayList<Integer>> calculateDependedRelationAdjacencyList() {
    ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < this.size(); i++) {
      adj.add(new ArrayList<Integer>());
      for (Sequence.RelativeNegativeIndex rni : this.getStatement(i).getInputs()) {
        int dependedStmt = i + rni.index;
        if (!adj.get(dependedStmt).contains(i)) {
          adj.get(dependedStmt).add(i);
        }
      }
    }
    return adj;
  }

  /**
   * 深度优先（别的方式也行）遍历邻接表 dependedRelationAdj 代表的有向图。对访问到的结点 i，设置 toRemove[i] = true。
   *
   * <p>Side effect: update visited and toRemove!!!
   *
   * @param dependedRelationAdj
   * @param stmtIndexToRemove
   * @param visited
   * @param toRemove
   */
  final void dfsForRemoval(
      ArrayList<ArrayList<Integer>> dependedRelationAdj,
      int stmtIndexToRemove,
      boolean[] visited,
      boolean[] toRemove) {
    if (!visited[stmtIndexToRemove]) {
      toRemove[stmtIndexToRemove] = true;
      visited[stmtIndexToRemove] = true;
      for (Integer neighbor : dependedRelationAdj.get(stmtIndexToRemove)) {
        dfsForRemoval(dependedRelationAdj, neighbor, visited, toRemove);
      }
    }
  }

  public String toLongFormString() {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < size(); i++) {
      // Don't dump primitive initializations, if using literals.
      // But do print them if they are the last statement;
      // otherwise, the sequence might print as the empty string.
      //      if (i != size() - 1) {
      //        if (canUseShortForm() && getStatement(i).getShortForm() != null) {
      //          continue;
      //        }
      //      }
      appendCode(b, i);
      b.append(Globals.lineSep);
    }
    return b.toString();
  }
}
