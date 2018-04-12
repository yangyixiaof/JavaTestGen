package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;

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
}
