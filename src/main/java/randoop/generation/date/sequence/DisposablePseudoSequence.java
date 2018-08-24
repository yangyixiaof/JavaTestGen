package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import randoop.operation.TypedOperation;

public class DisposablePseudoSequence extends PseudoSequence {

	public DisposablePseudoSequence(ArrayList<TypedOperation> operations) {
		super(operations);
	}
	
	public DisposablePseudoSequence(PseudoVariable pv, ArrayList<TypedOperation> operations) {
		super(pv, operations);
	}
	
	@Override
	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		new Exception("This mutate should not be invoked!").printStackTrace();
		System.exit(1);
		return null;
	}
	
	@Override
	public PseudoVariable Append(TypedOperation operation, ArrayList<PseudoVariable> inputVariables,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		Assert.isTrue(inputVariables.size() == 0);
		Assert.isTrue(statements.size() == 0);
		statements.add(new PseudoStatement(operation, inputVariables));
		return new PseudoVariable(this, 0);
	}

}
