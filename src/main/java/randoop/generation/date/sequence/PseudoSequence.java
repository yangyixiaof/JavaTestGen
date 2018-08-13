package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import randoop.operation.TypedOperation;

public class PseudoSequence {
	
	ArrayList<PseudoStatement> statements = new ArrayList<PseudoStatement>();
	
	HashSet<PseudoSequence> sequences_which_use_this_sequence = new HashSet<PseudoSequence>();
	
	public PseudoSequence() {
	}
	
	public void Append(TypedOperation operation, ArrayList<PseudoVariable> inputVariables) {
		statements.add(new PseudoStatement(operation, inputVariables));
		AddReferenceForAllVariables(inputVariables);
	}
	
	public void Reset(int retain_length) {
		if (retain_length > statements.size()) {
			retain_length = statements.size();
		}
		List<PseudoStatement> retain_statements = statements.subList(0, retain_length);
		for (PseudoStatement stmt : statements) {
			RemoveReferenceForAllVariables(stmt.inputVariables);
		}
		statements.clear();
		statements.addAll(retain_statements);
		for (PseudoStatement stmt : statements) {
			AddReferenceForAllVariables(stmt.inputVariables);
		}
	}
	
	private void AddReferenceForAllVariables(List<PseudoVariable> inputVariables) {
		for (PseudoVariable pv : inputVariables) {
			AddReferenceToInUseSequence(pv.sequence);
		}
	}
	
	private void RemoveReferenceForAllVariables(List<PseudoVariable> inputVariables) {
		for (PseudoVariable pv : inputVariables) {
			RemoveReferenceToInUseSequence(pv.sequence);
		}
	}
	
	private void AddReferenceToInUseSequence(PseudoSequence in_use_sequence) {
		in_use_sequence.sequences_which_use_this_sequence.add(this);
	}
	
	private void RemoveReferenceToInUseSequence(PseudoSequence in_use_sequence) {
		in_use_sequence.sequences_which_use_this_sequence.remove(this);
	}
	
	public PseudoSequence CopySelfInDeepCloneWay() {
		PseudoSequence copy_version = new PseudoSequence();
		for (PseudoStatement stmt : statements) {
			PseudoStatement copy_stmt = stmt.CopySelfInDeepCloneWay();
			copy_version.Append(copy_stmt.operation, copy_stmt.inputVariables);
		}
		return copy_version;
	}
	
	public LinkedSequence GenerateLinkedSequence() {
		
		
		
	}
	
}
