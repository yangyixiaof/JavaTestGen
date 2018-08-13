package randoop.generation.date.sequence;

import randoop.sequence.Statement;

public class StatementWithIndex {

  int index = 0;
  Statement stmt = null;
  TraceableSequence sequence = null;

  public StatementWithIndex(int index, Statement stmt, TraceableSequence sequence) {
    this.index = index;
    this.stmt = stmt;
    this.sequence = sequence;
  }

  public int GetIndex() {
    return index;
  }

  public Statement GetStatement() {
    return stmt;
  }

  public TraceableSequence GetTraceableSequence() {
    return sequence;
  }
}
