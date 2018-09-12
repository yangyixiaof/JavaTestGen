package randoop.generation.date.sequence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import randoop.operation.TypedOperation;

public class PseudoVariable {

	public PseudoSequence sequence = null;
	public int index = -1;
	
	String headed_variable_string = null;
	
	Map<TypedOperation, Integer> operation_use_count = new HashMap<TypedOperation, Integer>();

	HashSet<PseudoSequence> sequences_which_use_this_variable = new HashSet<PseudoSequence>();
	
	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
	}
	
	public void SetVariableToString(String variable_string) {
//		if (headed_variable_string == null) {
//			Assert.isTrue(this.headed_variable_string == null);
//		} else {
//			if (this.headed_variable_string == null) {
//				this.headed_variable_string = headed_variable_string;
//			} else {
//				Assert.isTrue(headed_variable_string.equals(this.headed_variable_string));
//			}
//		}
		this.headed_variable_string = variable_string;
	}

	public PseudoVariable CopySelfInDeepCloneWay(Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		PseudoSequence copied_sequence = sequence.CopySelfInDeepCloneWay(origin_copied_sequence_map,
				class_object_headed_sequence);
		return new PseudoVariable(copied_sequence, index);
	}
	
	public PseudoVariable CopySelfAndCitersInDeepCloneWay(
			Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		PseudoSequence copied_sequence = sequence.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
				class_object_headed_sequence);
		return new PseudoVariable(copied_sequence, index);
	}
	
	public void OperationApplied(TypedOperation to) {
		Integer count = operation_use_count.get(to);
		count = (count == null ? 0 : count) + 1;
		operation_use_count.put(to, count);
	}
	
	public int SizeOfUsers() {
		return sequences_which_use_this_variable.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PseudoVariable) {
			PseudoVariable pv = (PseudoVariable) obj;
			if (sequence == pv.sequence && index == pv.index) {
				return true;
			}
		}
		return false;
	}

}
