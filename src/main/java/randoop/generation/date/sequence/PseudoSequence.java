package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import randoop.generation.date.influence.Penalizable;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Variable;
import randoop.types.Type;
import randoop.types.TypeTuple;

public class PseudoSequence implements Penalizable {

	PseudoVariable headed_variable = null;

	ArrayList<TypedOperation> operations = null;// these operations only contain object modify operations.
	Map<TypedOperation, Integer> operation_use_count = new HashMap<TypedOperation, Integer>();

	ArrayList<PseudoStatement> statements = new ArrayList<PseudoStatement>();

	HashSet<PseudoSequence> sequences_which_use_this_sequence = new HashSet<PseudoSequence>();

	public PseudoSequence(ArrayList<TypedOperation> operations) {
		this.operations = operations;
	}

	public void SetHeadedVariable(PseudoVariable headed_variable) {
		this.headed_variable = headed_variable;
	}

	public PseudoSequence(PseudoVariable headed_variable, ArrayList<TypedOperation> operations) {
		this.headed_variable = headed_variable;
		this.operations = operations;
	}

	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable,
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
		if (input_pseudo_variables.size() == type_list.size()) {
			HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
			PseudoSequence ps = this.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
					class_object_headed_sequence);
			input_pseudo_variables.add(0, ps.headed_variable);
			LinkedSequence before_linked_sequence = ps.GenerateLinkedSequence();
			ps.Append(selected_to, input_pseudo_variables, class_object_headed_sequence);
			LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
			result = new BeforeAfterLinkedSequence(selected_to, ps.headed_variable, before_linked_sequence,
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
			AddReferenceToInUseSequence(pv.sequence);
		}
	}

	private void AddReferenceToInUseSequence(PseudoSequence in_use_sequence) {
		in_use_sequence.sequences_which_use_this_sequence.add(this);
	}

	public PseudoSequence CopySelfInDeepCloneWay(Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
		if (origin_copied_sequence_map.containsKey(this)) {
			return origin_copied_sequence_map.get(this);
		}
		PseudoVariable copied_headed_variable = headed_variable.CopySelfInDeepCloneWay(origin_copied_sequence_map,
				class_object_headed_sequence);
		PseudoSequence copy_version = new PseudoSequence(copied_headed_variable, operations);
		origin_copied_sequence_map.put(this, copy_version);
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
		PseudoVariable copied_headed_variable = headed_variable
				.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map, class_object_headed_sequence);
		PseudoSequence copy_version = new PseudoSequence(copied_headed_variable, operations);
		origin_copied_sequence_map.put(this, copy_version);
		// clone statements
		for (PseudoStatement stmt : statements) {
			PseudoStatement copy_stmt = stmt.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
					class_object_headed_sequence);
			copy_version.Append(copy_stmt.operation, copy_stmt.inputVariables, class_object_headed_sequence);
		}
		// clone citer sequences
		for (PseudoSequence citer : sequences_which_use_this_sequence) {
			copy_version.sequences_which_use_this_sequence.add(
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

	private void BuildDependency(PseudoSequence ps, HashSet<PseudoSequence> depends,
			HashSet<PseudoSequence> encountered) {
		if (encountered.contains(this)) {
			return;
		}
		encountered.add(this);
		for (PseudoStatement stmt : ps.statements) {
			for (PseudoVariable pv : stmt.inputVariables) {
				PseudoSequence pv_sequence = pv.sequence;
				depends.add(pv_sequence);
				BuildDependency(pv.sequence, depends, encountered);
			}
		}
		for (PseudoSequence ps_which_uses_this : sequences_which_use_this_sequence) {
			depends.add(ps_which_uses_this);
			BuildDependency(ps_which_uses_this, depends, encountered);
		}
	}

	public LinkedSequence GenerateLinkedSequence() {
		Sequence sequence = new Sequence();
		ArrayList<PseudoVariable> pseudo_sequence_with_index_for_each_statement_in_sequence = new ArrayList<PseudoVariable>();
		HashMap<PseudoSequence, TreeMap<Integer, Integer>> pseudo_sequence_index_to_sequence_index = new HashMap<PseudoSequence, TreeMap<Integer, Integer>>();
		// build all dependency set for this pseudo sequence
		HashSet<PseudoSequence> depends = new HashSet<PseudoSequence>();
		HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
		BuildDependency(this, depends, encountered);
		HashMap<PseudoSequence, Integer> ps_safe_length = new HashMap<PseudoSequence, Integer>();
		for (PseudoSequence depend : depends) {
			ps_safe_length.put(depend, 0);
			pseudo_sequence_index_to_sequence_index.put(depend, new TreeMap<Integer, Integer>());
		}
		while (depends.size() > 0) {
			HashSet<PseudoSequence> need_to_remove = new HashSet<PseudoSequence>();
			Iterator<PseudoSequence> d_itr = depends.iterator();
			while (d_itr.hasNext()) {
				PseudoSequence ps = d_itr.next();
				int safe_length = ps_safe_length.get(ps);
				PseudoStatement need_to_handle = ps.statements.get(safe_length);
				ArrayList<PseudoVariable> ivs = need_to_handle.inputVariables;
				Iterator<PseudoVariable> iv_itr = ivs.iterator();
				boolean ensure_safe = true;
				ArrayList<Variable> realInputVariables = new ArrayList<Variable>();
				while (iv_itr.hasNext()) {
					PseudoVariable pv = iv_itr.next();
					realInputVariables.add(sequence
							.getVariable(pseudo_sequence_index_to_sequence_index.get(pv.sequence).get(pv.index)));
					if (pv.index >= ps_safe_length.get(pv.sequence)) {
						ensure_safe = false;
						break;
					}
				}
				if (ensure_safe) {
					// handle real Sequence, append to last of the real Sequence.
					int recently_add_index = sequence.size();
					pseudo_sequence_index_to_sequence_index.get(ps).put(safe_length, recently_add_index);
					sequence.extend(need_to_handle.operation, realInputVariables);
					pseudo_sequence_with_index_for_each_statement_in_sequence.add(new PseudoVariable(ps, safe_length));
					safe_length++;
					ps_safe_length.put(ps, safe_length);

					if (safe_length >= ps.statements.size()) {
						need_to_remove.add(ps);
					}
				}
			}
			depends.removeAll(need_to_remove);
		}
		return new LinkedSequence(sequence.statements, pseudo_sequence_with_index_for_each_statement_in_sequence);
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

	public int SizeOfCiters() {
		return sequences_which_use_this_sequence.size();
	}

	@Override
	public double GetPunishment(TypedOperation selected_op) {
		Integer count = operation_use_count.get(selected_op);
		if (count != null) {
			return -10.0 * (count * 1.0);
		}
		return 0.0;
	}

}
