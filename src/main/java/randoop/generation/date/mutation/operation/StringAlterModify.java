package randoop.generation.date.mutation.operation;

import java.util.Map;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;

public class StringAlterModify extends MutationOperation {

	int stmtIndex = -1;
	int varIndex = -1;
	int charIndex = -1;
	int deltaValue = -1;

	public StringAlterModify(TraceableSequence sequence, int stmtIndex, int varIndex, int charIndex, int deltaValue) {
		super(sequence);
		this.stmtIndex = stmtIndex;
		this.varIndex = varIndex;
		this.charIndex = charIndex;
		this.deltaValue = deltaValue;
	}

	@Override
	public TraceableSequence ApplyMutation() {
		return sequence.modifyStringModify(stmtIndex, varIndex, charIndex, deltaValue);
	}

	@Override
	public String toString() {
		return "StringAlterModify at:" + stmtIndex + "#varIndex:" + varIndex + "#charIndex:" + charIndex
				+ "#deltaValue:" + deltaValue;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(Map<TypedOperation, Integer> operation_id_map,
			Map<String, Integer> other_value_id_map) {
		
		return null;
	}
}
