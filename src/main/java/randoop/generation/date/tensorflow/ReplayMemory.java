package randoop.generation.date.tensorflow;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

public class ReplayMemory {
	
	Map<String, QTransition> all_transitions = new TreeMap<String, QTransition>();
	
	public ReplayMemory() {
	}
	
	public void StoreTransitions(List<QTransition> transitions) {
		for (QTransition qt : transitions) {
			String q_tran_representation = qt.toString();
			Assert.isTrue(!all_transitions.containsKey(q_tran_representation));
			all_transitions.put(q_tran_representation, qt);
		}
	}
	
	public void SampleMiniBatch() {
		// TODO ha
	}
	
}
