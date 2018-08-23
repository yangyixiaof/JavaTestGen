package randoop.generation.date.influence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		double total_reward = 0.0;
		double max_w = 5.0;
		double min_w = 1.0;
		double gap_w = (max_w-min_w)/((interested_branch.size()-1)*1.0);
		double w = max_w;
		Iterator<String> ib_itr = interested_branch.iterator();
		while (ib_itr.hasNext()) {
			String ib = ib_itr.next();
			Double gap = branch_average_gap.get(ib);
			double r_gap = 0.0;
			if (gap != null) {
				r_gap += 1 / gap;
			}
			r_gap *= w;
			w -= gap_w;
			total_reward += r_gap;
		}
		if (total_reward < 0) {
			total_reward = Double.MAX_VALUE;
		}
		return total_reward;
	}
	
}
