package randoop.generation.date.sequence;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.mutation.DeltaChangeTypedOperationMutated;
import randoop.generation.date.sequence.helper.PrimitiveGeneratorHelper;
import randoop.operation.TypedOperation;
import randoop.types.Type;
import randoop.util.Randomness;

public class StringPseudoSequence extends PseudoSequence {
	
	// {
		// deprecated block. it seems delta change is unnecessary
		// position delta, based on DeltaChangePseudoSequence this child class only needs to record position information
		// int position = 0;
		// int delta = 0;
	// }
	
	
	
	// position inserted, the position range is 0 ... n, n is the length of content of headed_variable
//	Map<Integer, TriedChars> inserted_char = new TreeMap<Integer, TriedChars>();
//	int recent_tried_position = -1;
	
	public StringPseudoSequence() {// ArrayList<TypedOperation> operations
		super();// operations
	}
	
//	public StringDeltaChangePseudoSequence(PseudoVariable pv, ArrayList<TypedOperation> operations) {
//		super(pv, operations);
//	}
	
	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch, DateGenerator dg) {
		// fetch content of this headed variable
		String content = dg.pseudo_variable_content.get(headed_variable);
		// create input variable of operation
		ArrayList<PseudoVariable> input_variables = new ArrayList<PseudoVariable>();
		PseudoSequence copied_this = this.CopySelfAndCitersInDeepCloneWay(dg);
		input_variables.add(copied_this.headed_variable);
		int position = -1;
		int character = 0;
		// insert situation
		if (selected_to.toString().startsWith("randoop.generation.date.runtime.DateRuntimeSupport.InsertString")) {
			if (content == null) {
				// use the default value
			} else {
				// select suitable position (random) and suitable character (random) 
				position = Randomness.nextRandomInt(content.length()+1);
				character = Randomness.nextRandomInt(200);
			}
		} else if (selected_to.toString().startsWith("randoop.generation.date.runtime.DateRuntimeSupport.ChangeDeltaInPositionOfString")) {
			if (content == null) {
				// use the default value
			} else {
				// select suitable position (random) and suitable character delta (random) 
				// TODO 实现新的选择逻辑
				
			}
		} else {
			Assert.isTrue(false, "Error! Unsupported operation for String: " + selected_to.toString());
		}
		PseudoVariable position_pv = PrimitiveGeneratorHelper.CreatePrimitiveVariable(Type.forClass(int.class), position);
		PseudoVariable character_pv = PrimitiveGeneratorHelper.CreatePrimitiveVariable(Type.forClass(int.class), character);
		input_variables.add(position_pv);
		input_variables.add(character_pv);
		PseudoVariable new_generated = copied_this.Append(selected_to, input_variables);
		// generate compare sequences
		LinkedSequence before_linked_sequence = this.container.GenerateLinkedSequence();
		LinkedSequence after_linked_sequence = copied_this.container.GenerateLinkedSequence();
		BeforeAfterLinkedSequence result = new BeforeAfterLinkedSequence(selected_to,
				new DeltaChangeTypedOperationMutated(copied_this, true, new_generated, true, new_generated),
				before_linked_sequence, after_linked_sequence);
		return result;
		
		// compute whether changing a position is meaningful or not 
		// meaningful: the influenced position has not been exactly covered. 
		// not meaningful: the influenced position has been exactly covered. 
		
		// record branch whole structure changing: 
		// if changed, e.g, appearance number of one same branch from 4 to 3: it shows that influences really happen. 
		// in this situation, we can not decide whether the changes are over, so the changing position should be less selected. 
		// this branch whole structure changed, decrease the weights of the selected changing position. 
		
		// all two schemas must be accompanied with trying-times discount
		
		// some operations are not in sampling, but in force-executing such as modifying-directly-after-inserting
	}
	
//	@Override
//	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
//			Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable, Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
//		if (selected_to.getInputTypes().size() == 1) {
//			return super.Mutate(selected_to, interested_branch, dg);
//		} else {
//			int next_position = 0;
//			double next_delta = 1.0;
//			double in_use_delta = 0.0;
//			int max_range = 1;
//			if (headed_variable_string != null) {
//				max_range = headed_variable_string.length();
//			}
//			// compute position
//			double rnd = Math.random();
//			if (rnd <= 0.5) {
//				// use current position.
//				next_position = position;
//			} else {
//				// use new position.
//				next_position = Randomness.nextRandomInt(max_range);
//			}
//			
//			BeforeAfterLinkedSequence result = null;
//			ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
//			// initialize candidates.
//			HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
//			DeltaChangePseudoSequence ps = (DeltaChangePseudoSequence)this.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
//					class_object_headed_sequence);
//			ps.SetPreviousSequence(this);
//			
//			HashSet<Double> have_tried_delta = have_tried_delta_for_position.get(next_position);
//			if (have_tried_delta == null) {
//				have_tried_delta = new HashSet<Double>();
//				have_tried_delta_for_position.put(next_position, have_tried_delta);
//			}
//			
//			Map<String, Influence> in_use_influence = null;
//			if (delta != 0) {
//				in_use_delta = delta;
//				in_use_influence = all_branches_influences_compared_to_previous;
//			} else {
//				PseudoSequence headed_variable_sequence = this.headed_variable.sequence;
//				if (headed_variable_sequence instanceof DeltaChangePseudoSequence) {
//					DeltaChangePseudoSequence ndcps = (DeltaChangePseudoSequence)headed_variable_sequence;
//					double headed_variable_sequence_delta = ndcps.delta;
//					if (headed_variable_sequence_delta != 0) {
//						in_use_delta = headed_variable_sequence_delta;
//						in_use_influence = ndcps.all_branches_influences_compared_to_previous;
//					}
//				}
//			}
//			double influence = 0.0;
//			if (in_use_influence != null) {
//				influence = SimpleInfluenceComputer.ComputeAveragedInfluence(interested_branch, in_use_influence);
//			}
//			next_delta = SequenceGeneratorHelper.ComputeDelta(in_use_delta, influence, have_tried_delta);
//			ps.delta = next_delta;
//			input_pseudo_variables.add(0, ps.headed_variable);
//			ArrayList<TypedOperation> dp_operations = new ArrayList<TypedOperation>();
//			DisposablePseudoSequence dps = new DisposablePseudoSequence(dp_operations);
//			TypedOperation dp_op = TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE, next_delta);
//			dp_operations.add(dp_op);
//			PseudoVariable dpv = dps.Append(dp_op, new ArrayList<PseudoVariable>(), class_object_headed_sequence);
//			dps.SetHeadedVariable(dpv);
//			input_pseudo_variables.add(1, dpv);
//			ArrayList<TypedOperation> dp_operations2 = new ArrayList<TypedOperation>();
//			DisposablePseudoSequence dps2 = new DisposablePseudoSequence(dp_operations2);
//			TypedOperation dp_op2 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, next_position);
//			dp_operations2.add(dp_op2);
//			PseudoVariable dpv2 = dps2.Append(dp_op2, new ArrayList<PseudoVariable>(), class_object_headed_sequence);
//			dps.SetHeadedVariable(dpv2);
//			LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
//			PseudoVariable pv = ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
//			ps.ReplacePseudoVariableInCitesAndCiters(ps, headed_variable, pv);
//			LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
//			result = new BeforeAfterLinkedSequence(selected_to, ps.headed_variable, ps, before_linked_sequence,
//						after_linked_sequence);
//			return result;
//		}
//	}
	
//	@Override
//	public double GetPunishment(TypedOperation selected_op) {
//		if (selected_op.getInputTypes().size() == 1) { // refer to randoop.generation.date.runtime.DateRuntimeSupport.AppendString
//			return super.GetPunishment(selected_op);
//		}
//		return 0.0;
//	}

}

//class TriedChars {
//	
//	Set<Integer> tried_set = new TreeSet<Integer>();
//	// the key is mutated from value
//	Map<Integer, Integer> track = new TreeMap<Integer, Integer>();
//	Map<Integer, PseudoVariable> tried_crsp_variable = new TreeMap<Integer, PseudoVariable>();
//	
//}
