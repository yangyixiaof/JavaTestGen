package randoop.generation.date.influence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BranchValueState implements Rewardable {
	
	Map<String, Double> branch_average_gap = new HashMap<String, Double>();
	
	public BranchValueState() {
	}
	
	public void AddBranchValueState(String branch, Double state) {
		branch_average_gap.put(branch, state);
	}

	@Override
	public double GetReward(ArrayList<String> interested_branch) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
