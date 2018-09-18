package randoop.generation.date.sequence;

import java.util.Map;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;

public class PseudoVariable {

	public PseudoSequence sequence = null;
	public int index = -1;

	// String headed_variable_string = null;

	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
		Assert.isTrue(index >= 0);
	}

	// public void SetVariableToString(String variable_string) {
	// if (headed_variable_string == null) {
	// Assert.isTrue(this.headed_variable_string == null);
	// } else {
	// if (this.headed_variable_string == null) {
	// this.headed_variable_string = headed_variable_string;
	// } else {
	// Assert.isTrue(headed_variable_string.equals(this.headed_variable_string));
	// }
	// }
	// this.headed_variable_string = variable_string;
	// }

	public PseudoVariable CopySelfInDeepCloneWay(PseudoSequenceContainer container, Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			DateGenerator dg) {// Map<PseudoVariable, PseudoSequence> class_object_headed_sequence
		PseudoSequence copied_sequence = sequence.CopySelfInDeepCloneWay(container, origin_copied_sequence_map, dg);
		return new PseudoVariable(copied_sequence, index);
	}

//	public PseudoVariable CopySelfAndCitersInDeepCloneWay(
//			Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
//			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
//		PseudoSequence copied_sequence = sequence.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
//				class_object_headed_sequence);
//		return new PseudoVariable(copied_sequence, index);
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sequence.hashCode();
		result = prime * result + index;
		return result;
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
	
	@Override
	public String toString() {
		return "index:" + index + "#sequence:" + sequence;
	}

}
