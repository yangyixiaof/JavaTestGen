package randoop.generation.date.embed;

import java.util.HashMap;
import java.util.Map;

public class StringIDAssigner {
	
	private int id = 500;
	
	private Map<String, Integer> string_value_id_map = new HashMap<String, Integer>();
	
	public StringIDAssigner() {
	}
	
	public int AssignID(String s_v) {
		Integer s_id = string_value_id_map.get(s_v);
		if (s_id == null) {
			s_id = id++;
			string_value_id_map.put(s_v, s_id);
		}
		return s_id;
	}
	
}
