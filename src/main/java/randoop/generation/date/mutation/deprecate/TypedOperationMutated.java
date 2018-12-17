
package randoop.generation.date.mutation.deprecate;

import randoop.generation.date.sequence.PseudoSequence;
import randoop.generation.date.sequence.PseudoVariable;

public class TypedOperationMutated extends Mutated {
	
	PseudoSequence pseudo_sequence = null;
	boolean has_return_value = false;
	PseudoVariable returned_pseudo_variable = null;
	boolean is_mutating_variable = false;
	PseudoVariable in_use_mutated_pseudo_variable = null;
	
	public TypedOperationMutated(PseudoSequence pseudo_sequence, boolean has_return_value, PseudoVariable returned_pseudo_variable, boolean is_mutating_variable, PseudoVariable in_use_mutated_pseudo_variable) {
		this.pseudo_sequence = pseudo_sequence;
		this.has_return_value = has_return_value;
		this.returned_pseudo_variable = returned_pseudo_variable;
		this.is_mutating_variable = is_mutating_variable;
		this.in_use_mutated_pseudo_variable = in_use_mutated_pseudo_variable;
	}
	
	public PseudoSequence GetPseudoSequence() {
		return pseudo_sequence;
	}
	
	public boolean HasReturnedPseudoVariable() {
		return has_return_value;
	}

	public PseudoVariable GetReturnedPseudoVariable() {
		return returned_pseudo_variable;
	}
	
	public boolean IsMutatingVariable() {
		return is_mutating_variable;
	}
	
	public PseudoVariable GetInUseMutatedPseudoVariable() {
		return in_use_mutated_pseudo_variable;
	}
	
}
