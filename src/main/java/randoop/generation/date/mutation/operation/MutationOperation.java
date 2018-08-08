package randoop.generation.date.mutation.operation;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.sequence.LinkedSequence;

public abstract class MutationOperation {

  public LinkedSequence sequence = null;

  public MutationOperation(LinkedSequence sequence) {
    this.sequence = sequence;
  }

  public abstract LinkedSequence ApplyMutation();

  public abstract String toString();
  
  public abstract DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner);
  
}
