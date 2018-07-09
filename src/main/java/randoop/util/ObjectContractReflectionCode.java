package randoop.util;

import java.util.Arrays;
import randoop.contract.ObjectContract;
import randoop.generation.date.execution.TracePrintController;

public final class ObjectContractReflectionCode extends ReflectionCode {

  final ObjectContract c;
  final Object[] objs;

  public ObjectContractReflectionCode(final ObjectContract c, final Object... objs) {
    this.c = c;
    this.objs = objs;
  }

  @Override
  protected void runReflectionCodeRaw() {
    try {
      TracePrintController.OpenPrintFlag();
      retval = c.evaluate(objs);
      TracePrintController.ClosePrintFlag();
    } catch (Throwable e) {
      exceptionThrown = e;
    }
  }

  @Override
  public String toString() {
    return "Check of ObjectContract " + c + " args: " + Arrays.toString(objs) + status();
  }
}
