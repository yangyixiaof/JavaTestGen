package randoop.generation.date.mutation;

import randoop.generation.date.sequence.PseudoSequence;
import randoop.generation.date.sequence.PseudoVariable;

public class DeltaChangeTypedOperationMutated extends TypedOperationMutated {

	public DeltaChangeTypedOperationMutated(PseudoSequence pseudo_sequence, boolean has_return_value,
			PseudoVariable returned_pseudo_variable, boolean is_mutating_variable, PseudoVariable in_use_mutated_pseudo_variable) {
		super(pseudo_sequence, has_return_value, returned_pseudo_variable, is_mutating_variable, in_use_mutated_pseudo_variable);
	}

}
