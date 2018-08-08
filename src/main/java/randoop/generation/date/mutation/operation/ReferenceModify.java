package randoop.generation.date.mutation.operation;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.sequence.Variable;

public class ReferenceModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;
	Variable targetVariable = null;

	public ReferenceModify(LinkedSequence sequence, int stmtIndex, int varIndex, Variable targetVariable) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
		this.targetVariable = targetVariable;
	}

	@Override
	public LinkedSequence ApplyMutation() {
		return sequence.modifyReference(this, stmtIndex, varIndex, targetVariable);
	}

	@Override
	public String toString() {
		return "ReferenceModify at:" + stmtIndex + "#input_index:" + varIndex + "#targetVariable:" + targetVariable;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		return null;
	}
}
