package randoop.generation.date.mutation;

public class StringMutation extends Mutation {
	
	int position = -1;
	int delta = -1;
	
	public StringMutation(int position, int delta) {
		this.position = position;
		this.delta = delta;
	}
	
	public int GetPosition() {
		return position;
	}
	
	public int GetDelta() {
		return delta;
	}
	
}
