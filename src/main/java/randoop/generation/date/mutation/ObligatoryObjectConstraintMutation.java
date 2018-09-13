package randoop.generation.date.mutation;

import java.util.ArrayList;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.sequence.PseudoSequenceContainer;

public class ObligatoryObjectConstraintMutation extends ObjectConstraintMutation {

	public ObligatoryObjectConstraintMutation(InfluenceOfBranchChange influence, PseudoSequenceContainer container) {
		super(influence, container);
	}

	@Override
	protected PseudoSequenceContainer GeneratedMutatedNewContainer(DateGenerator dg) {
		PseudoSequenceContainer new_container = container.MutateByApplyingObligatoryConstraint(dg);
		return new_container;
	}

	@Override
	public double GetReward(ArrayList<String> interested_branch) {
		return 2 * super.GetReward(interested_branch);
	}

}
