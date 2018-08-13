package randoop.generation.date.sequence;

public class PseudoVariable {
	
	public PseudoSequence sequence = null;
	public int index = -1;
	
	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
	}

	public PseudoVariable CopySelfInDeepCloneWay() {
		PseudoSequence copyed_sequence = sequence.CopySelfInDeepCloneWay();
		copyed_sequence.Reset(index+1);
		return new PseudoVariable(copyed_sequence, index);
	}
	
}
