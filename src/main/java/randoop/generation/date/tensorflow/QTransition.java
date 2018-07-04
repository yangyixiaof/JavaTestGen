package randoop.generation.date.tensorflow;

import randoop.generation.date.sequence.TraceableSequence;
import randoop.sequence.ExecutableSequence;

public class QTransition {

	TraceableSequence state = null;
	int action = -1;
	float reward = 1.0f;
	TraceableSequence next_state = null;

	public QTransition(TraceableSequence state, TraceableSequence next_state, int action) {
		this.state = state;
		this.next_state = next_state;
		this.action = action;
	}

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
