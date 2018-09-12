package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.influence.Rewardable;
import randoop.generation.date.influence.TraceInfo;
import randoop.generation.date.mutation.Mutation;
import randoop.generation.date.mutation.ObjectConstraintMutation;
import randoop.generation.date.mutation.ObligatoryObjectConstraintMutation;
import randoop.generation.date.mutation.TypedOperationMutation;
import randoop.generation.date.sequence.constraint.PseudoSequenceAddressConstraint;
import randoop.generation.date.sequence.constraint.PseudoSequenceTypeConstraint;
import randoop.generation.date.util.ClassUtil;
import randoop.operation.TypedOperation;

public class PseudoSequenceContainer implements Rewardable {
	
	PseudoSequence end = null;

	HashSet<PseudoSequence> contained_sequences = new HashSet<PseudoSequence>();

	// ========= split line =========
	// the following are set up by execution trace
	TraceInfo info = null;

	// must satisfied constraint in next generation
	boolean obligatory_constraint_solved = false;
	HashSet<PseudoSequenceAddressConstraint> acs = new HashSet<PseudoSequenceAddressConstraint>();
	HashSet<PseudoSequenceTypeConstraint> tcs = new HashSet<PseudoSequenceTypeConstraint>();

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
	}

	public TraceInfo GetTraceInfo() {
		return info;
	}
	
	public LinkedSequence GenerateLinkedSequence() {
		return end.GenerateLinkedSequence();
	}
	
	public boolean ObligatoryConstraintSolved() {
		return obligatory_constraint_solved;
	}
	
	public PseudoSequenceContainer MutateByApplyingObligatoryConstraint() {
		// TODO
		
		obligatory_constraint_solved = true;
		return null;
	}
	
	public ObligatoryObjectConstraintMutation GenerateObligatoryObjectConstraintMutation() {
		return new ObligatoryObjectConstraintMutation(null, this);
	}
	
	public boolean HasUnsolvedConstraint() {
		return optional_tcs.size() > 0;
	}
	
	public PseudoSequenceContainer MutateByApplyingOneRandomUnsolvedConstraint() {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public ObjectConstraintMutation GenerateObjectConstraintMutation(InfluenceOfBranchChange object_constraint_branch_influence) {
		return new ObjectConstraintMutation(object_constraint_branch_influence, this);
	}

	@Override
	public double GetReward(ArrayList<String> interested_branch) {
		// TODO Auto-generated method stub
		Assert.isTrue(info != null);
		
		return 0;
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
			Set<Class<?>> could_assign_classes = ClassUtil.GetAssignableClasses(classes, var_class);
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
