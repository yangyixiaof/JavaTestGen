package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import randoop.generation.date.random.RandomSelect;
import randoop.types.Type;
import randoop.types.TypeTuple;

public class SequenceGeneratorHelper {
	
	public static void GenerateInputPseudoVariables(ArrayList<PseudoVariable> input_pseudo_variables, List<Type> r_type_list, Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable, Map<PseudoVariable, PseudoSequence> pseudo_variable_headed_sequence) {
		ArrayList<ArrayList<PseudoVariable>> each_position_candidates = new ArrayList<ArrayList<PseudoVariable>>();
		Iterator<Type> r_t_itr = r_type_list.iterator();
		while (r_t_itr.hasNext()) {
			ArrayList<PseudoVariable> candidates = new ArrayList<PseudoVariable>();
			each_position_candidates.add(candidates);
			Type tp = r_t_itr.next();
			Class<?> tp_runtime_class = tp.getRuntimeClass();
			Set<Class<?>> class_set = class_pseudo_variable.keySet();
			Iterator<Class<?>> citr = class_set.iterator();
			while (citr.hasNext()) {
				Class<?> cls = citr.next();
				if (tp_runtime_class.isAssignableFrom(cls)) {
					candidates.addAll(class_pseudo_variable.get(cls));
				}
			}
		}
		if (each_position_candidates.size() == r_type_list.size()-1) {
			Iterator<ArrayList<PseudoVariable>> ipv_itr = each_position_candidates.iterator();
			while (ipv_itr.hasNext()) {
				ArrayList<PseudoVariable> pvs = ipv_itr.next();
				PseudoVariable param_selected_pv = RandomSelect.RandomPseudoVariableListAccordingToLength(pvs);
				Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
				PseudoVariable cpoied_pv = param_selected_pv.CopySelfInDeepCloneWay(origin_copied_sequence_map, pseudo_variable_headed_sequence);
				input_pseudo_variables.add(cpoied_pv);
			}
		}
	}
	
	public static List<Type> TypeTupleToTypeList(TypeTuple tt) {
		List<Type> type_list = new ArrayList<Type>();
		Iterator<Type> it_itr = tt.iterator();
		while (it_itr.hasNext()) {
			Type t = it_itr.next();
			type_list.add(t);
		}
		return type_list;
	}
	
}
