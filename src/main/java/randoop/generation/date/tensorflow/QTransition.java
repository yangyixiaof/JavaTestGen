package randoop.generation.date.tensorflow;

import java.util.HashMap;
import java.util.Map;

import randoop.generation.date.sequence.TraceableSequence;
import randoop.sequence.ExecutableSequence;

public class QTransition {

	TraceableSequence state = null;
	int action = -1;
	TraceableSequence next_state = null;
	Map<String, Double> influences = new HashMap<String, Double>();

	public QTransition(TraceableSequence state, int action, TraceableSequence next_state) {
		this.state = state;
		this.action = action;
		this.next_state = next_state;
	}
	
	public void  SetUpInfluences(Map<String, Double> influences) {
		this.influences.putAll(influences);
	}

	@Override
	public String toString() {
		return state + "#" + action + "#" + next_state;
	}

	public ExecutableSequence GetExecutableSequence() {
		return new ExecutableSequence(next_state);
	}

	public TraceableSequence GetSourceSequence() {
		return state;
	}
	
	public TraceableSequence GetTargetSequence() {
		return next_state;
	}
	
}
