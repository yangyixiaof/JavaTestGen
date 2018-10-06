package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.influence.Reward;
import randoop.generation.date.influence.Rewardable;
import randoop.generation.date.mutation.TypedOperationMutated;
import randoop.generation.date.operation.OperationKind;
import randoop.operation.TypedOperation;
import randoop.sequence.Variable;
import randoop.types.Type;
import randoop.types.TypeTuple;

public class PseudoSequence implements Rewardable {

	PseudoSequenceContainer container = null;

	PseudoVariable headed_variable = null;

	// ArrayList<TypedOperation> operations = null;// these operations only contain
	// object modify operations.
	Map<TypedOperation, Integer> operation_use_count = new HashMap<TypedOperation, Integer>();

	ArrayList<PseudoStatement> statements = new ArrayList<PseudoStatement>();

	HashSet<PseudoSequence> sequences_which_use_headed_variable = new HashSet<PseudoSequence>();

	PseudoSequence previous = null;

	InfluenceOfBranchChange headed_variable_branch_influence = new InfluenceOfBranchChange();

	public PseudoSequence() {// ArrayList<TypedOperation> operations
		// this.operations = operations;
	}

	public void SetContainer(PseudoSequenceContainer container) {
		this.container = container;
	}

	public void SetHeadedVariable(PseudoVariable headed_variable) {
		this.headed_variable = headed_variable;
	}

	// public PseudoSequence(PseudoVariable headed_variable,
	// ArrayList<TypedOperation> operations) {
	// this.headed_variable = headed_variable;
	// this.operations = operations;
	// }

	public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to, ArrayList<String> interested_branch,
			DateGenerator dg) {
		BeforeAfterLinkedSequence result = null;
		// add parameters.
		TypeTuple input_types = selected_to.getInputTypes();
		List<Type> type_list = SequenceGeneratorHelper.TypeTupleToTypeList(input_types);
		List<Type> r_type_list = type_list.subList(1, type_list.size());
		// initialize candidates.
		ArrayList<ArrayList<PseudoVariable>> candidates = SequenceGeneratorHelper.GetMatchedPseudoVariables(r_type_list,
				dg);
		if (candidates.size() == r_type_list.size()) {
			// HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new
			// HashMap<PseudoSequence, PseudoSequence>();
			PseudoSequence ps = this.CopySelfAndCitersInDeepCloneWay(dg);// origin_copied_sequence_map,
			ps.SetPreviousSequence(this);
			ArrayList<PseudoVariable> input_pseudo_variables = new ArrayList<PseudoVariable>();
			SequenceGeneratorHelper.GenerateInputPseudoVariables(candidates, ps.container, input_pseudo_variables,
					r_type_list, dg);
			input_pseudo_variables.add(0, ps.headed_variable);
			LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
			boolean append_to_second_last = false;
			if (this == container.end) {
				TypedOperation last_to = GetLastStatement().operation;
				OperationKind ok = dg.operation_kind.get(last_to);
				if (ok.equals(OperationKind.branch) && !last_to.isConstructorCall()) {
					append_to_second_last = true;
				}
			}
			ps.Append(selected_to, input_pseudo_variables, append_to_second_last);// , dg.pseudo_variable_headed_sequence
			LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
			boolean has_return_value = !selected_to.getOutputType().isVoid();
			result = new BeforeAfterLinkedSequence(selected_to,
					new TypedOperationMutated(ps, has_return_value,
							has_return_value ? new PseudoVariable(ps, ps.Size() - 1) : null, true, ps.headed_variable),
					before_linked_sequence, after_linked_sequence);
		}
		if (result != null) {
			Assert.isTrue(headed_variable != null);
			OperationApplied(selected_to);
			container.mutated_number++;
		}
		return result;
	}

	public void SetPreviousSequence(PseudoSequence pseudo_sequence) {
		this.previous = pseudo_sequence;
	}

	public PseudoVariable Append(TypedOperation operation, ArrayList<PseudoVariable> inputVariables, boolean append_to_second_last) {
		// Map<PseudoVariable, PseudoSequence> class_object_headed_sequence
		PseudoVariable pv = null;
		if (!operation.getOutputType().isVoid()) {
			int soon_be_added_variable_index_of_statement = statements.size();
			pv = new PseudoVariable(this, soon_be_added_variable_index_of_statement);
			// PseudoSequence ps = class_object_headed_sequence.get(pv);
			// Assert.isTrue(ps == null);
			// ps = new PseudoSequence();
			// class_object_headed_sequence.put(pv, ps);
		}
		if (append_to_second_last) {
			statements.add(statements.size()-1, new PseudoStatement(operation, inputVariables));
		} else {
			statements.add(new PseudoStatement(operation, inputVariables));
		}
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
			pv.sequence.sequences_which_use_headed_variable.add(this);
		}
	}

	public PseudoSequence CopySelfInDeepCloneWay(PseudoSequenceContainer container,
			Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map, DateGenerator dg) {// Map<PseudoVariable,
																								// PseudoSequence>
																								// class_object_headed_sequence
		if (origin_copied_sequence_map.containsKey(this)) {
			return origin_copied_sequence_map.get(this);
		}
		boolean container_created = false;
		if (container == null) {
			container = new PseudoSequenceContainer(this.container);
			container_created = true;
		}
		PseudoSequence copy_version = null;
		try {
			copy_version = this.getClass().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (container_created) {
			container.SetEndPseudoSequence(copy_version);
		}
		copy_version.SetContainer(container);
		container.AddPseudoSequence(copy_version);
		origin_copied_sequence_map.put(this, copy_version);
		PseudoVariable copied_headed_variable = headed_variable.CopySelfInDeepCloneWay(container,
				origin_copied_sequence_map, dg);
		copy_version.SetHeadedVariable(copied_headed_variable);
		copy_version.headed_variable_branch_influence = headed_variable_branch_influence.CopySelfInDeepCloneWay();
		dg.pseudo_variable_headed_sequence.put(copied_headed_variable, copy_version);
		// clone statements
		int stmt_index = -1;
		for (PseudoStatement stmt : statements) {
			stmt_index++;
			PseudoStatement copy_stmt = stmt.CopySelfInDeepCloneWay(container, origin_copied_sequence_map, dg);
			copy_version.Append(copy_stmt.operation, copy_stmt.inputVariables, false);// , class_object_headed_sequence
			if (!stmt.operation.getOutputType().isVoid()) {
				PseudoVariable stmt_returned_pv = new PseudoVariable(this, stmt_index);
				if (dg.pseudo_variable_class.containsKey(stmt_returned_pv)) {
					PseudoVariable copied_stmt_returned_pv = stmt_returned_pv.CopySelfInDeepCloneWay(container,
							origin_copied_sequence_map, dg);
					Assert.isTrue(copied_stmt_returned_pv != null);
				}
			}
		}
		return copy_version;
	}

	public PseudoSequence CopySelfAndCitersInDeepCloneWay(DateGenerator dg) {// Map<PseudoVariable, PseudoSequence>
																				// class_object_headed_sequence
		Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new HashMap<PseudoSequence, PseudoSequence>();
		// PseudoSequence new_end =
		container.end.CopySelfInDeepCloneWay(null, origin_copied_sequence_map, dg);
		PseudoSequence copied_this = origin_copied_sequence_map.get(this);
		Assert.isTrue(copied_this != null,
				"copied this is null, but this is not null? " + container.contained_sequences.contains(this)
						+ "#this sequence' content is:" + toString() + "#over!"
						+ "#headed_variable sequence's content is:" + headed_variable.sequence.toString() + "#over!");
		// System.out.println("container.contained_sequences.contains(this):" +
		// container.contained_sequences.contains(this));
		// System.out.println("container.end:" + container.end + "#this:" + this);
		// PseudoSequenceContainer new_container = new_end.container;
		return copied_this;
		// if (origin_copied_sequence_map.containsKey(this)) {
		// return origin_copied_sequence_map.get(this);
		// }
		// PseudoSequence copy_version = null;
		// try {
		// copy_version = this.getClass().getConstructor(ArrayList.class).newInstance();
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.exit(1);
		// }
		// origin_copied_sequence_map.put(this, copy_version);
		// PseudoVariable copied_headed_variable = headed_variable
		// .CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
		// class_object_headed_sequence);
		// copy_version.SetHeadedVariable(copied_headed_variable);
		// // clone statements
		// for (PseudoStatement stmt : statements) {
		// PseudoStatement copy_stmt =
		// stmt.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
		// class_object_headed_sequence);
		// copy_version.Append(copy_stmt.operation, copy_stmt.inputVariables);// ,
		// class_object_headed_sequence
		// }
		// // clone citer sequences
		// Assert.isTrue(headed_variable != null);
		// HashSet<PseudoSequence> sequences_which_use_this_headed_variable =
		// sequences_which_use_headed_variable;
		// for (PseudoSequence citer : sequences_which_use_this_headed_variable) {
		// sequences_which_use_headed_variable.add(
		// citer.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
		// class_object_headed_sequence));
		// }
		// clone influences
		// Set<TypedOperation> tobi_keys = typed_operation_branch_influence.keySet();
		// Iterator<TypedOperation> tobi_itr = tobi_keys.iterator();
		// while (tobi_itr.hasNext()) {
		// TypedOperation to = tobi_itr.next();
		// InfluenceOfBranchChange copied_influence =
		// typed_operation_branch_influence.get(to).CopySelfInDeepCloneWay();
		// copy_version.typed_operation_branch_influence.put(to, copied_influence);
		// }
		// return copy_version;
	}

	public void ReplacePseudoVariableInDependency(DateGenerator dg, PseudoVariable be_replaced,
			PseudoVariable the_replace) {
		PseudoSequence be_replaced_self = dg.pseudo_variable_headed_sequence.get(be_replaced);
		HashSet<PseudoSequence> encountered = new HashSet<PseudoSequence>();
		this.BuildDependency(encountered);
		for (PseudoSequence ps : encountered) {
			if (ps == be_replaced_self) {
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

	public void BuildValidDependantPseudoVariables(HashSet<PseudoVariable> variables,
			HashSet<PseudoSequence> encountered, DateGenerator dg) {
		if (encountered.contains(this)) {
			return;
		}
		encountered.add(this);
		if (!variables.contains(headed_variable)
				&& !headed_variable.sequence.getClass().equals(DisposablePseudoSequence.class)
				&& dg.pseudo_variable_class.containsKey(headed_variable)) {
			variables.add(headed_variable);
		}
		for (PseudoStatement stmt : this.statements) {
			for (PseudoVariable pv : stmt.inputVariables) {
				if (!variables.contains(pv) && !pv.sequence.getClass().equals(DisposablePseudoSequence.class)
						&& dg.pseudo_variable_class.containsKey(pv)) {
					variables.add(pv);
					PseudoSequence pv_sequence = pv.sequence;
					pv_sequence.BuildValidDependantPseudoVariables(variables, encountered, dg);
				}
			}
		}
	}

	protected void BuildDependency(HashSet<PseudoSequence> encountered) {
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
	}

	protected void BuildUsage(HashSet<PseudoSequence> encountered) {
		if (headed_variable == null) {
			Assert.isTrue(statements.size() == 0);
		} else {
			for (PseudoSequence ps_which_uses_this : sequences_which_use_headed_variable) {
				ps_which_uses_this.BuildDependency(encountered);
			}
		}
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

	// @Override
	// public double GetPunishment(TypedOperation selected_op) {
	// Integer count = operation_use_count.get(selected_op);
	// if (count != null) {
	// return -(count * 1.0);
	// }
	// return 0.0;
	// }

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
		return new LinkedSequence(container, sw.sequence.statements,
				pseudo_sequence_with_index_for_each_statement_in_sequence);
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

	public void OperationApplied(TypedOperation to) {
		Integer count = operation_use_count.get(to);
		count = (count == null ? 0 : count) + 1;
		operation_use_count.put(to, count);
	}

	public boolean OperationHasBeenApplied(TypedOperation to) {
		return operation_use_count.containsKey(to);
	}

	public int SizeOfUsers() {
		return sequences_which_use_headed_variable.size();
	}

	public void AddInfluenceOfBranchesForHeadedVariable(Map<String, Influence> all_branches_influences) {
		headed_variable_branch_influence.AddInfluenceOfBranches(all_branches_influences);
	}

	@Override
	public Reward GetReward(ArrayList<String> interested_branch) {
		return headed_variable_branch_influence.GetReward(interested_branch);
	}

	@Override
	public String toString() {
		String result = "ContentStart!";
		for (PseudoStatement stmt : this.statements) {
			result += stmt.operation.getName() + "!";
		}
		result += "ContentOver!";
		return result;
	}

	public PseudoStatement GetLastStatement() {
		return statements.get(statements.size() - 1);
	}

}
