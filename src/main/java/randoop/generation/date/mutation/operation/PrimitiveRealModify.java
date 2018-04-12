package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;

public class PrimitiveRealModify extends MutationOperation {

  int stmtIndex = -1;
  int varIndex = -1;
  Object deltaValue = null;

  public PrimitiveRealModify(
		  TraceableSequence sequence, int stmtIndex, int varIndex, Object deltaValue) {
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
    return "PrimitiveRealModify at:"
        + stmtIndex
        + "#input_index:"
        + varIndex
        + "#deltaValue:"
        + deltaValue;
  }
}
