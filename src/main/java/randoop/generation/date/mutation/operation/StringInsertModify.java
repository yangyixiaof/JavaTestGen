package randoop.generation.date.mutation.operation;

import java.util.Map;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.mutation.util.StatementInfoFetcher;
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
	public DenseObjectMatrix2D toComputeTensor(Map<TypedOperation, Integer> operation_id_map,
			Map<String, Integer> other_value_id_map) {
		DenseObjectMatrix2D result = new DenseObjectMatrix2D(2, 4);
		result.set(0, 0, stmtIndex);
		result.set(1, 0, 2);
		int operation_index = operation_id_map.size() + other_value_id_map.get("StringInsertModify");
		result.set(0, 1, operation_index);
		result.set(1, 1, 1);
		result.set(0, 2, StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex));
		result.set(1, 2, 0);
		result.set(0, 3, other_value_id_map.get("CharIndex" + charIndex));
		result.set(1, 3, 1);
		return result;
	}
}
