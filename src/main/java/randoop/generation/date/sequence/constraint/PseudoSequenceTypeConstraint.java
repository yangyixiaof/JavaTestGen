package randoop.generation.date.sequence.constraint;

import randoop.generation.date.sequence.PseudoVariable;

public class PseudoSequenceTypeConstraint {

	PseudoVariable pv = null;
	Class<?> type = null;

	public PseudoSequenceTypeConstraint(PseudoVariable pv, Class<?> type) {
		this.pv = pv;
		this.type = type;
	}

	public PseudoVariable GetPseudoSequence() {
		return pv;
	}

	public Class<?> GetSpecifiedType() {
		return type;
	}

}
