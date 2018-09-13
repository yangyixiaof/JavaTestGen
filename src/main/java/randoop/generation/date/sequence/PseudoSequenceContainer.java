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
import randoop.generation.date.sequence.constraint.PseudoSequenceAddressConstraint;
import randoop.generation.date.sequence.constraint.PseudoSequenceConstraint;
import randoop.generation.date.sequence.constraint.PseudoSequenceTypeConstraint;
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
//	HashSet<PseudoSequenceAddressConstraint> acs = new HashSet<PseudoSequenceAddressConstraint>();
//	HashSet<PseudoSequenceTypeConstraint> tcs = new HashSet<PseudoSequenceTypeConstraint>();

	HashSet<PseudoSequenceConstraint> solved_obligatory_tcs = new HashSet<PseudoSequenceConstraint>();
	HashSet<PseudoSequenceConstraint> obligatory_tcs = new HashSet<PseudoSequenceConstraint>();

	// optional satisfied constraint
	HashSet<PseudoSequenceTypeConstraint> solved_optional_tcs = new HashSet<PseudoSequenceTypeConstraint>();
	HashSet<PseudoSequenceTypeConstraint> optional_tcs = new HashSet<PseudoSequenceTypeConstraint>();

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
	
	private PseudoSequenceContainer MutateByApplyingObjectAddressConstraint(DateGenerator dg, PseudoSequenceAddressConstraint psac) {
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
		PseudoSequence copied_end = (PseudoSequence)end.CopySelfInDeepCloneWay(origin_copied_sequence_map, dg.pseudo_variable_headed_sequence);
		copied_end.ReplacePseudoVariableInDependency(dg, psac.GetShouldBeSamePseudoVariableOne(), psac.GetShouldBeSamePseudoVariableTwo());
		return copied_end.container;
	}
	
	private PseudoSequenceContainer MutateByApplyingObjectTypeConstraint(DateGenerator dg, PseudoSequenceTypeConstraint pstc) {
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
	
	private PseudoSequenceContainer MakeSelectedVariableMatchSelectedClasses(PseudoVariable pv, Set<Class<?>> selected_classes, DateGenerator dg) {
		ArrayList<PseudoVariable> candidates = new ArrayList<PseudoVariable>();
		SequenceGeneratorHelper.SelectToListFromMap(selected_classes, dg.class_pseudo_variable, candidates);
		PseudoVariable sv = RandomSelect.RandomPseudoVariableListAccordingToLength(candidates);
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
		PseudoVariable copied_sv = sv.CopySelfInDeepCloneWay(origin_copied_sequence_map, dg.pseudo_variable_headed_sequence);
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map_for_end = new HashMap<PseudoSequence, PseudoSequence>();
		PseudoSequence copied_end = (PseudoSequence)end.CopySelfInDeepCloneWay(origin_copied_sequence_map_for_end, dg.pseudo_variable_headed_sequence);
		copied_end.ReplacePseudoVariableInDependency(dg, pv, copied_sv);
		return copied_end.container;
	}
	
	public boolean HasUnsolvedObligatoryConstraint() {
		return obligatory_tcs.size() > 0;
	}
	
	public PseudoSequenceContainer MutateByApplyingObligatoryConstraint(DateGenerator dg) {
		PseudoSequenceConstraint oc = Randomness.randomSetMember(obligatory_tcs);
		obligatory_tcs.remove(oc);
		solved_obligatory_tcs.add(oc);
		if (oc instanceof PseudoSequenceAddressConstraint) {
			return MutateByApplyingObjectAddressConstraint(dg, (PseudoSequenceAddressConstraint)oc);
		}
		if (oc instanceof PseudoSequenceTypeConstraint) {
			return MutateByApplyingObjectTypeConstraint(dg, (PseudoSequenceTypeConstraint)oc);
		}
		return null;
	}
	
	public ObligatoryObjectConstraintMutation GenerateObligatoryObjectConstraintMutation(InfluenceOfBranchChange object_constraint_branch_influence) {
		return new ObligatoryObjectConstraintMutation(object_constraint_branch_influence, this);
	}
	
	public boolean HasUnsolvedConstraint() {
		return optional_tcs.size() > 0;
	}
	
	public PseudoSequenceContainer MutateByApplyingOptionalConstraint(DateGenerator dg) {
		PseudoSequenceTypeConstraint tc = Randomness.randomSetMember(optional_tcs);
		optional_tcs.remove(tc);
		solved_optional_tcs.add(tc);
		return MutateByApplyingObjectTypeConstraint(dg, tc);
	}
	
	public ObjectConstraintMutation GenerateObjectConstraintMutation(InfluenceOfBranchChange object_constraint_branch_influence) {
		return new ObjectConstraintMutation(object_constraint_branch_influence, this);
	}

	@Override
	public double GetReward(ArrayList<String> interested_branch) {
		Assert.isTrue(info != null && val_state != null);
		return val_state.GetReward(interested_branch);
	}

	public List<Mutation> UntriedMutations(Map<TypedOperation, Class<?>> operation_class,
			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations,
			Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence, Map<PseudoVariable, Class<?>> pseudo_variable_class) {
		HashMap<TypedOperation, HashSet<PseudoVariable>> op_vars = new HashMap<TypedOperation, HashSet<PseudoVariable>>();
		Set<Class<?>> classes = for_use_object_modify_operations.keySet();
		HashSet<PseudoVariable> variables = new HashSet<PseudoVariable>();
		HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
		end.BuildDependantPseudoVariables(variables, encountered);
		for (PseudoVariable var : variables) {
			ArrayList<TypedOperation> tos = new ArrayList<TypedOperation>();
			Class<?> var_class = pseudo_variable_class.get(var);
			Set<Class<?>> could_assign_classes = ClassUtil.GetSuperClasses(classes, var_class);
			for (Class<?> ca_cls : could_assign_classes) {
				tos.addAll(for_use_object_modify_operations.get(ca_cls));
			}
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
		for (TypedOperation to : tos) {
			HashSet<PseudoVariable> pvs = op_vars.get(to);
			if (pvs.size() > 0) {
				TypedOperationMutation tom = new TypedOperationMutation(typed_operation_branch_influence.get(to), to, pvs);
				mutations.add(tom);
			}
		}
		return mutations;
	}

}
