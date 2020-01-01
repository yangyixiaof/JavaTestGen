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

import org.eclipse.core.runtime.Assert;

import cn.yyx.research.bitmap.YYXHaHaStrangeMem;
import randoop.ExceptionalExecution;
import randoop.ExecutionOutcome;
import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
import randoop.generation.date.execution.TracePrintController;
import randoop.generation.date.influence.BranchStateSummary;
import randoop.generation.date.influence.InfluenceOfTraceCompare;
import randoop.generation.date.influence.SimpleInfluenceComputer;
import randoop.generation.date.influence.TraceInfo;
import randoop.generation.date.influence.TraceReader;
import randoop.generation.date.mutation.RandomMutationInfo;
import randoop.generation.date.operation.OperationKind;
import randoop.generation.date.sequence.BeforeAfterLinkedSequence;
import randoop.generation.date.sequence.DisposablePseudoSequence;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.generation.date.sequence.PseudoSequence;
import randoop.generation.date.sequence.PseudoSequenceContainer;
import randoop.generation.date.sequence.PseudoVariable;
import randoop.generation.date.sequence.StringPseudoSequence;
import randoop.generation.date.sequence.helper.SeedHelper;
import randoop.generation.date.sequence.helper.SequenceGeneratorHelper;
import randoop.generation.date.util.MapUtil;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.types.Type;
import randoop.util.SimpleList;

/** Randoop-DATE's "Sequence-based" generator. */
public class DateGenerator extends AbstractGenerator {

	Random random = new Random();

	public int curr_seed_length = 1;
	private int curr_length_seed_left_times = -1;

	// meta data
	public Map<Class<?>, Class<?>> for_use_object_create_sequence_type = new HashMap<Class<?>, Class<?>>();
	public ArrayList<TypedOperation> create_operations = new ArrayList<TypedOperation>();
	// public Map<Class<?>, ArrayList<TypedOperation>>
	// for_use_object_create_operations = new HashMap<Class<?>,
	// ArrayList<TypedOperation>>();
	// public ArrayList<TypedOperation> modify_operations = new
	// ArrayList<TypedOperation>();
	// public Map<Class<?>, ArrayList<TypedOperation>>
	// for_use_object_modify_operations = new HashMap<Class<?>,
	// ArrayList<TypedOperation>>();
	public Map<TypedOperation, Class<?>> operation_class = new HashMap<TypedOperation, Class<?>>();
	// this means this operation should not be the last.
	// public Map<TypedOperation, Boolean> operation_is_hidden = new
	// HashMap<TypedOperation, Boolean>();
	public Map<Class<?>, PseudoVariable> hidden_variables = new HashMap<Class<?>, PseudoVariable>();
	// public Map<TypedOperation, Boolean> operation_is_to_create = new
	// HashMap<TypedOperation, Boolean>();
	// public Map<TypedOperation, Boolean> operation_is_delta_change = new
	// HashMap<TypedOperation, Boolean>();
	public Map<TypedOperation, Boolean> operation_been_created = new HashMap<TypedOperation, Boolean>();

	// influence for typed operation
	// public Map<TypedOperation, InfluenceOfBranchChange>
	// typed_operation_branch_influence = new HashMap<TypedOperation,
	// InfluenceOfBranchChange>();
	// public InfluenceOfBranchChange object_constraint_branch_influence = new
	// InfluenceOfBranchChange();

	// typed operation runtime information
	public Map<TypedOperation, OperationKind> operation_kind = new HashMap<TypedOperation, OperationKind>();

	// runtime information
	// public Map<PseudoVariable, PseudoSequence> pseudo_variable_headed_sequence =
	// new HashMap<PseudoVariable, PseudoSequence>();
	// public Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable = new
	// HashMap<Class<?>, ArrayList<PseudoVariable>>();
	// public Map<PseudoVariable, Class<?>> pseudo_variable_class = new
	// HashMap<PseudoVariable, Class<?>>();
	// public Map<PseudoVariable, String> pseudo_variable_content = new
	// HashMap<PseudoVariable, String>();
	// public Set<PseudoVariable> pseudo_variable_with_null_value = new
	// HashSet<PseudoVariable>();
	// Map<PseudoVariable, BranchValueState> pseudo_variable_branch_value_state =
	// new HashMap<PseudoVariable, BranchValueState>();
	// public HashSet<PseudoSequenceContainer>
	// pseudo_sequence_obligatory_constraint_containers = new
	// HashSet<PseudoSequenceContainer>();
	// public HashSet<PseudoSequenceContainer>
	// pseudo_sequence_optional_constraint_containers = new
	// HashSet<PseudoSequenceContainer>();
	// public HashSet<PseudoSequenceContainer> pseudo_sequence_containers = new
	// HashSet<PseudoSequenceContainer>();
	// public HashSet<PseudoSequenceContainer> pseudo_sequence_containers = new
	// HashSet<PseudoSequenceContainer>();

	// public Map<String, TraceInfo> recorded_traces = new HashMap<String,
	// TraceInfo>();
	public BranchStateSummary branch_state = new BranchStateSummary();

	// Map<TypedOperation, InfluenceOfStateChangeForTypedOperationInClass>
	// operation_self_state_influence = new HashMap<TypedOperation,
	// InfluenceOfStateChangeForTypedOperationInClass>();

	// Set<Type> encountered_types = new HashSet<Type>();
	// TypeInstantiator instantiator = new TypeInstantiator(encountered_types);

	private ArrayList<Sequence> allSequences = new ArrayList<Sequence>();

	// The set of all primitive values seen during generation and execution
	// of sequences. This set is used to tell if a new primitive value has
	// been generated, to add the value to the components.
	// private Set<Object> runtimePrimitivesSeen = new LinkedHashSet<>();
	private static final SimpleList<Statement> empty_statements = new Sequence().statements;

//	public Map<String, PseudoSequenceContainer> content_container_map = new TreeMap<String, PseudoSequenceContainer>();

	// public static final int trying_maximum_steps = 10;
	// int trying_total_steps = 0;
	// int trying_step = 1;
	// int trying_remain_steps = -1;
	PseudoSequenceContainer current_container = null;
	TreeMap<Integer, LinkedList<PseudoSequenceContainer>> containers = new TreeMap<Integer, LinkedList<PseudoSequenceContainer>>();

	// pseudo sequence containers
	// public TreeMap<Integer, HashSet<PseudoSequenceContainer>>
	// mutated_number_pseudo_sequence_container_map = new TreeMap<Integer,
	// HashSet<PseudoSequenceContainer>>();

	public DateGenerator(List<TypedOperation> operations, Set<TypedOperation> observers,
			GenInputsAbstract.Limits limits, ComponentManager componentManager,
			RandoopListenerManager listenerManager) {
		super(operations, limits, componentManager, null, listenerManager); // stopper
		try {
			PreProcessAllTypedOperations(operations);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Iterator<Sequence> cgitr =
		// componentManager.getAllPrimitiveSequences().iterator();
		// while (cgitr.hasNext()) {
		// Sequence s = cgitr.next();
		// Statement stmt = s.getStatement(0);
		// TypedOperation op = stmt.getOperation();
		// for_use_operations.add(op);
		// // System.out.println("ComponentSequence:" + s);
		// }
		// for (TypedOperation to : for_use_operations) {
		// System.out.println("TypedOperation:" + to);
		// }
		// System.out.println("operations_size:" + operations.size());
		// System.out.println("observers_size:" + observers.size());
		// this.observers = observers;
		// initialize allSequences
		// encountered_types.add(Type.forClass(Integer.class));
		// this.instantiator = componentManager.getTypeInstantiator();
		// add simple confuse examples
		// TraceableSequence new_seq =
		// SequenceGenerator.GenerateTraceTestExampleSequence();
		// allSequences.add(new_seq);
		// needExploreSequences.put(new_seq.toLongFormString(), new_seq);
		// initialize generation used data
		// this.d = new ReplayMemory();
		// this.state_action_pool = new StateActionPool(this.instantiator,
		// for_use_operations);
		// this.q_learn = new QLearning(this.d, this.state_action_pool);
	}

	@Override
	public ExecutableSequence step() {

		// try {
		// Thread.sleep(1500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

		long startTime = System.nanoTime();

		// ExecutableSequence test_eSeq = new
		// ExecutableSequence(allSequences.iterator().next());
		// test_eSeq.execute(executionVisitor, checkGenerator);
		// String test_after_trace_info = TracePrintController.GetPrintedTrace();
		// System.out.println();
		// System.out.println("test_after_trace_info:" + test_after_trace_info);
		// System.exit(1);

		// ExecutableSequence eSeq = new ExecutableSequence(lSeq);
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
		// ExecutableSequence eSeq = new
		// ExecutableSequence(allSequences.values().iterator().next());
		BeforeAfterLinkedSequence n_cmp_sequence = null;
		boolean meet_null = true;
		while (true) {
			n_cmp_sequence = CreateNewCompareSequence();
			// debugging code, waiting to be deleted.
			if (n_cmp_sequence != null) {
				// Assert.isTrue(meet_null == false);
				meet_null = false;
				System.out.println("Newly generated sequence:" + n_cmp_sequence.GetAfterLinkedSequence().toCodeString());
//				System.out.println("Newly generated sequence, char form:"
//						+ n_cmp_sequence.GetAfterLinkedSequence().GetPseudoSequenceContainer()
//								.FetchStringPseudoSequence().GetContent()
//						+ "#each char int form:" + n_cmp_sequence.GetAfterLinkedSequence().GetPseudoSequenceContainer()
//								.FetchStringPseudoSequence().GetContentWithTheFormOfEachCharIntegerValue());
				break;
			} else {
				Assert.isTrue(meet_null == true);
				// meet_null = true;
				System.out.println("Failed One Generation! The generated sequence is null!");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		PseudoSequenceContainer previous_container = n_cmp_sequence.GetBeforeLinkedSequence()
				.GetPseudoSequenceContainer();
		PseudoSequenceContainer newly_created_container = n_cmp_sequence.GetAfterLinkedSequence()
				.GetPseudoSequenceContainer();
		// pseudo_sequence_containers.add(newly_created_container);
		// System.out.println("Before ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);

		TraceInfo before_trace = previous_container == null ? null : previous_container.GetTraceInfo();
		// recorded_traces.get(n_cmp_sequence.GetBeforeLinkedSequence().toParsableString());

		// System.out.println("Executing sequence: size:" +
		// n_cmp_sequence.GetAfterLinkedSequence().size() + "#"
		// + n_cmp_sequence.GetAfterLinkedSequence());
		ExecutableSequence eSeq = new ExecutableSequence(n_cmp_sequence.GetAfterLinkedSequence());

		// try {
		// execute sequence

		YYXHaHaStrangeMem.clear();

		eSeq.execute(executionVisitor, checkGenerator);
		
		// } catch (Exception e) {
		// System.err.println("=== not flaky ===");
		// e.printStackTrace();
		// System.exit(1);
		// }

		// System.out.println("==== execution outcome begin ====");
		// int e_size = eSeq.size();
		// for (int i = 0; i < e_size; i++) {
		// ExecutionOutcome e_outcome = eSeq.getResult(i);
		// System.out.println(e_outcome);
		// if (e_outcome instanceof NormalExecution) {
		// NormalExecution ne = (NormalExecution) e_outcome;
		// Object o = ne.getRuntimeValue();
		// System.out.println("NormalExecution Object Address:" +
		// System.identityHashCode(o));
		// }
		// }
		// System.out.println("==== execution outcome end ====");

		// analyze trace and compute influence.
		String after_trace_info = TracePrintController.GetPrintedTrace();
//		String after_trace_sig = YYXHaHaStrangeMem.GetSig();
		
		// System.out.println("one trace:" + after_trace_info);
		// if (!after_trace_info.equals("")) {
		// System.exit(1);
		// }

		// ArrayList<TraceInfo> after_traces =
		// TraceReader.HandleOneTrace(after_trace_info);
		// System.out.println("=== print traces begin ===");
		// for (TraceInfo a_t_i : after_traces) {
		// System.out.println("one_a_t_i:" + a_t_i);
		// }
		// System.out.println("=== print traces end ===");
		// newly_created_container.SetTraceInfo(after_traces);
		// TraceInfo after_trace = after_traces.get(after_traces.size() - 1);
		// recorded_traces.put(n_cmp_sequence.GetAfterLinkedSequence().toParsableString(),
		// after_trace);
		
		TraceInfo after_trace = TraceReader.HandleOneTrace(after_trace_info);
		String after_trace_sig = after_trace.GetTraceSignature();
//		String after_trace_content = after_trace.GetTraceContent();
//		after_trace.SetTraceSignature(after_trace_sig);
//		System.out.println(" ========= begin printing after_trace_content ========= ");
//		System.out.println("after_trace_content:" + after_trace_sig);
//		System.out.print(after_trace_info);
//		System.out.println(" ========= end printing after_trace_content ========= ");
		boolean after_trace_first_encounter = branch_state.StateFirstEncountered(after_trace_sig);
		if (after_trace_first_encounter) {
			allSequences.add(n_cmp_sequence.GetAfterLinkedSequence());
			SeedHelper.SeedIsVeryInteresting(this, newly_created_container);
		}

		// String branch_state_representation_before =
		// branch_state.RepresentationOfUnCoveredBranchWithState();

		// RandomMutationInfo rmi = n_cmp_sequence.GetRandomMutationInfo();
		// String newly_content =
		// newly_created_container.FetchStringPseudoSequence().GetContent();
		//// LinkedList<PseudoSequenceContainer> psc_ll =
		// containers.get(newly_created_container.GetStringLength());
		//// Assert.isTrue(psc_ll != null);
		// if (!after_trace_first_encounter && rmi != null &&
		// !content_container_map.containsKey(newly_content)) {
		// double prob = rmi.GetProbToAddRandomMutate();
		// SeedHelper.SeedIsInteresting(this, newly_created_container, prob);
		// }

		InfluenceOfTraceCompare all_branches_influences = SimpleInfluenceComputer.BuildGuidedModel(branch_state,
				n_cmp_sequence.GetMutation(), before_trace, after_trace);
		newly_created_container.SetTraceInfo(after_trace);
		// if (previous_container != null) {
		// previous_container.AddRecentInfluence(newly_created_container,
		// all_branches_influences);
		// }
		System.out
				.println(all_branches_influences == null ? "PrintInfluence! null" : all_branches_influences.toString());
		n_cmp_sequence.SetInfluence(all_branches_influences);
		// newly_created_container.AddRecentInfluence(before_trace,
		// all_branches_influences);

		// n_cmp_sequence.GetPseudoSequence().SetAllBranchesInfluencesComparedToPrevious(all_branches_influences);
		// String branch_state_representation_after =
		// branch_state.RepresentationOfUnCoveredBranchWithState();
		// if
		// (!branch_state_representation_before.equals(branch_state_representation_after))
		// {
		// allSequences.add(n_cmp_sequence.GetAfterLinkedSequence());
		// }

		// TypedOperation applied_to = n_cmp_sequence.GetTypedOperation();
		// set up influence of the modified TypedOperation
		// if (applied_to == null) {
		// object constraint
		// object_constraint_branch_influence.AddInfluenceOfBranches(all_branches_influences);
		// } else {
		// typed operation
		// InfluenceOfBranchChange branch_influence_operation =
		// typed_operation_branch_influence
		// .get(n_cmp_sequence.GetTypedOperation());
		// if (branch_influence_operation != null)
		// branch_influence_operation.AddInfluenceOfBranches(all_branches_influences);
		// }

		// if (branch_influence_operation == null) {
		// branch_influence_operation = new InfluenceOfBranchChange();
		// typed_operation_branch_influence.put(n_cmp_sequence.GetTypedOperation(),
		// branch_influence_operation);
		// }
		// set up value state of PseudoVariable headed sequence
		// if (branch_influence_operation != null) {
		// }
		// BranchValueState branch_value_state_pseudo_variable =
		// pseudo_variable_branch_value_state
		// .get(n_cmp_sequence.GetPseudoVariable());
		// Assert.isTrue(branch_value_state_pseudo_variable == null);
		// BranchValueState branch_v_stat =
		// SimpleInfluenceComputer.CreateBranchValueState(after_trace);
		// pseudo_variable_branch_value_state.put(n_cmp_sequence.GetPseudoVariable(),
		// branch_v_stat);

		// check whether the outcome has exception.

		int e_size = eSeq.size();
		boolean running_with_exception = false;
		for (int i = 0; i < e_size; i++) {
			ExecutionOutcome e_result = eSeq.getResult(i);
			// System.out.println("e_result:" + e_result);
			TypedOperation e_op = n_cmp_sequence.GetAfterLinkedSequence().getStatement(i).getOperation();
			PseudoVariable e_pv = n_cmp_sequence.GetAfterLinkedSequence().GetPseudoVariable(i);
			if (!e_op.getOutputType().isVoid() && !e_pv.sequence.getClass().equals(DisposablePseudoSequence.class)) {
				// System.out.println("=== executed! ===");
				if (e_result instanceof ExceptionalExecution) {
					// ExceptionalExecution ee = (ExceptionalExecution)e_result;
					running_with_exception = true;
					break;
					// System.out.println("Encountering exceptional execution! The system will
					// stop!");
					// System.exit(1);
				}
			}
		}

		// if (running_with_exception) {
		// // remove all necessary created objects
		// pseudo_sequence_containers.remove(newly_created_container);
		// for (int i = 0; i < e_size; i++) {
		// TypedOperation e_op =
		// n_cmp_sequence.GetAfterLinkedSequence().getStatement(i).getOperation();
		// PseudoVariable e_pv =
		// n_cmp_sequence.GetAfterLinkedSequence().GetPseudoVariable(i);
		// if (!e_op.getOutputType().isVoid()
		// && !e_pv.sequence.getClass().equals(DisposablePseudoSequence.class)) {
		// pseudo_variable_headed_sequence.remove(e_pv);
		// }
		// }
		// } else
		// {
		// set up execution outcome.
		// Map<Integer, PseudoVariable> address_variable_map = new HashMap<Integer,
		// PseudoVariable>();
		// for (int i = 0; i < e_size; i++) {
		// ExecutionOutcome e_result = eSeq.getResult(i);
		// System.out.println("e_result:" + e_result);
		// TypedOperation e_op =
		// n_cmp_sequence.GetAfterLinkedSequence().getStatement(i).getOperation();
		// PseudoVariable e_pv =
		// n_cmp_sequence.GetAfterLinkedSequence().GetPseudoVariable(i);
		// if (!e_op.getOutputType().isVoid() &&
		// !e_pv.sequence.getClass().equals(DisposablePseudoSequence.class)) {
		// if (e_result instanceof NormalExecution) {
		// System.out.println("=== normally executed! ===");
		// NormalExecution ne = (NormalExecution) e_result;
		// Object out_obj = ne.getRuntimeValue();
		// if (out_obj != null) {
		// int out_obj_address = System.identityHashCode(out_obj);
		// address_variable_map.put(out_obj_address, e_pv);
		// Class<?> out_class = out_obj.getClass();
		// System.out.println("out_obj:" + out_obj);
		// System.out.println("out_class:" + out_class);
		// encountered_types.add(Type.forClass(out_class));
		// ArrayList<PseudoVariable> pvs = class_pseudo_variable.get(out_class);
		// if (pvs == null) {
		// pvs = new ArrayList<PseudoVariable>();
		// class_pseudo_variable.put(out_class, pvs);
		// }
		// pvs.add(e_pv);
		// pseudo_variable_class.put(e_pv, out_class);
		// System.out.println("e_pv:" + e_pv + "#out_class:" + out_class);
		// pseudo_variable_content.put(e_pv, out_obj.toString());
		// if (!e_pv.equals(n_cmp_sequence.GetPseudoVariable())) {
		// BranchValueState e_pv_branch_value_state =
		// pseudo_variable_branch_value_state.get(e_pv);
		// Assert.isTrue(e_pv_branch_value_state == null);
		// pseudo_variable_branch_value_state.put(e_pv, branch_v_stat);
		// }
		// } else {
		// pseudo_variable_with_null_value.add(e_pv);
		// }
		// }
		// }
		// }

		// set up influence for this operation
		// Mutated mutated = n_cmp_sequence.GetMutated();
		// if (mutated instanceof ObjectConstraintMutated) {
		// do nothing now.
		// }
		// if (mutated instanceof TypedOperationMutated) {
		// TypedOperationMutated tom = (TypedOperationMutated) mutated;
		// if (tom.HasReturnedPseudoVariable()) {
		// PseudoVariable pv = tom.GetReturnedPseudoVariable();
		// Class<?> cls = pseudo_variable_class.get(pv);
		// if (cls != null) {
		// PseudoSequence selected_pv_headed_sequence =
		// pseudo_variable_headed_sequence.get(pv);
		// if (selected_pv_headed_sequence == null) {
		// Set<Class<?>> base_classes = for_use_object_create_sequence_type.keySet();
		// Set<Class<?>> classes = ClassUtil.GetSuperClasses(base_classes, cls);
		// if (classes.size() > 1) {
		// classes.remove(Object.class);
		// }
		// Class<?> sequence_type =
		// for_use_object_create_sequence_type.get(classes.iterator().next());
		// selected_pv_headed_sequence = CreatePseudoSequence(sequence_type);
		// selected_pv_headed_sequence.SetHeadedVariable(pv);
		// selected_pv_headed_sequence.SetContainer(newly_created_container);
		// newly_created_container.AddPseudoSequence(selected_pv_headed_sequence);
		// pseudo_variable_headed_sequence.put(pv, selected_pv_headed_sequence);
		// }
		// if (tom instanceof DeltaChangeTypedOperationMutated) {
		// ((DeltaChangePseudoSequence) selected_pv_headed_sequence)
		// .SetAllBranchesInfluencesComparedToPrevious(all_branches_influences);
		// }
		// }
		// }
		// if (tom.IsMutatingVariable()) {
		// PseudoVariable in_use_var = tom.GetInUseMutatedPseudoVariable();
		// Class<?> in_use_var_cls = pseudo_variable_class.get(in_use_var);
		// if (in_use_var_cls != null) {
		// PseudoSequence in_use_var_headed_sequence =
		// pseudo_variable_headed_sequence.get(in_use_var);
		// Assert.isTrue(in_use_var_headed_sequence != null);
		// in_use_var_headed_sequence.AddInfluenceOfBranchesForHeadedVariable(all_branches_influences);
		// }
		// }
		// }
		// process object address related constraints
		// ProcessObjectAddressConstraintToPseudoVariableConstraint(after_trace,
		// newly_created_container,
		// address_variable_map);
		// if (after_trace.BranchesExistInTrace()) {
		// int sl = newly_created_container.GetStringLength();
		// // System.out.println("sl:" + sl);
		// LinkedList<PseudoSequenceContainer> c_arr = containers.get(sl);
		// if (c_arr == null) {
		// c_arr = new LinkedList<PseudoSequenceContainer>();
		// containers.put(sl, c_arr);
		// }
		// c_arr.add(newly_created_container);
		// }
		if (running_with_exception) {// && !newly_created_container.HasUnsolvedObligatoryConstraint()
			// pseudo_sequence_obligatory_constraint_containers.add(newly_created_container);
			// pseudo_sequence_containers.remove(newly_created_container);
			// for (int i = 0; i < e_size; i++) {
			// TypedOperation e_op =
			// n_cmp_sequence.GetAfterLinkedSequence().getStatement(i).getOperation();
			// PseudoVariable e_pv =
			// n_cmp_sequence.GetAfterLinkedSequence().GetPseudoVariable(i);
			// if (!e_op.getOutputType().isVoid()
			// && !e_pv.sequence.getClass().equals(DisposablePseudoSequence.class)) {
			// pseudo_variable_headed_sequence.remove(e_pv);
			// Class<?> cls = pseudo_variable_class.remove(e_pv);
			// pseudo_variable_content.remove(e_pv);
			// if (cls != null) {
			// ArrayList<PseudoVariable> pvs = class_pseudo_variable.get(cls);
			// pvs.remove(e_pv);
			// } else {
			// pseudo_variable_with_null_value.remove(e_pv);
			// }
			// }
			// }
			// current_container = null;
		}
		// else {
		// int mutated_number = newly_created_container.GetMutatedNumber();
		// HashSet<PseudoSequenceContainer> sequence_set =
		// mutated_number_pseudo_sequence_container_map
		// .get(mutated_number);
		// if (sequence_set == null) {
		// sequence_set = new HashSet<PseudoSequenceContainer>();
		// mutated_number_pseudo_sequence_container_map.put(mutated_number,
		// sequence_set);
		// }
		// sequence_set.add(newly_created_container);
		// TypedOperation ended_to = newly_created_container.GetEndedTypedOperation();
		// OperationKind ok = operation_kind.get(ended_to);
		// if (ok == null) {
		// ok = OperationKind.unknown;
		// }
		// if (newly_created_container.HasBranches()) {
		// ok = ok.BitOr(OperationKind.branch);
		// } else {
		// ok = ok.BitOr(OperationKind.no_branch);
		// }
		// operation_kind.put(ended_to, ok);
		// current_container = newly_created_container;
		// }
		// if (newly_created_container.HasUnsolvedConstraint()) {
		// pseudo_sequence_optional_constraint_containers.add(newly_created_container);
		// pseudo_sequence_containers.remove(newly_created_container);
		// }
		// }

		// System.out.println(System.getProperty("line.separator") + "trace:" + trace);
		// System.exit(1);

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

		System.out.println("### find paths: " + allSequences.size() + " ###");

		eSeq.gentime = System.nanoTime() - startTime;
		return eSeq;
	}

	// private void
	// ProcessObjectAddressConstraintToPseudoVariableConstraint(TraceInfo info,
	// PseudoSequenceContainer container, Map<Integer, PseudoVariable>
	// address_variable_map) {
	// LinkedList<ObjectAddressConstraint> obcs = info.GetObligatoryConstraint();
	// for (ObjectAddressConstraint oac : obcs) {
	// PseudoVariableConstraint pvc =
	// HandleOneObjectAddressConstraint(address_variable_map, oac);
	// if (pvc != null) {
	// container.AddObligatoryConstraint(pvc);
	// }
	// }
	// LinkedList<ObjectAddressConstraint> opcs = info.GetOptionalConstraint();
	// for (ObjectAddressConstraint oac : opcs) {
	// PseudoVariableConstraint pvc =
	// HandleOneObjectAddressConstraint(address_variable_map, oac);
	// if (pvc != null) {
	// container.AddOptionalConstraint(pvc);
	// }
	// }
	// }
	//
	// private PseudoVariableConstraint
	// HandleOneObjectAddressConstraint(Map<Integer, PseudoVariable>
	// address_variable_map,
	// ObjectAddressConstraint oac) {
	// if (oac instanceof ObjectAddressTypeConstraint) {
	// ObjectAddressTypeConstraint oatc = (ObjectAddressTypeConstraint) oac;
	// int ad = oatc.GetObjectAddress();
	// Class<?> spec_type = oatc.GetType();
	// PseudoVariable pv = address_variable_map.get(ad);
	// if (pv != null) {
	// Class<?> pv_cls = pseudo_variable_class.get(pv);
	// if (pv_cls != null) {
	// boolean type_match = spec_type.isAssignableFrom(pv_cls);
	// if (oatc.IsObligatory() && type_match) {
	// // do nothing.
	// } else {
	// return new PseudoVariableTypeConstraint(pv, spec_type, !type_match);
	// }
	// }
	// }
	// }
	// if (oac instanceof ObjectAddressSameConstraint) {
	// ObjectAddressSameConstraint oasc = (ObjectAddressSameConstraint) oac;
	// int ad1 = oasc.GetAddressOne();
	// int ad2 = oasc.GetAddressTwo();
	// PseudoVariable pv1 = address_variable_map.get(ad1);
	// PseudoVariable pv2 = address_variable_map.get(ad2);
	// if (pv1 != null && pv2 != null) {
	// return new PseudoVariableAddressSameConstraint(pv1, pv2);
	// }
	// }
	// return null;
	// }

	private BeforeAfterLinkedSequence CreateNewCompareSequence() {
		// select create operations.
		Assert.isTrue(operation_been_created.size() <= create_operations.size());
		if (operation_been_created.size() < create_operations.size()) {
			// try create operation one by one.
			// create new sequence
			for (TypedOperation to : create_operations) {
				if (!operation_been_created.containsKey(to)) {
					Class<?> sequence_type = GetSequenceTypeFromTypedOperation(to);
					BeforeAfterLinkedSequence new_created_sequence = CreatePseudoSequenceWithCreateOperation(
							sequence_type, to);
					return new_created_sequence;
				}
			}
		}
		// select useful TypedOperation.
		// if (selected_to != null)
		// {
		// TypedOperation could_use_to = selected_to;
		// if (selected_to.isGeneric() || selected_to.hasWildcardTypes()) {
		// System.out.println();
		// System.out.println("selected typed operation:" + selected_to.getClass());
		// could_use_to = instantiator.instantiate((TypedClassOperation) selected_to);
		// System.out.println("instantiated typed operation is generic or wild? " +
		// (could_use_to.isGeneric() || could_use_to.hasWildcardTypes()) +
		// "#instantiated typed operation:" + could_use_to);
		// System.exit(1);
		// }
		// ArrayList<String> interested_branch =
		// branch_state.GetSortedUnCoveredBranches();
		// System.out.println("interested branch size:" + interested_branch.size());
		// for (String ib : interested_branch) {
		// System.out.println("interested branch:" + ib);
		// }
		// if (interested_branch.size() > 0) {
		// System.out.println("encountering interested branch!");
		// System.exit(1);
		// }
		// if () {
		//
		// } else
		// {
		// if (selected_to.isStatic()) {
		// create a new one, append to last, if implemented, add the
		// mechanism for API sequence (object state).
		// } else
		// {
		// List<Mutation> mutations = new LinkedList<Mutation>();
		// if (mutations.size() == 0) {
		// if (Math.random() > 0.6) {
		// // handle obligatory constraint
		// PseudoSequenceContainer selected_container = (PseudoSequenceContainer)
		// RandomSelect
		// .RandomElementFromSetByRewardableElements(
		// pseudo_sequence_obligatory_constraint_containers, interested_branch, null);
		// if (selected_container != null) {
		// if (selected_container.HasUnsolvedObligatoryConstraint()) {
		// mutations.add(selected_container.GenerateObligatoryObjectConstraintMutation(
		// object_constraint_branch_influence));
		// } else {
		// pseudo_sequence_obligatory_constraint_containers.remove(selected_container);
		// }
		// }
		// }
		// }
		// if (mutations.size() == 0) {
		// if (Math.random() > 0.6) {
		// PseudoSequenceContainer selected_container = (PseudoSequenceContainer)
		// RandomSelect
		// .RandomElementFromSetByRewardableElements(pseudo_sequence_optional_constraint_containers,
		// interested_branch, null);
		// if (selected_container != null) {
		// if (selected_container.HasUnsolvedConstraint()) {
		// mutations.add(selected_container
		// .GenerateObjectConstraintMutation(object_constraint_branch_influence));
		// } else {
		// pseudo_sequence_optional_constraint_containers.remove(selected_container);
		// }
		// }
		// }
		// }
		// if (mutations.size() == 0) {
		// handle non obligatory constraint and typed operation
		// first select a container, then select typed operation
		// PseudoSequenceContainer selected_container = null;

		// if (current_container != null && trying_remain_steps > 0) {
		// trying_remain_steps--;
		// mutations.addAll(GenerateMutationsFromOneContainer(current_container));
		// } else {
		// trying_remain_steps = trying_maximum_steps;
		// while (mutations.size() == 0
		// && trying_total_steps <= mutated_number_pseudo_sequence_container_map.size())
		// {
		// HashSet<PseudoSequenceContainer> containers =
		// mutated_number_pseudo_sequence_container_map
		// .get(trying_total_steps);
		// if (containers == null) {
		// trying_total_steps += trying_step;
		// continue;
		// }
		// for (PseudoSequenceContainer one_container : containers) {
		// mutations.addAll(GenerateMutationsFromOneContainer(one_container));
		// }
		// }
		// }
		if (current_container == null) {
			// TreeMap<Integer, RewardableInteger> rewardables = new TreeMap<Integer,
			// RewardableInteger>();
			// Set<Integer> c_keys = containers.keySet();
			// Iterator<Integer> c_k_itr = c_keys.iterator();
			// while (c_k_itr.hasNext()) {
			// Integer c_k = c_k_itr.next();
			// rewardables.put(c_k, new RewardableInteger(c_k + 5));
			// }
			// Integer c_k = RandomSelect.RandomKeyFromMapByRewardableValue(rewardables,
			// this);
			if (curr_length_seed_left_times == -1) {
				curr_length_seed_left_times = 1;
			}
			LinkedList<PseudoSequenceContainer> all_c_k_cs = containers.get(curr_seed_length);
			// Assert.isTrue(all_c_k_cs != null, "WTF! all_c_k_cs is null?");
			if (all_c_k_cs == null) {
				all_c_k_cs = new LinkedList<PseudoSequenceContainer>();
				containers.put(curr_seed_length, all_c_k_cs);
			}
			if (all_c_k_cs.size() == 0) {// && curr_length_seed_left_times == curr_seed_length
				curr_length_seed_left_times = 0;
				current_container = containers.get(0).get(0);
			} else {
				current_container = all_c_k_cs.remove(0);
			}
			// current_container = (PseudoSequenceContainer) RandomSelect
			// .RandomElementFromSetByRewardableElements(all_c_k_cs, this, null);
			// System.out.println("size of containers: " + containers.size());
			// System.out.println("==== Begin ====");
			// System.out.println("ck container size:" + all_c_k_cs.size() +
			// "#current_container:" + current_container);// + "#The container of selected:"
			// + current_container.FetchStringPseudoSequence()
			System.out.println("=========================== ck container size:" + all_c_k_cs.size()
					+ "#current_container:" + current_container.toString().trim());
//					+ "#The content of selected:"
//					+ current_container.FetchStringPseudoSequence().GetContent().trim() + "#its each char:"
//					+ current_container.FetchStringPseudoSequence().GetContentWithTheFormOfEachCharIntegerValue());
			// System.out.println("==== End ====");
		}
		BeforeAfterLinkedSequence result = null;
		while (result == null) {
			result = current_container.Mutate(this);
		}
		if (result.IsEnd()) {
			// System.out.println("result is null.");
//			int cc_len = current_container.GetStringLength();
//			LinkedList<PseudoSequenceContainer> ctn = containers.get(cc_len);
//			Assert.isTrue(ctn != null, "WTF! container queue is null? cc_len:" + cc_len);
			// System.out.println("remove executed!");
			// ctn.remove(current_container);
			// if (ctn.size() == 0) {
			// System.out.println("container remove executed!");
			// containers.remove(cc_len);
			// }
			// current_container.ResetMutate(this);
			current_container = null;
			// set up tried times
			curr_length_seed_left_times--;
			if (curr_length_seed_left_times <= 0) {
				curr_length_seed_left_times = -1;
				curr_seed_length++;
				if (curr_seed_length > StringPseudoSequence.MAX_SEED_LENGTH) {
					curr_seed_length = 1;
				}
			}
		}
		// else {
		Assert.isTrue(result != null);
		Assert.isTrue(result.GetBeforeLinkedSequence() != null && result.GetAfterLinkedSequence() != null);
		return result;
		// }
		// (PseudoSequenceContainer) RandomSelect
		// .RandomElementFromSetByRewardableElements(pseudo_sequence_containers,
		// interested_branch,
		// null);
		// if (selected_container != null) {
		//
		// }
		// System.out.println("selected_container:" + selected_container);
		// first select a typed operation, then select a container
		// TypedOperation best_op =
		// RandomSelect.RandomKeyFromMapByRewardableValue(typed_operation_branch_influence,
		// interested_branch, null);
		// Class<?> best_op_cls = operation_class.get(best_op);
		// Set<Class<?>> candidates = class_pseudo_variable.keySet();
		// Set<Class<?>> selected_candidates =
		// ClassUtil.GetDescendantClasses(candidates, best_op_cls);
		// ArrayList<PseudoVariable> candidate_pvs = new ArrayList<PseudoVariable>();
		// for (Class<?> selected : selected_candidates) {
		// candidate_pvs.addAll(class_pseudo_variable.get(selected));
		// }
		// ArrayList<PseudoVariable> remove_pvs = new ArrayList<PseudoVariable>();
		// Iterator<PseudoVariable> cpv_itr = candidate_pvs.iterator();
		// while (cpv_itr.hasNext()) {
		// PseudoVariable cpv = cpv_itr.next();
		// PseudoSequence pv_ps = pseudo_variable_headed_sequence.get(cpv);
		// if (pv_ps.OperationHasBeenApplied(best_op)) {
		// remove_pvs.add(cpv);
		// }
		// }
		// candidate_pvs.removeAll(remove_pvs);
		// PseudoVariable selected_var = (PseudoVariable) RandomSelect
		// .RandomElementFromSetByRewardableElements(candidate_pvs, interested_branch,
		// null);
		// HashSet<PseudoVariable> selected_vars = new HashSet<>();
		// selected_vars.add(selected_var);
		// mutations.add(new
		// TypedOperationMutation(typed_operation_branch_influence.get(best_op),
		// best_op, selected_vars));
		// }

		// Mutation one_mutate = (Mutation)
		// RandomSelect.RandomElementFromSetByRewardableElements(mutations,
		// interested_branch, null);
		// if (one_mutate != null) {
		// BeforeAfterLinkedSequence result = one_mutate.Apply(interested_branch, this);
		// return result;
		// }
		// }
		// }
		// }
	}

	public PseudoSequence CreatePseudoSequence(Class<?> sequence_type) {
		PseudoSequence created_sequence = null;
		try {
			// ArrayList.class
			created_sequence = (PseudoSequence) sequence_type.getConstructor().newInstance();
			// for_use_object_modify_operations.get(selected_to_class)
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return created_sequence;
	}

	// public List<Mutation>
	// GenerateMutationsFromOneContainer(PseudoSequenceContainer one_container) {
	// List<Mutation> mutations = new LinkedList<Mutation>();
	// if (one_container.HasUnsolvedObligatoryConstraint()) {
	// mutations.add(one_container.GenerateObligatoryObjectConstraintMutation(object_constraint_branch_influence));
	// } else {
	// if (one_container.HasUnsolvedConstraint()) {
	// mutations.add(one_container.GenerateObjectConstraintMutation(object_constraint_branch_influence));
	// }
	// mutations.addAll(one_container.UntriedMutations(this));
	// }
	// return mutations;
	// }

	private Class<?> GetSequenceTypeFromTypedOperation(TypedOperation selected_to) {
		Class<?> selected_to_class = operation_class.get(selected_to);
		Class<?> sequence_type = for_use_object_create_sequence_type.get(selected_to_class);
		return sequence_type;
	}

	private BeforeAfterLinkedSequence CreatePseudoSequenceWithCreateOperation(Class<?> sequence_type,
			TypedOperation selected_to) {
		List<Type> type_list = SequenceGeneratorHelper.TypeTupleToTypeList(selected_to.getInputTypes());
		ArrayList<PseudoVariable> copied_input_pseudo_variables = new ArrayList<PseudoVariable>();
		ArrayList<PseudoVariable> input_pseudo_variables = SequenceGeneratorHelper
				.GetExactlyMatchedPseudoVariableAsOneList(type_list, hidden_variables);
		if (input_pseudo_variables.size() == type_list.size()) {
			PseudoSequenceContainer container = new PseudoSequenceContainer();
			// SequenceGeneratorHelper.GenerateInputPseudoVariables(candidates, container,
			// input_pseudo_variables,
			// type_list, this);
			PseudoSequence ps = CreatePseudoSequence(sequence_type);
			ps.SetContainer(container);
			container.AddPseudoSequence(ps);
			container.SetEndPseudoSequence(ps);
//			System.out.println(" ======== printing begin! ======== ");
			for (PseudoVariable pv : input_pseudo_variables) {
				HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
				PseudoVariable copied_pv = pv.CopySelfInDeepCloneWay(container, origin_copied_sequence_map, this);
				copied_input_pseudo_variables.add(copied_pv);
//				System.out.println("copied_pv sequence address:" + copied_pv.sequence.hashCode());
			}
//			System.out.println(" ======== printing end! ======== ");
			LinkedSequence before_linked_sequence = new LinkedSequence(null, empty_statements, null);
			PseudoVariable created_pv = ps.Append(selected_to, copied_input_pseudo_variables);// , false
			// ps.SetHeadedVariable(created_pv);
			operation_been_created.put(selected_to, true);
			if (operation_kind.get(selected_to) == OperationKind.no_branch && created_pv != null) {
				Class<?> selected_to_class = operation_class.get(selected_to);
				hidden_variables.put(selected_to_class, created_pv);
				return null;
			}
//			if (container.FetchStringPseudoSequence() == null) {
//				return null;
//			}
			// pseudo_variable_headed_sequence.put(created_pv, ps);
			LinkedSequence after_linked_sequence = container.GetLinkedSequence();
			return new BeforeAfterLinkedSequence(selected_to, null, before_linked_sequence, after_linked_sequence, true,
					new RandomMutationInfo(1.0));
			// new TypedOperationMutated(ps, true, created_pv, true, created_pv)
		}
		return null;
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

	public ArrayList<Sequence> GetAllSequencesInReference() {
		return allSequences;
	}

	public TreeMap<Integer, LinkedList<PseudoSequenceContainer>> GetContainers() {
		return containers;
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
	// private QTransition createNewUniqueSequence() {
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
		// TypedOperation test_trace_simple_branch_invoke =
		// TypedOperation.forMethod(System.class.getMethod("gc"));
		// System.out.println("to:" + test_trace_simple_branch_invoke +
		// "#to.getOutputType():" +
		// test_trace_simple_branch_invoke.getOutputType().isVoid());
		// System.out.println("String.class.getName():" + String.class.getName());
		{
			// primitives creation initialization
			// TypedOperation str_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateString"));
			TypedOperation str_ob = TypedOperation.createPrimitiveInitialization(Type.forClass(String.class), "");
			MapUtil.Insert(str_ob, String.class, StringPseudoSequence.class, true, false, OperationKind.no_branch, this
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence
			);
			// TypedOperation bool_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateBoolean"));
			// MapUtil.Insert(bool_ob, Boolean.class, PseudoSequence.class, true, false,
			// OperationKind.no_branch, this
			// // for_use_object_create_sequence_type,
			// // for_use_object_create_operations, for_use_object_modify_operations,
			// // operation_class,
			// // operation_is_to_create, typed_operation_branch_influence
			// );
			// TypedOperation char_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateCharacter"));
			// MapUtil.Insert(char_ob, Character.class, NumberPseudoSequence.class, true,
			// false, OperationKind.no_branch,
			// this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
			// TypedOperation byte_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateByte"));
			// MapUtil.Insert(byte_ob, Byte.class, NumberPseudoSequence.class, true, false,
			// OperationKind.no_branch, this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
			// TypedOperation short_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateShort"));
			// MapUtil.Insert(short_ob, Short.class, NumberPseudoSequence.class, true,
			// false, OperationKind.no_branch, this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
			// TypedOperation int_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateInteger"));
			// MapUtil.Insert(int_ob, Integer.class, NumberPseudoSequence.class, true,
			// false, OperationKind.no_branch, this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
			// TypedOperation long_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateLong"));
			// MapUtil.Insert(long_ob, Long.class, NumberPseudoSequence.class, true, false,
			// OperationKind.no_branch, this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
			// TypedOperation float_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateFloat"));
			// MapUtil.Insert(float_ob, Float.class, NumberPseudoSequence.class, true,
			// false, OperationKind.no_branch, this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
			// TypedOperation double_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateDouble"));
			// MapUtil.Insert(double_ob, Double.class, NumberPseudoSequence.class, true,
			// false, OperationKind.no_branch,
			// this
			// // for_use_object_create_sequence_type, for_use_object_create_operations,
			// // for_use_object_modify_operations, operation_class, operation_is_to_create,
			// // typed_operation_branch_influence
			// );
		}
		Iterator<TypedOperation> io_itr = inherit_operations.iterator();
		while (io_itr.hasNext()) {
			TypedOperation to = io_itr.next();
			// if (!to.isConstructorCall()) {
			// continue;
			// }
			if (!to.isStatic() || to.toString().startsWith("java.lang.Object")) {
				continue;
			}
			System.out.println("operation is generic or wild? " + (to.isGeneric() || to.hasWildcardTypes())
					+ "#TypedOperation:" + to);
			// System.out.println("to:" + to + "#to.getOutputType():" +
			// to.getOutputType().isVoid());
			String to_sig = to.getSignatureString();
			// extract qualified class name from signature string.
			String class_name_method_name = to_sig.substring(0, to_sig.indexOf('('));
			String class_name = class_name_method_name.substring(0, class_name_method_name.lastIndexOf('.'));
			int wild_idx = class_name.indexOf('<');
			String exclude_generic_class_name = class_name.substring(0, wild_idx < 0 ? class_name.length() : wild_idx);
			MapUtil.Insert(to, Class.forName(exclude_generic_class_name), PseudoSequence.class,
					to.isConstructorCall() || to.isStatic(), false, OperationKind.branch, this);
			// for_use_object_create_sequence_type, create_operations,
			// for_use_object_create_operations, modify_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence
		}
		// {
		// add operations to modify primitives
		// TypedOperation str_modify_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("InsertString", String.class,
		// int.class, int.class));
		// MapUtil.Insert(str_modify_ob, String.class, StringPseudoSequence.class,
		// false, true,
		// OperationKind.no_branch, this);
		// TypedOperation str_modify_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("ModifyString", String.class,
		// Double.class));
		// MapUtil.Insert(str_modify_ob, String.class, StringPseudoSequence.class,
		// false, true,
		// OperationKind.no_branch, this
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence
		// );
		// TypedOperation str_append_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("AppendString", String.class));
		// MapUtil.Insert(str_append_ob, String.class, StringPseudoSequence.class,
		// false, false,
		// OperationKind.no_branch, this
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence
		// );
		// TypedOperation bool_ob =
		// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("not",
		// Boolean.class));
		// MapUtil.Insert(bool_ob, Boolean.class, PseudoSequence.class, false, false,
		// OperationKind.no_branch, this
		// // for_use_object_create_sequence_type,
		// // for_use_object_create_operations, for_use_object_modify_operations,
		// // operation_class,
		// // operation_is_to_create, typed_operation_branch_influence
		// );
		// TypedOperation char_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Character.class,
		// Double.class));
		// MapUtil.Insert(char_ob, Character.class, NumberPseudoSequence.class, false,
		// true, OperationKind.no_branch,
		// this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// TypedOperation byte_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Byte.class,
		// Double.class));
		// MapUtil.Insert(byte_ob, Byte.class, NumberPseudoSequence.class, false, true,
		// OperationKind.no_branch, this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// TypedOperation short_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Short.class,
		// Double.class));
		// MapUtil.Insert(short_ob, Short.class, NumberPseudoSequence.class, false,
		// true, OperationKind.no_branch, this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// TypedOperation int_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Integer.class,
		// Double.class));
		// MapUtil.Insert(int_ob, Integer.class, NumberPseudoSequence.class, false,
		// true, OperationKind.no_branch, this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// TypedOperation long_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Long.class,
		// Double.class));
		// MapUtil.Insert(long_ob, Long.class, NumberPseudoSequence.class, false, true,
		// OperationKind.no_branch, this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// TypedOperation float_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Float.class,
		// Double.class));
		// MapUtil.Insert(float_ob, Float.class, NumberPseudoSequence.class, false,
		// true, OperationKind.no_branch, this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// TypedOperation double_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Double.class,
		// Double.class));
		// MapUtil.Insert(double_ob, Double.class, NumberPseudoSequence.class, false,
		// true, OperationKind.no_branch,
		// this
		// // for_use_object_create_sequence_type, for_use_object_create_operations,
		// // for_use_object_modify_operations, operation_class, operation_is_to_create,
		// // typed_operation_branch_influence
		// );
		// }
	}

}

// class ClassDoubleMapValueComparator implements Comparator<Map.Entry<Class<?>,
// Double>> {
//
// @Override
// public int compare(Map.Entry<Class<?>, Double> me1, Map.Entry<Class<?>,
// Double> me2) {
// return -me1.getValue().compareTo(me2.getValue());
// }
//
// }
