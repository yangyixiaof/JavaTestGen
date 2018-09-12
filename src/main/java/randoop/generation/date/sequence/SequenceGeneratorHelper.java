package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import randoop.generation.date.random.RandomSelect;
import randoop.types.Type;
import randoop.types.TypeTuple;
import randoop.util.Randomness;

public class SequenceGeneratorHelper {
	
	public static void SelectToListFromMap(Set<Class<?>> selected_classes, Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable, ArrayList<PseudoVariable> candidates) {
		for (Class<?> sc : selected_classes) {
			candidates.addAll(class_pseudo_variable.get(sc));
		}
	}
	
	public static void GenerateInputPseudoVariables(ArrayList<PseudoVariable> input_pseudo_variables, List<Type> r_type_list, Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable, Map<PseudoVariable, PseudoSequence> pseudo_variable_headed_sequence) {
		ArrayList<ArrayList<PseudoVariable>> each_position_candidates = new ArrayList<ArrayList<PseudoVariable>>();
		Iterator<Type> r_t_itr = r_type_list.iterator();
		while (r_t_itr.hasNext()) {
			ArrayList<PseudoVariable> candidates = new ArrayList<PseudoVariable>();
			Type tp = r_t_itr.next();
			Set<Class<?>> selected_classes = new HashSet<Class<?>>();
			Set<Class<?>> class_set = class_pseudo_variable.keySet();
			Iterator<Class<?>> citr = class_set.iterator();
			while (citr.hasNext()) {
				Class<?> cls = citr.next();
				if (tp.isAssignableFrom(Type.forClass(cls))) {
					selected_classes.add(cls);
//					candidates.addAll(class_pseudo_variable.get(cls));
				}
			}
			SelectToListFromMap(selected_classes, class_pseudo_variable, candidates);
			if (candidates.size() > 0) {
				each_position_candidates.add(candidates);
			}
		}
		if (each_position_candidates.size() == r_type_list.size()) {
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
	

	public static double ComputeDelta(double previous_delta, double influence, Set<Double> have_tried_delta) {
		double delta = 1.0;
		if (previous_delta != 0.0) {
			if (influence > 0) {
				delta = previous_delta * 2.0;
			} else {
				if (influence == 0) {
					delta = 1.0;
				} else {
					delta = - previous_delta / 2.0;
				}
			}
		}
		int try_times = 0;
		double computed_delta = delta;
		while (!have_tried_delta.contains(computed_delta) && try_times < 5) {
			computed_delta = Randomness.randomSetMember(have_tried_delta) * 2.0;
			int sig = Randomness.nextRandomInt(2)-1;
			if (sig == 0) {
				sig = 1;
			}
			computed_delta *= sig;
			try_times++;
		}
		if (have_tried_delta.contains(computed_delta)) {
			computed_delta = Math.random() * 100000.0;
		}
		delta = computed_delta;
		have_tried_delta.add(delta);
		return delta;
	}
	
}
