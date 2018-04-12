package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;
import randoop.sequence.Variable;

public class ReferenceModify extends MutationOperation {

  int stmtIndex = -1;
  int varIndex = -1;
  Variable targetVariable = null;

  public ReferenceModify(
      TraceableSequence sequence, int stmtIndex, int varIndex, Variable targetVariable) {
    super(sequence);
    this.stmtIndex = stmtIndex;
    this.varIndex = varIndex;
    this.targetVariable = targetVariable;
  }

  @Override
  public TraceableSequence ApplyMutation() {
    return sequence.modifyReference(stmtIndex, varIndex, targetVariable);
  }

  @Override
  public String toString() {
    return "ReferenceModify at:"
        + stmtIndex
        + "#input_index:"
        + varIndex
        + "#targetVariable:"
        + targetVariable;
  }
}
