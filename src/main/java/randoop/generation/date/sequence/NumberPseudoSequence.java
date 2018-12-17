package randoop.generation.date.sequence;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.mutation.deprecate.DeltaChangeTypedOperationMutated;
import randoop.generation.date.sequence.helper.PrimitiveGeneratorHelper;
import randoop.generation.date.sequence.helper.SequenceGeneratorHelper;
import randoop.operation.TypedOperation;
import randoop.operation.TypedTermOperation;
import randoop.types.Type;

public class NumberPseudoSequence extends PseudoSequence {

	double delta = 0.0;

	// Set<Double> have_tried_delta = new HashSet<Double>();

	public NumberPseudoSequence() {
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
		NumberPseudoSequence dcps = (NumberPseudoSequence) super.CopySelfAndCitersInDeepCloneWay(dg);
		// origin_copied_sequence_map, class_object_headed_sequence
		dcps.delta = delta;
		// dcps.have_tried_delta.addAll(have_tried_delta);
		return dcps;
	}

	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			DateGenerator dg) {
		if (dg.operation_is_delta_change.get(selected_to)) {
			container.mutated_number++;
			BeforeAfterLinkedSequence result = null;
			NumberPseudoSequence ps = (NumberPseudoSequence) this.CopySelfAndCitersInDeepCloneWay(dg);// origin_copied_sequence_map,
			ps.SetPreviousSequence(this);
			
			LinkedSequence before_linked_sequence = container.GenerateLinkedSequence();
			
			// compute next delta
			double next_delta = ComputeNextDleta(interested_branch);
			ps.delta = next_delta;
			
			// compute the value which applies the computed delta
			PseudoVariable pv = ps.GetLastStatement().inputVariables.get(0);
			TypedOperation op = pv.sequence.GetLastStatement().operation;
			Assert.isTrue(op instanceof TypedTermOperation);
			TypedTermOperation tto = (TypedTermOperation) op;
			Double term_value = (Double) tto.getValue();
			term_value += next_delta;
			
			// create new primitive int and replace the old variable (pv) with new variable (new_pv). 
			PseudoVariable new_pv = PrimitiveGeneratorHelper.CreatePrimitiveVariable(Type.forClass(Double.class), term_value);
			ps.GetLastStatement().inputVariables.set(0, new_pv);
			PseudoVariable new_mutated = new PseudoVariable(ps, ps.Size() - 1);
			
			LinkedSequence after_linked_sequence = ps.container.GenerateLinkedSequence();
			
			result = new BeforeAfterLinkedSequence(selected_to,
					new DeltaChangeTypedOperationMutated(ps, true, new_mutated, true, new_mutated),
					before_linked_sequence, after_linked_sequence);
			
			return result;
		} else {
			return super.Mutate(selected_to, interested_branch, dg);
		}
	}
	
	private double ComputeNextDleta(ArrayList<String> interested_branch) {
		double next_delta = 0.0;
		double in_use_delta = 0.0;
		InfluenceOfBranchChange in_use_influence = null;
		if (delta != 0) {
			in_use_delta = delta;
			in_use_influence = headed_variable_branch_influence;
		} else {
			PseudoSequence headed_variable_sequence = this.headed_variable.sequence;
			if (headed_variable_sequence instanceof NumberPseudoSequence) {
				NumberPseudoSequence ndcps = (NumberPseudoSequence) headed_variable_sequence;
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
		return next_delta;
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
