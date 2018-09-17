package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.SimpleInfluenceComputer;
import randoop.generation.date.mutation.DeltaChangeTypedOperationMutated;
import randoop.operation.TypedOperation;
import randoop.types.JavaTypes;

public class DeltaChangePseudoSequence extends PseudoSequence {

	double delta = 0.0;

	Set<Double> have_tried_delta = new HashSet<Double>();

	// Random random = new Random();

	Map<String, Influence> to_previous_branches_influences = null;

	// ArrayList<TypedOperation> operations
	public DeltaChangePseudoSequence() {
		super();// operations
	}

	// public NumberDeltaChangePseudoSequence(PseudoVariable pv,
	// ArrayList<TypedOperation> operations) {
	// super(pv, operations);
	// }

	@Override
	public PseudoSequence CopySelfAndCitersInDeepCloneWay(DateGenerator dg) {// Map<PseudoSequence, PseudoSequence>
																				// origin_copied_sequence_map,
																				// Map<PseudoVariable, PseudoSequence>
																				// class_object_headed_sequence
		DeltaChangePseudoSequence dcps = (DeltaChangePseudoSequence) super.CopySelfAndCitersInDeepCloneWay(dg);
		// origin_copied_sequence_map, class_object_headed_sequence
		dcps.delta = delta;
		dcps.have_tried_delta.addAll(have_tried_delta);
		if (to_previous_branches_influences != null) {
			dcps.to_previous_branches_influences = new HashMap<String, Influence>();
			dcps.to_previous_branches_influences.putAll(to_previous_branches_influences);
		}
		return dcps;
	}

	public BeforeAfterLinkedSequence SuperMutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			DateGenerator dg) {
		return super.Mutate(selected_to, interested_branch, dg);
	}

	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			DateGenerator dg) {
		BeforeAfterLinkedSequence result = null;
		ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
		// initialize candidates.
		// HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new
		// HashMap<PseudoSequence, PseudoSequence>();
		DeltaChangePseudoSequence ps = (DeltaChangePseudoSequence) this.CopySelfAndCitersInDeepCloneWay(dg);// origin_copied_sequence_map,
		ps.SetPreviousSequence(this);
		ArrayList<TypedOperation> dp_operations = new ArrayList<TypedOperation>();
		double next_delta = 0.0;
		double in_use_delta = 0.0;
		Map<String, Influence> in_use_influence = null;
		if (delta != 0) {
			in_use_delta = delta;
			in_use_influence = to_previous_branches_influences;
		} else {
			PseudoSequence headed_variable_sequence = this.headed_variable.sequence;
			if (headed_variable_sequence instanceof DeltaChangePseudoSequence) {
				DeltaChangePseudoSequence ndcps = (DeltaChangePseudoSequence) headed_variable_sequence;
				double headed_variable_sequence_delta = ndcps.delta;
				if (headed_variable_sequence_delta != 0) {
					in_use_delta = headed_variable_sequence_delta;
					in_use_influence = ndcps.to_previous_branches_influences;
				}
			}
		}
		double influence = 0.0;
		if (in_use_influence != null) {
			influence = SimpleInfluenceComputer.ComputeAveragedInfluence(interested_branch, in_use_influence);
		}
		next_delta = SequenceGeneratorHelper.ComputeDelta(in_use_delta, influence, have_tried_delta);
		ps.delta = next_delta;
		TypedOperation dp_op = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, next_delta);
		dp_operations.add(dp_op);
		DisposablePseudoSequence dps = new DisposablePseudoSequence();// dp_operations
		PseudoVariable dpv = dps.Append(dp_op, new ArrayList<PseudoVariable>());// , dg.pseudo_variable_headed_sequence
		dps.SetHeadedVariable(dpv);
		input_pseudo_variables.add(0, ps.headed_variable);
		input_pseudo_variables.add(1, dpv);
		LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
		PseudoVariable pv = ps.Append(selected_to, input_pseudo_variables);// , dg.pseudo_variable_headed_sequence
		ps.ReplacePseudoVariableInDependency(dg, headed_variable, pv);
		LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
		// ps.headed_variable, ps,
		result = new BeforeAfterLinkedSequence(selected_to,
				new DeltaChangeTypedOperationMutated(ps, true, new PseudoVariable(ps, ps.Size() - 1)),
				before_linked_sequence, after_linked_sequence);
		return result;
	}

	public void SetAllBranchesInfluencesComparedToPrevious(
			Map<String, Influence> all_branches_influences_compared_to_previous) {
		this.to_previous_branches_influences = all_branches_influences_compared_to_previous;
	}

	@Override
	public void OperationApplied(TypedOperation to) {
		if (to.getInputTypes().size() == 1) {
			super.OperationApplied(to);
		}
	}

	// @Override
	// public double GetPunishment(TypedOperation selected_op) {
	// return 0.0;
	// }

}
