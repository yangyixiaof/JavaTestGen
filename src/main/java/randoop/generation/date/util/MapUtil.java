package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Map;

import randoop.operation.TypedOperation;

public class MapUtil {
	
	public static void Insert(Map<TypedOperation, Class<?>> insert, Map<Class<?>, ArrayList<TypedOperation>> be_inserted, Class<?> key, TypedOperation value) {
		insert.put(value, key);
		ArrayList<TypedOperation> to_list = be_inserted.get(key);
		if (to_list == null) {
			to_list = new ArrayList<TypedOperation>();
			be_inserted.put(key, to_list);
		}
		to_list.add(value);
	}
	
}
