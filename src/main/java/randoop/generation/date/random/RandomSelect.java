package randoop.generation.date.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.random.filter.SelectFileter;
import randoop.generation.date.sequence.PseudoVariable;

public class RandomSelect {
	
	public static Random random = new Random();
	
	public static <T> T RandomKeyFromMapByValue(Map<T, Double> wait_select) {
		ArrayList<T> all_ts = new ArrayList<T>();
		ArrayList<Double> all_rewards = new ArrayList<Double>();
		
		double total_rewards = 0.0;
		Set<T> keys = wait_select.keySet();
		Iterator<T> kitr = keys.iterator();
		while (kitr.hasNext()) {
			T t = kitr.next();
			Double val = wait_select.get(t);
			all_ts.add(t);
			all_rewards.add(val);
			total_rewards += val;
		}
		
		double select_double = random.nextDouble() * total_rewards;
		int size = all_ts.size();
		double accumulated_rewards = 0.0;
		int i=0;
		for (;i<size;i++) {
			Double reward = all_rewards.get(i);
			accumulated_rewards += reward;
			if (select_double < accumulated_rewards) {
				break;
			}
		}
		Assert.isTrue(i<size);
		T selected_to = all_ts.get(i);
		return selected_to;
	}
	
	public static <T> T RandomKeyFromMapByValueOfBranchInfluence(Map<T, InfluenceOfBranchChange> wait_select, ArrayList<String> interested_branch, SelectFileter<T> filter) {
		Map<T, Double> final_wait_select = new HashMap<T, Double>();
		Set<T> keys = wait_select.keySet();
		Iterator<T> kitr = keys.iterator();
		while (kitr.hasNext()) {
			T t = kitr.next();
			if (filter == null || filter.Retain(t)) {
				double reward = 0.5;
				InfluenceOfBranchChange to_branch_influence = wait_select.get(t);
				if (to_branch_influence != null) {
					reward += to_branch_influence.GetReward(interested_branch);
				}
				final_wait_select.put(t, reward);
			}
		}
		if (final_wait_select.size() == 0) {
			return null;
		}
		return RandomKeyFromMapByValue(final_wait_select);
	}
	
	public static PseudoVariable RandomPseudoVariableListAccordingToLength(ArrayList<PseudoVariable> pvs) {
		Map<PseudoVariable, Double> wait_select = new HashMap<PseudoVariable, Double>();
		for (PseudoVariable pv : pvs) {
			double reward = pv.sequence.SizeOfCiters() * 2.0 + pv.sequence.Size() * 1.5 + pv.index * 1.0;
			wait_select.put(pv, reward);
		}
		return RandomKeyFromMapByValue(wait_select);
	}
	
}
