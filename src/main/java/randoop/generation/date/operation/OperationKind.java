package randoop.generation.date.operation;

public enum OperationKind {
	
	unknown(0),
	exceptional(1),
	no_branch(2),
	branch(3),
	;
	
	int value = -1;
	
	private OperationKind(int value) {
		this.value = value;
	}
	
	public int GetValue() {
		return value;
	}
	
}
