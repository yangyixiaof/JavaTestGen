package randoop.generation.date.influence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import randoop.generation.date.mutation.Mutation;
import randoop.generation.date.mutation.StringMutation;

public class SimpleInfluenceComputer {

	public static InfluenceOfTraceCompare BuildGuidedModel(BranchStateSummary branch_state, Mutation mutation,
			TraceInfo previous_trace_info, TraceInfo current_trace_info) {
		if (!(mutation instanceof StringMutation)) {
			return null;
		}

		InfluenceOfTraceCompare influence = new InfluenceOfTraceCompare();
		
		StringMutation string_mutation = (StringMutation) mutation;
		int string_mutation_position = string_mutation.GetPosition();
		
//		String previous_trace_sig = previous_trace_info == null ? "EmptyTraceSig"
//				: previous_trace_info.GetTraceSignature();
//		TreeSet<String> already_covered_for_previous_vobs = branch_state.already_covered_branch.get(previous_trace_sig);
//		if (already_covered_for_previous_vobs == null) {
//			already_covered_for_previous_vobs = new TreeSet<String>();
//			branch_state.already_covered_branch.put(previous_trace_sig, already_covered_for_previous_vobs);
//		}
//		TreeMap<String, TreeSet<Integer>> branch_positions_with_influence = branch_state.not_covered_and_with_influence_branch_positions_pair.get(previous_trace_sig);
//		if (branch_positions_with_influence == null) {
//			branch_positions_with_influence = new TreeMap<String, TreeSet<Integer>>();
//			branch_state.not_covered_and_with_influence_branch_positions_pair.put(previous_trace_sig, branch_positions_with_influence);
//		}
//		TreeMap<Integer, TreeSet<String>> position_branches_with_influence = branch_state.not_covered_and_with_influence_position_branches_pair.get(previous_trace_sig);
//		if (position_branches_with_influence == null) {
//			position_branches_with_influence = new TreeMap<Integer, TreeSet<String>>();
//			branch_state.not_covered_and_with_influence_position_branches_pair.put(previous_trace_sig, position_branches_with_influence);
//		}
		Map<String, InfoOfBranch> previous_branches = previous_trace_info == null ? null
				: previous_trace_info.GetInfoOfBranches();
		Map<String, InfoOfBranch> current_branches = current_trace_info.GetInfoOfBranches();
		Set<String> sig_set = new HashSet<String>();
		Set<String> pset = previous_branches == null ? null : previous_branches.keySet();
		if (pset != null) {
			sig_set.addAll(pset);
		}
		Set<String> cset = current_branches.keySet();
		sig_set.addAll(cset);
		for (String sig : sig_set) {
			// if (branch_state.BranchHasBeenIteratedOver(sig)) {
			// continue;
			// }
			InfoOfBranch previous_branch_info_for_sig = previous_branches == null ? null : previous_branches.get(sig);
			InfoOfBranch current_branch_info_for_sig = current_branches.get(sig);
			// Integer state = branch_state.GetBranchState(sig);
			// Integer previous_state = previous_trace_info == null ? null :
			// previous_trace_info.GetBranchState(sig);
			// if (previous_state != null) {
			// if (state == null) {
			// state = previous_state;
			// } else {
			// state &= previous_state;
			// }
			// }
			// Integer current_state = current_trace_info.GetBranchState(sig);
			// if (current_state != null) {
			// if (state == null) {
			// state = current_state;
			// } else {
			// state &= current_state;
			// }
			// }
			if (previous_branch_info_for_sig == null) {
				if (current_branch_info_for_sig != null) {
					// sig_influence = 0.5;
				} else {
					new Exception("Impossible! previous is null and current is also null?").printStackTrace();
					System.exit(1);
				}
			} else {
				if (current_branch_info_for_sig == null) {
					// sig_influence = -0.5;
				} else {
					ArrayList<ValuesOfBranch> previous_vob_list = previous_branch_info_for_sig.GetAllValuesOfBranch();
					ArrayList<ValuesOfBranch> current_vob_list = current_branch_info_for_sig.GetAllValuesOfBranch();
					int previous_vob_list_size = previous_vob_list.size();
					int current_vob_list_size = current_vob_list.size();
					if (previous_vob_list_size == current_vob_list_size) {
						// thought as exactly matched
						int ite_len = previous_vob_list_size;
						for (int i = 0; i < ite_len; i++) {
							ValuesOfBranch previous_vob = previous_vob_list.get(i);
							ValuesOfBranch current_vob = current_vob_list.get(i);
							String sig_of_this_vob = sig + "#" + i;
							HandleOneLogOfBranchInTrace(sig_of_this_vob, string_mutation_position, previous_vob, current_vob, influence);
							// , already_covered_for_previous_vobs, branch_positions_with_influence, position_branches_with_influence
						}
					} else {
						// thought as not exactly matched
						int ite_len = Math.min(previous_vob_list_size, current_vob_list_size);
						for (int i = 0; i < ite_len; i++) {
							ValuesOfBranch previous_vob = previous_vob_list.get(i);
							ValuesOfBranch current_vob = current_vob_list.get(i);
							if (previous_vob.GetLineIndex() != current_vob.GetLineIndex()) {
								break;
							}
							String sig_of_this_vob = sig + "#" + i;
							HandleOneLogOfBranchInTrace(sig_of_this_vob, string_mutation_position, previous_vob, current_vob, influence);
							// , already_covered_for_previous_vobs, branch_positions_with_influence, position_branches_with_influence
						}
					}
//					double prev_gap_avg = AverageGapOfBranch(previous_branch_info_for_sig);
//					double curr_gap_avg = AverageGapOfBranch(current_branch_info_for_sig);
//					sig_influence = prev_gap_avg - curr_gap_avg;
//					if (sig_influence > 0) {
//						sig_influence += 0.5;
//					}
//					if (sig_influence < 0) {
//						sig_influence -= 0.5;
//					}
				}
			}
			// branch_state.PutBranchState(sig, state, sig_influence != 0, sig_influence >
			// 0);
//			influence.put(sig, new Influence(sig_influence));
		}
		return influence;
	}
	
	private static void HandleOneLogOfBranchInTrace(String sig_of_this_vob, int string_mutation_position, ValuesOfBranch previous_vob, ValuesOfBranch current_vob, InfluenceOfTraceCompare influence) {
		// TreeSet<String> already_covered_for_previous_vobs, TreeMap<String, TreeSet<Integer>> branch_positions_with_influence, TreeMap<Integer, TreeSet<String>> position_branches_with_influence, 
		if (current_vob.GetState() != previous_vob.GetState()) {
			// set up already covered branches
//			already_covered_for_previous_vobs.add(sig_of_this_vob);
			// remove the influence for the already covered
//			TreeSet<Integer> positions = branch_positions_with_influence.remove(sig_of_this_vob);
//			if (positions != null && positions.size() > 0) {
//				Iterator<Integer> pitr = positions.iterator();
//				while (pitr.hasNext()) {
//					Integer pos = pitr.next();
//					position_branches_with_influence.get(pos).remove(sig_of_this_vob);
//				}
//			}
		} else {
//			if (!already_covered_for_previous_vobs.contains(sig_of_this_vob)) {
				double prev_vob_gap = previous_vob.GetGap();
				double curr_vob_gap = current_vob.GetGap();
				double gap_delta = prev_vob_gap - curr_vob_gap;
				double sig_influence = 0.0;
				if (gap_delta != 0) {
					if (gap_delta > 0) {
						sig_influence = 1;
					} else {
						sig_influence = 0.2;
					}
					influence.influences.put(sig_of_this_vob, new Influence(sig_influence, previous_vob.GetState() != current_vob.GetState()));
//					TreeSet<Integer> positions = branch_positions_with_influence.get(sig_of_this_vob);
//					if (positions == null) {
//						positions = new TreeSet<Integer>();
//						branch_positions_with_influence.put(sig_of_this_vob, positions);
//					}
//					positions.add(string_mutation_position);
//					TreeSet<String> branches = position_branches_with_influence.get(string_mutation_position);
//					if (branches == null) {
//						branches = new TreeSet<String>();
//						position_branches_with_influence.put(string_mutation_position, branches);
//					}
//					branches.add(sig_of_this_vob);
				}
//			}
		}
	}

	// public static double AverageGapOfBranch(LinkedList<ValuesOfBranch> vobs) {
	// double all_gaps = 0.0;
	// Iterator<ValuesOfBranch> vob_itr = vobs.iterator();
	// while (vob_itr.hasNext()) {
	// ValuesOfBranch vob = vob_itr.next();
	// double gap = Math.abs(vob.GetBranchValue1() - vob.GetBranchValue2());
	// all_gaps += gap;
	// }
	// all_gaps /= (vobs.size() * 1.0);
	// return all_gaps;
	// }

	// public static BranchValueState CreateBranchValueState(TraceInfo trace_info) {
	// BranchValueState bvs = new BranchValueState();
	// Map<String, LinkedList<ValuesOfBranch>> vob_map =
	// trace_info.GetValuesOfBranches();
	// Set<String> vob_keys = vob_map.keySet();
	// Iterator<String> vob_keys_itr = vob_keys.iterator();
	// while (vob_keys_itr.hasNext()) {
	// String branch = vob_keys_itr.next();
	// LinkedList<ValuesOfBranch> vobs = vob_map.get(branch);
	// Iterator<ValuesOfBranch> vob_itr = vobs.iterator();
	// double minimum_gap = Double.MAX_VALUE;
	// while (vob_itr.hasNext()) {
	// ValuesOfBranch vob = vob_itr.next();
	// double gap = Math.abs(vob.GetBranchValue1() - vob.GetBranchValue2());
	// if (minimum_gap > gap) {
	// minimum_gap = gap;
	// }
	// }
	// bvs.AddBranchValueState(branch, minimum_gap);
	// }
	// return bvs;
	// }

	// public static double ComputeAveragedInfluence(ArrayList<String>
	// interested_branch,
	// Map<String, Influence> branch_influence) {
	// double average_influence = 0.0;
	// double max_w = 5.0;
	// double min_w = 0.0;
	// double gap_w = (max_w - min_w) / ((interested_branch.size() + 1) * 1.0);
	// double w = max_w;
	// Iterator<String> ib_itr = interested_branch.iterator();
	// while (ib_itr.hasNext()) {
	// String ib = ib_itr.next();
	// double influence = branch_influence.get(ib).GetInfluence();
	// average_influence += w * influence;
	// w -= gap_w;
	// }
	// return average_influence;
	// }

	public static Integer IdentifyStateForBranches(LinkedList<ValuesOfBranch> vobs) {
		Integer state = null;
		Iterator<ValuesOfBranch> vob_itr = vobs.iterator();
		while (vob_itr.hasNext()) {
			ValuesOfBranch vob = vob_itr.next();
			state = MergeBranchState(vob.GetCmpOptr(), IdentifyStateForOneBranch(vob), state);
		}
		return state;
	}

	public static Integer MergeBranchState(String cmp_optr, Integer vob_state, Integer state) {
		if (cmp_optr.startsWith("SWITCH")) {
			if (state == null) {
				state = 0b11;
			}
		} else {
			switch (cmp_optr) {
			// ``compare then store'' series
			case "D$CMPG":
			case "D$CMPL":
			case "F$CMPG":
			case "F$CMPL":
			case "L$CMP":
				if (state == null) {
					state = 0b111;
				}
				// // ``compare then store'' series BLOCK
				break;
			// eq neq series... *8
			case "I$==":
			case "I$!=":
			case "A$==":
			case "A$!=":
			case "IZ$==":
			case "IZ$!=":
			case "N$!=":
			case "N$==":
				if (state == null) {
					state = 0b11;
				}
				break;
			// ge, ge 0 *2
			case "I$>=":
			case "IZ$>=":
				if (state == null) {
					state = 0b11;
				}
				break;
			// le, le 0
			case "I$<=":
			case "IZ$<=":
				if (state == null) {
					state = 0b11;
				}
				break;
			// gt, gt 0
			case "I$>":
			case "IZ$>":
				if (state == null) {
					state = 0b11;
				}
				break;
			// lt, lt 0
			case "I$<":
			case "IZ$<":
				if (state == null) {
					state = 0b11;
				}
				break;
			default:
				System.out.println("Serious error! no existing hit cases!");
				System.exit(1);
			} // switch (current_vob.GetCmpOptr())
		}
		state &= vob_state;
		return state;
	}

	public static Integer IdentifyStateForOneBranch(ValuesOfBranch vob) {
		double v1 = vob.GetBranchValue1();
		double v2 = vob.GetBranchValue2();
		String cmp_optr = vob.GetCmpOptr();
		if (cmp_optr.startsWith("SWITCH")) {
			return IdentifyEqualOrNot(v1, v2);
		} else {
			switch (cmp_optr) {
			// ``compare then store'' series
			case "D$CMPG":
			case "D$CMPL":
			case "F$CMPG":
			case "F$CMPL":
			case "L$CMP": {
				if (v1 == v2) {
					return 0b101;
				} else {
					if (v1 > v2) {
						return 0b110;
					} else {
						return 0b011;
					}
				}
			} // // ``compare then store'' series BLOCK
				// eq neq series... *8
			case "I$==":
			case "I$!=":
			case "A$==":
			case "A$!=":
			case "IZ$==":
			case "IZ$!=":
			case "N$!=":
			case "N$==": {
				return IdentifyEqualOrNot(v1, v2);
			}
			// ge, ge 0 *2
			case "I$>=":
			case "IZ$>=": {
				if (v1 >= v2) {
					return 0b10;
				} else {
					return 0b01;
				}
			}
			// le, le 0
			case "I$<=":
			case "IZ$<=": {
				if (v1 <= v2) {
					return 0b01;
				} else {
					return 0b10;
				}
			}
			// gt, gt 0
			case "I$>":
			case "IZ$>": {
				if (v1 > v2) {
					return 0b10;
				} else {
					return 0b01;
				}
			}
			// lt, lt 0
			case "I$<":
			case "IZ$<": {
				if (v1 < v2) {
					return 0b01;
				} else {
					return 0b10;
				}
			}
			default:
				System.out.println("Serious error! no existing hit cases!");
				System.exit(1);
			} // switch (current_vob.GetCmpOptr())
		}
		return null;
	}

	private static Integer IdentifyEqualOrNot(double v1, double v2) {
		if (v1 == v2) {
			return 0b01;
		} else {
			return 0b10;
		}
	}

}
