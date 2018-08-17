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

import cn.yyx.labtask.runtime.memory.state.BranchNodesState;
import cn.yyx.labtask.test_agent_trace_reader.InfluenceComputer;
import cn.yyx.labtask.test_agent_trace_reader.TraceInfo;
import cn.yyx.labtask.test_agent_trace_reader.TraceReader;
import randoop.ExecutionOutcome;
import randoop.NormalExecution;
import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
import randoop.generation.date.execution.TracePrintController;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.random.RandomSelect;
import randoop.generation.date.random.filter.PseudoVariableSelectFilter;
import randoop.generation.date.runtime.DateRuntime;
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

/** Randoop-DATE's "Sequence-based" generator. */
public class DateGenerator extends AbstractGenerator {
	
	Random random = new Random();
	
	Map<Class<?>, ArrayList<TypedOperation>> for_use_operations = new HashMap<Class<?>, ArrayList<TypedOperation>>();
	Map<TypedOperation, Class<?>> operation_class = new HashMap<TypedOperation, Class<?>>();
	Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence = new HashMap<TypedOperation, InfluenceOfBranchChange>();
	
	Map<PseudoVariable, PseudoSequence> pseudo_variable_headed_sequence = new HashMap<PseudoVariable, PseudoSequence>();
	Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable = new HashMap<Class<?>, ArrayList<PseudoVariable>>();
	Map<PseudoVariable, Class<?>> pseudo_variable_class = new HashMap<PseudoVariable, Class<?>>();
	Map<PseudoVariable, InfluenceOfBranchChange> pseudo_variable_branch_influence = new HashMap<PseudoVariable, InfluenceOfBranchChange>();
	
//	Map<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass> operation_self_state_influence = new HashMap<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass>();
	
	private ArrayList<Sequence> allSequences = new ArrayList<Sequence>();
	
	private Map<String, TraceInfo> recorded_traces = new HashMap<String, TraceInfo>();
	
	// The set of all primitive values seen during generation and execution
	// of sequences. This set is used to tell if a new primitive value has
	// been generated, to add the value to the components.
	// private Set<Object> runtimePrimitivesSeen = new LinkedHashSet<>();
	
	private TypeInstantiator instantiator = null;

	BranchNodesState branch_state = new BranchNodesState();
	InfluenceComputer influence_computer = new InfluenceComputer(branch_state);
	
	public DateGenerator(List<TypedOperation> operations, Set<TypedOperation> observers,
			GenInputsAbstract.Limits limits, ComponentManager componentManager,
			RandoopListenerManager listenerManager) {
		super(operations, limits, componentManager, null, listenerManager); // stopper
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
		allSequences.add(new_seq);
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
		BeforeAfterLinkedSequence n_cmp_sequence = null;
		while (n_cmp_sequence == null) {
			n_cmp_sequence = CreateNewCompareSequence();
		}
		// System.out.println("Before ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);
		
		TraceInfo before_trace = recorded_traces.get(n_cmp_sequence.before_linked_sequence.toParsableString());
		
		ExecutableSequence eSeq = new ExecutableSequence(n_cmp_sequence.after_linked_sequence);
		eSeq.execute(executionVisitor, checkGenerator);
		
		// set up execution outcome.
		int e_size = eSeq.size();
		for (int i=0;i<e_size;i++) {
			ExecutionOutcome e_result = eSeq.getResult(i);
			TypedOperation e_op = n_cmp_sequence.after_linked_sequence.getStatement(i).getOperation();
			PseudoVariable e_pv = n_cmp_sequence.after_linked_sequence.GetPseudoVariable(i);
			if (e_result instanceof NormalExecution) {
				NormalExecution ne = (NormalExecution)e_result;
				Object out_obj = ne.getRuntimeValue();
				Class<?> out_class = out_obj.getClass();
				if (!e_op.getOutputType().isVoid()) {
					ArrayList<PseudoVariable> pvs = class_pseudo_variable.get(out_class);
					if (pvs == null) {
						pvs = new ArrayList<PseudoVariable>();
						class_pseudo_variable.put(out_class, pvs);
					}
					pvs.add(e_pv);
					pseudo_variable_class.put(e_pv, out_class);
				}
			}
		}

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

		// analyze trace and set up information in model.
		String after_trace_info = TracePrintController.GetPrintedTrace();
		TraceInfo after_trace = TraceReader.HandleOneTrace(after_trace_info);
		recorded_traces.put(n_cmp_sequence.after_linked_sequence.toParsableString(), after_trace);
		
		String branch_state_representation_before = branch_state.RepresentationOfUnCoveredBranchWithState();
		Map<String, Double> all_branches_influences = influence_computer.BuildGuidedModel(before_trace, after_trace);
		String branch_state_representation_after = branch_state.RepresentationOfUnCoveredBranchWithState();
		if (!branch_state_representation_before.equals(branch_state_representation_after)) {
			allSequences.add(n_cmp_sequence.after_linked_sequence);
		}
		
		InfluenceOfBranchChange branch_influence_operation = typed_operation_branch_influence.get(n_cmp_sequence.operation);
		if (branch_influence_operation == null) {
			branch_influence_operation = new InfluenceOfBranchChange();
			typed_operation_branch_influence.put(n_cmp_sequence.operation, branch_influence_operation);
		}
		branch_influence_operation.AddInfluenceOfBranches(all_branches_influences);
		InfluenceOfBranchChange branch_influence_pseudo_variable = pseudo_variable_branch_influence.get(n_cmp_sequence.pseudo_variable);
		if (branch_influence_pseudo_variable == null) {
			branch_influence_pseudo_variable = new InfluenceOfBranchChange();
			pseudo_variable_branch_influence.put(n_cmp_sequence.pseudo_variable, branch_influence_pseudo_variable);
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
		// select useful TypedOperation.
		ArrayList<String> interested_branch = branch_state.GetSortedUnCoveredBranches();
		TypedOperation selected_to = RandomSelect.RandomKeyFromMapByValue(typed_operation_branch_influence, interested_branch, null);
		// after selecting TypedOperation, select suitable variable to mutate which I mean the suitable-variable headed sequence exactly. 
		if (selected_to.isStatic()) {
			// reuse first parameter.
			if (selected_to.getSignatureString().startsWith(DateRuntime.class.getName())) {
				// the static method is defined by us to modify primitives.
				return AppendMethodCallToFirstParameterHeadedSequence(selected_to, interested_branch);
			}
		} else {
			if (selected_to.isConstructorCall() || selected_to.isNonreceivingValue()) {
				// create new sequence
				PseudoSequence ps = new PseudoSequence();
				ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
				List<Type> type_list = TypeTupleToTypeList(selected_to.getInputTypes());
				GenerateInputPseudoVariables(input_pseudo_variables, type_list);
				if (input_pseudo_variables.size() == type_list.size()) {
					LinkedSequence before_linked_sequence = ps.GenerateLinkedSequence();
					PseudoVariable created_pv = ps.Append(selected_to, input_pseudo_variables, pseudo_variable_headed_sequence);
					LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
					return new BeforeAfterLinkedSequence(selected_to, created_pv, before_linked_sequence, after_linked_sequence);
				}
			} else {
				if (selected_to.isMethodCall()) {
					// reuse first parameter.
					return AppendMethodCallToFirstParameterHeadedSequence(selected_to, interested_branch);
				} else {
					new Exception("TypedOperation unsupported: " + selected_to.getSignatureString()).printStackTrace();
					System.exit(1);
				}
			}
		}
		return null;
	}
	
	private void GenerateInputPseudoVariables(ArrayList<PseudoVariable> input_pseudo_variables, List<Type> r_type_list) {
		ArrayList<ArrayList<PseudoVariable>> each_position_candidates = new ArrayList<ArrayList<PseudoVariable>>();
		Iterator<Type> r_t_itr = r_type_list.iterator();
		while (r_t_itr.hasNext()) {
			ArrayList<PseudoVariable> candidates = new ArrayList<PseudoVariable>();
			each_position_candidates.add(candidates);
			Type tp = r_t_itr.next();
			Class<?> tp_runtime_class = tp.getRuntimeClass();
			Set<Class<?>> class_set = class_pseudo_variable.keySet();
			Iterator<Class<?>> citr = class_set.iterator();
			while (citr.hasNext()) {
				Class<?> cls = citr.next();
				if (tp_runtime_class.isAssignableFrom(cls)) {
					candidates.addAll(class_pseudo_variable.get(cls));
				}
			}
		}
		if (each_position_candidates.size() == r_type_list.size()-1) {
			Iterator<ArrayList<PseudoVariable>> ipv_itr = each_position_candidates.iterator();
			while (ipv_itr.hasNext()) {
				ArrayList<PseudoVariable> pvs = ipv_itr.next();
				PseudoVariable param_selected_pv = RandomSelect.RandomPseudoVariableListAccordingToLength(pvs);
				Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
				PseudoVariable cpoied_pv = param_selected_pv.CopySelfInDeepCloneWay(origin_copied_sequence_map, pseudo_variable_headed_sequence);
				input_pseudo_variables.add(cpoied_pv);
			}
		}
	}
	
	private BeforeAfterLinkedSequence AppendMethodCallToFirstParameterHeadedSequence(TypedOperation selected_to, ArrayList<String> interested_branch) {
		PseudoVariableSelectFilter pvsf = new PseudoVariableSelectFilter(operation_class.get(selected_to), pseudo_variable_class);
		PseudoVariable selected_pv = RandomSelect.RandomKeyFromMapByValue(pseudo_variable_branch_influence, interested_branch, pvsf);
		PseudoSequence selected_pv_headed_sequence = pseudo_variable_headed_sequence.get(selected_pv);
		// modify the headed sequence.
		ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
		input_pseudo_variables.add(selected_pv);
		// add parameters.
		TypeTuple input_types = selected_to.getInputTypes();
		List<Type> type_list = TypeTupleToTypeList(input_types);
		List<Type> r_type_list = type_list.subList(1, type_list.size());
		GenerateInputPseudoVariables(input_pseudo_variables, r_type_list);
		// initialize candidates.
		if (input_pseudo_variables.size() == type_list.size()) {
			LinkedSequence before_linked_sequence = selected_pv_headed_sequence.GenerateLinkedSequence();
			selected_pv_headed_sequence.Append(selected_to, input_pseudo_variables, pseudo_variable_headed_sequence);
			LinkedSequence after_linked_sequence = selected_pv_headed_sequence.GenerateLinkedSequence();
			return new BeforeAfterLinkedSequence(selected_to, selected_pv, before_linked_sequence, after_linked_sequence);
		}
		return null;
	}
	
	public List<Type> TypeTupleToTypeList(TypeTuple tt) {
		List<Type> type_list = new ArrayList<Type>();
		Iterator<Type> it_itr = tt.iterator();
		while (it_itr.hasNext()) {
			Type t = it_itr.next();
			type_list.add(t);
		}
		return type_list;
	}
	
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
		LinkedHashSet<Sequence> sequence_set = new LinkedHashSet<Sequence>(allSequences);
		return sequence_set;
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
//	
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
			MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Class.forName(class_name), to);
		}
//		System.out.println("String.class.getName():" + String.class.getName());
		TypedOperation str_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, String.class, str_ob);
		TypedOperation bool_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, boolean.class, bool_ob);
		TypedOperation bool_obj_ob = TypedOperation.forConstructor(Boolean.class.getConstructor(boolean.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Boolean.class, bool_obj_ob);
		TypedOperation char_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.CHAR_TYPE, ' ');
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, char.class, char_ob);
		TypedOperation char_obj_ob = TypedOperation.forConstructor(Character.class.getConstructor(char.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Character.class, char_obj_ob);
		TypedOperation byte_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.BYTE_TYPE, (byte)0);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, byte.class, byte_ob);
		TypedOperation byte_obj_ob = TypedOperation.forConstructor(Byte.class.getConstructor(byte.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Byte.class, byte_obj_ob);
		TypedOperation short_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.SHORT_TYPE, (short)0);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, short.class, short_ob);
		TypedOperation short_obj_ob = TypedOperation.forConstructor(Short.class.getConstructor(short.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Short.class, short_obj_ob);
		TypedOperation int_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, 0);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, int.class, int_ob);
		TypedOperation int_obj_ob = TypedOperation.forConstructor(Integer.class.getConstructor(int.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Integer.class, int_obj_ob);
		TypedOperation long_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.LONG_TYPE, 0L);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, long.class, long_ob);
		TypedOperation long_obj_ob = TypedOperation.forConstructor(Long.class.getConstructor(long.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Long.class, long_obj_ob);
		TypedOperation float_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.FLOAT_TYPE, 0.0f);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, float.class, float_ob);
		TypedOperation float_obj_ob = TypedOperation.forConstructor(Float.class.getConstructor(float.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Float.class, float_obj_ob);
		TypedOperation double_ob = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, (double)0.0);
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, double.class, double_ob);
		TypedOperation double_obj_ob = TypedOperation.forConstructor(Double.class.getConstructor(double.class));
		MapUtil.Insert(typed_operation_branch_influence, operation_class, for_use_operations, Double.class, double_obj_ob);
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
