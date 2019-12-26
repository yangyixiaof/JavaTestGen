package randoop.generation.date.mutation;

public class StringMutation extends Mutation {
	
	Integer sequence_index = -1;
	Integer position = -1;
	Integer delta = -1;
	
	public StringMutation(Integer sequence_index, Integer position, Integer delta) {
		this.sequence_index = sequence_index;
		this.position = position;
		this.delta = delta;
	}
	
	public int GetSequenceIndex() {
		return sequence_index;
	}
	
	public int GetPosition() {
		return position;
	}
	
	public Integer GetDelta() {
		return delta;
	}
	
}
