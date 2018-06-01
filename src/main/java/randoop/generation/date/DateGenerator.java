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
   * 杩欎釜瀛楁绠�鐩粹�︹�� 娌℃湁瀹冨氨闅句互瀹炵幇 numGeneratedSequences() 鍜� getAllSequences() 鍛€��
   *
   * <p>鏈夊瓧娈靛氨寰楃淮鎶ゅ瓧娈碉紝鍝煎挄銆俆ODO check TODO 瀛� ForwardGenerator
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
  // * <p>IDEA 鑷姩浠庡熀绫� copy 鐨� doc鈥︹�� 杩欐槸涓� good practice 涔堬紵
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
    super(operations, limits, componentManager, null, listenerManager);// stopper

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

    long startTime = System.nanoTime();

    // 浠ョ幇鍦ㄧ殑瑙勬ā锛屾棤瀹�
    if (componentManager.numGeneratedSequences() % GenInputsAbstract.clear == 0) {
      componentManager.clearGeneratedSequences();
    }

    //    ExecutableSequence eSeq = createNewUniqueSequence(); // make it!
    List<QTransition> eSeqs = createNewUniqueSequences(numOfSeqSelected, numOfMutSelected);
    System.out.println("after ============ List<ExecutableSequence> eSeqs = createNewUniqueSequences(numOfSeqSelected, numOfMutSelected);");
    for (ExecutableSequence eSeq : eSeqs) {
      if (eSeq == null) {
        return null;
      }

      // TODO 璇曡瘯 dontexecute 鐨勯�夐」
      if (GenInputsAbstract.dontexecute) {
        this.componentManager.addGeneratedSequence(eSeq.sequence);
        return null;
      }

      // 鍞� 鏈夌偣璁� currSeq 澶卞幓鎰忎箟浜嗏�︹�ODO
      //    setCurrentSequence(eSeq.sequence);
      setCurrentSequence(eSeq.sequence);
    }

    long gentime1 = System.nanoTime() - startTime; // rename it

    //    System.out.println("Before ------eSeq.execute(executionVisitor, checkGenerator);");
    //    System.out.println(eSeq);
    // 鎻掑叆鐨� TypedOperation 鏄惁瀹屽叏娌℃湁绫诲瀷鍙傛暟鐨勪俊鎭紵
    //    eSeq.execute(executionVisitor, checkGenerator);
    //    System.out.println("After ------eSeq.execute(executionVisitor, checkGenerator);");
    //    System.out.println(eSeq);
    // TODO 寮勬竻 execute 浣滅敤鈥︹��
    process_execute(eSeqs); // 骞惰鍖栦箣鍓嶆尯鎱㈢殑 TODO 瀹氶噺娴嬩竴娴�

    startTime = System.nanoTime(); // reset start time.

    // 鍙ｆ�曪紝鍏堜笉绠″畠 锛氾級
    // determineActiveIndices(eSeq);

    //    determineActiveIndices(ExecutableSequence seq) 浣滅敤锛�
    //    濡傛灉鎵ц鏈変换浣曢棶棰橈紙4绉嶏級灏辨妸鍏ㄩ儴 statement 璁句负涓� active锛�
    //    濡傛灉鍝釜 Statement 娌¤繑鍥炲�笺�佹槸鏂规硶璋冪敤浣嗘柟娉曞湪observer闆嗛噷锛堣矊浼兼槸鍦ㄨ皟鐢╮andoop鏃堕�氳繃鍙傛暟浼犲叆锛燂級銆佽繑鍥炲�兼槸 primitive锛岀殕璁句负涓� active銆�
    //    鏉細鎬讳箣灏辨槸锛屽涓�涓� ExecutableSequence锛屽垎鏋愬嚭瀹冧骇鍑轰簡鍝簺鑳藉綋浣滆緭鍏ョ殑鍊笺��

    // if (eSeq.sequence.hasActiveFlags()) {
    // componentManager.addGeneratedSequence(eSeq.sequence);
    // }

    long gentime2 = System.nanoTime() - startTime; // rename it

    //    eSeq.gentime = gentime1 + gentime2;

    //    return eSeq;

    // TODO FFFFFFML? eSeqs.get(0)
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
   * 浠呯敤浜庡垽瀹氬仠姝€�侤see
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

  private List<QTransition> createNewUniqueSequences(
      int numOfSeqSelected, int numOfMutSelected) {

    List<Sequence> sourceSequences = new ArrayList<>();
    for (int i = 0; i < numOfSeqSelected; i++) {
      sourceSequences.add(
          Randomness.randomSetMember(this.allSequences.values())); // TODO 鍘婚噸銆佹垨鐩存帴楂樻晥涓嶉�夐噸澶�
    }
    //    Sequence sourceSequence = Randomness.randomSetMember(this.allSequences.values());

    List<ExecutableSequence> newSequences = new ArrayList<>();
    for (Sequence sourceSequence : sourceSequences) {
      MutationAnalyzer analyzer =
          new MutationAnalyzer((TraceableSequence) sourceSequence, instantiator);
      // TODO 璇� GenerateMutationOperations 鏀寔骞夸竴鐐圭殑鏁版嵁缁撴瀯鍚
      List<MutationOperation> candidateMutations = new LinkedList<MutationOperation>();
      try {
        analyzer.GenerateMutationOperations(new HashSet<>(this.operations), candidateMutations);
      } catch (DateWtfException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < numOfMutSelected; i++) {
        MutationOperation selectedMutation = Randomness.randomMember(candidateMutations);
        TraceableSequence newSequence = selectedMutation.ApplyMutation();
        // 绔熺劧鐢� LongFormString 褰� key 鍚椻�︹�� 鏉ラ槻姝㈤噸澶� // TODO 鐢ㄥ畠 HashCode 闃诧紵
        if (this.allSequences.containsKey(newSequence.toLongFormString())) {
          // TODO 瑕佹部鐢ㄦ棩蹇楁牸寮忕殑璇濓紝灏卞緱浠� MutationOperation 閲屾嬁 TypedOperation
          // operationHistory.add(operation, OperationOutcome.SEQUENCE_DISCARDED);
          Log.logLine("Sequence discarded because the same sequence was previously created.");
          //          return null;
          // 涓嶅姞杩欎釜鑰屽凡锛屽埆杩斿洖 null
        }
        this.allSequences.put(newSequence.toLongFormString(), newSequence);
        newSequences.add(new ExecutableSequence(newSequence));
      }
    }

    return newSequences;
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
      createNewUniqueSequence() { // TODO鏄惁 instantiated? 1. operation 2. 鍒濆allSequences
    Sequence sourceSequence = Randomness.randomSetMember(this.allSequences.values());
    MutationAnalyzer analyzer =
        new MutationAnalyzer((TraceableSequence) sourceSequence, instantiator);
    // TODO 璇� GenerateMutationOperations 鏀寔骞夸竴鐐圭殑鏁版嵁缁撴瀯鍚
    List<MutationOperation> candidateMutations = new LinkedList<MutationOperation>();
    try {
      analyzer.GenerateMutationOperations(new HashSet<>(this.operations), candidateMutations);
    } catch (DateWtfException e) {
      e.printStackTrace();
    }
    MutationOperation selectedMutation = Randomness.randomMember(candidateMutations);
    TraceableSequence newSequence = selectedMutation.ApplyMutation();

    // 绔熺劧鐢� LongFormString 褰� key 鍚椻�︹�� 鏉ラ槻姝㈤噸澶� // TODO 鐢ㄥ畠 HashCode 闃诧紵
    if (this.allSequences.containsKey(newSequence.toLongFormString())) {
      // TODO 瑕佹部鐢ㄦ棩蹇楁牸寮忕殑璇濓紝灏卞緱浠� MutationOperation 閲屾嬁 TypedOperation
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
