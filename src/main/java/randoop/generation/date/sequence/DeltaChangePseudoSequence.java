package randoop.generation.date.sequence;

import java.util.ArrayList;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.mutation.DeltaChangeTypedOperationMutated;
import randoop.operation.TypedOperation;
import randoop.types.JavaTypes;

public class DeltaChangePseudoSequence extends PseudoSequence {

	double delta = 0.0;

	// Set<Double> have_tried_delta = new HashSet<Double>();

	// Random random = new Random();

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
		// dcps.have_tried_delta.addAll(have_tried_delta);
		return dcps;
	}

	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			DateGenerator dg) {
		if (dg.operation_is_delta_change.get(selected_to)) {
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
			InfluenceOfBranchChange in_use_influence = null;
			if (delta != 0) {
				in_use_delta = delta;
				in_use_influence = headed_variable_branch_influence;
			} else {
				PseudoSequence headed_variable_sequence = this.headed_variable.sequence;
				if (headed_variable_sequence instanceof DeltaChangePseudoSequence) {
					DeltaChangePseudoSequence ndcps = (DeltaChangePseudoSequence) headed_variable_sequence;
					double headed_variable_sequence_delta = ndcps.delta;
					if (headed_variable_sequence_delta != 0) {
						in_use_delta = headed_variable_sequence_delta;
						in_use_influence = ndcps.headed_variable_branch_influence;
					}
				}
			}
			double influence = 0.0;
			if (in_use_influence != null) {
				influence = in_use_influence.GetReward(interested_branch).GetReward();
				// influence =
				// SimpleInfluenceComputer.ComputeAveragedInfluence(interested_branch,
				// in_use_influence);
			}
			next_delta = SequenceGeneratorHelper.ComputeDelta(in_use_delta, influence);// , have_tried_delta
			ps.delta = next_delta;
			TypedOperation dp_op = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, next_delta);
			dp_operations.add(dp_op);
			DisposablePseudoSequence dps = new DisposablePseudoSequence();// dp_operations
			PseudoVariable dpv = dps.Append(dp_op, new ArrayList<PseudoVariable>(), false);// ,
																					// dg.pseudo_variable_headed_sequence
			dps.SetHeadedVariable(dpv);
			input_pseudo_variables.add(0, ps.headed_variable);
			input_pseudo_variables.add(1, dpv);
			LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
			PseudoVariable pv = ps.Append(selected_to, input_pseudo_variables, false);// , dg.pseudo_variable_headed_sequence
			ps.ReplacePseudoVariableInDependency(dg, headed_variable, pv);
			LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
			// ps.headed_variable, ps,
			result = new BeforeAfterLinkedSequence(selected_to,
					new DeltaChangeTypedOperationMutated(ps, true, new PseudoVariable(ps, ps.Size() - 1), true, pv),
					before_linked_sequence, after_linked_sequence);
			container.mutated_number++;
			return result;
		} else {
			return super.Mutate(selected_to, interested_branch, dg);
		}
	}

	// public void SetAllBranchesInfluencesComparedToPrevious(
	// Map<String, Influence> all_branches_influences_compared_to_previous) {
	// this.to_previous_branches_influences =
	// all_branches_influences_compared_to_previous;
	// }

	// @Override
	// public void OperationApplied(TypedOperation to) {
	// if (to.getInputTypes().size() == 1) {
	// super.OperationApplied(to);
	// }
	// }

	// @Override
	// public double GetPunishment(TypedOperation selected_op) {
	// return 0.0;
	// }

}
