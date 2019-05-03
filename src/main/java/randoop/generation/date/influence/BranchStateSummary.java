package randoop.generation.date.influence;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A data repository class, storing the coverage states of all visited
 * (byte code) branch nodes.
 */
public class BranchStateSummary {

//	private Map<String, Integer> has_been_over_branch_state = new TreeMap<String, Integer>();
//	private Map<String, Integer> branch_state = new TreeMap<String, Integer>();
//	private Map<String, TreeMap<Integer, Integer>> branch_update_times = new TreeMap<String, TreeMap<Integer, Integer>>();
//	private Map<String, TreeMap<Integer, Integer>> branch_value_change_times = new TreeMap<String, TreeMap<Integer, Integer>>();
//	private Map<String, TreeMap<Integer, Integer>> branch_valid_value_change_times = new TreeMap<String, TreeMap<Integer, Integer>>();
	
//	private TreeSet<Integer> already_covered_branch_states = new TreeSet<Integer>();
	
	// the following key is the representation for a trace
	// the following value is as described in variable name
	
//	Map<Integer, TreeSet<String>> already_covered_position_branches = new TreeMap<Integer, TreeSet<String>>();
	
	Map<String, TreeSet<String>> already_flipped_branch = new TreeMap<String, TreeSet<String>>();
	Map<String, TreeSet<String>> already_hit_branch = new TreeMap<String, TreeSet<String>>();
//	Map<String, TreeMap<Integer, TreeSet<String>>> not_covered_and_with_influence_position_branches_pair = new TreeMap<String, TreeMap<Integer, TreeSet<String>>>();
//	Map<String, TreeMap<String, TreeSet<Integer>>> not_covered_and_with_influence_branch_positions_pair = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	
	public BranchStateSummary() {
	}

//	public Integer GetBranchState(String sig) {
//		if (has_been_over_branch_state.containsKey(sig)) {
//			try {
//				throw new Exception("Serious error! The branch of sig #" + sig + "# has been iterated over!");
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//		}
//		return branch_state.get(sig);
//	}
//
//	public void PutBranchState(String sig, Integer state, boolean value_change, boolean is_valid_change) {
//		if (state == 0) {
//			has_been_over_branch_state.put(sig, state);
//			branch_state.remove(sig);
//			branch_update_times.remove(sig);
//			branch_value_change_times.remove(sig);
//			branch_valid_value_change_times.remove(sig);
//		} else {
//			branch_state.put(sig, state);
//			IncrementMapIntegerValueInMap(sig, state, branch_update_times);
//			if (value_change) {
//				IncrementMapIntegerValueInMap(sig, state, branch_value_change_times);
//			}
//			if (is_valid_change) {
//				IncrementMapIntegerValueInMap(sig, state, branch_valid_value_change_times);
//			}
//		}
//	}
//	
//	private void IncrementMapIntegerValueInMap(String sig, Integer state, Map<String, TreeMap<Integer, Integer>> map_map) {
//		TreeMap<Integer, Integer> state_sig_times = map_map.get(sig);
//		if (state_sig_times == null) {
//			state_sig_times = new TreeMap<Integer, Integer>();
//			map_map.put(sig, state_sig_times);
//		}
//		Integer u_times = state_sig_times.get(state);
//		if (u_times == null) {
//			u_times = 0;
//		}
//		u_times++;
//		state_sig_times.put(state, u_times);
//	}
//
//	public boolean BranchHasBeenIteratedOver(String sig) {
////		Integer state = branch_state.get(sig);
////		return state != null && state == 0;
//		return has_been_over_branch_state.containsKey(sig);
//	}
//	
//	public ArrayList<String> GetSortedUnCoveredBranches() {
//		Map<String, Double> wait_sort_map = new TreeMap<String, Double>();
//		Set<String> bs_keys = branch_state.keySet();
//		Iterator<String> bs_itr = bs_keys.iterator();
//		while (bs_itr.hasNext()) {
//			String sig = bs_itr.next();
//			Integer state = branch_state.get(sig);
//			Integer u_ts = branch_update_times.get(sig).get(state);
//			Integer vc_ts = branch_value_change_times.get(sig).get(state);
//			Integer vvc_ts = branch_valid_value_change_times.get(sig).get(state);
//			wait_sort_map.put(sig, ((double)(vc_ts+vvc_ts))/((double)(u_ts+vc_ts)));
//		}
//		return SortUtil.SortMapByValue(wait_sort_map);
//	}
//	
//	public Map<String, Integer> GetUnCoveredBranchesStates() {
//		return branch_state;
//	}
//	
//	public String RepresentationOfUnCoveredBranchWithState() {
//		StringBuilder builder = new StringBuilder();
//		Set<String> bs_keys = branch_state.keySet();
//		Iterator<String> bs_itr = bs_keys.iterator();
//		while (bs_itr.hasNext()) {
//			String bs = bs_itr.next();
//			Integer state = branch_state.get(bs);
//			builder.append(bs + "@" + state + "#");
//		}
//		return builder.toString();
//	}
	
//	public void RecordAlreadyCoverredBranches(Integer position_to_mutate, String branch_with_state) {
//		TreeSet<String> branch_with_states = already_covered_position_branches.get(position_to_mutate);
//		if (branch_with_states == null) {
//			branch_with_states = new TreeSet<String>();
//			already_covered_position_branches.put(position_to_mutate, branch_with_states);
//		}
//		branch_with_states.add(branch_with_state);
//	}
	
	private void BranchStateChange(Map<String, TreeSet<String>> branch_state, String whole_state, String one_branch) {
		TreeSet<String> whole_state_covered = branch_state.get(whole_state);
		if (whole_state_covered == null) {
			whole_state_covered = new TreeSet<String>();
			branch_state.put(whole_state, whole_state_covered);
		}
		whole_state_covered.add(one_branch);
	}
	
	private boolean IsBranchStateChanged(Map<String, TreeSet<String>> branch_state, String whole_state, String one_branch) {
		TreeSet<String> whole_state_covered = branch_state.get(whole_state);
		if (whole_state_covered == null) {
			return false;
		} else {
			return whole_state_covered.contains(one_branch);
		}
	}
	
	public void FlipBranch(String whole_state, String one_branch) {
		BranchStateChange(already_flipped_branch, whole_state, one_branch);
	}
	
	public boolean IsBranchFlipped(String whole_state, String one_branch) {
		return IsBranchStateChanged(already_flipped_branch, whole_state, one_branch);
	}
	
	public void HitBranch(String whole_state, String one_branch) {
		BranchStateChange(already_hit_branch, whole_state, one_branch);
	}
	
	public boolean IsBranchHit(String whole_state, String one_branch) {
		return IsBranchStateChanged(already_hit_branch, whole_state, one_branch);
	}
	
//	public Map<String, TreeSet<String>> GetAlreadyCoveredBranch() {
//		return already_covered_branch;
//	}
//	
//	public Map<String, TreeMap<Integer, TreeSet<String>>> GetNotCoveredAndWithInfluencePositionBranchesPair() {
//		return not_covered_and_with_influence_position_branches_pair;
//	}
//	
//	public Map<Integer, TreeSet<String>> GetNotCoveredAndWithInfluencePositionBranchesPairForTrace(TraceInfo trace_info) {
//		return not_covered_and_with_influence_position_branches_pair.get(trace_info.GetTraceSignature());
//	}
//	
//	public Map<String, TreeMap<String, TreeSet<Integer>>> GetNotCoveredAndWithInfluenceBranchPositionsPair() {
//		return not_covered_and_with_influence_branch_positions_pair;
//	}
//	
//	public TreeMap<String, TreeSet<Integer>> GetNotCoveredAndWithInfluenceBranchPositionsPairForTrace(TraceInfo trace_info) {
//		return not_covered_and_with_influence_branch_positions_pair.get(trace_info.GetTraceSignature());
//	}
	
}
