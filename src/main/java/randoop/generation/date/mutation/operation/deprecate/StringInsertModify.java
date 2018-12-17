package randoop.generation.date.mutation.operation.deprecate;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.mutation.util.deprecate.StatementInfoFetcher;
import randoop.generation.date.sequence.TraceableSequence;

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
		return sequence.modifyStringInsert(this, stmtIndex, varIndex, charIndex);
	}

	@Override
	public String toString() {
		return "StringInsertModify at:" + stmtIndex + "#varIndex:" + varIndex + "#charIndex:" + charIndex;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		DenseObjectMatrix2D result = new DenseObjectMatrix2D(2, 4);
		result.set(0, 0, stmtIndex);
		result.set(1, 0, 2);
		int operation_index = string_id_assigner.AssignID("StringInsertModify");
		result.set(0, 1, operation_index);
		result.set(1, 1, 1);
		result.set(0, 2, StatementInfoFetcher.FetchVariableDeclareStatementIndex(sequence, stmtIndex, varIndex));
		result.set(1, 2, 0);
		result.set(0, 3, string_id_assigner.AssignID("CharIndex" + charIndex));
		result.set(1, 3, 1);
		return result;
	}
}
