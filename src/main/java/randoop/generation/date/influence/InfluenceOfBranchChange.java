package randoop.generation.date.influence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

public class InfluenceOfBranchChange implements Rewardable {

	Map<String, Double> all_count = new HashMap<String, Double>();
	Map<String, Double> positive_value_change_count = new HashMap<String, Double>();
	Map<String, Double> negative_value_change_count = new HashMap<String, Double>();
	Map<String, Double> reach_branch_count = new HashMap<String, Double>();
	Map<String, Double> lose_branch_count = new HashMap<String, Double>();

	public void AddInfluenceOfBranches(Map<String, Influence> branch_influence) {
		AddInfluenceOfBranchesWithDiscount(branch_influence, 1.0);
	}
	
	public void AddInfluenceOfBranchesWithDiscount(Map<String, Influence> branch_influence, double discount) {
		Set<String> bi_keys = branch_influence.keySet();
		Iterator<String> bi_itr = bi_keys.iterator();
		while (bi_itr.hasNext()) {
			String branch = bi_itr.next();
			Influence influence_object = branch_influence.get(branch);
			double influence = influence_object.GetInfluence();
			Double pi = influence > 0.5 ? 1.0 : 0.0;
			Double ni = influence < -0.5 ? 1.0 : 0.0;
			Double rb = influence == 0.5 ? 1.0 : 0.0;
			Double lb = influence == -0.5 ? 1.0 : 0.0;
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
			ac += 1.0 * discount;
			pvcc += pi * discount;
			nvcc += ni * discount;
			rbc += rb * discount;
			lbc += lb * discount;
			all_count.put(branch, ac);
			positive_value_change_count.put(branch, pvcc);
			negative_value_change_count.put(branch, nvcc);
			reach_branch_count.put(branch, rbc);
			lose_branch_count.put(branch, lbc);
		}
	}

	@Override
	public Reward GetReward(ArrayList<String> interested_branch) {
		double weights_max = 1.0;
		double weights_min = 0.2;
		double weight_gap = (weights_max - weights_min) / (interested_branch.size() * 1.0);
		double all_reward = 0.0;
		double weight = weights_max;
		Iterator<String> a_itr = interested_branch.iterator();
		while (a_itr.hasNext()) {
			String branch = a_itr.next();
			Double ac = all_count.get(branch);
			Double pvcc = positive_value_change_count.get(branch);
//			Double nvcc = negative_value_change_count.get(branch);
//			Double rbc = reach_branch_count.get(branch);
//			Double lbc = lose_branch_count.get(branch);
			if (ac != null) {
				Assert.isTrue(pvcc <= ac && ac > 0);
//				double value_change = ((pvcc + nvcc + rbc + lbc)) / (ac);
//				double positive_value_change = (pvcc + rbc) / (pvcc + nvcc + rbc + lbc);
//				all_reward += weight * (0.6 * value_change + 0.4 * positive_value_change);
//				all_reward += (1.0 / (1.0 + ac - pvcc) * weight);
				all_reward += ((pvcc - ac) / ac * weight);
			}
			weight -= weight_gap;
		}
		return new Reward(all_reward);
	}

	public InfluenceOfBranchChange CopySelfInDeepCloneWay() {
		InfluenceOfBranchChange res = new InfluenceOfBranchChange();
		res.all_count.putAll(all_count);
		res.positive_value_change_count.putAll(positive_value_change_count);
		res.negative_value_change_count.putAll(negative_value_change_count);
		res.reach_branch_count.putAll(reach_branch_count);
		res.lose_branch_count.putAll(lose_branch_count);
		return res;
	}

}
