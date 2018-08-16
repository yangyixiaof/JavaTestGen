package randoop.generation.date.sequence;

import java.util.Map;

public class PseudoVariable {
	
	public PseudoSequence sequence = null;
	public int index = -1;
	
	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
	}

	public PseudoVariable CopySelfInDeepCloneWay(Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map, Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		PseudoSequence copyed_sequence = sequence.CopySelfInDeepCloneWay(origin_copied_sequence_map, class_object_headed_sequence);
		return new PseudoVariable(copyed_sequence, index);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PseudoVariable) {
			PseudoVariable pv = (PseudoVariable)obj;
			if (sequence == pv.sequence && index == pv.index) {
				return true;
			}
		}
		return false;
	}
	
}
