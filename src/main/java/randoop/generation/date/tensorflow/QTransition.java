package randoop.generation.date.tensorflow;

public class QTransition {
	
	String state = null;
	int action = -1;
	float reward = 0.0f;
	String next_state = null;
	
	public QTransition(String state, int action, float reward, String next_state) {
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
