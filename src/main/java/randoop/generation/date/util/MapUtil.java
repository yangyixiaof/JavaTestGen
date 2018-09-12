package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.operation.TypedOperation;

public class MapUtil {

	public static void Insert(TypedOperation op, Class<?> op_for_class, Class<?> sequence_type, boolean is_to_create,
			Map<Class<?>, Class<?>> for_use_object_create_sequence_type,
			ArrayList<TypedOperation> create_operations,
			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_create_operations,
			ArrayList<TypedOperation> modify_operations,
			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations,
			Map<TypedOperation, Class<?>> operation_class, Map<TypedOperation, Boolean> operation_is_to_create, Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence) {
		if (is_to_create) {
			create_operations.add(op);
			ArrayList<TypedOperation> tos = for_use_object_create_operations.get(op_for_class);
			if (tos == null) {
				tos = new ArrayList<TypedOperation>();
				for_use_object_create_operations.put(op_for_class, tos);
			}
			for_use_object_create_operations.put(op_for_class, tos);
			// set up sequence type
			Class<?> already_sequence_type = for_use_object_create_sequence_type.get(op_for_class);
			if (already_sequence_type == null) {
				for_use_object_create_sequence_type.put(op_for_class, sequence_type);
			} else {
				Assert.isTrue(already_sequence_type.equals(sequence_type));
			}
		} else {
			modify_operations.add(op);
			ArrayList<TypedOperation> tos = for_use_object_modify_operations.get(op_for_class);
			if (tos == null) {
				tos = new ArrayList<TypedOperation>();
				for_use_object_modify_operations.put(op_for_class, tos);
			}
			for_use_object_modify_operations.put(op_for_class, tos);
			// only modify operations have influence change. 
			typed_operation_branch_influence.put(op, new InfluenceOfBranchChange());
		}
		operation_class.put(op, op_for_class);
		operation_is_to_create.put(op, is_to_create);
	}
	
}
