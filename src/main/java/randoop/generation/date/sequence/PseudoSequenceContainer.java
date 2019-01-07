package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfTraceCompare;
import randoop.generation.date.influence.Reward;
import randoop.generation.date.influence.Rewardable;
import randoop.generation.date.influence.TraceInfo;
import randoop.operation.TypedOperation;

public class PseudoSequenceContainer implements Rewardable {

	PseudoSequence end = null;

	HashSet<PseudoSequence> contained_sequences = new HashSet<PseudoSequence>();

	// ========= split line =========
	// the following are set up by execution trace
	// ArrayList<TraceInfo> infos = null;
	TraceInfo trace_info = null;
	
	// the key is meaning the previous trace info
	Map<TraceInfo, InfluenceOfTraceCompare> influences_compared_to_previous_trace = new TreeMap<TraceInfo, InfluenceOfTraceCompare>();
	
	// key is before mutating, value is the after mutating.
	// note that this is just the logical mapping, the real values may mutated from intermediate results.
	HashMap<PseudoSequence, PseudoSequence> logical_mutate_mapping = new HashMap<PseudoSequence, PseudoSequence>();
	
	// BranchValueState val_state = null;

	// must satisfied constraint in next generation
	// HashSet<PseudoSequenceAddressConstraint> acs = new
	// HashSet<PseudoSequenceAddressConstraint>();
	// HashSet<PseudoSequenceTypeConstraint> tcs = new
	// HashSet<PseudoSequenceTypeConstraint>();

	// HashSet<PseudoVariableConstraint> solved_obligatory_tcs = new
	// HashSet<PseudoVariableConstraint>();
	// HashSet<PseudoVariableConstraint> obligatory_tcs = new
	// HashSet<PseudoVariableConstraint>();
	//
	// // optional satisfied constraint
	// HashSet<PseudoVariableConstraint> solved_optional_tcs = new
	// HashSet<PseudoVariableConstraint>();
	// HashSet<PseudoVariableConstraint> optional_tcs = new
	// HashSet<PseudoVariableConstraint>();

	PseudoSequenceContainer previous = null;
	Set<PseudoSequenceContainer> nexts = new HashSet<PseudoSequenceContainer>();

	int mutated_number = 0;

	public PseudoSequenceContainer(PseudoSequenceContainer previous) {
		if (previous != null) {
			previous.nexts.add(this);
			this.previous = previous;
			this.mutated_number = previous.mutated_number;
		}
	}

	public void SetEndPseudoSequence(PseudoSequence end) {
		this.end = end;
	}

	public PseudoSequence GetEndPseudoSequence() {
		return end;
	}

	public void AddPseudoSequence(PseudoSequence e) {
		contained_sequences.add(e);
	}

	public void SetTraceInfo(TraceInfo info) {
		if (this.trace_info == null) {
			this.trace_info = info;
		}
	}

	// public void SetTraceInfo(ArrayList<TraceInfo> infos) {
	// this.infos = infos;
	// this.val_state =
	// SimpleInfluenceComputer.CreateBranchValueState(infos.get(infos.size() - 1));
	// }

	// public ArrayList<TraceInfo> GetTraceInfo() {
	// return infos;
	// }

	// public TraceInfo GetLastTraceInfo() {
	// return infos.get(infos.size() - 1);
	// }

	public TraceInfo GetTraceInfo() {
		return trace_info;
	}

	public LinkedSequence GenerateLinkedSequence() {
		return end.GenerateLinkedSequence();
	}

	// private PseudoSequenceContainer
	// MutateByApplyingObjectAddressConstraint(DateGenerator dg,
	// PseudoVariableAddressSameConstraint psac) {
	// Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new
	// HashMap<PseudoSequence, PseudoSequence>();
	// PseudoSequence copied_end = (PseudoSequence) end.CopySelfInDeepCloneWay(null,
	// origin_copied_sequence_map, dg);
	// copied_end.ReplacePseudoVariableInDependency(dg,
	// psac.GetShouldBeSamePseudoVariableOne(),
	// psac.GetShouldBeSamePseudoVariableTwo());
	// mutated_number++;
	// return copied_end.container;
	// }
	//
	// private PseudoSequenceContainer
	// MutateByApplyingObjectTypeConstraint(DateGenerator dg,
	// PseudoVariableTypeConstraint pstc) {
	// PseudoVariable pv = pstc.GetPseudoVariable();
	// Class<?> st = pstc.GetSpecifiedType();
	// boolean is_to_same = pstc.IsToSame();
	// Set<Class<?>> origin = dg.class_pseudo_variable.keySet();
	// Set<Class<?>> descendants = ClassUtil.GetDescendantClasses(origin, st);
	// Set<Class<?>> not_descendants = new HashSet<Class<?>>(origin);
	// not_descendants.removeAll(descendants);
	// mutated_number++;
	// if (is_to_same) {
	// return MakeSelectedVariableMatchSelectedClasses(pv, descendants, dg);
	// } else {
	// return MakeSelectedVariableMatchSelectedClasses(pv, not_descendants, dg);
	// }
	// }
	//
	// private PseudoSequenceContainer
	// MakeSelectedVariableMatchSelectedClasses(PseudoVariable pv,
	// Set<Class<?>> selected_classes, DateGenerator dg) {
	// ArrayList<PseudoVariable> candidates = new ArrayList<PseudoVariable>();
	// SequenceGeneratorHelper.SelectToListFromMap(selected_classes,
	// dg.class_pseudo_variable, candidates);
	// PseudoVariable sv =
	// RandomSelect.RandomPseudoVariableListAccordingToLength(candidates);
	// Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map_for_end = new
	// HashMap<PseudoSequence, PseudoSequence>();
	// PseudoSequence copied_end = (PseudoSequence) end.CopySelfInDeepCloneWay(null,
	// origin_copied_sequence_map_for_end, dg);
	// Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new
	// HashMap<PseudoSequence, PseudoSequence>();
	// PseudoVariable copied_sv = sv.CopySelfInDeepCloneWay(copied_end.container,
	// origin_copied_sequence_map, dg);
	// copied_end.ReplacePseudoVariableInDependency(dg, pv, copied_sv);
	// return copied_end.container;
	// }
	//
	// public boolean HasUnsolvedObligatoryConstraint() {
	// return obligatory_tcs.size() > 0;
	// }
	//
	// public PseudoSequenceContainer
	// MutateByApplyingObligatoryConstraint(DateGenerator dg) {
	// PseudoVariableConstraint oc = Randomness.randomSetMember(obligatory_tcs);
	// obligatory_tcs.remove(oc);
	// solved_obligatory_tcs.add(oc);
	// if (oc instanceof PseudoVariableAddressSameConstraint) {
	// return MutateByApplyingObjectAddressConstraint(dg,
	// (PseudoVariableAddressSameConstraint) oc);
	// }
	// if (oc instanceof PseudoVariableTypeConstraint) {
	// return MutateByApplyingObjectTypeConstraint(dg,
	// (PseudoVariableTypeConstraint) oc);
	// }
	// return null;
	// }
	//
	// public ObligatoryObjectConstraintMutation
	// GenerateObligatoryObjectConstraintMutation(
	// InfluenceOfBranchChange object_constraint_branch_influence) {
	// return new
	// ObligatoryObjectConstraintMutation(object_constraint_branch_influence, this);
	// }
	//
	// public boolean HasUnsolvedConstraint() {
	// return optional_tcs.size() > 0;
	// }
	//
	// public PseudoSequenceContainer
	// MutateByApplyingOptionalConstraint(DateGenerator dg) {
	// PseudoVariableTypeConstraint tc = (PseudoVariableTypeConstraint)
	// Randomness.randomSetMember(optional_tcs);
	// optional_tcs.remove(tc);
	// solved_optional_tcs.add(tc);
	// return MutateByApplyingObjectTypeConstraint(dg, tc);
	// }
	//
	// public ObjectConstraintMutation GenerateObjectConstraintMutation(
	// InfluenceOfBranchChange object_constraint_branch_influence) {
	// return new ObjectConstraintMutation(object_constraint_branch_influence,
	// this);
	// }

	@Override
	public Reward GetReward(ArrayList<String> interested_branch) {
		// Assert.isTrue(infos != null && val_state != null);
		// return val_state.GetReward(interested_branch);
		return new Reward(0.1);
	}

	public void ResetMutate(DateGenerator dg) {
		ArrayList<StringPseudoSequence> string_sequences = FetchAllStringPseudoSequences();
		int s_len = string_sequences.size();
		if (s_len > 0) {
			for (int i = 0; i < s_len; i++) {
				StringPseudoSequence to_mutate_sequence = string_sequences.get(i);
				to_mutate_sequence.ResetMutateString(dg);
			}
		}
	}

	public BeforeAfterLinkedSequence Mutate(DateGenerator dg) {
		// // here should be specified Mutation for string
		ArrayList<StringPseudoSequence> string_sequences = FetchAllStringPseudoSequences();
		int s_len = string_sequences.size();
		if (s_len > 0) {
			for (int i = 0; i < s_len; i++) {
				StringPseudoSequence to_mutate_sequence = string_sequences.get(i);
				BeforeAfterLinkedSequence mutated = to_mutate_sequence.MutateString(dg);
				if (mutated != null) {
					return mutated;
				}
			}
		}
		return null;
	}

	private ArrayList<StringPseudoSequence> FetchAllStringPseudoSequences() {
		ArrayList<StringPseudoSequence> string_sequences = new ArrayList<StringPseudoSequence>();
		Iterator<PseudoSequence> seq_itr = contained_sequences.iterator();
		while (seq_itr.hasNext()) {
			PseudoSequence ps = seq_itr.next();
			if (ps instanceof StringPseudoSequence) {
				string_sequences.add((StringPseudoSequence) ps);
			}
		}
		return string_sequences;
	}

	// public List<Mutation> UntriedMutations(DateGenerator dg) {
	//
	//
	// // Map<TypedOperation, Class<?>> operation_class,
	// // Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations,
	// // Map<TypedOperation, InfluenceOfBranchChange>
	// // typed_operation_branch_influence,
	// // Map<PseudoVariable, Class<?>> pseudo_variable_class
	// HashMap<TypedOperation, HashSet<PseudoVariable>> op_vars = new
	// HashMap<TypedOperation, HashSet<PseudoVariable>>();
	// Set<Class<?>> classes = dg.for_use_object_modify_operations.keySet();
	// // System.out.println("===== clses start =====");
	// // for (Class<?> cls : classes) {
	// // System.out.println("b_cls:" + cls);
	// // }
	// // System.out.println("===== clses end =====");
	// HashSet<PseudoVariable> variables = new HashSet<PseudoVariable>();
	// if (BranchesExistInTrace()) {
	// HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
	// end.BuildValidDependantPseudoVariables(variables, encountered, dg);
	// } else {
	// variables.add(end.headed_variable);
	// }
	// for (PseudoVariable var : variables) {
	// Class<?> var_class = dg.pseudo_variable_class.get(var);
	// if (var_class != null) {
	// // System.out.println("Ha#e_pv:" + var + "#out_class:" + var_class);
	// // System.out.println("var.sequence.getClass():" + var.sequence.getClass());
	// Set<Class<?>> could_assign_classes = ClassUtil.GetSuperClasses(classes,
	// var_class);
	// // System.out.println("===== classes start =====");
	// // System.out.println("var_class:" + var_class);
	// // for (Class<?> cls : could_assign_classes) {
	// // System.out.println("cls:" + cls);
	// // }
	// // System.out.println("===== classes end =====");
	// ArrayList<TypedOperation> tos = new ArrayList<TypedOperation>();
	// for (Class<?> ca_cls : could_assign_classes) {
	// tos.addAll(dg.for_use_object_modify_operations.get(ca_cls));
	// }
	// // System.out.println("just#tos.size():" + tos.size());
	// for (TypedOperation to : tos) {
	// if (!BranchesExistInTrace()) {
	// OperationKind ok = dg.operation_kind.get(to);
	// if (ok != null && ok.equals(OperationKind.no_branch)) {
	// continue;
	// }
	// }
	// HashSet<PseudoVariable> pvs = op_vars.get(to);
	// if (pvs == null) {
	// pvs = new HashSet<PseudoVariable>();
	// op_vars.put(to, pvs);
	// }
	// PseudoSequence seq = dg.pseudo_variable_headed_sequence.get(var);
	// Integer to_count = seq.operation_use_count.get(to);
	// if (to_count == null || to_count == 0) {
	// pvs.add(var);
	// }
	// }
	// }
	// }
	// List<Mutation> mutations = new LinkedList<Mutation>();
	// Set<TypedOperation> tos = op_vars.keySet();
	// // System.out.println("tos.size():" + tos.size());
	// for (TypedOperation to : tos) {
	// HashSet<PseudoVariable> pvs = op_vars.get(to);
	// if (pvs.size() > 0) {
	// TypedOperationMutation tom = new
	// TypedOperationMutation(dg.typed_operation_branch_influence.get(to), to,
	// pvs);
	// mutations.add(tom);
	// }
	// }
	// return mutations;
	// }

	// public void AddObligatoryConstraint(PseudoVariableConstraint pvc) {
	// obligatory_tcs.add(pvc);
	// }
	//
	// public void AddOptionalConstraint(PseudoVariableConstraint pvc) {
	// optional_tcs.add(pvc);
	// }

	@Override
	public String toString() {
		return super.toString() + end.GenerateLinkedSequence().toCodeString();
	}

	// private boolean BranchesExistInTrace() {
	// return infos.get(infos.size() - 1).HasBranches();
	// return trace_info.BranchesExistInTrace();
	// }

	public TypedOperation GetEndedTypedOperation() {
		PseudoStatement pstmt = end.GetLastStatement();
		return pstmt.operation;
	}

	public int GetMutatedNumber() {
		return mutated_number;
	}

	public void AddRecentInfluence(TraceInfo previous_trace_info, InfluenceOfTraceCompare all_branches_influences) {
		if (previous_trace_info != null) {
			influences_compared_to_previous_trace.put(previous_trace_info, all_branches_influences);
		}
	}

	public PseudoSequence GetLogicalMappingSequence(PseudoSequence b_this) {
		return logical_mutate_mapping.get(b_this);
	}

	public void SetLogicMapping(PseudoSequence b_this, PseudoSequence copied_this) {
		Assert.isTrue(contained_sequences.contains(copied_this));
		logical_mutate_mapping.put(b_this, copied_this);
	}

}
