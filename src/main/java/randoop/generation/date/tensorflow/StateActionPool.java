package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import randoop.generation.date.DateMeta;
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
	Map<TraceableSequence, ArrayList<Integer>> state_action_states = new TreeMap<>();// 1 means taken, 0 means un_taken
	Map<TraceableSequence, Integer> state_untaken_actions = new TreeMap<>();
	
	// correspond to state_actions
	Map<TraceableSequence, ArrayList<TreeMap<String, Double>>> all_accumulated_influences = new TreeMap<>();

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
			analyzer.GenerateMutationOperations(candidates, candidateMutations);
				// System.out.println("candidateMutations_size:" + candidateMutations.size());
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
	
	public boolean DoNotHaveUntakenActionsOfOneState(TraceableSequence state) {
		return state_untaken_actions.get(state) <= 0;
	}
	
	public ArrayList<MutationOperation> GetUntakenActionsOfOneState(TraceableSequence state) {
		Integer untakens_of_this_state = state_untaken_actions.get(state);
		if (untakens_of_this_state != null && untakens_of_this_state.equals(0)) {
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
	
	public void BackTraceToStoreDiscountedInfluence(QTransition transition, Map<String, Double> current_influences, int back_turns) {
		if (transition == null) {
			return;
		}
		TraceableSequence source_sequence = transition.GetSourceSequence();
		ArrayList<TreeMap<String, Double>> actions_accumulated_influences = all_accumulated_influences.get(source_sequence);
		if (actions_accumulated_influences == null) {
			actions_accumulated_influences = new ArrayList<TreeMap<String, Double>>();
			int action_size = state_actions.get(source_sequence).size();
			for (int i=0;i<action_size;i++) {
				actions_accumulated_influences.add(new TreeMap<String, Double>());
			}
			all_accumulated_influences.put(source_sequence, actions_accumulated_influences);
		}
		TreeMap<String, Double> accumulated_influences = actions_accumulated_influences.get(transition.action);
		
		Set<String> ci_keys = current_influences.keySet();
		Iterator<String> ci_itr = ci_keys.iterator();
		while (ci_itr.hasNext()) {
			String ci_key = ci_itr.next();
			Double accumulated_influence_ci_value = accumulated_influences.get(ci_key);
			if (accumulated_influence_ci_value == null) {
				accumulated_influence_ci_value = 0.0;
			}
			accumulated_influences.put(ci_key, accumulated_influence_ci_value + current_influences.get(ci_key) * Math.pow(DateMeta.reward_discount, back_turns));
		}
		BackTraceToStoreDiscountedInfluence(source_sequence.GetInputQTransition(), current_influences, back_turns+1);
	}
	
}
