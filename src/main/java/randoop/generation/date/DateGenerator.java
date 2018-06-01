package randoop.generation.date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import randoop.DummyVisitor;
import randoop.NormalExecution;
import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
import randoop.generation.date.execution.ProcessExecutor;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.generation.date.tensorflow.QLearning;
import randoop.generation.date.tensorflow.QTransition;
import randoop.generation.date.tensorflow.ReplayMemory;
import randoop.generation.date.tensorflow.StateActionPool;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.reflection.TypeInstantiator;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.sequence.SequenceExceptionError;
import randoop.test.DummyCheckGenerator;
import randoop.util.Log;
import randoop.util.Randomness;

/** Randoop-DATE's "Sequence-based" generator. */
public class DateGenerator extends AbstractGenerator {

  public int numOfSeqSelected = 1;
  public int numOfMutSelected = 1;

  /**
   * 这个字段简直…… 没有它就难以实现 numGeneratedSequences() 和 getAllSequences() 呢。
   *
   * <p>有字段就得维护字段，哼咕。TODO check TODO 学 ForwardGenerator
   *
   * <p>The set of ALL sequences ever generated, including sequences that were executed and then
   * discarded.
   *
   * <p>This must be ordered by insertion to allow for flaky test history collection in {@link
   * randoop.main.GenTests#printSequenceExceptionError(AbstractGenerator, SequenceExceptionError)}.
   */
  // private final LinkedHashSet<TraceableSequence> allSequences;

  private final Map<String, TraceableSequence> allSequences =
      new TreeMap<String, TraceableSequence>();

  // The set of all primitive values seen during generation and execution
  // of sequences. This set is used to tell if a new primitive value has
  // been generated, to add the value to the components.
  private Set<Object> runtimePrimitivesSeen = new LinkedHashSet<>();

  // private final Set<TypedOperation> observers;
  private final TypeInstantiator instantiator;

  // /**
  // * Constructs a generator with the given parameters.
  // *
  // * <p>IDEA 自动从基类 copy 的 doc…… 这是个 good practice 么？
  // *
  // * @param operations statements (e.g. methods and constructors) used to create sequences.
  // Cannot
  // * be null.
  // * @param limits maximum time and number of sequences to generate/output
  // * @param componentManager the component manager to use to store sequences during
  // component-based
  // * generation. Can be null, in which case the generator's component manager is initialized
  // as
  // * {@code new ComponentManager()}.
  // * @param stopper optional, additional stopping criterion for the generator. Can be null.
  // * @param listenerManager manager that stores and calls any listeners to use during
  // generation.
  // */
  // public DateGenerator(
  // List<TypedOperation> operations,
  // GenInputsAbstract.Limits limits,
  // ComponentManager componentManager,
  // IStopper stopper,
  // RandoopListenerManager listenerManager) {
  // super(operations, limits, componentManager, stopper, listenerManager);
  //
  // this.allSequences = new LinkedHashSet<>();
  // }

  ReplayMemory d;
  StateActionPool state_action_pool;
  QLearning q_learn;

  //  public DateGenerator(
  //      List<TypedOperation> operations,
  //      Set<TypedOperation> observers,
  //      GenInputsAbstract.Limits limits,
  //      ComponentManager componentManager,
  //      RandoopListenerManager listenerManager) {
  //    this(operations, observers, limits, componentManager, null, listenerManager);
  //  }

  public DateGenerator(
      List<TypedOperation> operations,
      Set<TypedOperation> observers,
      GenInputsAbstract.Limits limits,
      ComponentManager componentManager,
      //      IStopper stopper,
      RandoopListenerManager listenerManager) {
    super(operations, limits, componentManager, null, listenerManager); // stopper

    // this.observers = observers;
    this.instantiator = componentManager.getTypeInstantiator();
    this.d = new ReplayMemory();
    this.state_action_pool = new StateActionPool(this.instantiator, observers);
    this.q_learn = new QLearning(this.d, this.state_action_pool);
    initializeRuntimePrimitivesSeen();
  }

  @Override
  public ExecutableSequence step() {
    // FML TODO
    if (allSequences.isEmpty()) {
      Collection<Sequence> seed_sequences = componentManager.gralSeeds;
      for (Sequence seq : seed_sequences) {
        TraceableSequence new_seq = new TraceableSequence(seq);
        allSequences.put(new_seq.toLongFormString(), new_seq);
      }
      //      for (Sequence s : componentManager.gralSeeds) {
      //        System.out.println(s);
      //      }
    }

    //    long startTime = System.nanoTime();

    // 以现在的规模，无害
    if (componentManager.numGeneratedSequences() % GenInputsAbstract.clear == 0) {
      componentManager.clearGeneratedSequences();
    }

    // TODO 有一些产生了的没放进最终结果？
    //    ExecutableSequence eSeq = createNewUniqueSequence(); // make it!
    List<QTransition> transitions = createNewUniqueSequences(numOfSeqSelected, numOfMutSelected);
    System.out.println(
        "after ============ List<ExecutableSequence> eSeqs = createNewUniqueSequences(numOfSeqSelected, numOfMutSelected);");
    d.StoreTransitions(transitions);
    q_learn.QLearn();
    //    for (ExecutableSequence eSeq : eSeqs) {
    //      if (eSeq == null) {
    //        return null;
    //      }
    //
    //      // 璇曡瘯 dontexecute 鐨勯�夐」
    //      if (GenInputsAbstract.dontexecute) {
    //        this.componentManager.addGeneratedSequence(eSeq.sequence);
    //        return null;
    //      }
    //
    //      // 鍞� 鏈夌偣璁� currSeq 澶卞幓鎰忎箟浜嗏�︹�ODO
    //      //    setCurrentSequence(eSeq.sequence);
    //      setCurrentSequence(eSeq.sequence);
    //    }

    //    long gentime1 = System.nanoTime() - startTime; // rename it

    //    System.out.println("Before ------eSeq.execute(executionVisitor, checkGenerator);");
    //    System.out.println(eSeq);
    // 插入的 TypedOperation 是否完全没有类型参数的信息？
    //        eSeq.execute(executionVisitor, checkGenerator);
    //    System.out.println("After ------eSeq.execute(executionVisitor, checkGenerator);");
    //    System.out.println(eSeq);
    // 寮勬竻 execute 浣滅敤鈥︹��
    //    process_execute(eSeqs); // 骞惰鍖栦箣鍓嶆尯鎱㈢殑 TODO 瀹氶噺娴嬩竴娴�
    // TODO 弄清 execute 作用……
    // process_execute(eSeqs); // 并行化之前挺慢的

    //    for(ExecutableSequence eSeq:eSeqs){
    //      eSeq.execute(executionVisitor, checkGenerator);
    //    }

    //    startTime = System.nanoTime(); // reset start time.

    // 口怕，先不管它 ：）
    // determineActiveIndices(eSeq);

    //    determineActiveIndices(ExecutableSequence seq) 作用：
    //    如果执行有任何问题（4种）就把全部 statement 设为不 active；
    //    如果哪个 Statement 没返回值、是方法调用但方法在observer集里（貌似是在调用randoop时通过参数传入？）、返回值是 primitive，皆设为不 active。
    //    杨：总之就是，对一个 ExecutableSequence，分析出它产出了哪些能当作输入的值。

    // if (eSeq.sequence.hasActiveFlags()) {
    // componentManager.addGeneratedSequence(eSeq.sequence);
    // }

    //    long gentime2 = System.nanoTime() - startTime; // rename it

    //    eSeq.gentime = gentime1 + gentime2;

    //    return eSeq;

    // FFFFFFML? eSeqs.get(0)
    return null;
  }

  private void process_execute(List<ExecutableSequence> eSeqs) {
    List<String> test_cases = new LinkedList<String>();
    //    test_cases.add(currSeq.toString());
    for (ExecutableSequence eSeq : eSeqs) {
      test_cases.add(eSeq.toCodeString());
    }
    ProcessExecutor exe_ctor = new ProcessExecutor(test_cases);
    exe_ctor.ExecuteTestCases();
  }

  /**
   * 仅用于判定停止。@see
   *
   * @return
   */
  @Override
  public int numGeneratedSequences() {
    return allSequences.size();
  }

  @Override
  public LinkedHashSet<Sequence> getAllSequences() {
    LinkedHashSet<Sequence> sequence_set = new LinkedHashSet<Sequence>();
    for (Sequence sequence : allSequences.values()) {
      sequence_set.add(sequence);
    }
    return sequence_set;
  }

  private List<QTransition> createNewUniqueSequences(int numOfSeqSelected, int numOfMutSelected) {

    List<Sequence> sourceSequences = new ArrayList<>();
    for (int i = 0; i < numOfSeqSelected; i++) {
      sourceSequences.add(
          Randomness.randomSetMember(this.allSequences.values())); // TODO 去重、或直接高效不选重复
    }
    //    Sequence sourceSequence = Randomness.randomSetMember(this.allSequences.values());

    List<ExecutableSequence> newSequences = new ArrayList<>();
    for (Sequence sourceSequence : sourceSequences) {
      MutationAnalyzer analyzer =
          new MutationAnalyzer((TraceableSequence) sourceSequence, instantiator);
      // TODO 请 GenerateMutationOperations 支持广一点的数据结构吧w
      List<MutationOperation> candidateMutations = new LinkedList<MutationOperation>();
      try {
        analyzer.GenerateMutationOperations(new HashSet<>(this.operations), candidateMutations);
      } catch (DateWtfException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < numOfMutSelected; i++) {
        MutationOperation selectedMutation = Randomness.randomMember(candidateMutations);
        TraceableSequence newSequence = selectedMutation.ApplyMutation();
        // 竟然用 LongFormString 当 key 吗…… 来防止重复 // TODO 用它 HashCode 防？
        if (this.allSequences.containsKey(newSequence.toLongFormString())) {
          // TODO 要沿用日志格式的话，就得从 MutationOperation 里拿 TypedOperation
          // operationHistory.add(operation, OperationOutcome.SEQUENCE_DISCARDED);
          Log.logLine("Sequence discarded because the same sequence was previously created.");
          //          return null;
          // 不加这个而已，别返回 null
        }
        this.allSequences.put(newSequence.toLongFormString(), newSequence);
        ExecutableSequence newESeq = new ExecutableSequence(newSequence);
        StringBuilder mutationInfo = new StringBuilder();
        mutationInfo.append("[Original] ");
        mutationInfo.append(selectedMutation.sequence);
        mutationInfo.append("[Mutation] ");
        mutationInfo.append(selectedMutation);
        newESeq.mutationInfo = mutationInfo.toString();
        System.out.println("变异历程: " + mutationInfo); // 单线程时直接打无妨！
        System.out.println("newESeq.toCodeString() # " + newESeq.toCodeString());
        newESeq.sequence.disableShortForm(); // 回不来的！注意别影响之后
        System.out.println("newESeq.sequence.toCodeString() # " + newESeq.sequence.toCodeString());
        newSequences.add(newESeq);
      }
    }

    return null; // TODO implement it 2018-06-01
  }

  /**
   * Tries to create and execute a new sequence. If the sequence is new (not already in the
   * specified component manager), then it is executed and added to the manager's sequences. If the
   * sequence created is already in the manager's sequences, this method has no effect, and returns
   * null.
   *
   * @return a new sequence, or null
   */
  private ExecutableSequence
      createNewUniqueSequence() { // TODO是否 instantiated? 1. operation 2. 初始allSequences
    Sequence sourceSequence = Randomness.randomSetMember(this.allSequences.values());
    MutationAnalyzer analyzer =
        new MutationAnalyzer((TraceableSequence) sourceSequence, instantiator);
    // TODO 请 GenerateMutationOperations 支持广一点的数据结构吧w
    List<MutationOperation> candidateMutations = new LinkedList<MutationOperation>();
    try {
      analyzer.GenerateMutationOperations(new HashSet<>(this.operations), candidateMutations);
    } catch (DateWtfException e) {
      e.printStackTrace();
    }
    MutationOperation selectedMutation = Randomness.randomMember(candidateMutations);
    TraceableSequence newSequence = selectedMutation.ApplyMutation();

    // 竟然用 LongFormString 当 key 吗…… 来防止重复 // TODO 用它 HashCode 防？
    if (this.allSequences.containsKey(newSequence.toLongFormString())) {
      // TODO 要沿用日志格式的话，就得从 MutationOperation 里拿 TypedOperation
      // operationHistory.add(operation, OperationOutcome.SEQUENCE_DISCARDED);
      Log.logLine("Sequence discarded because the same sequence was previously created.");
      return null;
    }
    this.allSequences.put(newSequence.toLongFormString(), newSequence);
    return new ExecutableSequence(newSequence);
  }

  /**
   * The runtimePrimitivesSeen set contains primitive values seen during generation/execution and is
   * used to determine new values that should be added to the component set. The component set
   * initially contains a set of primitive sequences; this method puts those primitives in this set.
   */
  // XXX this is goofy - these values are available in other ways
  private void initializeRuntimePrimitivesSeen() {
    for (Sequence s : componentManager.getAllPrimitiveSequences()) {
      ExecutableSequence es = new ExecutableSequence(s);
      es.execute(new DummyVisitor(), new DummyCheckGenerator());
      NormalExecution e = (NormalExecution) es.getResult(0);
      Object runtimeValue = e.getRuntimeValue();
      runtimePrimitivesSeen.add(runtimeValue);
    }
  }

  /**
   * Returns the set of sequences that are included in other sequences to generate inputs (and, so,
   * are subsumed by another sequence).
   */
  @Override
  public Set<Sequence> getSubsumedSequences() {
    return new HashSet<Sequence>();
  }
}
