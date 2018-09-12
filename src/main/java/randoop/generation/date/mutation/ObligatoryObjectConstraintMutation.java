package randoop.generation.date.mutation;

import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.sequence.PseudoSequenceContainer;

public class ObligatoryObjectConstraintMutation extends ObjectConstraintMutation {
	
	public ObligatoryObjectConstraintMutation(InfluenceOfBranchChange influence, PseudoSequenceContainer container) {
		super(influence, container);
	}
	
	@Override
	protected PseudoSequenceContainer GeneratedMutatedNewContainer() {
		PseudoSequenceContainer new_container = container.MutateByApplyingOneRandomUnsolvedConstraint();
		return new_container;
	}
	
}
