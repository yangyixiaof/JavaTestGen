package randoop.generation.date.influence;

import randoop.generation.date.DateGenerator;

public class RewardableInteger implements Rewardable {

	int i = 0;
	
	public RewardableInteger(int i) {
		this.i = i;
	}
	
	@Override
	public Reward GetReward(DateGenerator dg) {
		return new Reward(new double[] {i});
	}
	
}
