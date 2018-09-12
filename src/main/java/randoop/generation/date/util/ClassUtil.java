package randoop.generation.date.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassUtil {
	
	private static Map<Class<?>, Set<Class<?>>> cache = new HashMap<>();
	
	public static Set<Class<?>> GetAssignableClasses(Set<Class<?>> classes, Class<?> spec) {
		if (cache.containsKey(spec)) {
			return cache.get(spec);
		}
		Set<Class<?>> result = new HashSet<Class<?>>();
		for (Class<?> cls : classes) {
			if (cls.isAssignableFrom(spec)) {
				result.add(cls);
			}
		}
		cache.put(spec, result);
		return result;
	}
	
}
