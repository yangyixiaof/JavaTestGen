package randoop.generation.date.mutation.operation;

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
}
