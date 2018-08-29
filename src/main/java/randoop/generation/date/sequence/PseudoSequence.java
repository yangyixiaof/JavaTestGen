package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.Penalizable;
import randoop.operation.TypedOperation;
import randoop.sequence.Variable;
import randoop.types.Type;
import randoop.types.TypeTuple;

public class PseudoSequence implements Penalizable {

	PseudoVariable headed_variable = null;

	ArrayList<TypedOperation> operations = null;// these operations only contain object modify operations.
	Map<TypedOperation, Integer> operation_use_count = new HashMap<TypedOperation, Integer>();

	ArrayList<PseudoStatement> statements = new ArrayList<PseudoStatement>();

	PseudoSequence previous = null;

	Map<String, Influence> all_branches_influences_compared_to_previous = null;

	String headed_variable_string = null;

	public PseudoSequence(ArrayList<TypedOperation> operations) {
		this.operations = operations;
	}

	public void SetHeadedVariable(PseudoVariable headed_variable) {
		this.headed_variable = headed_variable;
	}

	public void SetHeadedVariableString(String headed_variable_string) {
		if (headed_variable_string == null) {
			Assert.isTrue(this.headed_variable_string == null);
		} else {
			if (this.headed_variable_string == null) {
				this.headed_variable_string = headed_variable_string;
			} else {
				Assert.isTrue(headed_variable_string.equals(this.headed_variable_string));
			}
		}

	}

	public PseudoSequence(PseudoVariable headed_variable, ArrayList<TypedOperation> operations) {
		this.headed_variable = headed_variable;
		this.operations = operations;
	}

	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, 
			ArrayList<String> interested_branch, Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		BeforeAfterLinkedSequence result = null;
		ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
		// add parameters.
		TypeTuple input_types = selected_to.getInputTypes();
		List<Type> type_list = SequenceGeneratorHelper.TypeTupleToTypeList(input_types);
		List<Type> r_type_list = type_list.subList(1, type_list.size());
		SequenceGeneratorHelper.GenerateInputPseudoVariables(input_pseudo_variables, r_type_list, class_pseudo_variable,
				class_object_headed_sequence);
		// initialize candidates.
		if (input_pseudo_variables.size() == r_type_list.size()) {
			HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
			PseudoSequence ps = this.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
					class_object_headed_sequence);
			ps.SetPreviousSequence(this);
			input_pseudo_variables.add(0, ps.headed_variable);
			LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
			ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
			LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
			result = new BeforeAfterLinkedSequence(selected_to, ps.headed_variable, ps, before_linked_sequence,
					after_linked_sequence);
		}
		if (result != null) {
			Integer count = operation_use_count.get(selected_to);
			if (count == null) {
				count = 0;
			}
			count++;
			operation_use_count.put(selected_to, count);
		}
		return result;
	}

	public void SetPreviousSequence(PseudoSequence pseudo_sequence) {
		this.previous = pseudo_sequence;
	}

	public void SetAllBranchesInfluencesComparedToPrevious(
			Map<String, Influence> all_branches_influences_compared_to_previous) {
		this.all_branches_influences_compared_to_previous = all_branches_influences_compared_to_previous;
	}

	public PseudoVariable Append(TypedOperation operation, ArrayList<PseudoVariable> inputVariables,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		PseudoVariable pv = null;
		if (!operation.getOutputType().isVoid()) {
			int soon_be_added_variable_index_of_statement = statements.size();
			pv = new PseudoVariable(this, soon_be_added_variable_index_of_statement);
			PseudoSequence ps = class_object_headed_sequence.get(pv);
			// Assert.isTrue(ps == null);
			// ps = new PseudoSequence();
			class_object_headed_sequence.put(pv, ps);
		}
		statements.add(new PseudoStatement(operation, inputVariables));
		AddReferenceForAllVariables(inputVariables);
		return pv;
	}

	// public void Reset(int retain_length) {
	// if (retain_length > statements.size()) {
	// retain_length = statements.size();
	// }
	// List<PseudoStatement> retain_statements = statements.subList(0,
	// retain_length);
	// for (PseudoStatement stmt : statements) {
	// RemoveReferenceForAllVariables(stmt.inputVariables);
	// }
	// statements.clear();
	// statements.addAll(retain_statements);
	// for (PseudoStatement stmt : statements) {
	// AddReferenceForAllVariables(stmt.inputVariables);
	// }
	// }

	// private void RemoveReferenceForAllVariables(List<PseudoVariable>
	// inputVariables) {
	// for (PseudoVariable pv : inputVariables) {
	// RemoveReferenceToInUseSequence(pv.sequence);
	// }
	// }

	// private void RemoveReferenceToInUseSequence(PseudoSequence in_use_sequence) {
	// in_use_sequence.sequences_which_use_this_sequence.remove(this);
	// }

	private void AddReferenceForAllVariables(List<PseudoVariable> inputVariables) {
		for (PseudoVariable pv : inputVariables) {
			pv.sequences_which_use_this_variable.add(this);
		}
	}

	public PseudoSequence CopySelfInDeepCloneWay(Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		if (origin_copied_sequence_map.containsKey(this)) {
			return origin_copied_sequence_map.get(this);
		}
		PseudoSequence copy_version = null;
		try {
			copy_version = this.getClass().getConstructor(ArrayList.class).newInstance(operations);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		origin_copied_sequence_map.put(this, copy_version);
		PseudoVariable copied_headed_variable = headed_variable.CopySelfInDeepCloneWay(origin_copied_sequence_map,
				class_object_headed_sequence);
		copy_version.SetHeadedVariable(copied_headed_variable);
		// clone statements
		for (PseudoStatement stmt : statements) {
			PseudoStatement copy_stmt = stmt.CopySelfInDeepCloneWay(origin_copied_sequence_map,
					class_object_headed_sequence);
			copy_version.Append(copy_stmt.operation, copy_stmt.inputVariables, class_object_headed_sequence);
		}
		return copy_version;
	}

	public PseudoSequence CopySelfAndCitersInDeepCloneWay(
			Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		if (origin_copied_sequence_map.containsKey(this)) {
			return origin_copied_sequence_map.get(this);
		}
		PseudoSequence copy_version = null;
		try {
			copy_version = this.getClass().getConstructor(ArrayList.class).newInstance(operations);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		origin_copied_sequence_map.put(this, copy_version);
		PseudoVariable copied_headed_variable = headed_variable
				.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map, class_object_headed_sequence);
		copy_version.SetHeadedVariable(copied_headed_variable);
		// clone statements
		for (PseudoStatement stmt : statements) {
			PseudoStatement copy_stmt = stmt.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
					class_object_headed_sequence);
			copy_version.Append(copy_stmt.operation, copy_stmt.inputVariables, class_object_headed_sequence);
		}
		// clone citer sequences
		Assert.isTrue(headed_variable != null);
		HashSet<PseudoSequence> sequences_which_use_this_headed_variable = headed_variable.sequences_which_use_this_variable;
		for (PseudoSequence citer : sequences_which_use_this_headed_variable) {
			copied_headed_variable.sequences_which_use_this_variable.add(
					citer.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map, class_object_headed_sequence));
		}
		// clone influences
		// Set<TypedOperation> tobi_keys = typed_operation_branch_influence.keySet();
		// Iterator<TypedOperation> tobi_itr = tobi_keys.iterator();
		// while (tobi_itr.hasNext()) {
		// TypedOperation to = tobi_itr.next();
		// InfluenceOfBranchChange copied_influence =
		// typed_operation_branch_influence.get(to).CopySelfInDeepCloneWay();
		// copy_version.typed_operation_branch_influence.put(to, copied_influence);
		// }
		return copy_version;
	}

	public void ReplacePseudoVariableInCitesAndCiters(PseudoSequence self, PseudoVariable be_replaced,
			PseudoVariable the_replace) {
		HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
		this.BuildDependency(encountered);
		for (PseudoSequence ps : encountered) {
			if (ps == self) {
				continue;
			}
			for (PseudoStatement stmt : ps.statements) {
				ArrayList<PseudoVariable> ivs = stmt.inputVariables;
				int iv_len = ivs.size();
				for (int i = 0; i < iv_len; i++) {
					PseudoVariable pv = ivs.get(i);
					if (pv.equals(be_replaced)) {
						ivs.set(i, the_replace);
					}
				}
			}
		}
	}

	private void BuildDependency(HashSet<PseudoSequence> encountered) {
		if (encountered.contains(this)) {
			return;
		}
		encountered.add(this);
		for (PseudoStatement stmt : this.statements) {
			for (PseudoVariable pv : stmt.inputVariables) {
				PseudoSequence pv_sequence = pv.sequence;
				pv_sequence.BuildDependency(encountered);
			}
		}
		if (headed_variable == null) {
			Assert.isTrue(statements.size() == 0);
		} else {
			HashSet<PseudoSequence> sequences_which_use_this_headed_variable = headed_variable.sequences_which_use_this_variable;
			for (PseudoSequence ps_which_uses_this : sequences_which_use_this_headed_variable) {
				ps_which_uses_this.BuildDependency(encountered);
			}
		}
	}

	public LinkedSequence GenerateLinkedSequence() {
		SequenceWrapper sw = new SequenceWrapper();
		ArrayList<PseudoVariable> pseudo_sequence_with_index_for_each_statement_in_sequence = new ArrayList<PseudoVariable>();
		HashMap<PseudoSequence, TreeMap<Integer, Integer>> pseudo_sequence_index_to_sequence_index = new HashMap<PseudoSequence, TreeMap<Integer, Integer>>();
		// build all dependency set for this pseudo sequence
		HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
		this.BuildDependency(encountered);
		HashMap<PseudoSequence, Integer> ps_safe_length = new HashMap<PseudoSequence, Integer>();
		for (PseudoSequence depend : encountered) {
			ps_safe_length.put(depend, 0);
			pseudo_sequence_index_to_sequence_index.put(depend, new TreeMap<Integer, Integer>());
		}
		while (encountered.size() > 0) {
			HashSet<PseudoSequence> need_to_remove = new HashSet<PseudoSequence>();
			Iterator<PseudoSequence> d_itr = encountered.iterator();
			while (d_itr.hasNext()) {
				PseudoSequence ps = d_itr.next();
				boolean over = HandleOneSequenceAsFar(sw, ps, ps_safe_length, pseudo_sequence_index_to_sequence_index,
						pseudo_sequence_with_index_for_each_statement_in_sequence);
				if (over) {
					need_to_remove.add(ps);
				}
			}
			encountered.removeAll(need_to_remove);
		}
		// System.out.println("sequence_size:" + sw.sequence.size() + "#sequence:" +
		// sw.sequence);
		return new LinkedSequence(sw.sequence.statements, pseudo_sequence_with_index_for_each_statement_in_sequence);
	}

	private static boolean HandleOneSequenceAsFar(SequenceWrapper sw, PseudoSequence ps,
			HashMap<PseudoSequence, Integer> ps_safe_length,
			HashMap<PseudoSequence, TreeMap<Integer, Integer>> pseudo_sequence_index_to_sequence_index,
			ArrayList<PseudoVariable> pseudo_sequence_with_index_for_each_statement_in_sequence) {
		while (true) {
			int safe_length = ps_safe_length.get(ps);
			// System.out.println("safe_length:" + safe_length + "#ps.statements.size():" +
			// ps.statements.size());
			if (safe_length >= ps.statements.size()) {
				break;
			}
			PseudoStatement need_to_handle = ps.statements.get(safe_length);
			ArrayList<PseudoVariable> ivs = need_to_handle.inputVariables;
			Iterator<PseudoVariable> iv_itr = ivs.iterator();
			boolean ensure_safe = true;
			ArrayList<Variable> realInputVariables = new ArrayList<Variable>();
			while (iv_itr.hasNext()) {
				PseudoVariable pv = iv_itr.next();
				Assert.isTrue(pv.index >= 0);
				if (pv.index >= ps_safe_length.get(pv.sequence)) {
					ensure_safe = false;
					break;
				}
				int index_in_sequence = pseudo_sequence_index_to_sequence_index.get(pv.sequence).get(pv.index);
				// System.out.println("index_in_sequence:" + index_in_sequence);
				realInputVariables.add(sw.sequence.getVariable(index_in_sequence));
			}
			if (ensure_safe) {
				// handle real Sequence, append to last of the real Sequence.
				int recently_add_index = sw.sequence.size();
				pseudo_sequence_index_to_sequence_index.get(ps).put(safe_length, recently_add_index);
				// System.out.println("need_to_handle.operation:" + need_to_handle.operation);
				sw.sequence = sw.sequence.extend(need_to_handle.operation, realInputVariables);
				pseudo_sequence_with_index_for_each_statement_in_sequence.add(new PseudoVariable(ps, safe_length));
				safe_length++;
				ps_safe_length.put(ps, safe_length);
			} else {
				break;
			}
		}
		// System.out.println("sequence:" + sw.sequence);
		if (ps_safe_length.get(ps) >= ps.statements.size()) {
			return true;
		}
		return false;
	}

	// private Map<TypedOperation, InfluenceOfBranchChange>
	// EnsureTypedOperationBranchInfluence(Map<TypedOperation,
	// InfluenceOfBranchChange> typed_operation_branch_influence) {
	// Map<TypedOperation, InfluenceOfBranchChange>
	// real_use_typed_operation_branch_influence = new HashMap<TypedOperation,
	// InfluenceOfBranchChange>();
	// for (TypedOperation op : operations) {
	// InfluenceOfBranchChange branch_influence =
	// typed_operation_branch_influence.get(op);
	// if (branch_influence == null) {
	// branch_influence = new InfluenceOfBranchChange();
	// }
	// real_use_typed_operation_branch_influence.put(op, branch_influence);
	// }
	// return real_use_typed_operation_branch_influence;
	// }

	public int Size() {
		return statements.size();
	}

	@Override
	public double GetPunishment(TypedOperation selected_op) {
		Integer count = operation_use_count.get(selected_op);
		if (count != null) {
			return -(count * 1.0);
		}
		return 0.0;
	}

}
