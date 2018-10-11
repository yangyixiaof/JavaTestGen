package randoop.generation.date.influence;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SimpleInfluenceComputer {

	public static Map<String, Influence> BuildGuidedModel(BranchNodesState branch_state, TraceInfo previous_trace_info,
			TraceInfo current_trace_info) {
		Map<String, LinkedList<ValuesOfBranch>> previous_branch_signature = previous_trace_info == null ? null : previous_trace_info.GetValuesOfBranches();
		Map<String, LinkedList<ValuesOfBranch>> current_branch_signature = current_trace_info.GetValuesOfBranches();
		Map<String, Influence> influence = new TreeMap<String, Influence>();
		Set<String> sig_set = new HashSet<String>();
		Set<String> pset = previous_branch_signature == null ? null : previous_branch_signature.keySet();
		if (pset != null) {
			sig_set.addAll(pset);
		}
		Set<String> cset = current_branch_signature.keySet();
		sig_set.addAll(cset);
		for (String sig : sig_set) {
			if (branch_state.BranchHasBeenIteratedOver(sig)) {
				continue;
			}
			double sig_influence = 0.0;
			LinkedList<ValuesOfBranch> previous_vobs = previous_branch_signature == null ? null : previous_branch_signature.get(sig);
			LinkedList<ValuesOfBranch> current_vobs = current_branch_signature.get(sig);
			Integer state = branch_state.GetBranchState(sig);
			Integer previous_state = previous_trace_info == null ? null : previous_trace_info.GetBranchState(sig);
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
				if (current_vobs == null || current_vobs.size() == 0) {
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

//	public static double ComputeAveragedInfluence(ArrayList<String> interested_branch,
//			Map<String, Influence> branch_influence) {
//		double average_influence = 0.0;
//		double max_w = 5.0;
//		double min_w = 0.0;
//		double gap_w = (max_w - min_w) / ((interested_branch.size() + 1) * 1.0);
//		double w = max_w;
//		Iterator<String> ib_itr = interested_branch.iterator();
//		while (ib_itr.hasNext()) {
//			String ib = ib_itr.next();
//			double influence = branch_influence.get(ib).GetInfluence();
//			average_influence += w * influence;
//			w -= gap_w;
//		}
//		return average_influence;
//	}
	
	public static Integer IdentifyBranchState(LinkedList<ValuesOfBranch> vobs) {
		Integer state = null;
		Iterator<ValuesOfBranch> vob_itr = vobs.iterator();
		while (vob_itr.hasNext()) {
			ValuesOfBranch vob = vob_itr.next();
			state = IndentifyBranchState(vob, state);
		}
		return state;
	}
	
	private static Integer IndentifyBranchState(ValuesOfBranch vob, Integer state) {
		double v1 = vob.GetBranchValue1();
		double v2 = vob.GetBranchValue2();
		switch (vob.GetCmpOptr()) {
		// ``compare then store'' series
		case "D$CMPG":
		case "D$CMPL":
		case "F$CMPG":
		case "F$CMPL":
		case "L$CMP": {
			if (state == null) {
				state = 0b111;
			}
			if (v1 == v2) {
				state &= 0b101;
			} else {
				if (v1 > v2) {
					state &= 0b110;
				} else {
					state &= 0b011;
				}
			}
		} // // ``compare then store'' series BLOCK
			break;
		// eq neq series... *8
		case "I$==":
		case "I$!=":
		case "A$==":
		case "A$!=":
		case "IZ$==":
		case "IZ$!=":
		case "N$!=":
		case "N$==": {
			if (state == null) {
				state = 0b11;
			}
			if (v1 == v2) {
				state &= 0b01;
			} else {
				state &= 0b10;
			}
		}
			break;
		// ge, ge 0 *2
		case "I$>=":
		case "IZ$>=": {
			if (state == null) {
				state = 0b11;
			}
			if (v1 >= v2) {
				state &= 0b10;
			} else {
				state &= 0b01;
			}
		}
			break;
		// le, le 0
		case "I$<=":
		case "IZ$<=": {
			{
				if (state == null) {
					state = 0b11;
				}
				if (v1 <= v2) {
					state &= 0b01;
				} else {
					state &= 0b10;
				}
			}
		}
			break;
		// gt, gt 0
		case "I$>":
		case "IZ$>": {
			if (state == null) {
				state = 0b11;
			}
			if (v1 > v2) {
				state &= 0b10;
			} else {
				state &= 0b01;
			}
		}
			break;
		// lt, lt 0
		case "I$<":
		case "IZ$<": {
			if (state == null) {
				state = 0b11;
			}
			if (v1 < v2) {
				state &= 0b01;
			} else {
				state &= 0b10;
			}
		}
			break;
		} // switch (current_vob.GetCmpOptr())
		return state;
	}

}
