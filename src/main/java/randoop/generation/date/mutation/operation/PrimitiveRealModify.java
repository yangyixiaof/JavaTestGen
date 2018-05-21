package randoop.generation.date.mutation.operation;

import java.util.Map;

import randoop.generation.date.mutation.util.StatementInfoFetcher;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;

public class PrimitiveRealModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;
	Object deltaValue = null;

	public PrimitiveRealModify(TraceableSequence sequence, int stmtIndex, int varIndex, Object deltaValue) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
		this.deltaValue = deltaValue;
	}

	@Override
	public TraceableSequence ApplyMutation() {
		return sequence.modifyReal(stmtIndex, varIndex, deltaValue);
	}

	@Override
	public String toString() {
		return "PrimitiveRealModify at:" + stmtIndex + "#input_index:" + varIndex + "#deltaValue:" + deltaValue;
	}

	@Override
	public int[][] toMutationComputeTensor(Map<TypedOperation, Integer> operation_id_map,
			Map<String, Integer> other_value_id_map) {
		int[][] result = new int[2][4];
		result[0][0] = stmtIndex;
		result[1][0] = 2;
		int operation_index = operation_id_map.size() + other_value_id_map.get("PrimitiveRealModify");
		result[0][1] = operation_index;
		result[1][1] = 1;
		result[0][2] = StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex);
		result[1][2] = 0;
		result[0][3] = other_value_id_map.get(deltaValue.toString());
		result[1][3] = 1;
		return result;
	}
}
