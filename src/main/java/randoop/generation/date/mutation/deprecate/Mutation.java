package randoop.generation.date.mutation.deprecate;

import java.util.ArrayList;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.influence.Reward;
import randoop.generation.date.influence.Rewardable;
import randoop.generation.date.sequence.BeforeAfterLinkedSequence;

public abstract class Mutation implements Rewardable {

	InfluenceOfBranchChange influence = null;

	public Mutation(InfluenceOfBranchChange influence) {
		this.influence = influence;
	}

	@Override
	public Reward GetReward(ArrayList<String> interested_branch) {
		return influence.GetReward(interested_branch);
	}

	public abstract BeforeAfterLinkedSequence Apply(ArrayList<String> interested_branch, DateGenerator dg);

}
