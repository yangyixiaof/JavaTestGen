package randoop.generation.date.mutation.operation;

import java.util.Map;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;

public abstract class MutationOperation {

  TraceableSequence sequence = null;

  public MutationOperation(TraceableSequence sequence) {
    this.sequence = sequence;
  }

  public abstract TraceableSequence ApplyMutation();

  public abstract String toString();
  
  public abstract DenseObjectMatrix2D toComputeTensor(Map<TypedOperation, Integer> operation_id_map, Map<String, Integer> other_value_id_map);
  
}
