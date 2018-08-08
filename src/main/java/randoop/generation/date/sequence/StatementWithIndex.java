package randoop.generation.date.sequence;

import randoop.sequence.Statement;

public class StatementWithIndex {

  int index = 0;
  Statement stmt = null;
  LinkedSequence sequence = null;

  public StatementWithIndex(int index, Statement stmt, LinkedSequence sequence) {
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

  public LinkedSequence GetTraceableSequence() {
    return sequence;
  }
}
