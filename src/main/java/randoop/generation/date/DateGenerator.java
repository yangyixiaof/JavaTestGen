package randoop.generation.date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import cn.yyx.labtask.runtime.memory.state.BranchNodesState;
import cn.yyx.labtask.test_agent_trace_reader.InfluenceComputer;
import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
import randoop.generation.date.execution.TracePrintController;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.generation.date.test.SequenceGenerator;
import randoop.generation.date.util.MapUtil;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.reflection.TypeInstantiator;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.types.JavaTypes;

/** Randoop-DATE's "Sequence-based" generator. */
public class DateGenerator extends AbstractGenerator {

	// public int numOfSeqSelected = 1;
	// public int numOfMutSelected = 1;

	Random random = new Random();

	/**
	 * the key in the following map is meaning the qualified class name
	 */
	Map<String, LinkedList<TypedOperation>> for_use_operations = new HashMap<String, LinkedList<TypedOperation>>();
	Map<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass> operation_self_state_influence = new HashMap<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass>();
	Map<String, InfluenceOfBranchChangeForClass> class_branch_overview_influence = new HashMap<String, InfluenceOfBranchChangeForClass>();
	
	/**
	 * the keys in the following two maps are meaning the detailed class name
	 */
	private final Map<String, ArrayList<LinkedSequence>> class_object_headed_sequence = new HashMap<String, ArrayList<LinkedSequence>>();
	private final Map<String, ArrayList<LinkedSequenceWithIndex>> class_object_created_sequence_with_index = new HashMap<String, ArrayList<LinkedSequenceWithIndex>>();

	private final Map<String, Sequence> allSequences = new TreeMap<String, Sequence>();
	
	// private final Map<String, TraceableSequence> needExploreSequences = new
	// TreeMap<String, TraceableSequence>();

	// The set of all primitive values seen during generation and execution
	// of sequences. This set is used to tell if a new primitive value has
	// been generated, to add the value to the components.
	// private Set<Object> runtimePrimitivesSeen = new LinkedHashSet<>();
	
	private TypeInstantiator instantiator = null;

	// /**
	// * Constructs a generator with the given parameters.
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

	BranchNodesState branch_state = new BranchNodesState();
	InfluenceComputer influence_computer = new InfluenceComputer(branch_state);

	// ReplayMemory d;
	// StateActionPool state_action_pool;
	// QLearning q_learn;

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
		// for (TypedOperation to : operations) {
		// System.out.println("TypedOperation:" + to);
		// }
		try {
			PreProcessAllTypedOperations(operations);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Iterator<Sequence> cgitr = componentManager.getAllPrimitiveSequences().iterator();
//		while (cgitr.hasNext()) {
//			Sequence s = cgitr.next();
//			Statement stmt = s.getStatement(0);
//			TypedOperation op = stmt.getOperation();
//			for_use_operations.add(op);
//			// System.out.println("ComponentSequence:" + s);
//		}
		// for (TypedOperation to : for_use_operations) {
		// System.out.println("TypedOperation:" + to);
		// }
		// System.out.println("operations_size:" + operations.size());
		// System.out.println("observers_size:" + observers.size());
		// this.observers = observers;
		// initialize allSequences
		LinkedSequence new_seq = SequenceGenerator.GenerateTraceTestExampleSequence();
		allSequences.put(new_seq.toLongFormString(), new_seq);
		// needExploreSequences.put(new_seq.toLongFormString(), new_seq);
		// initialize generation used data
		this.instantiator = componentManager.getTypeInstantiator();
		// this.d = new ReplayMemory();
		// this.state_action_pool = new StateActionPool(this.instantiator,
		// for_use_operations);
		// this.q_learn = new QLearning(this.d, this.state_action_pool);
	}

	@Override
	public ExecutableSequence step() {
		long startTime = System.nanoTime();

		// harmless for current scale
		// if (componentManager.numGeneratedSequences() % GenInputsAbstract.clear == 0)
		// {
		// componentManager.clearGeneratedSequences();
		// }

		// ExecutableSequence eSeq = createNewUniqueSequence(); // make it!
		// QTransition transition = null;
		// while (transition == null) {
		// transition = createNewUniqueSequence();
		// }
		// ExecutableSequence eSeq = transition.GetExecutableSequence();
		// List<QTransition> transitions = createNewUniqueSequences(numOfSeqSelected,
		// numOfMutSelected);
		// System.out.println(
		// "after ============ List<ExecutableSequence> eSeqs =
		// createNewUniqueSequences(numOfSeqSelected, numOfMutSelected);");
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

		ExecutableSequence eSeq = new ExecutableSequence(allSequences.values().iterator().next());

		// System.out.println("Before ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);
		
		eSeq.execute(executionVisitor, checkGenerator);

//		System.out.println("==== execution outcome begin ====");
//		int e_size = eSeq.size();
//		for (int i = 0; i < e_size; i++) {
//			ExecutionOutcome e_outcome = eSeq.getResult(i);
//			System.out.println(e_outcome);
//			if (e_outcome instanceof NormalExecution) {
//				NormalExecution ne = (NormalExecution) e_outcome;
//				Object o = ne.getRuntimeValue();
//				System.out.println("NormalExecution Object Address:" + System.identityHashCode(o));
//			}
//		}
//		System.out.println("==== execution outcome end ====");

		String trace = TracePrintController.GetPrintedTrace();
		System.out.println(System.getProperty("line.separator") + "trace:" + trace);
		System.exit(1);

		// TraceableSequence e_sequence = transition.GetTargetSequence();
		// e_sequence.SetExecutionTrace(TraceReader.HandleOneTrace(trace));
		// TraceableSequence s_sequence = transition.GetSourceSequence();
		// TraceInfo s_t_i = s_sequence.GetTraceInfo();
		// TraceInfo e_t_i = e_sequence.GetTraceInfo();
		//
		// Map<String, Double> all_branches_influences =
		// influence_computer.BuildGuidedModel(s_t_i, e_t_i);
		// transition.SetUpInfluences(all_branches_influences);
		// state_action_pool.BackTraceToStoreDiscountedInfluence(transition,
		// transition.GetInfluences(), 0);
		// System.out.println("After ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);
		// process_execute(eSeqs);

		// for(ExecutableSequence eSeq:eSeqs){
		// eSeq.execute(executionVisitor, checkGenerator);
		// }

		// d.StoreTransition(transition);
		// q_learn.QLearn();

		// determineActiveIndices(eSeq);
		//
		// if (eSeq.sequence.hasActiveFlags()) {
		// componentManager.addGeneratedSequence(eSeq.sequence);
		// }

		eSeq.gentime = System.nanoTime() - startTime;
		return eSeq;
	}

	// private void process_execute(List<ExecutableSequence> eSeqs) {
	// List<String> test_cases = new LinkedList<String>();
	// // test_cases.add(currSeq.toString());
	// for (ExecutableSequence eSeq : eSeqs) {
	// test_cases.add(eSeq.toCodeString());
	// }
	// ProcessExecutor exe_ctor = new ProcessExecutor(test_cases);
	// exe_ctor.ExecuteTestCases();
	// }

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

	// /**
	// * ...
	// *
	// * <p>
	// * 1. Uniformly select m distinct sequences from previously generated sequence
	// * pool
	// *
	// * <p>
	// * 2. For each selected sequence s, uniformly select n distinct mutations
	// * applicable to s; apply the mutations, producing m*n sequences - some are
	// new
	// * and others not; add the new ones to this.allSequences
	// *
	// * <p>
	// * 3. Execute the new sequences!
	// *
	// * <p>
	// * 4. Construct at most m*n QTransition-s
	// *
	// * @param numOfSeqSelected
	// * m
	// * @param numOfMutSelected
	// * n
	// * @return
	// */
	// private List<QTransition> createNewUniqueSequences(int numOfSeqSelected, int
	// numOfMutSelected) {
	// // m
	// ArrayList<TraceableSequence> sourceSequences = new ArrayList<>();
	// for (int i = 0; i < numOfSeqSelected; i++) {
	// sourceSequences.add(Randomness.randomSetMember(this.allSequences.values()));
	// // TODO_ remove duplicate, to realize m *distinct* sequences
	// // TODO_ better: implement Randomness.randomSetMemberN(Collection<T> set, int
	// n)
	// }
	//
	// // <= m*n
	// ArrayList<QTransition> transitions = new ArrayList<>();
	// for (TraceableSequence sourceSequence : sourceSequences) {
	// ArrayList<MutationOperation> candidateMutations =
	// state_action_pool.GetAllActionsOfOneState(sourceSequence);
	// for (int i = 0; i < numOfMutSelected; i++) {
	// MutationOperation selectedMutation =
	// Randomness.randomMember(candidateMutations);
	// int actionIndex = candidateMutations.indexOf(selectedMutation);
	// // TODO_ remove duplicate, to realize n *distinct* mutations
	// // TODO_ better: implement Randomness.randomSetMemberN(Collection<T> set, int
	// n)
	//
	// TraceableSequence newSequence = selectedMutation.ApplyMutation();
	// if (this.allSequences.containsKey(newSequence.toLongFormString())) {
	// Log.logLine("Sequence discarded because the same sequence was previously
	// created.");
	// } else {
	// transitions.add(new QTransition(sourceSequence, newSequence, actionIndex));
	// this.allSequences.put(newSequence.toLongFormString(), newSequence);
	// }
	// }
	// }
	// System.out.println("transitions_size:" + transitions.size());
	//
	// // process_execute(transitions); // TODO_ how to pass reward
	// for (QTransition tran : transitions) {
	// // fill in the reward
	// }
	// return transitions;
	// }

	// /**
	// * Tries to create and execute a new sequence. If the sequence is new (not
	// * already in the specified component manager), then it is executed and added
	// to
	// * the manager's sequences. If the sequence created is already in the
	// manager's
	// * sequences, this method has no effect, and returns null.
	// *
	// * @return a new sequence, or null
	// */
	// private QTransition createNewUniqueSequence() { // TODO whether instantiated?
	// 1. operation 2. initial allSequences
	// Map<String, Double> sorted_uncovered_branches =
	// branch_state.GetSortedUnCoveredBranches();
	// Map<String, Integer> uncovered_branch_states =
	// branch_state.GetUnCoveredBranchesStates();
	// // System.out.println("uncovered_branch_states.size():" +
	// // uncovered_branch_states.size());
	// double fitness_upper = 0.0;
	// Collection<TraceableSequence> trace_seqs = needExploreSequences.values();
	// ArrayList<TraceableSequence> o_seqs = new ArrayList<TraceableSequence>();
	// ArrayList<Double> o_seq_up_bounds = new ArrayList<Double>();
	// Iterator<TraceableSequence> ts_itr = trace_seqs.iterator();
	// while (ts_itr.hasNext()) {
	// TraceableSequence ts = ts_itr.next();
	// o_seqs.add(ts);
	// double fitness = ts.GetTraceInfo().Fitness(sorted_uncovered_branches,
	// uncovered_branch_states);
	// fitness_upper = fitness_upper + fitness;
	// o_seq_up_bounds.add(fitness_upper);
	// }
	// // sample state
	// double fall_position = random.nextDouble() * fitness_upper;
	// int ps = 0;
	// int pmax = o_seqs.size();
	// for (; ps < pmax; ps++) {
	// if (fall_position <= o_seq_up_bounds.get(ps)) {
	// break;
	// }
	// }
	// Assert.isTrue(ps < pmax, "ps:" + ps + "pmax:" + pmax);
	// TraceableSequence sourceSequence = o_seqs.get(ps); //
	// Randomness.randomSetMember(this.allSequences.values());
	// ArrayList<MutationOperation> candidateMutations =
	// state_action_pool.GetUntakenActionsOfOneState(sourceSequence);
	//
	// Iterator<MutationOperation> citr = candidateMutations.iterator();
	//
	// System.out.println("=== start mo ===");
	// while (citr.hasNext()) {
	// MutationOperation mo = citr.next();
	// System.out.println(mo);
	// }
	// System.out.println("=== end mo ===");
	//
	// int arg_max_ab_index = random.nextInt(candidateMutations.size());
	// // System.out.println("candidateMutations.size():" +
	// candidateMutations.size());
	// if (uncovered_branch_states.size() > 0) {
	// Map<String, Map<String, List<Double>>> sig_action_branches =
	// q_learn.QPredict(sourceSequence,
	// candidateMutations, uncovered_branch_states);
	// Map<String, List<Double>> action_branches =
	// sig_action_branches.entrySet().iterator().next().getValue();
	// Set<String> ab_keys = action_branches.keySet();
	// // System.out.println("ab_keys.size():" + ab_keys.size());
	// Iterator<String> ab_itr = ab_keys.iterator();
	// double max_all_q_val = Double.MIN_VALUE;
	// while (ab_itr.hasNext()) {
	// double a_q_val = 0.0;
	// String ab = ab_itr.next();
	// int ab_index = Integer.parseInt(ab);
	// List<Double> branches = action_branches.get(ab);
	// Iterator<Double> b_itr = branches.iterator();
	// Set<String> ubs_keys = uncovered_branch_states.keySet();
	// Iterator<String> ubs_itr = ubs_keys.iterator();
	// while (ubs_itr.hasNext()) {
	// Double q_val = b_itr.next();
	// String ubs_sig = ubs_itr.next();
	// Double branch_weight = sorted_uncovered_branches.get(ubs_sig);
	// a_q_val += branch_weight * q_val;
	// }
	// if (max_all_q_val < a_q_val) {
	// max_all_q_val = a_q_val;
	// arg_max_ab_index = ab_index;
	// }
	// }
	// }
	// MutationOperation selectedMutation =
	// candidateMutations.get(arg_max_ab_index); //
	// Randomness.randomMember(candidateMutations);
	//
	// int action_index = candidateMutations.indexOf(selectedMutation);
	// state_action_pool.ActionOfOneStateBeTaken(sourceSequence, action_index);
	// if (state_action_pool.DoNotHaveUntakenActionsOfOneState(sourceSequence)) {
	// needExploreSequences.remove(sourceSequence.toLongFormString());
	// }
	// TraceableSequence newSequence = selectedMutation.ApplyMutation();
	//
	// if (this.allSequences.containsKey(newSequence.toLongFormString())) {
	// // Log.logLine("Sequence discarded because the same sequence was previously
	// // created.");
	// return null;
	// }
	//
	// this.allSequences.put(newSequence.toLongFormString(), newSequence);
	//
	// QTransition qt = new QTransition(sourceSequence, action_index, newSequence);
	// return qt;
	// }

	// /**
	// * The runtimePrimitivesSeen set contains primitive values seen during
	// * generation/execution and is used to determine new values that should be
	// added
	// * to the component set. The component set initially contains a set of
	// primitive
	// * sequences; this method puts those primitives in this set.
	// */
	// private void initializeRuntimePrimitivesSeen() {
	// for (Sequence s : componentManager.getAllPrimitiveSequences()) {
	// ExecutableSequence es = new ExecutableSequence(s);
	// es.execute(new DummyVisitor(), new DummyCheckGenerator());
	// NormalExecution e = (NormalExecution) es.getResult(0);
	// Object runtimeValue = e.getRuntimeValue();
	// runtimePrimitivesSeen.add(runtimeValue);
	// }
	// }

	/**
	 * Returns the set of sequences that are included in other sequences to generate
	 * inputs (and, so, are subsumed by another sequence).
	 */
	@Override
	public Set<Sequence> getSubsumedSequences() {
		return new HashSet<Sequence>();
	}
	
	private void PreProcessAllTypedOperations(List<TypedOperation> inherit_operations) throws Exception {
		Iterator<TypedOperation> io_itr = inherit_operations.iterator();
		while (io_itr.hasNext()) {
			TypedOperation to = io_itr.next();
			String to_sig = to.getSignatureString();
			// extract qualified class name from signature string.
			String class_name_method_name = to_sig.substring(0, to_sig.indexOf('('));
			String class_name = class_name_method_name.substring(0, class_name_method_name.lastIndexOf('.'));
			MapUtil.Insert(for_use_operations, Class.forName(class_name).getName(), to);
		}
//		System.out.println("String.class.getName():" + String.class.getName());
		TypedOperation str_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");
		MapUtil.Insert(for_use_operations, String.class.getName(), str_ob);
		TypedOperation bool_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);
		MapUtil.Insert(for_use_operations, boolean.class.getName(), bool_ob);
		TypedOperation bool_obj_ob = TypedOperation.forConstructor(Boolean.class.getConstructor(boolean.class));
		MapUtil.Insert(for_use_operations, Boolean.class.getName(), bool_obj_ob);
		TypedOperation char_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.CHAR_TYPE, ' ');
		MapUtil.Insert(for_use_operations, char.class.getName(), char_ob);
		TypedOperation char_obj_ob = TypedOperation.forConstructor(Character.class.getConstructor(char.class));
		MapUtil.Insert(for_use_operations, Character.class.getName(), char_obj_ob);
		TypedOperation byte_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.BYTE_TYPE, (byte)0);
		MapUtil.Insert(for_use_operations, byte.class.getName(), byte_ob);
		TypedOperation byte_obj_ob = TypedOperation.forConstructor(Byte.class.getConstructor(byte.class));
		MapUtil.Insert(for_use_operations, Byte.class.getName(), byte_obj_ob);
		TypedOperation short_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.SHORT_TYPE, (short)0);
		MapUtil.Insert(for_use_operations, short.class.getName(), short_ob);
		TypedOperation short_obj_ob = TypedOperation.forConstructor(Short.class.getConstructor(short.class));
		MapUtil.Insert(for_use_operations, Short.class.getName(), short_obj_ob);
		TypedOperation int_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, 0);
		MapUtil.Insert(for_use_operations, int.class.getName(), int_ob);
		TypedOperation int_obj_ob = TypedOperation.forConstructor(Integer.class.getConstructor(int.class));
		MapUtil.Insert(for_use_operations, Integer.class.getName(), int_obj_ob);
		TypedOperation long_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.LONG_TYPE, 0L);
		MapUtil.Insert(for_use_operations, long.class.getName(), long_ob);
		TypedOperation long_obj_ob = TypedOperation.forConstructor(Long.class.getConstructor(long.class));
		MapUtil.Insert(for_use_operations, Long.class.getName(), long_obj_ob);
		TypedOperation float_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.FLOAT_TYPE, 0.0f);
		MapUtil.Insert(for_use_operations, float.class.getName(), float_ob);
		TypedOperation float_obj_ob = TypedOperation.forConstructor(Float.class.getConstructor(float.class));
		MapUtil.Insert(for_use_operations, Float.class.getName(), float_obj_ob);
		TypedOperation double_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, (double)0.0);
		MapUtil.Insert(for_use_operations, double.class.getName(), double_ob);
		TypedOperation double_obj_ob = TypedOperation.forConstructor(Double.class.getConstructor(double.class));
		MapUtil.Insert(for_use_operations, Double.class.getName(), double_obj_ob);
	}

}

class LinkedSequenceWithIndex {

	LinkedSequence seq = null;
	int index = -1;

	public LinkedSequenceWithIndex(LinkedSequence seq, int index) {
		this.seq = seq;
		this.index = index;
	}

}

class InfluenceOfStateChangeForTypedOperationInClass {
	
	int all_count = 0;
	int enter_new_state_change_count = 0;
	int back_to_old_state_change_count = 0;
	
}

class InfluenceOfBranchChangeForClass {
	
	Map<String, Integer> all_count = new HashMap<String, Integer>();
	Map<String, Integer> positive_value_change_count = new HashMap<String, Integer>();
	Map<String, Integer> negative_value_change_count = new HashMap<String, Integer>();
	Map<String, Integer> reach_branch_count = new HashMap<String, Integer>();
	Map<String, Integer> lose_branch_count = new HashMap<String, Integer>();
	
}
