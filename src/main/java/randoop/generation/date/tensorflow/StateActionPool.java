package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.mutation.operation.MutationOperation;

public class StateActionPool {
	
	Map<String, ArrayList<MutationOperation>> state_actions = new TreeMap<String, ArrayList<MutationOperation>>();
	
	public StateActionPool() {
	}
	
	public void StoreAllActionsOfOneState(String state, ArrayList<MutationOperation> actions) {
		Assert.isTrue(!state_actions.containsKey(state));
		state_actions.put(state, actions);
	}
	
	public ArrayList<MutationOperation> GetAllActionsOfOneState(String state) {
		return state_actions.get(state);
	}
	
}
