package randoop.generation.date.influence;

import java.util.Map;
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
	
}
