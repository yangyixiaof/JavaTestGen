# Randoop 数据结构

## Variable 极薄，只有两字段：

+ 我属于哪个 sequence: Sequence 
+ 我被第几个 Statement 产生的: int

##Sequence 的 hashcode 都提前算好，构造时传入。

```
The hashcode of a sequence is the sum of each statement's hashcode
```

## TypedOperation 有 3 个子类。

1. TypedClassOperation

```
/**
 * Represents a TypedOperation and its declaring class. Examples of TypedOperations that have a
 * declaring class are a method call or field access.
 */
public class TypedClassOperation extends TypedOperation {
```

进一步：

2. TypedClassOperationWithCast （间接实现）

```
/**
 * Represents a method with a return type that is a type variable that must be instantiated, and for
 * which execution performs a cast to the instantiating type to emulate handling of casts that are
 * not done in reflection.
 */
public class TypedClassOperationWithCast extends TypedClassOperation {
```

3. TypedTermOperation

```
/**
 * Represents operations that have no declaring class, such as cast or array
 * creation/access/assignment.
 */
class TypedTermOperation extends TypedOperation {
```

## int a = 1 的 1 存在哪儿？。

在 Statement 的 TypedOperation 的 CallableOperation 的一个子类—— NonreceiverTerm 的 value ！

## 哪些地方用到了 Sequence#extend 方法

注意有三个 overloading methods.

`extend(TypedOperation operation, List<Variable> inputVariables)` 18 处

`extend(TypedOperation operation, Variable... inputs)` 7 处

`extend(Statement statement, List<Variable> inputs)` 0 处…… 亲测注释掉也能编译 (`./gradlew assemble`)

先看 7 处的。

randoop.sequence 里：

+ ` createSequenceForPrimitive(Object value)` 
  + randoop.generation 里:
    + ForwardGenerator 的 `private void determineActiveIndices(ExecutableSequence seq)`
      + ForwardGenerator#step()
    + SeedSequences 的 public static Set<Sequence> objectsToSeeds(List<Object> seeds)
      + SeedSequences 的 public static Set<Sequence> defaultSeeds()
        + randoop.main.GenTests.handle(String[] args)


randoop.main 里：

+ randoop.main.GenTests.handle(String[] args)
  + 用法理解：Sequence newObj = new Sequence().extend(objectConstructor); 然后加入 excludeSet，即总是排除仅有一句 `new Object()` 的 sequence。
+ randoop.generation 里:
  + ForwardGenerator 的 `private Sequence repeat(Sequence seq, TypedOperation operation, int times)`









## 哪些方法能产生 Sequence TODO

（查看的方式：IntelliJ IDEA 看 Sequence 类的 usage，Method return type 分类）

1. Sequence 类中：

```
Lee 自产的
public final Sequence insert(int index, TypedOperation operation, List<Variable> inputVariables)
public final Sequence modifyReference(int stmtIndex, int varIndex, Variable targetVariable)
public final Sequence modifyPrimitive(int stmtIndex, int varIndex, Object deltaValue)
public final Sequence remove(int stmtIndex)
```

```
extend 三种
public final Sequence extend(TypedOperation operation, List<Variable> inputVariables)
public final Sequence extend(TypedOperation operation, Variable... inputs)
public final Sequence extend(Statement statement, List<Variable> inputs)

还有特别多……
public Sequence()
public Sequence(SimpleList<Statement> statements)
private Sequence(SimpleList<Statement> statements, int hashCode, int netSize)


public static Sequence zero(Type c)
public static Sequence createSequenceForPrimitive(Object value)
public static Sequence createSequence(TypedOperation operation, List<Sequence> inputSequences, List<Integer> indexes)
public static Sequence createSequence(TypedOperation operation, Sequence inputSequence)
public static Sequence createSequence(List<Sequence> sequences, List<Integer> variables)

public static Sequence concatenate(List<Sequence> sequences)
public static Sequence parse(List<String> statements)
public static Sequence parse(String string)
Sequence getSubsequence(int index)

```

2. randoop.sequence.SequenceExceptionError 1处

3. randoop.generation.ForwardGenerator 1处

   ```
   private Sequence repeat(Sequence seq, TypedOperation operation, int times)
   	一处使用：ForwardGenerator 的 private ExecutableSequence createNewUniqueSequence
   ```

4. ​
















## 插桩部分

### ExecutionVisitor

有三个实现，其中 Dummy 无趣：

`CoveredClassVisitor` 会更新 `Execution` 的 `Set<Class<?>> coveredClasses;`

`MultiVisitor` 方便开发者在一趟里执行多个 visitor…… 好想法

`DummyVisitor` 无趣，看 usage 都是当“零元”占位用

