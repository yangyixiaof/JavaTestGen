package randoop.generation.date;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import cn.yyx.labtask.runtime.memory.state.BranchNodesState;
import cn.yyx.labtask.test_agent_trace_reader.InfluenceComputer;
import cn.yyx.labtask.test_agent_trace_reader.TraceInfo;
import cn.yyx.labtask.test_agent_trace_reader.TraceReader;
import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
import randoop.generation.date.execution.TracePrintController;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.generation.date.sequence.PseudoSequence;
import randoop.generation.date.sequence.PseudoVariable;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.generation.date.test.SequenceGenerator;
import randoop.generation.date.util.MapUtil;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.reflection.TypeInstantiator;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.types.JavaTypes;
import randoop.types.Type;
import randoop.types.TypeTuple;
import randoop.util.Randomness;

/** Randoop-DATE's "Sequence-based" generator. */
public class DateGenerator extends AbstractGenerator {

	// public int numOfSeqSelected = 1;
	// public int numOfMutSelected = 1;

	Random random = new Random();
	
	Map<Class<?>, ArrayList<TypedOperation>> for_use_operations = new HashMap<Class<?>, ArrayList<TypedOperation>>();
	Map<TypedOperation, Class<?>> operation_class = new HashMap<TypedOperation, Class<?>>();
	Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence = new HashMap<TypedOperation, InfluenceOfBranchChange>();
	
	Map<PseudoVariable, PseudoSequence> class_object_headed_sequence = new HashMap<PseudoVariable, PseudoSequence>();
	Map<Class<?>, ArrayList<PseudoVariable>> class_object_created_sequence_with_index = new HashMap<Class<?>, ArrayList<PseudoVariable>>();
	
	Map<PseudoVariable, InfluenceOfBranchChange> pseudo_sequence_branch_influence = new HashMap<PseudoVariable, InfluenceOfBranchChange>();
	
//	Map<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass> operation_self_state_influence = new HashMap<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass>();
	
	private Map<String, Sequence> allSequences = new TreeMap<String, Sequence>();
	
	private Map<String, TraceInfo> recorded_traces = new HashMap<String, TraceInfo>();
	
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
		TraceableSequence new_seq = SequenceGenerator.GenerateTraceTestExampleSequence();
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
		
//		ExecutableSequence eSeq = new ExecutableSequence(lSeq);
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
//		ExecutableSequence eSeq = new ExecutableSequence(allSequences.values().iterator().next());
		BeforeAfterLinkedSequence n_cmp_sequence = CreateNewCompareSequence();
		// System.out.println("Before ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);
		
		TraceInfo before_trace = recorded_traces.get(n_cmp_sequence.before_linked_sequence.toParsableString());
		
		ExecutableSequence eSeq = new ExecutableSequence(n_cmp_sequence.after_linked_sequence);
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

		String after_trace_info = TracePrintController.GetPrintedTrace();
		TraceInfo after_trace = TraceReader.HandleOneTrace(after_trace_info);
		recorded_traces.put(n_cmp_sequence.after_linked_sequence.toParsableString(), after_trace);
		
		Map<String, Double> all_branches_influences = influence_computer.BuildGuidedModel(before_trace, after_trace);
		InfluenceOfBranchChange branch_influence_operation = typed_operation_branch_influence.get(n_cmp_sequence.operation);
		if (branch_influence_operation == null) {
			branch_influence_operation = new InfluenceOfBranchChange();
			typed_operation_branch_influence.put(n_cmp_sequence.operation, branch_influence_operation);
		}
		branch_influence_operation.AddInfluenceOfBranches(all_branches_influences);
		InfluenceOfBranchChange branch_influence_pseudo_variable = pseudo_sequence_branch_influence.get(n_cmp_sequence.pseudo_variable);
		if (branch_influence_pseudo_variable == null) {
			branch_influence_pseudo_variable = new InfluenceOfBranchChange();
			pseudo_sequence_branch_influence.put(n_cmp_sequence.pseudo_variable, branch_influence_pseudo_variable);
		}
		branch_influence_pseudo_variable.AddInfluenceOfBranches(all_branches_influences);
		
//		System.out.println(System.getProperty("line.separator") + "trace:" + trace);
//		System.exit(1);

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
	
	private BeforeAfterLinkedSequence CreateNewCompareSequence() {
		ArrayList<String> interested_branch = branch_state.GetSortedUnCoveredBranches();
		ArrayList<TypedOperation> all_typed_operations = new ArrayList<TypedOperation>();
		ArrayList<Double> all_typed_operation_rewards = new ArrayList<Double>();
		double total_rewards = 0.0;
		Set<Class<?>> API_classes = for_use_operations.keySet();
		Iterator<Class<?>> api_c_itr = API_classes.iterator();
		while (api_c_itr.hasNext()) {
			Class<?> c = api_c_itr.next();
			ArrayList<TypedOperation> tos = for_use_operations.get(c);
			all_typed_operations.addAll(tos);
			Iterator<TypedOperation> to_itr = tos.iterator();
			while (to_itr.hasNext()) {
				TypedOperation to = to_itr.next();
				double reward = 0.5;
				InfluenceOfBranchChange to_branch_influence = typed_operation_branch_influence.get(to);
				reward += to_branch_influence.GetReward(interested_branch);
				all_typed_operation_rewards.add(reward);
				total_rewards += reward;
			}
		}
		double select_double = random.nextDouble() * total_rewards;
		int size = all_typed_operation_rewards.size();
		double accumulated_rewards = 0.0;
		int i=0;
		for (;i<size;i++) {
			Double reward = all_typed_operation_rewards.get(i);
			accumulated_rewards += reward;
			if (select_double < accumulated_rewards) {
				break;
			}
		}
		Assert.isTrue(i<size);
		ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
		TypedOperation selected_to = all_typed_operations.get(i);
		TypeTuple input_types = selected_to.getInputTypes();
		Iterator<Type> it_itr = input_types.iterator();
		while (it_itr.hasNext()) {
			Type tp = it_itr.next();
			Class<?> tp_runtime_class = tp.getRuntimeClass();
			Set<Class<?>> class_set = class_object_created_sequence_with_index.keySet();
			ArrayList<Class<?>> valid_class_set = new ArrayList<Class<?>>();
			Iterator<Class<?>> citr = class_set.iterator();
			while (citr.hasNext()) {
				Class<?> cls = citr.next();
				if (tp_runtime_class.isAssignableFrom(cls)) {
					valid_class_set.add(cls);
				}
			}
			Class<?> selected_class = Randomness.randomMember(valid_class_set);
			ArrayList<PseudoVariable> variables = class_object_created_sequence_with_index.get(selected_class);
			if (variables != null && variables.size() > 0) {
				PseudoVariable pv = Randomness.randomMember(variables);
				input_pseudo_variables.add(pv);
			} else {
				break;
			}
		}
		if (input_pseudo_variables.size() == input_types.size()) {
			ArrayList<PseudoVariable> real_input_pseudo_variables = new ArrayList<PseudoVariable>();
			Iterator<PseudoVariable> ipv_itr = input_pseudo_variables.iterator();
			int ipv_index = -1;
			while (ipv_itr.hasNext()) {
				ipv_index++;
				PseudoVariable pv = ipv_itr.next();
				if (!selected_to.isStatic() && !selected_to.isConstructorCall() && selected_to.isMethodCall() && ipv_index == 0) {
					real_input_pseudo_variables.add(pv);
				} else {
					Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
					PseudoVariable cpoied_pv = pv.CopySelfInDeepCloneWay(origin_copied_sequence_map, class_object_headed_sequence);
					real_input_pseudo_variables.add(cpoied_pv);
				}
			}
			PseudoVariable in_handle_v = null;
			PseudoSequence in_handle_v_headed_ps = null;
			if (!selected_to.isStatic() && !selected_to.isConstructorCall() && selected_to.isMethodCall()) {
				PseudoVariable zero_pv = input_pseudo_variables.get(0);
				in_handle_ps = zero_pv.sequence;
			} else {
				in_handle_ps = new PseudoSequence();
			}
			LinkedSequence before_linked_sequence = in_handle_ps.GenerateLinkedSequence();
			if (!selected_to.isStatic() && !selected_to.isConstructorCall() && selected_to.isMethodCall()) {
				in_handle_ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
			} else {
				in_handle_ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
			}
			LinkedSequence after_linked_sequence = in_handle_ps.GenerateLinkedSequence();
			return new BeforeAfterLinkedSequence(selected_to, in_handle_v, before_linked_sequence, after_linked_sequence);
		}
		return null;
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
//		TypedOperation test_trace_simple_branch_invoke = TypedOperation.forMethod(System.class.getMethod("gc"));
//		System.out.println("to:" + test_trace_simple_branch_invoke + "#to.getOutputType():" + test_trace_simple_branch_invoke.getOutputType().isVoid());		
		Iterator<TypedOperation> io_itr = inherit_operations.iterator();
		while (io_itr.hasNext()) {
			TypedOperation to = io_itr.next();
//			System.out.println("to:" + to + "#to.getOutputType():" + to.getOutputType().isVoid());
			String to_sig = to.getSignatureString();
			// extract qualified class name from signature string.
			String class_name_method_name = to_sig.substring(0, to_sig.indexOf('('));
			String class_name = class_name_method_name.substring(0, class_name_method_name.lastIndexOf('.'));
			MapUtil.Insert(operation_class, for_use_operations, Class.forName(class_name), to);
		}
//		System.out.println("String.class.getName():" + String.class.getName());
		TypedOperation str_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");
		MapUtil.Insert(operation_class, for_use_operations, String.class, str_ob);
		TypedOperation bool_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);
		MapUtil.Insert(operation_class, for_use_operations, boolean.class, bool_ob);
		TypedOperation bool_obj_ob = TypedOperation.forConstructor(Boolean.class.getConstructor(boolean.class));
		MapUtil.Insert(operation_class, for_use_operations, Boolean.class, bool_obj_ob);
		TypedOperation char_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.CHAR_TYPE, ' ');
		MapUtil.Insert(operation_class, for_use_operations, char.class, char_ob);
		TypedOperation char_obj_ob = TypedOperation.forConstructor(Character.class.getConstructor(char.class));
		MapUtil.Insert(operation_class, for_use_operations, Character.class, char_obj_ob);
		TypedOperation byte_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.BYTE_TYPE, (byte)0);
		MapUtil.Insert(operation_class, for_use_operations, byte.class, byte_ob);
		TypedOperation byte_obj_ob = TypedOperation.forConstructor(Byte.class.getConstructor(byte.class));
		MapUtil.Insert(operation_class, for_use_operations, Byte.class, byte_obj_ob);
		TypedOperation short_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.SHORT_TYPE, (short)0);
		MapUtil.Insert(operation_class, for_use_operations, short.class, short_ob);
		TypedOperation short_obj_ob = TypedOperation.forConstructor(Short.class.getConstructor(short.class));
		MapUtil.Insert(operation_class, for_use_operations, Short.class, short_obj_ob);
		TypedOperation int_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, 0);
		MapUtil.Insert(operation_class, for_use_operations, int.class, int_ob);
		TypedOperation int_obj_ob = TypedOperation.forConstructor(Integer.class.getConstructor(int.class));
		MapUtil.Insert(operation_class, for_use_operations, Integer.class, int_obj_ob);
		TypedOperation long_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.LONG_TYPE, 0L);
		MapUtil.Insert(operation_class, for_use_operations, long.class, long_ob);
		TypedOperation long_obj_ob = TypedOperation.forConstructor(Long.class.getConstructor(long.class));
		MapUtil.Insert(operation_class, for_use_operations, Long.class, long_obj_ob);
		TypedOperation float_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.FLOAT_TYPE, 0.0f);
		MapUtil.Insert(operation_class, for_use_operations, float.class, float_ob);
		TypedOperation float_obj_ob = TypedOperation.forConstructor(Float.class.getConstructor(float.class));
		MapUtil.Insert(operation_class, for_use_operations, Float.class, float_obj_ob);
		TypedOperation double_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, (double)0.0);
		MapUtil.Insert(operation_class, for_use_operations, double.class, double_ob);
		TypedOperation double_obj_ob = TypedOperation.forConstructor(Double.class.getConstructor(double.class));
		MapUtil.Insert(operation_class, for_use_operations, Double.class, double_obj_ob);
	}

}

class ClassDoubleMapValueComparator implements Comparator<Map.Entry<Class<?>, Double>> {

	@Override
	public int compare(Map.Entry<Class<?>, Double> me1, Map.Entry<Class<?>, Double> me2) {
		return -me1.getValue().compareTo(me2.getValue());
	}

}

class BeforeAfterLinkedSequence {

	TypedOperation operation = null;
	PseudoVariable pseudo_variable = null;
	LinkedSequence before_linked_sequence = null;
	LinkedSequence after_linked_sequence = null;
	
	public BeforeAfterLinkedSequence(TypedOperation operation, PseudoVariable pseudo_variable, LinkedSequence before_linked_sequence, LinkedSequence after_linked_sequence) {
		this.operation = operation;
		this.pseudo_variable = pseudo_variable;
		this.before_linked_sequence = before_linked_sequence;
		this.after_linked_sequence = after_linked_sequence;
	}
	
}
