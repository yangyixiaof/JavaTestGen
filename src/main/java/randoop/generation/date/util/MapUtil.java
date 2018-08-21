package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Map;

import randoop.operation.TypedOperation;

public class MapUtil {
	
//	public static void Insert(Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence, Map<TypedOperation, Class<?>> insert, Map<Class<?>, ArrayList<TypedOperation>> be_inserted, Class<?> key, TypedOperation value) {
//		typed_operation_branch_influence.put(value, new InfluenceOfBranchChange());
//		insert.put(value, key);
//		ArrayList<TypedOperation> to_list = be_inserted.get(key);
//		if (to_list == null) {
//			to_list = new ArrayList<TypedOperation>();
//			be_inserted.put(key, to_list);
//		}
//		to_list.add(value);
//	}

	public static void Insert(TypedOperation op, Class<?> op_for_class, boolean is_to_create,
			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_create_operations,
			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations,
			Map<TypedOperation, Class<?>> operation_class, Map<TypedOperation, Boolean> operation_is_to_create) {
		if (is_to_create) {
			ArrayList<TypedOperation> tos = for_use_object_create_operations.get(op_for_class);
			if (tos == null) {
				tos = new ArrayList<TypedOperation>();
				for_use_object_create_operations.put(op_for_class, tos);
			}
			for_use_object_create_operations.put(op_for_class, tos);
		} else {
			ArrayList<TypedOperation> tos = for_use_object_modify_operations.get(op_for_class);
			if (tos == null) {
				tos = new ArrayList<TypedOperation>();
				for_use_object_modify_operations.put(op_for_class, tos);
			}
			for_use_object_modify_operations.put(op_for_class, tos);
		}
		operation_class.put(op, op_for_class);
		operation_is_to_create.put(op, is_to_create);
	}
	
}
