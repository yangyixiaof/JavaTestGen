package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import randoop.generation.date.DateWtfException;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.reflection.TypeInstantiator;

/**
 * A singleton class, memoizing available mutations of sequences.
 *
 * <p>
 * this.state_actions stores...
 *
 * <p>
 * If not stored, use MutationAnalyzer to analyze and to store.
 */
public class StateActionPool {

	TypeInstantiator ti = null;
	Collection<TypedOperation> candidates = null;

	Map<TraceableSequence, ArrayList<MutationOperation>> state_actions = new TreeMap<>();
	Map<TraceableSequence, ArrayList<Integer>> state_action_states = new TreeMap<>();// 1 means taken, 0 means untaken
	Map<TraceableSequence, Integer> state_untaken_actions = new TreeMap<>();

	public StateActionPool(TypeInstantiator ti, Collection<TypedOperation> candidates) {
		this.ti = ti;
		this.candidates = candidates;
	}

	// private void StoreAllActionsOfOneState(TraceableSequence state,
	// ArrayList<MutationOperation>
	// actions) {
	// Assert.isTrue(!state_actions.containsKey(state));
	// state_actions.put(state, actions);
	// }

	/**
	 * "Memoizationalized" and wrapped version of
	 * MutationAnalyzer#GenerateMutationOperations
	 *
	 * @param state
	 * @return
	 */
	public ArrayList<MutationOperation> GetAllActionsOfOneState(TraceableSequence state) {
		if (!state_actions.containsKey(state)) {
			MutationAnalyzer analyzer = new MutationAnalyzer(state, ti);
			ArrayList<MutationOperation> candidateMutations = new ArrayList<>();
			try {
				analyzer.GenerateMutationOperations(candidates, candidateMutations);
				// System.out.println("candidateMutations_size:" + candidateMutations.size());
			} catch (DateWtfException e) {
				e.printStackTrace();
			}
			state_actions.put(state, candidateMutations);
			int mo_size = candidateMutations.size();
			ArrayList<Integer> ss = new ArrayList<Integer>();
			for (int i=0;i<mo_size;i++) {
				ss.add(0);
			}
			state_action_states.put(state, ss);
			state_untaken_actions.put(state, mo_size);
		}
		return state_actions.get(state);
	}
	
	public void ActionOfOneStateBeTaken(TraceableSequence state, int action_index) {
		state_action_states.get(state).set(action_index, 1);
		state_untaken_actions.put(state, state_untaken_actions.get(state)-1);
	}
	
	public boolean HasUntakenActionsOfOneState(TraceableSequence state) {
		return state_untaken_actions.get(state) > 0;
	}
	
	public ArrayList<MutationOperation> GetUntakenActionsOfOneState(TraceableSequence state) {
		if (state_untaken_actions.get(state).equals(0)) {
			try {
				throw new Exception("state does not have untaken actions!");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		ArrayList<MutationOperation> untaken_mos = new ArrayList<MutationOperation>();
		ArrayList<MutationOperation> all_mos = GetAllActionsOfOneState(state);
		ArrayList<Integer> sts = state_action_states.get(state);
		Iterator<MutationOperation> aitr = all_mos.iterator();
		Iterator<Integer> sitr = sts.iterator();
		while (aitr.hasNext()) {
			MutationOperation mo = aitr.next();
			Integer st = sitr.next();
			if (st == 0) {
				untaken_mos.add(mo);
			}
		}
		return untaken_mos;
	}
	
}
