package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.BranchValueState;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.influence.Rewardable;
import randoop.generation.date.influence.SimpleInfluenceComputer;
import randoop.generation.date.influence.TraceInfo;
import randoop.generation.date.mutation.Mutation;
import randoop.generation.date.mutation.ObjectConstraintMutation;
import randoop.generation.date.mutation.ObligatoryObjectConstraintMutation;
import randoop.generation.date.mutation.TypedOperationMutation;
import randoop.generation.date.random.RandomSelect;
import randoop.generation.date.sequence.constraint.PseudoVariableAddressSameConstraint;
import randoop.generation.date.sequence.constraint.PseudoVariableConstraint;
import randoop.generation.date.sequence.constraint.PseudoVariableTypeConstraint;
import randoop.generation.date.util.ClassUtil;
import randoop.operation.TypedOperation;
import randoop.util.Randomness;

public class PseudoSequenceContainer implements Rewardable {

	PseudoSequence end = null;

	HashSet<PseudoSequence> contained_sequences = new HashSet<PseudoSequence>();

	// ========= split line =========
	// the following are set up by execution trace
	TraceInfo info = null;
	BranchValueState val_state = null;

	// must satisfied constraint in next generation
	// HashSet<PseudoSequenceAddressConstraint> acs = new
	// HashSet<PseudoSequenceAddressConstraint>();
	// HashSet<PseudoSequenceTypeConstraint> tcs = new
	// HashSet<PseudoSequenceTypeConstraint>();

	HashSet<PseudoVariableConstraint> solved_obligatory_tcs = new HashSet<PseudoVariableConstraint>();
	HashSet<PseudoVariableConstraint> obligatory_tcs = new HashSet<PseudoVariableConstraint>();

	// optional satisfied constraint
	HashSet<PseudoVariableConstraint> solved_optional_tcs = new HashSet<PseudoVariableConstraint>();
	HashSet<PseudoVariableConstraint> optional_tcs = new HashSet<PseudoVariableConstraint>();

	public PseudoSequenceContainer() {
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
		this.info = info;
		this.val_state = SimpleInfluenceComputer.CreateBranchValueState(info);
	}

	public TraceInfo GetTraceInfo() {
		return info;
	}

	public LinkedSequence GenerateLinkedSequence() {
		return end.GenerateLinkedSequence();
	}

	private PseudoSequenceContainer MutateByApplyingObjectAddressConstraint(DateGenerator dg,
			PseudoVariableAddressSameConstraint psac) {
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
		PseudoSequence copied_end = (PseudoSequence) end.CopySelfInDeepCloneWay(null, origin_copied_sequence_map, dg);
		copied_end.ReplacePseudoVariableInDependency(dg, psac.GetShouldBeSamePseudoVariableOne(),
				psac.GetShouldBeSamePseudoVariableTwo());
		return copied_end.container;
	}

	private PseudoSequenceContainer MutateByApplyingObjectTypeConstraint(DateGenerator dg,
			PseudoVariableTypeConstraint pstc) {
		PseudoVariable pv = pstc.GetPseudoVariable();
		Class<?> st = pstc.GetSpecifiedType();
		boolean is_to_same = pstc.IsToSame();
		Set<Class<?>> origin = dg.class_pseudo_variable.keySet();
		Set<Class<?>> descendants = ClassUtil.GetDescendantClasses(origin, st);
		Set<Class<?>> not_descendants = new HashSet<Class<?>>(origin);
		not_descendants.removeAll(descendants);
		if (is_to_same) {
			return MakeSelectedVariableMatchSelectedClasses(pv, descendants, dg);
		} else {
			return MakeSelectedVariableMatchSelectedClasses(pv, not_descendants, dg);
		}
	}

	private PseudoSequenceContainer MakeSelectedVariableMatchSelectedClasses(PseudoVariable pv,
			Set<Class<?>> selected_classes, DateGenerator dg) {
		ArrayList<PseudoVariable> candidates = new ArrayList<PseudoVariable>();
		SequenceGeneratorHelper.SelectToListFromMap(selected_classes, dg.class_pseudo_variable, candidates);
		PseudoVariable sv = RandomSelect.RandomPseudoVariableListAccordingToLength(candidates);
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map_for_end = new HashMap<PseudoSequence, PseudoSequence>();
		PseudoSequence copied_end = (PseudoSequence) end.CopySelfInDeepCloneWay(null,
				origin_copied_sequence_map_for_end, dg);
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
		PseudoVariable copied_sv = sv.CopySelfInDeepCloneWay(copied_end.container, origin_copied_sequence_map, dg);
		copied_end.ReplacePseudoVariableInDependency(dg, pv, copied_sv);
		return copied_end.container;
	}

	public boolean HasUnsolvedObligatoryConstraint() {
		return obligatory_tcs.size() > 0;
	}

	public PseudoSequenceContainer MutateByApplyingObligatoryConstraint(DateGenerator dg) {
		PseudoVariableConstraint oc = Randomness.randomSetMember(obligatory_tcs);
		obligatory_tcs.remove(oc);
		solved_obligatory_tcs.add(oc);
		if (oc instanceof PseudoVariableAddressSameConstraint) {
			return MutateByApplyingObjectAddressConstraint(dg, (PseudoVariableAddressSameConstraint) oc);
		}
		if (oc instanceof PseudoVariableTypeConstraint) {
			return MutateByApplyingObjectTypeConstraint(dg, (PseudoVariableTypeConstraint) oc);
		}
		return null;
	}

	public ObligatoryObjectConstraintMutation GenerateObligatoryObjectConstraintMutation(
			InfluenceOfBranchChange object_constraint_branch_influence) {
		return new ObligatoryObjectConstraintMutation(object_constraint_branch_influence, this);
	}

	public boolean HasUnsolvedConstraint() {
		return optional_tcs.size() > 0;
	}

	public PseudoSequenceContainer MutateByApplyingOptionalConstraint(DateGenerator dg) {
		PseudoVariableTypeConstraint tc = (PseudoVariableTypeConstraint) Randomness.randomSetMember(optional_tcs);
		optional_tcs.remove(tc);
		solved_optional_tcs.add(tc);
		return MutateByApplyingObjectTypeConstraint(dg, tc);
	}

	public ObjectConstraintMutation GenerateObjectConstraintMutation(
			InfluenceOfBranchChange object_constraint_branch_influence) {
		return new ObjectConstraintMutation(object_constraint_branch_influence, this);
	}

	@Override
	public double GetReward(ArrayList<String> interested_branch) {
		Assert.isTrue(info != null && val_state != null);
		return val_state.GetReward(interested_branch);
	}

	public List<Mutation> UntriedMutations(DateGenerator dg) {
		// Map<TypedOperation, Class<?>> operation_class,
		// Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations,
		// Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence,
		// Map<PseudoVariable, Class<?>> pseudo_variable_class
		HashMap<TypedOperation, HashSet<PseudoVariable>> op_vars = new HashMap<TypedOperation, HashSet<PseudoVariable>>();
		Set<Class<?>> classes = dg.for_use_object_modify_operations.keySet();
//		System.out.println("===== clses start =====");
//		for (Class<?> cls : classes) {
//			System.out.println("b_cls:" + cls);
//		}
//		System.out.println("===== clses end =====");
		HashSet<PseudoVariable> variables = new HashSet<PseudoVariable>();
		HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
		end.BuildDependantPseudoVariables(variables, encountered);
		for (PseudoVariable var : variables) {
			Class<?> var_class = dg.pseudo_variable_class.get(var);
//			System.out.println("Ha#e_pv:" + var + "#out_class:" + var_class);
			Set<Class<?>> could_assign_classes = ClassUtil.GetSuperClasses(classes, var_class);
			System.out.println("===== classes start =====");
			System.out.println("var_class:" + var_class);
			for (Class<?> cls : could_assign_classes) {
				System.out.println("cls:" + cls);
			}
			System.out.println("===== classes end =====");
			ArrayList<TypedOperation> tos = new ArrayList<TypedOperation>();
			for (Class<?> ca_cls : could_assign_classes) {
				tos.addAll(dg.for_use_object_modify_operations.get(ca_cls));
			}
//			System.out.println("just#tos.size():" + tos.size());
			for (TypedOperation to : tos) {
				HashSet<PseudoVariable> pvs = op_vars.get(to);
				if (pvs == null) {
					pvs = new HashSet<PseudoVariable>();
					op_vars.put(to, pvs);
				}
				pvs.add(var);
			}
		}
		List<Mutation> mutations = new LinkedList<Mutation>();
		Set<TypedOperation> tos = op_vars.keySet();
//		System.out.println("tos.size():" + tos.size());
		for (TypedOperation to : tos) {
			HashSet<PseudoVariable> pvs = op_vars.get(to);
			if (pvs.size() > 0) {
				TypedOperationMutation tom = new TypedOperationMutation(dg.typed_operation_branch_influence.get(to), to, pvs);
				mutations.add(tom);
			}
		}
		return mutations;
	}

	public void AddObligatoryConstraint(PseudoVariableConstraint pvc) {
		obligatory_tcs.add(pvc);
	}

	public void AddOptionalConstraint(PseudoVariableConstraint pvc) {
		optional_tcs.add(pvc);
	}

}
