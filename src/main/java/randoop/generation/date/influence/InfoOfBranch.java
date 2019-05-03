package randoop.generation.date.influence;

import java.util.ArrayList;

public class InfoOfBranch {

	ArrayList<ValuesOfBranch> traces = new ArrayList<ValuesOfBranch>();
	ArrayList<Integer> states = new ArrayList<Integer>();

	// ValuesOfBranch trace = null;

	// Integer state = null;

	double min_gap = Double.MAX_VALUE;
	double max_gap = 0;
	double sum_gap = 0;

	public InfoOfBranch() {
	}

	public void HandleOneValueOfBranch(ValuesOfBranch vob) {
		traces.add(vob);

		Integer one_state = SimpleInfluenceComputer.IdentifyStateForOneBranch(vob);
		vob.SetState(one_state);
		states.add(one_state);

		// state = SimpleInfluenceComputer.MergeBranchState(vob.GetCmpOptr(), one_state,
		// state);

		double gap = vob.GetGap();
		if (min_gap > gap) {
			min_gap = gap;
		}
		if (max_gap < gap) {
			max_gap = gap;
		}
		sum_gap += gap;
	}

	public Integer GetBranchState(int in_loop_index) {
		return traces.get(in_loop_index).GetState();
	}

	public ArrayList<ValuesOfBranch> GetAllValuesOfBranch() {
		return traces;
	}

	public String GenerateBranchStateSignature() {
		String[] state_array = new String[states.size()];
		for (int i = 0; i < states.size(); i++) {
			state_array[i] = states.get(i) + "";
		}
		return String.join("#", state_array);
	}

	// A B C
	// D A C
	// G H A C
	// P A C H

	// A B C
	// A D C
	// A G H C
	// A P C H

	// A0
	// A1 B0
	// A1 B1

	// 0 0 0
	// 0 0 1 ; 1 0 1
	// 1 0 0 ; 1 0 1

	// 0 0 1 ; 1 0 0
	// \ /
	// 1 0 1

	// state_1 -> state_2 through mutate (position)
	// for a state, if mutating a position causes transferring, we must see how many
	// branches it influences to decide the weights
	// for a state, we could record how many states are transfered to through
	// mutating a position

}
