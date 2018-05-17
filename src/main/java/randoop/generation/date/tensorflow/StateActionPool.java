package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateWtfException;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.reflection.TypeInstantiator;

public class StateActionPool {

	TypeInstantiator ti = null;
	Set<TypedOperation> candidates = null;
	
	Map<TraceableSequence, ArrayList<MutationOperation>> state_actions = new TreeMap<TraceableSequence, ArrayList<MutationOperation>>();

	public StateActionPool(TypeInstantiator ti, Set<TypedOperation> candidates) {
		this.ti = ti;
		this.candidates = candidates;
	}

	private void StoreAllActionsOfOneState(TraceableSequence state, ArrayList<MutationOperation> actions) {
		Assert.isTrue(!state_actions.containsKey(state));
		state_actions.put(state, actions);
	}

	public ArrayList<MutationOperation> GetAllActionsOfOneState(TraceableSequence state) {
		if (!state_actions.containsKey(state)) {
			MutationAnalyzer analyzer = new MutationAnalyzer(state, ti);
			ArrayList<MutationOperation> candidateMutations = new ArrayList<MutationOperation>();
			try {
				analyzer.GenerateMutationOperations(candidates, candidateMutations);
			} catch (DateWtfException e) {
				e.printStackTrace();
			}
			state_actions.put(state, candidateMutations);
		}
		return state_actions.get(state);
	}

}
