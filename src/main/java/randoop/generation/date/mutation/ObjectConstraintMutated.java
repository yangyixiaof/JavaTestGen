package randoop.generation.date.mutation;

import randoop.generation.date.sequence.PseudoSequenceContainer;

public class ObjectConstraintMutated extends Mutated {
	
	PseudoSequenceContainer container = null;
	
	public ObjectConstraintMutated(PseudoSequenceContainer container) {
		this.container = container;
	}
	
	public PseudoSequenceContainer GetPseudoSequenceContainer() {
		return container;
	}
	
}
