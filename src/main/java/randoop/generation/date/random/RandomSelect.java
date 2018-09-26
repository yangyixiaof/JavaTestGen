package randoop.generation.date.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import randoop.generation.date.influence.Penalizable;
import randoop.generation.date.influence.Rewardable;
import randoop.generation.date.random.filter.SelectFileter;
import randoop.generation.date.sequence.PseudoVariable;
import randoop.operation.TypedOperation;

public class RandomSelect {

	public static Random random = new Random();

	private static <T> TotalAlignedResult<T> GenerateTotalAlignedMap(Map<T, Double> wait_select) {
		List<Map.Entry<T, Double>> list = new LinkedList<>(wait_select.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<T, Double>>() {
			@Override
			public int compare(Map.Entry<T, Double> o1, Map.Entry<T, Double> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});

		double max_r = 10.0;
		double min_r = 0.0;
		double gap_r = (max_r - min_r) / ((list.size() + 1) * 1.0);
		double r = max_r;

		Map<T, Double> aligned_map = new HashMap<T, Double>();

		double total_rewards = 0.0;
		double prev_reward = 0.0;
		Iterator<Map.Entry<T, Double>> kitr = list.iterator();
		while (kitr.hasNext()) {
			Map.Entry<T, Double> entry = kitr.next();
			T curr_t = entry.getKey();
			double curr_reward = entry.getValue();
			if (curr_reward != prev_reward) {
				r -= gap_r;
			}
			double r_r = Math.pow(r, 2.5);
			total_rewards += r_r;
			aligned_map.put(curr_t, r_r);
		}
		return new TotalAlignedResult<T>(total_rewards, aligned_map);
	}

	public static <T> T RandomKeyFromAlignedResult(TotalAlignedResult<T> aligned) {
		double total_rewards = aligned.total_rewards;
		Map<T, Double> aligned_map = aligned.aligned_map;
		double select_double = random.nextDouble() * total_rewards;
		Set<T> aks = aligned_map.keySet();
		double accumulated_rewards = 0.0;
		Iterator<T> ak_itr = aks.iterator();
		T t = null;
		while (ak_itr.hasNext()) {
			t = ak_itr.next();
			Double reward = aligned_map.get(t);
			accumulated_rewards += reward;
			if (select_double < accumulated_rewards) {
				break;
			}
		}
		return t;
	}

	public static Object RandomKeyFromSetByRewardableElement(Collection<? extends Rewardable> wait_select,
			ArrayList<String> interested_branch, SelectFileter<?> filter) {
		Map<Object, Double> final_wait_select = new HashMap<Object, Double>();
		Iterator<? extends Rewardable> kitr = wait_select.iterator();
		while (kitr.hasNext()) {
			Rewardable t = kitr.next();
			// if (filter == null || filter.Retain(t))
			{
				double reward = 0.0;
				reward += t.GetReward(interested_branch);
				// Rewardable to_branch_influence = wait_select.get(t);
				// if (to_branch_influence != null) {
				// reward += to_branch_influence.GetReward(interested_branch);
				// }
				final_wait_select.put(t, reward);
				// TODO º«µ√ÃÌº”
				// if (t instanceof PseudoSequenceContainer) {
				// System.out.println("===== reward:" + reward + " =====");
				// System.out.println(t);
				// }
			}
		}
		if (final_wait_select.size() == 0) {
			return null;
		}
		return RandomKeyFromMapByValue(final_wait_select);
	}

	public static <T> T RandomKeyFromMapByValue(Map<T, Double> wait_select) {
		// sort, big first
		return RandomKeyFromAlignedResult(GenerateTotalAlignedMap(wait_select));
	}

	public static <T> T GetBestKeyFromMapByRewardableValue(Map<T, ? extends Rewardable> wait_select,
			ArrayList<String> interested_branch) {
		Map<T, Double> final_wait_select = new HashMap<T, Double>();
		Set<T> w_keys = wait_select.keySet();
		Iterator<T> kitr = w_keys.iterator();
		while (kitr.hasNext()) {
			T k = kitr.next();
			Rewardable t = wait_select.get(k);
			double reward = 0.0;
			reward += t.GetReward(interested_branch);
			final_wait_select.put(k, reward);
		}
		if (final_wait_select.size() == 0) {
			return null;
		}
		return MaxValueKey(final_wait_select);
	}

	public static <T> T MaxValueKey(Map<T, Double> wait_select) {
		List<Map.Entry<T, Double>> list = new LinkedList<>(wait_select.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<T, Double>>() {
			@Override
			public int compare(Map.Entry<T, Double> o1, Map.Entry<T, Double> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
		return list.get(0).getKey();
	}

	public static <T> T RandomKeyFromMapByRewardableValue(Map<T, ? extends Rewardable> wait_select,
			ArrayList<String> interested_branch, SelectFileter<T> filter) {
		Map<T, Double> final_wait_select = new HashMap<T, Double>();
		Set<T> keys = wait_select.keySet();
		Iterator<T> kitr = keys.iterator();
		while (kitr.hasNext()) {
			T t = kitr.next();
			if (filter == null || filter.Retain(t)) {
				double reward = 0.0;
				Rewardable to_branch_influence = wait_select.get(t);
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

	public static <T> T RandomKeyFromMapByRewardableValueWithPenalizableValue(Map<T, ? extends Rewardable> wait_select,
			Map<T, ? extends Penalizable> punish, ArrayList<String> interested_branch, SelectFileter<T> filter,
			TypedOperation to) {
		Map<T, Double> final_wait_select = new HashMap<T, Double>();
		Set<T> keys = wait_select.keySet();
		Iterator<T> kitr = keys.iterator();
		System.out.println("=== start ===");
		while (kitr.hasNext()) {
			T t = kitr.next();
			if (filter == null || filter.Retain(t)) {
				System.out.println("Retained:" + t);
				double reward = 0.0;
				Rewardable to_branch_influence = wait_select.get(t);
				if (to_branch_influence != null) {
					reward += to_branch_influence.GetReward(interested_branch);
				}
				double punish_v = 0.0;
				Penalizable punish_val = punish.get(t);
				if (punish_val != null) {
					punish_v += punish_val.GetPunishment(to);
				}
				if (punish_v >= 0) {
					final_wait_select.put(t, reward);
				}
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
			double reward = pv.sequence.SizeOfUsers() * 2.0 + pv.sequence.Size() * 1.5 + pv.index * 1.0;
			wait_select.put(pv, -reward);
		}
		return RandomKeyFromMapByValue(wait_select);
	}

}

class TotalAlignedResult<T> {

	double total_rewards;
	Map<T, Double> aligned_map;

	public TotalAlignedResult(double total, Map<T, Double> aligned_map) {
		this.total_rewards = total;
		this.aligned_map = aligned_map;
	}

}
