package randoop.generation.date.mutation.operation;

import randoop.generation.date.sequence.TraceableSequence;

public abstract class MutationOperation {

  TraceableSequence sequence = null;

  public MutationOperation(TraceableSequence sequence) {
    this.sequence = sequence;
  }

  public abstract TraceableSequence ApplyMutation();

  public abstract String toString();
  
  public abstract int[][] toMutationComputeTensor();
  
}
