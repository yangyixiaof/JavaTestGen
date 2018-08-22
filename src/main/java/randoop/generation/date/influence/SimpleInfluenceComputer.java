package randoop.generation.date.influence;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.yyx.labtask.runtime.memory.state.BranchNodesState;
import cn.yyx.labtask.test_agent_trace_reader.TraceInfo;
import cn.yyx.labtask.test_agent_trace_reader.ValuesOfBranch;

public class SimpleInfluenceComputer {
	
	public static Map<String, Influence> BuildGuidedModel(BranchNodesState branch_state, TraceInfo previous_trace_info, TraceInfo current_trace_info) {
		Map<String, LinkedList<ValuesOfBranch>> previous_branch_signature = previous_trace_info.GetValuesOfBranches();
		Map<String, LinkedList<ValuesOfBranch>> current_branch_signature = current_trace_info.GetValuesOfBranches();
		Map<String, Influence> influence = new TreeMap<String, Influence>();
		Set<String> pset = previous_branch_signature.keySet();
		Set<String> cset = current_branch_signature.keySet();
		Set<String> sig_set = new HashSet<String>();
		sig_set.addAll(pset);
		sig_set.addAll(cset);
		for (String sig : sig_set) {
			if (branch_state.BranchHasBeenIteratedOver(sig)) {
				continue;
			}
			double sig_influence = 0.0;
			LinkedList<ValuesOfBranch> previous_vobs = previous_branch_signature.get(sig);
			LinkedList<ValuesOfBranch> current_vobs = current_branch_signature.get(sig);
			Integer state = branch_state.GetBranchState(sig);
			Integer previous_state = previous_trace_info.GetBranchState(sig);
			if (previous_state != null) {
				if (state == null) {
					state = previous_state;
				} else {
					state &= previous_state;
				}
			}
			Integer current_state = current_trace_info.GetBranchState(sig);
			if (current_state != null) {
				if (state == null) {
					state = current_state;
				} else {
					state &= current_state;
				}
			}
			if (previous_vobs == null) {
				if (current_vobs != null) {
					sig_influence = 0.5;
				} else {
					new Exception("Impossible! previous is null and current is also null?").printStackTrace();
					System.exit(1);
				}
			} else {
				if (current_vobs.size() == 0) {
					sig_influence = -0.5;
				} else {
					double prev_gap_avg = AverageGapOfBranch(previous_vobs);
					double curr_gap_avg = AverageGapOfBranch(current_vobs);
					sig_influence = prev_gap_avg - curr_gap_avg;
					if (sig_influence > 0) {
						sig_influence += 0.5;
					}
					if (sig_influence < 0) {
						sig_influence -= 0.5;
					}
				}
			}
			branch_state.PutBranchState(sig, state, sig_influence != 0, sig_influence > 0);
			influence.put(sig, new Influence(sig_influence));
		}
		return influence;
	}
	
	public static double AverageGapOfBranch(LinkedList<ValuesOfBranch> vobs) {
		double all_gaps = 0.0;
		Iterator<ValuesOfBranch> vob_itr = vobs.iterator();
		while (vob_itr.hasNext()) {
			ValuesOfBranch vob = vob_itr.next();
			double gap = Math.abs(vob.GetBranchValue1() - vob.GetBranchValue2());
			all_gaps += gap;
		}
		all_gaps /= (vobs.size() * 1.0);
		return all_gaps;
	}
	
	public static BranchValueState CreateBranchValueState(TraceInfo trace_info) {
		BranchValueState bvs = new BranchValueState();
		Map<String, LinkedList<ValuesOfBranch>> vob_map = trace_info.GetValuesOfBranches();
		Set<String> vob_keys = vob_map.keySet();
		Iterator<String> vob_keys_itr = vob_keys.iterator();
		while (vob_keys_itr.hasNext()) {
			String branch = vob_keys_itr.next();
			LinkedList<ValuesOfBranch> vobs = vob_map.get(branch);
			Iterator<ValuesOfBranch> vob_itr = vobs.iterator();
			double minimum_gap = Double.MAX_VALUE;
			while (vob_itr.hasNext()) {
				ValuesOfBranch vob = vob_itr.next();
				double gap = Math.abs(vob.GetBranchValue1() - vob.GetBranchValue2());
				if (minimum_gap > gap) {
					minimum_gap = gap;
				}
			}
			bvs.AddBranchValueState(branch, minimum_gap);
		}
		return bvs;
	}
	
}
