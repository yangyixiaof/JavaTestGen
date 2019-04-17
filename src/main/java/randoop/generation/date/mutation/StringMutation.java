package randoop.generation.date.mutation;

public class StringMutation extends Mutation {
	
	Integer position = -1;
	Integer delta = -1;
	
	public StringMutation(Integer position, Integer delta) {
		this.position = position;
		this.delta = delta;
	}
	
	public int GetPosition() {
		return position;
	}
	
	public Integer GetDelta() {
		return delta;
	}
	
}
