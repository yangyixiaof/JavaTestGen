package randoop.generation.date.influence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import randoop.generation.date.util.SortUtil;

/**
 * A data repository class, storing the coverage states of all visited
 * (byte code) branch nodes.
 */
public class BranchNodesState {

	private Map<String, Integer> has_been_over_branch_state = new TreeMap<String, Integer>();
	private Map<String, Integer> branch_state = new TreeMap<String, Integer>();
	private Map<String, TreeMap<Integer, Integer>> branch_update_times = new TreeMap<String, TreeMap<Integer, Integer>>();
	private Map<String, TreeMap<Integer, Integer>> branch_value_change_times = new TreeMap<String, TreeMap<Integer, Integer>>();
	private Map<String, TreeMap<Integer, Integer>> branch_valid_value_change_times = new TreeMap<String, TreeMap<Integer, Integer>>();

	public BranchNodesState() {
	}

	public Integer GetBranchState(String sig) {
		if (has_been_over_branch_state.containsKey(sig)) {
			try {
				throw new Exception("Serious error! The branch of sig #" + sig + "# has been iterated over!");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return branch_state.get(sig);
	}

	public void PutBranchState(String sig, Integer state, boolean value_change, boolean is_valid_change) {
		if (state == 0) {
			has_been_over_branch_state.put(sig, state);
			branch_state.remove(sig);
			branch_update_times.remove(sig);
			branch_value_change_times.remove(sig);
			branch_valid_value_change_times.remove(sig);
		} else {
			branch_state.put(sig, state);
			IncrementMapIntegerValueInMap(sig, state, branch_update_times);
			if (value_change) {
				IncrementMapIntegerValueInMap(sig, state, branch_value_change_times);
			}
			if (is_valid_change) {
				IncrementMapIntegerValueInMap(sig, state, branch_valid_value_change_times);
			}
		}
	}
	
	private void IncrementMapIntegerValueInMap(String sig, Integer state, Map<String, TreeMap<Integer, Integer>> map_map) {
		TreeMap<Integer, Integer> state_sig_times = map_map.get(sig);
		if (state_sig_times == null) {
			state_sig_times = new TreeMap<Integer, Integer>();
		}
		Integer u_times = state_sig_times.get(state);
		if (u_times == null) {
			u_times = 0;
		}
		u_times++;
		state_sig_times.put(state, u_times);
	}

	public boolean BranchHasBeenIteratedOver(String sig) {
//		Integer state = branch_state.get(sig);
//		return state != null && state == 0;
		return has_been_over_branch_state.containsKey(sig);
	}
	
	public ArrayList<String> GetSortedUnCoveredBranches() {
		Map<String, Double> wait_sort_map = new TreeMap<String, Double>();
		Set<String> bs_keys = branch_state.keySet();
		Iterator<String> bs_itr = bs_keys.iterator();
		while (bs_itr.hasNext()) {
			String sig = bs_itr.next();
			Integer state = branch_state.get(sig);
			Integer u_ts = branch_update_times.get(sig).get(state);
			Integer vc_ts = branch_value_change_times.get(sig).get(state);
			Integer vvc_ts = branch_valid_value_change_times.get(sig).get(state);
			wait_sort_map.put(sig, ((double)(vc_ts+vvc_ts))/((double)(u_ts+vc_ts)));
		}
		return SortUtil.SortMapByValue(wait_sort_map);
	}
	
	public Map<String, Integer> GetUnCoveredBranchesStates() {
		return branch_state;
	}
	
	public String RepresentationOfUnCoveredBranchWithState() {
		StringBuilder builder = new StringBuilder();
		Set<String> bs_keys = branch_state.keySet();
		Iterator<String> bs_itr = bs_keys.iterator();
		while (bs_itr.hasNext()) {
			String bs = bs_itr.next();
			Integer state = branch_state.get(bs);
			builder.append(bs + "@" + state + "#");
		}
		return builder.toString();
	}
	
}
