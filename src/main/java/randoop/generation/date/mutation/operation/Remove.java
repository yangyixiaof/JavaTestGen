package randoop.generation.date.mutation.operation;

import java.util.Map;

import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;

public class Remove extends MutationOperation {

	int stmtIndex = -1;

	public Remove(TraceableSequence sequence, int stmtIndex) {
		super(sequence);
		this.stmtIndex = stmtIndex;
	}

	@Override
	public TraceableSequence ApplyMutation() {
		return sequence.remove(stmtIndex);
	}

	@Override
	public String toString() {
		return "Remove at:" + stmtIndex;
	}

	@Override
	public int[][] toMutationComputeTensor(Map<TypedOperation, Integer> operation_id_map, Map<String, Integer> other_value_id_map) {
		return null;
	}
}
