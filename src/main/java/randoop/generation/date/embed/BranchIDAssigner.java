package randoop.generation.date.embed;

import java.util.HashMap;
import java.util.Map;

public class BranchIDAssigner {
	
	public BranchIDAssigner() {
	}
	
	private int id = 0;
	
	private Map<String, Integer> branch_value_id_map = new HashMap<String, Integer>();
	
	public int AssignID(String b_v) {
		Integer b_id = branch_value_id_map.get(b_v);
		if (b_id == null) {
			b_id = id++;
			branch_value_id_map.put(b_v, b_id);
		}
		return b_id;
	}
	
}
