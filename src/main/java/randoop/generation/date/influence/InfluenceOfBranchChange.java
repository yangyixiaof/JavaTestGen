package randoop.generation.date.influence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class InfluenceOfBranchChange {

	Map<String, Double> all_count = new HashMap<String, Double>();
	Map<String, Double> positive_value_change_count = new HashMap<String, Double>();
	Map<String, Double> negative_value_change_count = new HashMap<String, Double>();
	Map<String, Double> reach_branch_count = new HashMap<String, Double>();
	Map<String, Double> lose_branch_count = new HashMap<String, Double>();

	public void AddInfluenceOfBranches(Map<String, Double> branch_influence) {
		Set<String> bi_keys = branch_influence.keySet();
		Iterator<String> bi_itr = bi_keys.iterator();
		while (bi_itr.hasNext()) {
			String branch = bi_itr.next();
			Double influence = branch_influence.get(branch);
			Double pi = (influence > 0.0 && influence < 10.0) ? 1.0 : 0.0;
			Double ni = (influence > -10.0 && influence < 0.0) ? 1.0 : 0.0;
			Double rb = (influence >= 10.0) ? 1.0 : 0.0;
			Double lb = (influence <= -10.0) ? 1.0 : 0.0;
			Double ac = all_count.get(branch);
			Double pvcc = positive_value_change_count.get(branch);
			Double nvcc = negative_value_change_count.get(branch);
			Double rbc = reach_branch_count.get(branch);
			Double lbc = lose_branch_count.get(branch);
			if (ac == null) {
				ac = 0.0;
				pvcc = 0.0;
				nvcc = 0.0;
				rbc = 0.0;
				lbc = 0.0;
			}
			ac++;
			pvcc += pi;
			nvcc += ni;
			rbc += rb;
			lbc += lb;
			all_count.put(branch, ac);
			positive_value_change_count.put(branch, pvcc);
			negative_value_change_count.put(branch, nvcc);
			reach_branch_count.put(branch, rbc);
			lose_branch_count.put(branch, lbc);
		}
	}

	public double GetReward(ArrayList<String> interested_branch) {
		double weights_max = 1.0;
		double weights_min = 0.4;
		double weight_gap = (weights_max - weights_min) / (interested_branch.size() * 1.0);
		double all_reward = 0.0;
		double weight = weights_max;
		Iterator<String> a_itr = interested_branch.iterator();
		while (a_itr.hasNext()) {
			String branch = a_itr.next();
			Double ac = all_count.get(branch);
			Double pvcc = positive_value_change_count.get(branch);
			Double nvcc = negative_value_change_count.get(branch);
			Double rbc = reach_branch_count.get(branch);
			Double lbc = lose_branch_count.get(branch);
			if (ac != null) {
				double value_change = ((pvcc + nvcc + rbc + lbc)) / (ac);
				double positive_value_change = (pvcc + rbc) / (pvcc + nvcc + rbc + lbc);
				all_reward += weight * (0.6 * value_change + 0.4 * positive_value_change);
			}
			weight -= weight_gap;
		}
		return all_reward;
	}

}
