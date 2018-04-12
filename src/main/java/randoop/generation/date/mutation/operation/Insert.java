package randoop.generation.date.mutation.operation;

import java.util.LinkedList;
import java.util.List;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.sequence.Variable;

public class Insert extends MutationOperation {

  int index = -1;
  TypedOperation operation = null;
  List<Variable> inputVariables = new LinkedList<Variable>();

  public Insert(
      TraceableSequence sequence,
      int index,
      TypedOperation operation,
      List<Variable> inputVariables) {
    super(sequence);
    this.index = index;
    this.operation = operation;
    this.inputVariables.addAll(inputVariables);
  }

  public TraceableSequence ApplyMutation() {
    return sequence.insert(index, operation, inputVariables);
  }

  @Override
  public String toString() {
    return "Insert at:" + index + "#operation:" + operation + "#inputs:" + inputVariables;
  }
}
