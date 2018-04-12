package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;

public class StringAlterModify extends MutationOperation {

  int stmtIndex = -1;
  int varIndex = -1;
  int charIndex = -1;
  int deltaValue = -1;

  public StringAlterModify(
      TraceableSequence sequence, int stmtIndex, int varIndex, int charIndex, int deltaValue) {
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
    return "StringAlterModify at:"
        + stmtIndex
        + "#varIndex:"
        + varIndex
        + "#charIndex:"
        + charIndex
        + "#deltaValue:"
        + deltaValue;
  }
}
