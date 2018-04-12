package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;

public class StringRemoveModify extends MutationOperation {

  int stmtIndex = -1;
  int varIndex = -1;
  int charIndex = -1;

  public StringRemoveModify(
      TraceableSequence sequence, int stmtIndex, int varIndex, int charIndex) {
    super(sequence);
    this.stmtIndex = stmtIndex;
    this.varIndex = varIndex;
    this.charIndex = charIndex;
  }

  @Override
  public TraceableSequence ApplyMutation() {
    return sequence.modifyStringRemove(stmtIndex, varIndex, charIndex);
  }

  @Override
  public String toString() {
    return "StringRemoveModify at:"
        + stmtIndex
        + "#varIndex:"
        + varIndex
        + "#charIndex:"
        + charIndex;
  }
}
