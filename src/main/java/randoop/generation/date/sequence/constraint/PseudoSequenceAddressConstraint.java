package randoop.generation.date.sequence.constraint;

import randoop.generation.date.sequence.PseudoVariable;

public class PseudoSequenceAddressConstraint {

	PseudoVariable pv1 = null;
	PseudoVariable pv2 = null;

	public PseudoSequenceAddressConstraint(PseudoVariable pv1, PseudoVariable pv2) {
		this.pv1 = pv1;
		this.pv1 = pv1;
	}

	public PseudoVariable GetShouldBeSamePseudoVariableOne() {
		return pv1;
	}

	public PseudoVariable GetShouldBeSamePseudoVariableTwo() {
		return pv2;
	}

}
