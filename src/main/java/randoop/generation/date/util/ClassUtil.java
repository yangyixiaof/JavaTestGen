package randoop.generation.date.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import randoop.types.Type;

public class ClassUtil {
	
//	private static Map<Class<?>, Set<Class<?>>> cache = new HashMap<>();
	
	public static Set<Class<?>> GetSuperClasses(Set<Class<?>> classes, Class<?> spec) {
//		if (cache.containsKey(spec)) {
//			return cache.get(spec);
//		}
		Set<Class<?>> result = new HashSet<Class<?>>();
		for (Class<?> cls : classes) {
			if (cls.isAssignableFrom(spec)) {
				result.add(cls);
			}
		}
//		cache.put(spec, result);
		return result;
	}
	
	public static Set<Class<?>> GetDescendantClasses(Set<Class<?>> classes, Class<?> spec) {
		Set<Class<?>> result = new HashSet<Class<?>>();
		for (Class<?> cls : classes) {
			if (spec.isAssignableFrom(cls)) {
				result.add(cls);
			}
		}
		return result;
	}
	
	public static final List<String> assignable_primitives = Arrays.asList(new String[] {"byte#java.lang.Byte", "short#java.lang.Short", "int#java.lang.Integer", "long#java.lang.Long", "float#java.lang.Float", "double#java.lang.Double", "boolean#java.lang.Boolean", "char#java.lang.Character"});
	
	public static boolean TypeOneIsAssignableFromTypeTwo(Type type_one, Type type_two) {
		boolean run_time_class_is_assignable = type_one.getRuntimeClass().isAssignableFrom(type_two.getRuntimeClass());
		boolean primitive_assignable = false;
		if ((type_one.isBoxedPrimitive() && type_two.isPrimitive()) || (type_one.isPrimitive() && type_two.isBoxedPrimitive())) {
			String ot = type_one.getName() + "#" + type_two.getName();
			String to = type_two.getName() + "#" + type_one.getName();
			if (assignable_primitives.contains(ot) || assignable_primitives.contains(to)) {
				primitive_assignable = true;
			}
		}
		boolean is_assignable = run_time_class_is_assignable || primitive_assignable;
		return is_assignable;
	}
	
}
