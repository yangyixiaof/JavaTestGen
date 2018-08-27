package randoop.generation.date.sequence;

import java.util.HashSet;
import java.util.Map;

public class PseudoVariable {

	public PseudoSequence sequence = null;
	public int index = -1;

	HashSet<PseudoSequence> sequences_which_use_this_variable = new HashSet<PseudoSequence>();
	
	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
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
