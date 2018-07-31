package randoop.generation.date.mutation.operation;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.mutation.util.StatementInfoFetcher;
import randoop.generation.date.sequence.TraceableSequence;

public class PrimitiveIntegralModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;
	Object deltaValue = null;

	public PrimitiveIntegralModify(TraceableSequence sequence, int stmtIndex, int varIndex, Object deltaValue) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
		this.deltaValue = deltaValue;
	}

	@Override
	public TraceableSequence ApplyMutation() {
		return sequence.modifyIntegral(this, stmtIndex, varIndex, deltaValue);
	}

	@Override
	public String toString() {
		return "PrimitiveIntegralModify at:" + stmtIndex + "#input_index:" + varIndex + "#deltaValue:" + deltaValue;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		DenseObjectMatrix2D result = new DenseObjectMatrix2D(2, 4);
//		int[][] result = new int[2][4];
		result.set(0, 0, stmtIndex);
		result.set(1, 0, 2);
//		result[0][0] = stmtIndex;
//		result[1][0] = 2;
		int operation_index = string_id_assigner.AssignID("PrimitiveIntegralModify");
		result.set(0, 1, operation_index);
		result.set(1, 1, 1);
//		result[0][1] = operation_index;
//		result[1][1] = 1;
		result.set(0, 2, StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex));
		result.set(1, 2, 0);
//		result[0][2] = StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex);
//		result[1][2] = 0;
		result.set(0, 3, string_id_assigner.AssignID(deltaValue.toString()));
		result.set(1, 3, 1);
//		result[0][3] = other_value_id_map.get(deltaValue.toString());
//		result[1][3] = 1;
		return result;
	}
}
