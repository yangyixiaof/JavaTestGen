package randoop.generation.date.mutation.operation;

import java.util.Map;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.mutation.util.StatementInfoFetcher;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;

public class PrimitiveBooleanModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;

	public PrimitiveBooleanModify(TraceableSequence sequence, int stmtIndex, int varIndex) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
	}

	@Override
	public TraceableSequence ApplyMutation() {
		return sequence.modifyBoolean(stmtIndex, varIndex);
	}

	@Override
	public String toString() {
		return "PrimitiveBooleanModify at:" + stmtIndex + "#input_index:" + varIndex;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(Map<TypedOperation, Integer> operation_id_map,
			Map<String, Integer> other_value_id_map) {
		DenseObjectMatrix2D result = new DenseObjectMatrix2D(2, 3);
//		int[][] result = new int[2][3];
		result.set(0, 0, stmtIndex);
		result.set(1, 0, 2);
//		result[0][0] = stmtIndex;
//		result[1][0] = 2;
		int operation_index = operation_id_map.size() + other_value_id_map.get("PrimitiveBooleanModify");
		result.set(0, 1, operation_index);
		result.set(1, 1, 1);
//		result[0][1] = operation_index;
//		result[1][1] = 1;
		result.set(0, 2, StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex));
		result.set(1, 2, 0);
//		result[0][2] = StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex);
//		result[1][2] = 0;
		return result;
	}
	
}
