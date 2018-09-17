package randoop.generation.date;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.ExecutionOutcome;
import randoop.NormalExecution;
import randoop.generation.AbstractGenerator;
import randoop.generation.ComponentManager;
import randoop.generation.RandoopListenerManager;
import randoop.generation.date.execution.TracePrintController;
import randoop.generation.date.influence.BranchNodesState;
import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.influence.ObjectAddressConstraint;
import randoop.generation.date.influence.ObjectAddressSameConstraint;
import randoop.generation.date.influence.ObjectAddressTypeConstraint;
import randoop.generation.date.influence.SimpleInfluenceComputer;
import randoop.generation.date.influence.TraceInfo;
import randoop.generation.date.influence.TraceReader;
import randoop.generation.date.mutation.DeltaChangeTypedOperationMutated;
import randoop.generation.date.mutation.Mutated;
import randoop.generation.date.mutation.Mutation;
import randoop.generation.date.mutation.ObjectConstraintMutated;
import randoop.generation.date.mutation.TypedOperationMutated;
import randoop.generation.date.random.RandomSelect;
import randoop.generation.date.sequence.BeforeAfterLinkedSequence;
import randoop.generation.date.sequence.DeltaChangePseudoSequence;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.generation.date.sequence.PseudoSequence;
import randoop.generation.date.sequence.PseudoSequenceContainer;
import randoop.generation.date.sequence.PseudoVariable;
import randoop.generation.date.sequence.SequenceGeneratorHelper;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.generation.date.sequence.constraint.PseudoVariableAddressSameConstraint;
import randoop.generation.date.sequence.constraint.PseudoVariableConstraint;
import randoop.generation.date.sequence.constraint.PseudoVariableTypeConstraint;
import randoop.generation.date.test.SequenceGenerator;
import randoop.generation.date.util.ClassUtil;
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

	public Map<Class<?>, Class<?>> for_use_object_create_sequence_type = new HashMap<Class<?>, Class<?>>();
	public ArrayList<TypedOperation> create_operations = new ArrayList<TypedOperation>();
	public Map<Class<?>, ArrayList<TypedOperation>> for_use_object_create_operations = new HashMap<Class<?>, ArrayList<TypedOperation>>();
	public ArrayList<TypedOperation> modify_operations = new ArrayList<TypedOperation>();
	public Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations = new HashMap<Class<?>, ArrayList<TypedOperation>>();
	public Map<TypedOperation, Class<?>> operation_class = new HashMap<TypedOperation, Class<?>>();
	public Map<TypedOperation, Boolean> operation_is_to_create = new HashMap<TypedOperation, Boolean>();
	public Map<TypedOperation, Boolean> operation_been_created = new HashMap<TypedOperation, Boolean>();
	public Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence = new HashMap<TypedOperation, InfluenceOfBranchChange>();
	public InfluenceOfBranchChange object_constraint_branch_influence = new InfluenceOfBranchChange();

	public Map<PseudoVariable, PseudoSequence> pseudo_variable_headed_sequence = new HashMap<PseudoVariable, PseudoSequence>();
	public Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable = new HashMap<Class<?>, ArrayList<PseudoVariable>>();
	public Map<PseudoVariable, Class<?>> pseudo_variable_class = new HashMap<PseudoVariable, Class<?>>();
	public Map<PseudoVariable, String> pseudo_variable_content = new HashMap<PseudoVariable, String>();
	// Map<PseudoVariable, BranchValueState> pseudo_variable_branch_value_state =
	// new HashMap<PseudoVariable, BranchValueState>();
	public HashSet<PseudoSequenceContainer> pseudo_sequence_containers = new HashSet<PseudoSequenceContainer>();

	public Map<String, TraceInfo> recorded_traces = new HashMap<String, TraceInfo>();
	public BranchNodesState branch_state = new BranchNodesState();

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
		TraceableSequence new_seq = SequenceGenerator.GenerateTraceTestExampleSequence();
		allSequences.add(new_seq);
		// needExploreSequences.put(new_seq.toLongFormString(), new_seq);
		// initialize generation used data
		// this.d = new ReplayMemory();
		// this.state_action_pool = new StateActionPool(this.instantiator,
		// for_use_operations);
		// this.q_learn = new QLearning(this.d, this.state_action_pool);
	}

	@Override
	public ExecutableSequence step() {
		long startTime = System.nanoTime();
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
		while (n_cmp_sequence == null) {
			n_cmp_sequence = CreateNewCompareSequence();
		}
		PseudoSequenceContainer newly_created_container = n_cmp_sequence.GetAfterLinkedSequence().GetPseudoSequenceContainer();
		pseudo_sequence_containers.add(newly_created_container);
		// System.out.println("Before ------eSeq.execute(executionVisitor,
		// checkGenerator);");
		// System.out.println(eSeq);

		TraceInfo before_trace = recorded_traces.get(n_cmp_sequence.GetBeforeLinkedSequence().toParsableString());

		System.out.println("Executing sequence: size:" + n_cmp_sequence.GetAfterLinkedSequence().size() + "#"
				+ n_cmp_sequence.GetAfterLinkedSequence());
		ExecutableSequence eSeq = new ExecutableSequence(n_cmp_sequence.GetAfterLinkedSequence());
		eSeq.execute(executionVisitor, checkGenerator);

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
		TraceInfo after_trace = TraceReader.HandleOneTrace(after_trace_info);
		recorded_traces.put(n_cmp_sequence.GetAfterLinkedSequence().toParsableString(), after_trace);

		String branch_state_representation_before = branch_state.RepresentationOfUnCoveredBranchWithState();
		Map<String, Influence> all_branches_influences = SimpleInfluenceComputer.BuildGuidedModel(branch_state,
				before_trace, after_trace);
		// n_cmp_sequence.GetPseudoSequence().SetAllBranchesInfluencesComparedToPrevious(all_branches_influences);
		String branch_state_representation_after = branch_state.RepresentationOfUnCoveredBranchWithState();
		if (!branch_state_representation_before.equals(branch_state_representation_after)) {
			allSequences.add(n_cmp_sequence.GetAfterLinkedSequence());
		}
		
		TypedOperation applied_to = n_cmp_sequence.GetTypedOperation();
		// set up influence of the modified TypedOperation
		if (applied_to == null) {
			// object constraint
			object_constraint_branch_influence.AddInfluenceOfBranches(all_branches_influences);
		} else {
			// typed operation
			InfluenceOfBranchChange branch_influence_operation = typed_operation_branch_influence
					.get(n_cmp_sequence.GetTypedOperation());
			if (branch_influence_operation != null)
				branch_influence_operation.AddInfluenceOfBranches(all_branches_influences);
		}
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

		// set up execution outcome.
		Map<Integer, PseudoVariable> address_variable_map = new HashMap<Integer, PseudoVariable>();
		int e_size = eSeq.size();
		for (int i = 0; i < e_size; i++) {
			ExecutionOutcome e_result = eSeq.getResult(i);
			TypedOperation e_op = n_cmp_sequence.GetAfterLinkedSequence().getStatement(i).getOperation();
			PseudoVariable e_pv = n_cmp_sequence.GetAfterLinkedSequence().GetPseudoVariable(i);
			if (!e_op.getOutputType().isVoid()) {
				if (e_result instanceof NormalExecution) {
					NormalExecution ne = (NormalExecution) e_result;
					Object out_obj = ne.getRuntimeValue();
					int out_obj_address = System.identityHashCode(out_obj);
					address_variable_map.put(out_obj_address, e_pv);
					Class<?> out_class = out_obj.getClass();
					// System.out.println("out_obj:" + out_obj);
					// System.out.println("out_class:" + out_class);
					// encountered_types.add(Type.forClass(out_class));
					ArrayList<PseudoVariable> pvs = class_pseudo_variable.get(out_class);
					if (pvs == null) {
						pvs = new ArrayList<PseudoVariable>();
						class_pseudo_variable.put(out_class, pvs);
					}
					pvs.add(e_pv);
					pseudo_variable_class.put(e_pv, out_class);
					pseudo_variable_content.put(e_pv, out_obj.toString());
					// if (!e_pv.equals(n_cmp_sequence.GetPseudoVariable())) {
					// BranchValueState e_pv_branch_value_state =
					// pseudo_variable_branch_value_state.get(e_pv);
					// Assert.isTrue(e_pv_branch_value_state == null);
					// pseudo_variable_branch_value_state.put(e_pv, branch_v_stat);
					// }
				}
			}
		}
		
		// set up influence for this operation
		Mutated mutated = n_cmp_sequence.GetMutated();
		if (mutated instanceof ObjectConstraintMutated) {
			// do nothing now.
		}
		if (mutated instanceof TypedOperationMutated) {
			TypedOperationMutated tom = (TypedOperationMutated)mutated;
			if (tom.HasReturnedPseudoVariable()) {
				PseudoVariable pv = tom.GetReturnedPseudoVariable();
				Class<?> cls = pseudo_variable_class.get(pv);
				if (cls != null) {
					PseudoSequence selected_pv_headed_sequence = pseudo_variable_headed_sequence.get(pv);
					if (selected_pv_headed_sequence == null) {
						Set<Class<?>> base_classes = for_use_object_create_sequence_type.keySet();
						Set<Class<?>> classes = ClassUtil.GetSuperClasses(base_classes, cls);
						if (classes.size() > 1) {
							classes.remove(Object.class);
						}
						Class<?> sequence_type = for_use_object_create_sequence_type.get(classes.iterator().next());
						selected_pv_headed_sequence = CreatePseudoSequence(sequence_type);
						selected_pv_headed_sequence.SetHeadedVariable(pv);
					}
					if (tom instanceof DeltaChangeTypedOperationMutated) {
						((DeltaChangePseudoSequence)selected_pv_headed_sequence).SetAllBranchesInfluencesComparedToPrevious(all_branches_influences);
					}
				}
			}
		}
		
		ProcessObjectAddressConstraintToPseudoVariableConstraint(after_trace, newly_created_container, address_variable_map);
		
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

		eSeq.gentime = System.nanoTime() - startTime;
		return eSeq;
	}
	
	private void ProcessObjectAddressConstraintToPseudoVariableConstraint(TraceInfo info, PseudoSequenceContainer container, Map<Integer, PseudoVariable> address_variable_map) {
		LinkedList<ObjectAddressConstraint> obcs = info.GetObligatoryConstraint();
		for (ObjectAddressConstraint oac : obcs) {
			PseudoVariableConstraint pvc = HandleOneObjectAddressConstraint(address_variable_map, oac);
			if (pvc != null) {
				container.AddObligatoryConstraint(pvc);
			}
		}
		LinkedList<ObjectAddressConstraint> opcs = info.GetOptionalConstraint();
		for (ObjectAddressConstraint oac : opcs) {
			PseudoVariableConstraint pvc = HandleOneObjectAddressConstraint(address_variable_map, oac);
			if (pvc != null) {
				container.AddOptionalConstraint(pvc);
			}
		}
	}
	
	private PseudoVariableConstraint HandleOneObjectAddressConstraint(Map<Integer, PseudoVariable> address_variable_map, ObjectAddressConstraint oac) {
		if (oac instanceof ObjectAddressTypeConstraint) {
			ObjectAddressTypeConstraint oatc = (ObjectAddressTypeConstraint)oac;
			int ad = oatc.GetObjectAddress();
			Class<?> spec_type = oatc.GetType();
			PseudoVariable pv = address_variable_map.get(ad);
			if (pv != null) {
				Class<?> pv_cls = pseudo_variable_class.get(pv);
				if (pv_cls != null) {
					boolean type_match = spec_type.isAssignableFrom(pv_cls);
					if (oatc.IsObligatory() && type_match) {
						// do nothing.
					} else {
						return new PseudoVariableTypeConstraint(pv, spec_type, !type_match);
					}
				}
			}
		}
		if (oac instanceof ObjectAddressSameConstraint) {
			ObjectAddressSameConstraint oasc = (ObjectAddressSameConstraint)oac;
			int ad1 = oasc.GetAddressOne();
			int ad2 = oasc.GetAddressTwo();
			PseudoVariable pv1 = address_variable_map.get(ad1);
			PseudoVariable pv2 = address_variable_map.get(ad2);
			if (pv1 != null && pv2 != null) {
				return new PseudoVariableAddressSameConstraint(pv1, pv2);
			}
		}
		return null;
	}

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
					if (new_created_sequence != null) {
						operation_been_created.put(to, true);
						return new_created_sequence;
					}
				}
			}
		}
		// select useful TypedOperation.
		// if (selected_to != null)
		{
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
			ArrayList<String> interested_branch = branch_state.GetSortedUnCoveredBranches();
			// if () {
			//
			// } else
			{
				// if (selected_to.isStatic()) {
				// TODO 两个选择: create a new one, append to last, if implemented, add the
				// mechanism for .
				// } else
				{
					// first select a container
					PseudoSequenceContainer selected_container = (PseudoSequenceContainer) RandomSelect
							.RandomKeyFromSetByRewardableElement(pseudo_sequence_containers, interested_branch, null);
					// second identify mutation operations in that container and select one
					{
						List<Mutation> mutations = selected_container.UntriedMutations(operation_class,
								for_use_object_modify_operations, typed_operation_branch_influence,
								pseudo_variable_class);
						if (selected_container.HasUnsolvedObligatoryConstraint()) {
							mutations.add(selected_container
									.GenerateObligatoryObjectConstraintMutation(object_constraint_branch_influence));
						}
						if (selected_container.HasUnsolvedConstraint()) {
							mutations.add(selected_container
									.GenerateObjectConstraintMutation(object_constraint_branch_influence));
						}
						Mutation one_mutate = (Mutation) RandomSelect.RandomKeyFromSetByRewardableElement(mutations,
								interested_branch, null);
						BeforeAfterLinkedSequence result = one_mutate.Apply(interested_branch, this);
						return result;
					}
				}
			}
		}
		// return null;
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

	public Class<?> GetSequenceTypeFromTypedOperation(TypedOperation selected_to) {
		Class<?> selected_to_class = operation_class.get(selected_to);
		Class<?> sequence_type = for_use_object_create_sequence_type.get(selected_to_class);
		return sequence_type;
	}

	private BeforeAfterLinkedSequence CreatePseudoSequenceWithCreateOperation(Class<?> sequence_type,
			TypedOperation selected_to) {
		ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
		List<Type> type_list = SequenceGeneratorHelper.TypeTupleToTypeList(selected_to.getInputTypes());
		SequenceGeneratorHelper.GenerateInputPseudoVariables(input_pseudo_variables, type_list, class_pseudo_variable,
				pseudo_variable_headed_sequence);
		if (input_pseudo_variables.size() == type_list.size()) {
			PseudoSequenceContainer container = new PseudoSequenceContainer();
			PseudoSequence ps = CreatePseudoSequence(sequence_type);
			container.AddPseudoSequence(ps);
			LinkedSequence before_linked_sequence = new LinkedSequence(null, empty_statements, null);
			PseudoVariable created_pv = ps.Append(selected_to, input_pseudo_variables, pseudo_variable_headed_sequence);
			ps.SetHeadedVariable(created_pv);
			LinkedSequence after_linked_sequence = container.GenerateLinkedSequence();
			return new BeforeAfterLinkedSequence(selected_to, new TypedOperationMutated(ps, true, created_pv),
					before_linked_sequence, after_linked_sequence);
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
		Iterator<TypedOperation> io_itr = inherit_operations.iterator();
		while (io_itr.hasNext()) {
			TypedOperation to = io_itr.next();
			if (!to.isConstructorCall()) {
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
			MapUtil.Insert(to, Class.forName(exclude_generic_class_name), PseudoSequence.class, to.isConstructorCall(),
					for_use_object_create_sequence_type, create_operations, for_use_object_create_operations,
					modify_operations, for_use_object_modify_operations, operation_class, operation_is_to_create,
					typed_operation_branch_influence);
		}
		// System.out.println("String.class.getName():" + String.class.getName());
		{
			// primitives creation initialization
			// TypedClassOperation str_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateString"));
			// MapUtil.Insert(str_ob, String.class, StringDeltaChangePseudoSequence.class,
			// true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation bool_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateBoolean"));
			// MapUtil.Insert(bool_ob, Boolean.class, PseudoSequence.class, true,
			// for_use_object_create_sequence_type,
			// for_use_object_create_operations, for_use_object_modify_operations,
			// operation_class,
			// operation_is_to_create, typed_operation_branch_influence);
			// TypedOperation char_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateCharacter"));
			// MapUtil.Insert(char_ob, Character.class,
			// NumberDeltaChangePseudoSequence.class, true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation byte_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateByte"));
			// MapUtil.Insert(byte_ob, Byte.class, NumberDeltaChangePseudoSequence.class,
			// true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation short_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateShort"));
			// MapUtil.Insert(short_ob, Short.class, NumberDeltaChangePseudoSequence.class,
			// true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation int_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateInteger"));
			// MapUtil.Insert(int_ob, Integer.class, NumberDeltaChangePseudoSequence.class,
			// true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation long_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateLong"));
			// MapUtil.Insert(long_ob, Long.class, NumberDeltaChangePseudoSequence.class,
			// true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation float_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateFloat"));
			// MapUtil.Insert(float_ob, Float.class, NumberDeltaChangePseudoSequence.class,
			// true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
			// TypedOperation double_ob =
			// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("CreateDouble"));
			// MapUtil.Insert(double_ob, Double.class,
			// NumberDeltaChangePseudoSequence.class, true,
			// for_use_object_create_sequence_type, for_use_object_create_operations,
			// for_use_object_modify_operations, operation_class, operation_is_to_create,
			// typed_operation_branch_influence);
		}
		// {
		// // add operations to modify primitives
		// TypedOperation str_modify_ob = TypedOperation.forMethod(
		// DateRuntimeSupport.class.getMethod("ModifyString", String.class, int.class,
		// Object.class));
		// MapUtil.Insert(str_modify_ob, String.class,
		// StringDeltaChangePseudoSequence.class, false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation str_append_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("AppendString", String.class));
		// MapUtil.Insert(str_append_ob, String.class,
		// StringDeltaChangePseudoSequence.class, false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation bool_ob =
		// TypedOperation.forMethod(DateRuntimeSupport.class.getMethod("not",
		// Boolean.class));
		// MapUtil.Insert(bool_ob, Boolean.class, PseudoSequence.class, false,
		// for_use_object_create_sequence_type,
		// for_use_object_create_operations, for_use_object_modify_operations,
		// operation_class,
		// operation_is_to_create, typed_operation_branch_influence);
		// TypedOperation char_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Character.class,
		// Object.class));
		// MapUtil.Insert(char_ob, Character.class,
		// NumberDeltaChangePseudoSequence.class, false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation byte_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Byte.class,
		// Object.class));
		// MapUtil.Insert(byte_ob, Byte.class, NumberDeltaChangePseudoSequence.class,
		// false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation short_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Short.class,
		// Object.class));
		// MapUtil.Insert(short_ob, Short.class, NumberDeltaChangePseudoSequence.class,
		// false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation int_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Integer.class,
		// Object.class));
		// MapUtil.Insert(int_ob, Integer.class, NumberDeltaChangePseudoSequence.class,
		// false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation long_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Long.class,
		// Object.class));
		// MapUtil.Insert(long_ob, Long.class, NumberDeltaChangePseudoSequence.class,
		// false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation float_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Float.class,
		// Object.class));
		// MapUtil.Insert(float_ob, Float.class, NumberDeltaChangePseudoSequence.class,
		// false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// TypedOperation double_ob = TypedOperation
		// .forMethod(DateRuntimeSupport.class.getMethod("add", Double.class,
		// Object.class));
		// MapUtil.Insert(double_ob, Double.class,
		// NumberDeltaChangePseudoSequence.class, false,
		// for_use_object_create_sequence_type, for_use_object_create_operations,
		// for_use_object_modify_operations, operation_class, operation_is_to_create,
		// typed_operation_branch_influence);
		// }
	}

}

class ClassDoubleMapValueComparator implements Comparator<Map.Entry<Class<?>, Double>> {

	@Override
	public int compare(Map.Entry<Class<?>, Double> me1, Map.Entry<Class<?>, Double> me2) {
		return -me1.getValue().compareTo(me2.getValue());
	}

}
