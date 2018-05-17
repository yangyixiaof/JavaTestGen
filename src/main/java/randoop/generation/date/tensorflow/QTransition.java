package randoop.generation.date.tensorflow;

import randoop.generation.date.sequence.TraceableSequence;

public class QTransition {
	
	TraceableSequence state = null;
	int action = -1;
	float reward = 0.0f;
	TraceableSequence next_state = null;
	
	public QTransition(TraceableSequence state, int action, float reward, TraceableSequence next_state) {
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.next_state = next_state;
	}
	
	@Override
	public String toString() {
		return state + "#" + action + "#" + next_state;
	}
	
}
