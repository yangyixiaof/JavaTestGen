package cn.yyx.labtask.runtime.memory.state;

import java.util.Map;
import java.util.TreeMap;

/**
 * A data repository class, storing the coverage states of all visited
 * (byte code) branch nodes.
 */
public class BranchNodesState {

	private Map<String, Integer> has_been_over_branch_state = new TreeMap<String, Integer>();
	private Map<String, Integer> branch_state = new TreeMap<String, Integer>();

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

	public void PutBranchState(String sig, Integer state) {
		if (state == 0) {
			has_been_over_branch_state.put(sig, state);
			branch_state.remove(sig);
		} else {
			branch_state.put(sig, state);
		}
	}

	public boolean BranchHasBeenIteratedOver(String sig) {
//		Integer state = branch_state.get(sig);
//		return state != null && state == 0;
		return has_been_over_branch_state.containsKey(sig);
	}
	
	public Map<String, Integer> GetUnCoveredBranches() {
		return branch_state;
	}
	
}
