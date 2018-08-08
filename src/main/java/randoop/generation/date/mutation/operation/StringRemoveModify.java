package randoop.generation.date.mutation.operation;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.sequence.LinkedSequence;

public class StringRemoveModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;
	int charIndex = -1;

	public StringRemoveModify(LinkedSequence sequence, int stmtIndex, int varIndex, int charIndex) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
		this.charIndex = charIndex;
	}

	@Override
	public LinkedSequence ApplyMutation() {
		return sequence.modifyStringRemove(this, stmtIndex, varIndex, charIndex);
	}

	@Override
	public String toString() {
		return "StringRemoveModify at:" + stmtIndex + "#varIndex:" + varIndex + "#charIndex:" + charIndex;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		return null;
	}
}
