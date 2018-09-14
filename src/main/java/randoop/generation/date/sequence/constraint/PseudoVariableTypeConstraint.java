package randoop.generation.date.sequence.constraint;

import randoop.generation.date.sequence.PseudoVariable;

public class PseudoVariableTypeConstraint extends PseudoVariableConstraint {

	PseudoVariable pv = null;
	Class<?> type = null;
	boolean to_same = true;

	public PseudoVariableTypeConstraint(PseudoVariable pv, Class<?> type, boolean to_same) {
		this.pv = pv;
		this.type = type;
		this.to_same = to_same;
	}

	public PseudoVariable GetPseudoVariable() {
		return pv;
	}

	public Class<?> GetSpecifiedType() {
		return type;
	}
	
	public boolean IsToSame() {
		return to_same;
	}

}
