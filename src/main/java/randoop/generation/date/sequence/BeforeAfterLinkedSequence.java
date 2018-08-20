package randoop.generation.date.sequence;

import randoop.operation.TypedOperation;

public class BeforeAfterLinkedSequence {

	TypedOperation operation = null;
	PseudoVariable pseudo_variable = null;
	LinkedSequence before_linked_sequence = null;
	LinkedSequence after_linked_sequence = null;

	public BeforeAfterLinkedSequence(TypedOperation operation, PseudoVariable pseudo_variable,
			LinkedSequence before_linked_sequence, LinkedSequence after_linked_sequence) {
		this.operation = operation;
		this.pseudo_variable = pseudo_variable;
		this.before_linked_sequence = before_linked_sequence;
		this.after_linked_sequence = after_linked_sequence;
	}
	
	public TypedOperation GetTypedOperation() {
		return operation;
	}
	
	public PseudoVariable GetPseudoVariable() {
		return pseudo_variable;
	}
	
	public LinkedSequence GetBeforeLinkedSequence() {
		return before_linked_sequence;
	}
	
	public LinkedSequence GetAfterLinkedSequence() {
		return after_linked_sequence;
	}

}
