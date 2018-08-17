package randoop.generation.date.random.filter;

import java.util.Map;

import randoop.generation.date.sequence.PseudoVariable;

public class PseudoVariableSelectFilter implements SelectFileter<PseudoVariable> {
	
	Class<?> typed_operation_for_class = null;
	Map<PseudoVariable, Class<?>> pseudo_variable_class = null;
	
	public PseudoVariableSelectFilter(Class<?> typed_operation_for_class, Map<PseudoVariable, Class<?>> pseudo_variable_class) {
		this.typed_operation_for_class = typed_operation_for_class;
		this.pseudo_variable_class = pseudo_variable_class;
	}
	
	@Override
	public boolean Retain(PseudoVariable pv) {
		Class<?> pv_class = pseudo_variable_class.get(pv);
		if (pv_class != null) {
			if (typed_operation_for_class.isAssignableFrom(pv_class)) {
				return true;
			}
		}
		return false;
	}
	
}
