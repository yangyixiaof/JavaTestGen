package randoop.generation.date.random.filter;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.sequence.PseudoSequenceContainer;
import randoop.operation.TypedOperation;

public class PseudoSequenceContainerSelectFilter implements SelectFileter<Object> {

	public PseudoSequenceContainerSelectFilter(TypedOperation to) {
		
	}
	
	@Override
	public boolean Retain(Object t) {
		Assert.isTrue(t instanceof PseudoSequenceContainer);
		PseudoSequenceContainer psc = (PseudoSequenceContainer)t;
		System.out.println("running on psc:" + psc);
		System.exit(1);
		return false;
	}

}
