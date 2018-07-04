package randoop.generation.date;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
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
import randoop.util.Log;
import randoop.util.Randomness;

/** Randoop-DATE's "Sequence-based" generator. */
public class DateGenerator extends AbstractGenerator {

//	public int numOfSeqSelected = 1;
//	public int numOfMutSelected = 1;

	/**
	 * <p>
	 * The set of ALL sequences ever generated, including sequences that were
	 * executed and then discarded.
	 *
	 * <p>
	 * This must be ordered by insertion to allow for flaky test history collection
	 * in
	 * {@link randoop.main.GenTests#printSequenceExceptionError(AbstractGenerator, SequenceExceptionError)}.
	 */
	// private final LinkedHashSet<TraceableSequence> allSequences;

	private final Map<String, TraceableSequence> allSequences = new TreeMap<String, TraceableSequence>();

	// The set of all primitive values seen during generation and execution
	// of sequences. This set is used to tell if a new primitive value has
	// been generated, to add the value to the components.
//	private Set<Object> runtimePrimitivesSeen = new LinkedHashSet<>();

	// private final Set<TypedOperation> observers;
	private final TypeInstantiator instantiator;

	// /**
	// * Constructs a generator with the given parameters.
	// *
	// * <p>IDEA 自动从基类 copy 的 doc…… 这是个 good practice 么？
	// *
	// * @param operations statements (e.g. methods and constructors) used to create
	// sequences.
	// Cannot
	// * be null.
	// * @param limits maximum time and number of sequences to generate/output
	// * @param componentManager the component manager to use to store sequences
	// during
	// component-based
	// * generation. Can be null, in which case the generator's component manager is
	// initialized
	// as
	// * {@code new ComponentManager()}.
	// * @param stopper optional, additional stopping criterion for the generator.
	// Can be null.
	// * @param listenerManager manager that stores and calls any listeners to use
	// during
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

	// public DateGenerator(
	// List<TypedOperation> operations,
	// Set<TypedOperation> observers,
	// GenInputsAbstract.Limits limits,
	// ComponentManager componentManager,
	// RandoopListenerManager listenerManager) {
	// this(operations, observers, limits, componentManager, null, listenerManager);
	// }

	public DateGenerator(List<TypedOperation> operations, Set<TypedOperation> observers,
			GenInputsAbstract.Limits limits, ComponentManager componentManager,
			// IStopper stopper,
			RandoopListenerManager listenerManager) {
		super(operations, limits, componentManager, null, listenerManager); // stopper
		for (TypedOperation to : operations) {
			System.out.println("TypedOperation:" + to);
		}
		System.out.println("operations_size:" + operations.size());
		System.out.println("observers_size:" + observers.size());
		// this.observers = observers;
		this.instantiator = componentManager.getTypeInstantiator();
		this.d = new ReplayMemory();
		this.state_action_pool = new StateActionPool(this.instantiator, operations);
		this.q_learn = new QLearning(this.d, this.state_action_pool);
//		initializeRuntimePrimitivesSeen();
	}

	@Override
	public ExecutableSequence step() {
		if (allSequences.isEmpty()) {
			Collection<Sequence> seed_sequences = componentManager.gralSeeds;
			for (Sequence seq : seed_sequences) {
				TraceableSequence new_seq = new TraceableSequence(seq);
				allSequences.put(new_seq.toLongFormString(), new_seq);
			}
			// for (Sequence s : componentManager.gralSeeds) {
			// System.out.println(s);
			// }
		}

		long startTime = System.nanoTime();

		// harmless for current scale
		if (componentManager.numGeneratedSequences() % GenInputsAbstract.clear == 0) {
			componentManager.clearGeneratedSequences();
		}

		// ExecutableSequence eSeq = createNewUniqueSequence(); // make it!
		QTransition transition = null;
		while (transition == null) {
			transition = createNewUniqueSequence();
		}
		ExecutableSequence eSeq = transition.GetExecutableSequence();
		// List<QTransition> transitions = createNewUniqueSequences(numOfSeqSelected,
		// numOfMutSelected);
		// System.out.println(
		// "after ============ List<ExecutableSequence> eSeqs =
		// createNewUniqueSequences(numOfSeqSelected, numOfMutSelected);");
		d.StoreTransition(transition);
		q_learn.QLearn();
		// for (ExecutableSequence eSeq : eSeqs) {
		// if (eSeq == null) {
		// return null;
		// }
		//
		// if (GenInputsAbstract.dontexecute) {
		// this.componentManager.addGeneratedSequence(eSeq.sequence);
		// return null;
		// }
		//
		// setCurrentSequence(eSeq.sequence);
		// }

		long gentime1 = System.nanoTime() - startTime; // rename it

		// System.out.println("Before ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("now_record");
//			Boolean f_v1 = (Boolean)f.get(null);
			f.set(null, Boolean.TRUE);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		eSeq.execute(executionVisitor, checkGenerator);
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("now_record");
//			Boolean f_v2 = (Boolean)f.get(null);
			f.set(null, Boolean.FALSE);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		// System.out.println("After ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);
		// process_execute(eSeqs);

		// for(ExecutableSequence eSeq:eSeqs){
		// eSeq.execute(executionVisitor, checkGenerator);
		// }

		startTime = System.nanoTime(); // reset start time.

//		determineActiveIndices(eSeq);

		if (eSeq.sequence.hasActiveFlags()) {
			componentManager.addGeneratedSequence(eSeq.sequence);
		}

		long gentime2 = System.nanoTime() - startTime; // rename it

		eSeq.gentime = gentime1 + gentime2;

		return eSeq;

		// FFFFFFML? eSeqs.get(0)
		// return null;
	}

//	private void process_execute(List<ExecutableSequence> eSeqs) {
//		List<String> test_cases = new LinkedList<String>();
//		// test_cases.add(currSeq.toString());
//		for (ExecutableSequence eSeq : eSeqs) {
//			test_cases.add(eSeq.toCodeString());
//		}
//		ProcessExecutor exe_ctor = new ProcessExecutor(test_cases);
//		exe_ctor.ExecuteTestCases();
//	}

	/**
	 * only used to judge whether to stop if generated sequences are too many @see
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

//	/**
//	 * ...
//	 *
//	 * <p>
//	 * 1. Uniformly select m distinct sequences from previously generated sequence
//	 * pool
//	 *
//	 * <p>
//	 * 2. For each selected sequence s, uniformly select n distinct mutations
//	 * applicable to s; apply the mutations, producing m*n sequences - some are new
//	 * and others not; add the new ones to this.allSequences
//	 *
//	 * <p>
//	 * 3. Execute the new sequences!
//	 *
//	 * <p>
//	 * 4. Construct at most m*n QTransition-s
//	 *
//	 * @param numOfSeqSelected
//	 *            m
//	 * @param numOfMutSelected
//	 *            n
//	 * @return
//	 */
//	private List<QTransition> createNewUniqueSequences(int numOfSeqSelected, int numOfMutSelected) {
//		// m 
//		ArrayList<TraceableSequence> sourceSequences = new ArrayList<>();
//		for (int i = 0; i < numOfSeqSelected; i++) {
//			sourceSequences.add(Randomness.randomSetMember(this.allSequences.values()));
//			// TODO_ remove duplicate, to realize m *distinct* sequences
//			// TODO_ better: implement Randomness.randomSetMemberN(Collection<T> set, int n)
//		}
//
//		// <= m*n 
//		ArrayList<QTransition> transitions = new ArrayList<>();
//		for (TraceableSequence sourceSequence : sourceSequences) {
//			ArrayList<MutationOperation> candidateMutations = state_action_pool.GetAllActionsOfOneState(sourceSequence);
//			for (int i = 0; i < numOfMutSelected; i++) {
//				MutationOperation selectedMutation = Randomness.randomMember(candidateMutations);
//				int actionIndex = candidateMutations.indexOf(selectedMutation);
//				// TODO_ remove duplicate, to realize n *distinct* mutations
//				// TODO_ better: implement Randomness.randomSetMemberN(Collection<T> set, int n)
//
//				TraceableSequence newSequence = selectedMutation.ApplyMutation();
//				if (this.allSequences.containsKey(newSequence.toLongFormString())) {
//					Log.logLine("Sequence discarded because the same sequence was previously created.");
//				} else {
//					transitions.add(new QTransition(sourceSequence, newSequence, actionIndex));
//					this.allSequences.put(newSequence.toLongFormString(), newSequence);
//				}
//			}
//		}
//		System.out.println("transitions_size:" + transitions.size());
//
//		// process_execute(transitions); // TODO_ how to pass reward
//		for (QTransition tran : transitions) {
//			// fill in the reward
//		}
//		return transitions;
//	}

	/**
	 * Tries to create and execute a new sequence. If the sequence is new (not
	 * already in the specified component manager), then it is executed and added to
	 * the manager's sequences. If the sequence created is already in the manager's
	 * sequences, this method has no effect, and returns null.
	 *
	 * @return a new sequence, or null
	 */
	private QTransition createNewUniqueSequence() { // TODO whether instantiated? 1. operation 2. initial allSequences
		TraceableSequence sourceSequence = Randomness.randomSetMember(this.allSequences.values());
		ArrayList<MutationOperation> candidateMutations = state_action_pool.GetAllActionsOfOneState(sourceSequence);
		MutationOperation selectedMutation = Randomness.randomMember(candidateMutations);
		int actionIndex = candidateMutations.indexOf(selectedMutation);
		TraceableSequence newSequence = selectedMutation.ApplyMutation();
		
		if (this.allSequences.containsKey(newSequence.toLongFormString())) {
			Log.logLine("Sequence discarded because the same sequence was previously created.");
			return null;
		}
		this.allSequences.put(newSequence.toLongFormString(), newSequence);
		
		QTransition qt = new QTransition(sourceSequence, newSequence, actionIndex);
		return qt;
	}

//	/**
//	 * The runtimePrimitivesSeen set contains primitive values seen during
//	 * generation/execution and is used to determine new values that should be added
//	 * to the component set. The component set initially contains a set of primitive
//	 * sequences; this method puts those primitives in this set.
//	 */
//	private void initializeRuntimePrimitivesSeen() {
//		for (Sequence s : componentManager.getAllPrimitiveSequences()) {
//			ExecutableSequence es = new ExecutableSequence(s);
//			es.execute(new DummyVisitor(), new DummyCheckGenerator());
//			NormalExecution e = (NormalExecution) es.getResult(0);
//			Object runtimeValue = e.getRuntimeValue();
//			runtimePrimitivesSeen.add(runtimeValue);
//		}
//	}

	/**
	 * Returns the set of sequences that are included in other sequences to generate
	 * inputs (and, so, are subsumed by another sequence).
	 */
	@Override
	public Set<Sequence> getSubsumedSequences() {
		return new HashSet<Sequence>();
	}
	
}
