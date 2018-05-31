package randoop.generation.date.embed;

import java.util.HashMap;
import java.util.Map;

import randoop.operation.TypedOperation;

public class TypedOperationIDAssigner {
	
	private int id = 0;
	
	private Map<TypedOperation, Integer> operation_id_map = new HashMap<TypedOperation, Integer>();
	
	public TypedOperationIDAssigner() {
	}
	
	public int AssignID(TypedOperation t_o) {
		Integer s_id = operation_id_map.get(t_o);
		if (s_id == null) {
			s_id = id++;
			operation_id_map.put(t_o, s_id);
		}
		return s_id;
	}
	
}
