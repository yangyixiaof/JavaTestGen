package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.SimpleInfluenceComputer;
import randoop.operation.TypedOperation;
import randoop.types.JavaTypes;
import randoop.util.Randomness;

public class StringDeltaChangePseudoSequence extends PseudoSequence {
	
	int position = 0;
	double delta = 0.0;
	
	Map<Integer, HashSet<Double>> have_tried_delta_for_position = new TreeMap<Integer, HashSet<Double>>();
	
	Random random = new Random();
	
	public StringDeltaChangePseudoSequence(ArrayList<TypedOperation> operations) {
		super(operations);
	}
	
	public StringDeltaChangePseudoSequence(PseudoVariable pv, ArrayList<TypedOperation> operations) {
		super(pv, operations);
	}
	
	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		if (selected_to.getInputTypes().size() == 1) {
			return super.Mutate(selected_to, interested_branch, class_pseudo_variable, class_object_headed_sequence);
		} else {
			int next_position = 0;
			double next_delta = 1.0;
			double in_use_delta = 0.0;
			int max_range = 1;
			if (headed_variable_string != null) {
				max_range = headed_variable_string.length();
			}
			// compute position
			double rnd = Math.random();
			if (rnd <= 0.5) {
				// use current position.
				next_position = position;
			} else {
				// use new position.
				next_position = Randomness.nextRandomInt(max_range);
			}
			
			BeforeAfterLinkedSequence result = null;
			ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
			// initialize candidates.
			HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
			NumberDeltaChangePseudoSequence ps = (NumberDeltaChangePseudoSequence)this.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
					class_object_headed_sequence);
			ps.SetPreviousSequence(this);
			
			HashSet<Double> have_tried_delta = have_tried_delta_for_position.get(next_position);
			if (have_tried_delta == null) {
				have_tried_delta = new HashSet<Double>();
				have_tried_delta_for_position.put(next_position, have_tried_delta);
			}
			
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
			input_pseudo_variables.add(0, ps.headed_variable);
			ArrayList<TypedOperation> dp_operations = new ArrayList<TypedOperation>();
			DisposablePseudoSequence dps = new DisposablePseudoSequence(dp_operations);
			TypedOperation dp_op = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, next_delta);
			dp_operations.add(dp_op);
			PseudoVariable dpv = dps.Append(dp_op, new ArrayList<PseudoVariable>(), class_object_headed_sequence);
			dps.SetHeadedVariable(dpv);
			input_pseudo_variables.add(1, dpv);
			ArrayList<TypedOperation> dp_operations2 = new ArrayList<TypedOperation>();
			DisposablePseudoSequence dps2 = new DisposablePseudoSequence(dp_operations2);
			TypedOperation dp_op2 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, next_position);
			dp_operations2.add(dp_op2);
			PseudoVariable dpv2 = dps2.Append(dp_op2, new ArrayList<PseudoVariable>(), class_object_headed_sequence);
			dps.SetHeadedVariable(dpv2);
			LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
			PseudoVariable pv = ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
			ps.ReplacePseudoVariableInCitesAndCiters(ps, headed_variable, pv);
			LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
			result = new BeforeAfterLinkedSequence(selected_to, ps.headed_variable, ps, before_linked_sequence,
						after_linked_sequence);
			return result;
		}
	}
	
	@Override
	public double GetPunishment(TypedOperation selected_op) {
		if (selected_op.getInputTypes().size() == 1) { // refer to randoop.generation.date.runtime.DateRuntimeSupport.AppendString
			return super.GetPunishment(selected_op);
		}
		return 0.0;
	}

}
