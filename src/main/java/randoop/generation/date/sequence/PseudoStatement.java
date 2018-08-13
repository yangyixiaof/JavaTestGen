package randoop.generation.date.sequence;

import java.util.ArrayList;

import randoop.operation.TypedOperation;

class PseudoStatement {
	
	TypedOperation operation = null;
	ArrayList<PseudoVariable> inputVariables = null;
	
	public PseudoStatement(TypedOperation operation, ArrayList<PseudoVariable> inputVariables) {
		this.operation = operation;
		this.inputVariables = inputVariables;
	}

	public PseudoStatement CopySelfInDeepCloneWay() {
		ArrayList<PseudoVariable> copyInputVariables = new ArrayList<PseudoVariable>();
		for (PseudoVariable pv : inputVariables) {
			copyInputVariables.add(pv.CopySelfInDeepCloneWay());
		}
		return new PseudoStatement(operation, copyInputVariables);
	}
	
}
