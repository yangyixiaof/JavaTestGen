package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;

public class PrimitiveIntegralModify extends MutationOperation {

  int stmtIndex = -1;
  int varIndex = -1;
  Object deltaValue = null;

  public PrimitiveIntegralModify(
      TraceableSequence sequence, int stmtIndex, int varIndex, Object deltaValue) {
    super(sequence);
    this.stmtIndex = stmtIndex;
    this.varIndex = varIndex;
    this.deltaValue = deltaValue;
  }

  @Override
  public TraceableSequence ApplyMutation() {
    return sequence.modifyIntegral(stmtIndex, varIndex, deltaValue);
  }

  @Override
  public String toString() {
    return "PrimitiveIntegralModify at:"
        + stmtIndex
        + "#input_index:"
        + varIndex
        + "#deltaValue:"
        + deltaValue;
  }
}
