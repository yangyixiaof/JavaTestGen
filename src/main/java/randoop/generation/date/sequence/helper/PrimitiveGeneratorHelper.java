package randoop.generation.date.sequence.helper;

import java.util.ArrayList;

import randoop.generation.date.sequence.DisposablePseudoSequence;
import randoop.generation.date.sequence.PseudoVariable;
import randoop.operation.TypedOperation;
import randoop.types.Type;

public class PrimitiveGeneratorHelper {
	
	public static PseudoVariable CreatePrimitiveVariable(Type t, Object v) {
		TypedOperation new_op = TypedOperation.createPrimitiveInitialization(t, v);// Type.forClass(Double.class), term_value
		DisposablePseudoSequence dps = new DisposablePseudoSequence();
		PseudoVariable new_pv = dps.Append(new_op, new ArrayList<PseudoVariable>());
		return new_pv;
	}
	
}
