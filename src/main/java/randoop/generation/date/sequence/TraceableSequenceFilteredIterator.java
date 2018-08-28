package randoop.generation.date.sequence;

import java.lang.reflect.Method;

import randoop.operation.CallableOperation;
import randoop.operation.MethodCall;
import randoop.sequence.Statement;

public class TraceableSequenceFilteredIterator {

  int i = 0;
  Statement next_statement = null;
  // int relative_index = 0;
  TraceableSequence sequence = null;

  public TraceableSequenceFilteredIterator(TraceableSequence sequence) {
    this.sequence = sequence;
  }

  public boolean HasNext() {
    for (; i < sequence.statements.size(); i++) {
      Statement stmt = sequence.statements.get(i);
      boolean is_self_defined_helper_method = false;
      if (stmt.isMethodCall()) {
        CallableOperation co = stmt.getOperation().getOperation();
        if (co instanceof MethodCall) {
          MethodCall mc = (MethodCall) co;
          Method md = mc.getMethod();
          if (md.getDeclaringClass().getName().startsWith("randoop.generation.date")) {
            is_self_defined_helper_method = true;
          }
        }
      }
      if (stmt.isNonreceivingInitialization() || is_self_defined_helper_method) {
        continue;
      }
      next_statement = stmt;
      break;
    }
    if (next_statement != null) {
      return true;
    }
    //    if (i < sequence.statements.size()) {
    //      return true;
    //    }
    return false;
  }

  public StatementWithIndex Next() {
    Statement stmt = next_statement;
    int index = i;
    next_statement = null;
    i++;
    if (stmt == null) {
      System.err.println("Strange! no more next elements any more! index out of bound!");
      System.exit(1);
    }
    return new StatementWithIndex(index, stmt, sequence);
  }
}
