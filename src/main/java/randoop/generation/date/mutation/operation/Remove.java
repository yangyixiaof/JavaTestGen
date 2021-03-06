package randoop.generation.date.mutation.operation;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.sequence.TraceableSequence;

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
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		return null;
	}
}
