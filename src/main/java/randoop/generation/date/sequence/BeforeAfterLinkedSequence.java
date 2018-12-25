package randoop.generation.date.sequence;

import randoop.generation.date.mutation.Mutation;
import randoop.operation.TypedOperation;

public class BeforeAfterLinkedSequence {

	TypedOperation operation = null;
//	Mutated mutated = null;
	Mutation mutation = null;
	LinkedSequence before_linked_sequence = null;
	LinkedSequence after_linked_sequence = null;

	// PseudoVariable pseudo_variable, PseudoSequence pseudo_sequence,
	// Mutated mutated
	public BeforeAfterLinkedSequence(TypedOperation operation, Mutation mutation, LinkedSequence before_linked_sequence,
			LinkedSequence after_linked_sequence) {
		this.operation = operation;
//		this.mutated = mutated;
		this.mutation = mutation;
		this.before_linked_sequence = before_linked_sequence;
		this.after_linked_sequence = after_linked_sequence;
	}

	public TypedOperation GetTypedOperation() {
		return operation;
	}
	
//	public Mutated GetMutated() {
//		return mutated;
//	}
	
	public Mutation GetMutation() {
		return mutation;
	}

	public LinkedSequence GetBeforeLinkedSequence() {
		return before_linked_sequence;
	}

	public LinkedSequence GetAfterLinkedSequence() {
		return after_linked_sequence;
	}

}
