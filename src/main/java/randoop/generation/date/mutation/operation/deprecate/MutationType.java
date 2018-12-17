package randoop.generation.date.mutation.operation.deprecate;

public enum MutationType {
	insert(0), 
	modify_primitive_boolean(1),
	modify_primitive_integral(2),
	modify_primitive_real(3),
	modify_reference(4),
	remove(5),
	modify_string_alter(6),
	modify_string_insert(7),
	modify_string_remove(8)
	;

	int value = -1;
	
	private MutationType(int value) {
		this.value = value;
	}
	
	public int GetValue() {
		return value;
	}

}
