package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.Map;

import randoop.operation.TypedOperation;

class PseudoStatement {
	
	TypedOperation operation = null;
	ArrayList<PseudoVariable> inputVariables = null;
	
	public PseudoStatement(TypedOperation operation, ArrayList<PseudoVariable> inputVariables) {
		this.operation = operation;
		this.inputVariables = inputVariables;
	}

	public PseudoStatement CopySelfInDeepCloneWay(Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map, Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		ArrayList<PseudoVariable> copyInputVariables = new ArrayList<PseudoVariable>();
		for (PseudoVariable pv : inputVariables) {
			copyInputVariables.add(pv.CopySelfInDeepCloneWay(origin_copied_sequence_map, class_object_headed_sequence));
		}
		return new PseudoStatement(operation, copyInputVariables);
	}
	
}
