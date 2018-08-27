package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.SimpleInfluenceComputer;
import randoop.operation.TypedOperation;
import randoop.types.JavaTypes;

public class NumberDeltaChangePseudoSequence extends PseudoSequence {
	
	double delta = 0.0;
	
	Set<Double> have_tried_delta = new HashSet<Double>();
	
	Random random = new Random();
	
	public NumberDeltaChangePseudoSequence(ArrayList<TypedOperation> operations) {
		super(operations);
	}
	
	public NumberDeltaChangePseudoSequence(PseudoVariable pv, ArrayList<TypedOperation> operations) {
		super(pv, operations);
	}
	
	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, TypedOperation could_use_to, ArrayList<String> interested_branch,
			Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		BeforeAfterLinkedSequence result = null;
		ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
		// initialize candidates.
		HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
		NumberDeltaChangePseudoSequence ps = (NumberDeltaChangePseudoSequence)this.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
				class_object_headed_sequence);
		ps.SetPreviousSequence(this);
		ArrayList<TypedOperation> dp_operations = new ArrayList<TypedOperation>();
		double next_delta = 0.0;
		double in_use_delta = 0.0;
		Map<String, Influence> in_use_influence = null;
		if (delta != 0) {
			in_use_delta = delta;
			in_use_influence = all_branches_influences_compared_to_previous;
		} else {
			PseudoSequence headed_variable_sequence = this.headed_variable.sequence;
			if (headed_variable_sequence instanceof NumberDeltaChangePseudoSequence) {
				NumberDeltaChangePseudoSequence ndcps = (NumberDeltaChangePseudoSequence)headed_variable_sequence;
				double headed_variable_sequence_delta = ndcps.delta;
				if (headed_variable_sequence_delta != 0) {
					in_use_delta = headed_variable_sequence_delta;
					in_use_influence = ndcps.all_branches_influences_compared_to_previous;
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
		DisposablePseudoSequence dps = new DisposablePseudoSequence(dp_operations);
		PseudoVariable dpv = dps.Append(dp_op, new ArrayList<PseudoVariable>(), class_object_headed_sequence);
		dps.SetHeadedVariable(dpv);
		input_pseudo_variables.add(0, ps.headed_variable);
		input_pseudo_variables.add(1, dpv);
		LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
		PseudoVariable pv = ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
		ps.ReplacePseudoVariableInCitesAndCiters(ps, headed_variable, pv);
		LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
		result = new BeforeAfterLinkedSequence(selected_to, ps.headed_variable, ps, before_linked_sequence,
					after_linked_sequence);
		return result;
	}
	
	@Override
	public double GetPunishment(TypedOperation selected_op) {
		return 0.0;
	}
	
}
