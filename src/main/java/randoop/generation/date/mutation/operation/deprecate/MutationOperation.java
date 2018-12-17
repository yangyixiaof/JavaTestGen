package randoop.generation.date.mutation.operation.deprecate;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.sequence.TraceableSequence;

public abstract class MutationOperation {

  public TraceableSequence sequence = null;

  public MutationOperation(TraceableSequence sequence) {
    this.sequence = sequence;
  }

  public abstract TraceableSequence ApplyMutation();

  public abstract String toString();
  
  public abstract DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner);
  
}
