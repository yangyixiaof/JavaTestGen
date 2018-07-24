package cn.yyx.labtask.test_agent_trace_reader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class TraceInfo {
	
	Map<String, LinkedList<ValuesOfBranch>> vobs = new HashMap<String, LinkedList<ValuesOfBranch>>();
	List<StatementReturn> stmt_rets = new LinkedList<StatementReturn>();
	
	Map<String, Integer> branch_state = new HashMap<String, Integer>();
	
	public TraceInfo() {
	}
	
	public void AddOneReturnOfStatement(StatementReturn sr) {
		stmt_rets.add(sr);
	}
	
	public void AddOneValueOfBranch(String sig_info, ValuesOfBranch vob) {
		LinkedList<ValuesOfBranch> vob_list = vobs.get(sig_info);
		if (vob_list == null) {
			vob_list = new LinkedList<ValuesOfBranch>();
			vobs.put(sig_info, vob_list);
		}
		vob_list.add(vob);
	}
	
	public Map<String, LinkedList<ValuesOfBranch>> GetValuesOfBranches() {
		return vobs;
	}
	
	public void IdentifyStatesOfBranches() {
		Set<String> vkeys = vobs.keySet();
		Iterator<String> vitr = vkeys.iterator();
		while (vitr.hasNext()) {
			String vk = vitr.next();
			LinkedList<ValuesOfBranch> vk_vobs = vobs.get(vk);
			Integer state = InfluenceComputer.IdentifyBranchState(vk_vobs);
			branch_state.put(vk, state);
		}
	}
	
	public Integer GetBranchState(String sig) {
		return branch_state.get(sig);
	}
	
	public double Fitness(Map<String, Double> branch_priority, Map<String, Integer> branch_current_interest_state) {
		Map<String, Integer> branch_priority_regular = new TreeMap<String, Integer>();
		Set<String> bp_ks = branch_priority.keySet();
		int branch_index = 0;
		int branch_size = branch_priority.size();
		Iterator<String> bp_itr = bp_ks.iterator();
		while (bp_itr.hasNext()) {
			int b_p = branch_size - branch_index;
			String bk = bp_itr.next();
			branch_priority_regular.put(bk, b_p);
			branch_index++;
		}
		double fitness = 0.0;
		Set<Entry<String, Integer>> this_interest_branches = new HashSet<>(branch_state.entrySet());
		Set<Entry<String, Integer>> interest_branches = branch_current_interest_state.entrySet();
		this_interest_branches.retainAll(interest_branches);
		Iterator<Entry<String, Integer>> tib_itr = this_interest_branches.iterator();
		while (tib_itr.hasNext()) {
			Entry<String, Integer> sig_state = tib_itr.next();
			String sig = sig_state.getKey();
			double sig_fitness = ((double)(branch_priority_regular.get(sig)))/((double)(branch_size*1.5)) + branch_priority.get(sig);
			fitness += sig_fitness;
		}
		return fitness;
	}
	
}
