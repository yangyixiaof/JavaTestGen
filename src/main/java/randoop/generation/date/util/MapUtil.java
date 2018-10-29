package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.operation.OperationKind;
import randoop.operation.TypedOperation;

public class MapUtil {

	public static void Insert(TypedOperation op, Class<?> op_for_class, Class<?> sequence_type, boolean is_to_create, boolean is_delta_change,
			OperationKind op_kind, DateGenerator dg
//			Map<Class<?>, Class<?>> for_use_object_create_sequence_type,
//			ArrayList<TypedOperation> create_operations,
//			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_create_operations,
//			ArrayList<TypedOperation> modify_operations,
//			Map<Class<?>, ArrayList<TypedOperation>> for_use_object_modify_operations,
//			Map<TypedOperation, Class<?>> operation_class, Map<TypedOperation, Boolean> operation_is_to_create, Map<TypedOperation, InfluenceOfBranchChange> typed_operation_branch_influence
			) {
		if (is_to_create) {
			dg.create_operations.add(op);
			ArrayList<TypedOperation> tos = dg.for_use_object_create_operations.get(op_for_class);
			if (tos == null) {
				tos = new ArrayList<TypedOperation>();
				dg.for_use_object_create_operations.put(op_for_class, tos);
			}
			dg.for_use_object_create_operations.put(op_for_class, tos);
			// set up sequence type
			Class<?> already_sequence_type = dg.for_use_object_create_sequence_type.get(op_for_class);
			if (already_sequence_type == null) {
				dg.for_use_object_create_sequence_type.put(op_for_class, sequence_type);
			} else {
				Assert.isTrue(already_sequence_type.equals(sequence_type));
			}
		} else {
			dg.modify_operations.add(op);
			ArrayList<TypedOperation> tos = dg.for_use_object_modify_operations.get(op_for_class);
			if (tos == null) {
				tos = new ArrayList<TypedOperation>();
				dg.for_use_object_modify_operations.put(op_for_class, tos);
			}
			tos.add(op);
			// only modify operations have influence change. 
			dg.typed_operation_branch_influence.put(op, new InfluenceOfBranchChange());
		}
		dg.operation_class.put(op, op_for_class);
		dg.operation_is_to_create.put(op, is_to_create);
		dg.operation_is_delta_change.put(op, is_delta_change);
		dg.operation_kind.put(op, op_kind);
	}
	
	public static <T> void MapOneMergeMapTwo(Map<T, Double> map_1, Map<T, Double> map_2) {
		Set<T> m2_keys = map_2.keySet();
		Iterator<T> m2_itr = m2_keys.iterator();
		while (m2_itr.hasNext()) {
			T t = m2_itr.next();
			Double m2_t_v = map_2.get(t);
			Double m1_t_v = map_1.get(t);
			if (m1_t_v == null) {
				m1_t_v = m2_t_v;
			} else {
				m1_t_v += m2_t_v;
			}
			map_1.put(t, m1_t_v);
		}
	}
	
}
