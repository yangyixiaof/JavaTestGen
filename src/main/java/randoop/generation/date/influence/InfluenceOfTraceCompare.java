package randoop.generation.date.influence;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class InfluenceOfTraceCompare {
	
	// the key is the signature of a branch at byte-code level concatenated with "#" and the time the branch happens
	// for example:
	// A#1
	// A#2
	Map<String, Influence> influences = new TreeMap<String, Influence>();
	
	public Map<String, Influence> GetInfluences() {
		return influences;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("PrintInfluence! ");
		Set<String> in_keys = influences.keySet();
		Iterator<String> ik_itr = in_keys.iterator();
		while (ik_itr.hasNext()) {
			String k = ik_itr.next();
			Influence influ = influences.get(k);
			sb.append(k + ":" + influ.branch_gap_influence + ";");
		}
		return sb.toString();
	}
	
}
