package randoop.generation.date.util;

import java.util.LinkedList;
import java.util.Map;

import randoop.operation.TypedOperation;

public class MapUtil {
	
	public static void Insert(Map<String, LinkedList<TypedOperation>> be_inserted, String key, TypedOperation value) {
		LinkedList<TypedOperation> to_list = be_inserted.get(key);
		if (to_list == null) {
			to_list = new LinkedList<TypedOperation>();
			be_inserted.put(key, to_list);
		}
		to_list.add(value);
	}
	
}
