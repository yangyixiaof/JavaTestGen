package randoop.generation.date.sequence;

import java.util.List;

import randoop.operation.TypedOperation;

class PseudoStatement {
	
	TypedOperation operation = null;
	List<PseudoVariable> inputVariables = null;
	
	public PseudoStatement(TypedOperation operation, List<PseudoVariable> inputVariables) {
		this.operation = operation;
		this.inputVariables = inputVariables;
	}
	
}
