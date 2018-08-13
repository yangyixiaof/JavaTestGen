package randoop.generation.date.sequence;

import java.util.Map;

public class PseudoVariable {
	
	public PseudoSequence sequence = null;
	public int index = -1;
	
	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
	}

	public PseudoVariable CopySelfInDeepCloneWay(Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map) {
		PseudoSequence copyed_sequence = sequence.CopySelfInDeepCloneWay(origin_copied_sequence_map);
		return new PseudoVariable(copyed_sequence, index);
	}
	
}
