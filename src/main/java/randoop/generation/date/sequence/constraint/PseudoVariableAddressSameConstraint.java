package randoop.generation.date.sequence.constraint;

import randoop.generation.date.sequence.PseudoVariable;

public class PseudoVariableAddressSameConstraint extends PseudoVariableConstraint {

	PseudoVariable pv1 = null;
	PseudoVariable pv2 = null;

	public PseudoVariableAddressSameConstraint(PseudoVariable pv1, PseudoVariable pv2) {
		this.pv1 = pv1;
		this.pv1 = pv1;
	}

	public PseudoVariable GetShouldBeSamePseudoVariableOne() {
		if (pv1.sequence.SizeOfUsers() >= pv2.sequence.SizeOfUsers()) {
			return pv1;
		} else {
			return pv2;
		}
	}

	public PseudoVariable GetShouldBeSamePseudoVariableTwo() {
		if (pv1.sequence.SizeOfUsers() >= pv2.sequence.SizeOfUsers()) {
			return pv2;
		} else {
			return pv1;
		}
	}

}
