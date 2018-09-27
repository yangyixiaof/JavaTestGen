package randoop.generation.date.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import randoop.generation.date.influence.Reward;

public class RewardUtil {
	
	public static <T> Map<T, Double> RewardValueToDoubleValueInMap(Map<T, Reward> rewards) {
		Map<T, Double> result = new HashMap<T, Double>();
		Set<T> r_keys = rewards.keySet();
		Iterator<T> r_itr = r_keys.iterator();
		while (r_itr.hasNext()) {
			T t = r_itr.next();
			Reward r = rewards.get(t);
			result.put(t, r.GetReward());
		}
		return result;
	}
	
}
