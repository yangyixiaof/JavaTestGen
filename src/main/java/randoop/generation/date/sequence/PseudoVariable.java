package randoop.generation.date.sequence;

import java.util.Map;

import org.eclipse.core.runtime.Assert;

public class PseudoVariable {

	public PseudoSequence sequence = null;
	public int index = -1;
	
//	String headed_variable_string = null;
	
	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
		Assert.isTrue(index >= 0);
	}
	
//	public void SetVariableToString(String variable_string) {
//		if (headed_variable_string == null) {
//			Assert.isTrue(this.headed_variable_string == null);
//		} else {
//			if (this.headed_variable_string == null) {
//				this.headed_variable_string = headed_variable_string;
//			} else {
//				Assert.isTrue(headed_variable_string.equals(this.headed_variable_string));
//			}
//		}
//		this.headed_variable_string = variable_string;
//	}

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
