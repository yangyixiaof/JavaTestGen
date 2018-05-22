package randoop.generation.date.mutation.operation;

import java.util.Map;

import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;

public class StringInsertModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;
	int charIndex = -1;

	public StringInsertModify(TraceableSequence sequence, int stmtIndex, int varIndex, int charIndex) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
		this.charIndex = charIndex;
	}

	@Override
	public TraceableSequence ApplyMutation() {
		return sequence.modifyStringInsert(stmtIndex, varIndex, charIndex);
	}

	@Override
	public String toString() {
		return "StringInsertModify at:" + stmtIndex + "#varIndex:" + varIndex + "#charIndex:" + charIndex;
	}

	@Override
	public int[][] toMutationComputeTensor(Map<TypedOperation, Integer> operation_id_map,
			Map<String, Integer> other_value_id_map) {
		
		return null;
	}
}
