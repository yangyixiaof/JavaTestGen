package randoop.generation.date.sequence;

public class PseudoSequenceWithVariable {
	
	PseudoSequence ps = null;
	PseudoVariable pv = null;
	
	public PseudoSequenceWithVariable(PseudoSequence ps, PseudoVariable pv) {
		this.ps = ps;
		this.pv = pv;
	}
	
	public PseudoSequence GetPseudoSequence() {
		return ps;
	}
	
	public PseudoVariable GetPseudoVariable() {
		return pv;
	}
	
}
