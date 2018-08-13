package randoop.generation.date.mutation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import randoop.generation.date.mutation.operation.Insert;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.mutation.operation.PrimitiveBooleanModify;
import randoop.generation.date.mutation.operation.PrimitiveIntegralModify;
import randoop.generation.date.mutation.operation.PrimitiveRealModify;
import randoop.generation.date.mutation.operation.ReferenceModify;
import randoop.generation.date.mutation.operation.Remove;
import randoop.generation.date.mutation.operation.StringAlterModify;
import randoop.generation.date.mutation.operation.StringInsertModify;
import randoop.generation.date.mutation.operation.StringRemoveModify;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.generation.date.sequence.StatementWithIndex;
import randoop.generation.date.sequence.TraceableSequenceFilteredIterator;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedClassOperation;
import randoop.operation.TypedOperation;
import randoop.reflection.RandoopInstantiationError;
import randoop.reflection.TypeInstantiator;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.sequence.Variable;
import randoop.types.Type;
import randoop.types.TypeTuple;
import randoop.util.Log;

public class MutationAnalyzer {

	TraceableSequence sequence = null;
	private TypeInstantiator instantiator;

	public MutationAnalyzer(TraceableSequence sequence, TypeInstantiator instantiator) {
		this.sequence = sequence;
		this.instantiator = instantiator;
	}

	public void GenerateMutationOperations(Collection<TypedOperation> candidates, List<MutationOperation> mutates) {
		// remove
		// GenerateRemoveOperations(mutates);
		// insert
		GenerateInsertOperations(mutates, candidates);
		// modify
		GenerateModifyOperations(mutates);
	}

	/**
	 * 妯′豢 ForwardGenerator 鐨� createNewUniqueSequence
	 *
	 * <p>
	 * 鍏朵腑 instantiator 鐨勬潵婧愭槸 OperationModel -> ComponentManager ->
	 * ForwardGenerator
	 *
	 * <p>
	 * 浼间箮鏄紑灞�纭畾銆佽凯浠ｄ腑涓嶅彉锛燂紙鍠滐級
	 *
	 * @param operation
	 * @return
	 */
	private TypedOperation instantiateGenericType(TypedOperation operation) {
		try {
			return instantiator.instantiate((TypedClassOperation) operation);
		} catch (Throwable e) {
			if (GenInputsAbstract.fail_on_generation_error) {
				if (operation.isMethodCall() || operation.isConstructorCall()) {
					String opName = operation.getOperation().getReflectionObject().toString();
					throw new RandoopInstantiationError(opName, e);
				}
			} else {
				// operationHistory.add(operation, OperationOutcome.SEQUENCE_DISCARDED);
				Log.logLine("Instantiation error for operation " + operation);
				Log.logStackTrace(e);
				System.out.println("Instantiation error for operation " + operation);
				operation = null;
			}
		}
		// failed to instantiate generic
		return null;
	}

	public void GenerateInsertOperations(List<MutationOperation> mutates, Collection<TypedOperation> candidates) {
		// System.out.println("candidates_size:" + candidates.size());
		int i_len = sequence.size();
		for (int i = 0; i <= i_len; i++) {
			Iterator<TypedOperation> citr = candidates.iterator();
			while (citr.hasNext()) {
				TypedOperation optr = citr.next();
				if (optr.isGeneric() || optr.hasWildcardTypes()) {
					if ((optr = instantiateGenericType(optr)) == null) {
						// TODO 鎬庢牱鎶ラ敊濂斤紵
						// throw new DateWtfException();
						continue; // hmmmmmm
					}
				}
				TypeTuple input_type_tuple = optr.getInputTypes();
				int t_size = input_type_tuple.size();
				List<LinkedList<Variable>> suitable = new LinkedList<LinkedList<Variable>>();
				for (int t = 0; t < t_size; t++) {
					suitable.add(new LinkedList<Variable>());
				}
				for (int t = 0; t < t_size; t++) {
					Type input_type = input_type_tuple.get(t);
					for (int j = 0; j < i; j++) {
						Statement j_stmt = sequence.getStatement(j);
						Type o_t = j_stmt.getOutputType();
						if (o_t != null) {
							boolean can_be_assign = input_type.isAssignableFrom(o_t);
							if (can_be_assign) {
								suitable.get(t).add(new Variable(sequence, j));
							}
						}
					}
				}
				boolean all_suit = true;
				// int all_suit_num = 1;
				{
					Iterator<LinkedList<Variable>> sitr = suitable.iterator();
					while (sitr.hasNext()) {
						LinkedList<Variable> v_list = sitr.next();
						if (v_list.size() == 0) {
							all_suit = false;
						}
						// all_suit_num *= v_list.size();
					}
				}
				if (all_suit) {
					Stack<Variable> history = new Stack<Variable>();
					RecursiveToGenerateInsertOperations(i, optr, mutates, suitable, 0, history);
					// List<LinkedList<Variable>> ist_list = new LinkedList<LinkedList<Variable>>();
					// {
					// System.err.println("===== split line =====");
					// int cum = 1;
					// for (int a = 0; a < all_suit_num; a++) {
					// LinkedList<Variable> a_list = new LinkedList<Variable>();
					// int ss = suitable.size();
					// int curr = a;
					// int ts = 0;
					// for (; ts < ss;) {
					// int curr_suite_vars = suitable.get(ts).size();
					// cum *= curr_suite_vars;
					// int opm = all_suit_num / cum;
					// System.err.println("ts:" + ts);
					// System.err.println("curr_suite_vars:" + curr_suite_vars);
					// System.err.println("all_suit_num:" + all_suit_num);
					// System.err.println("cum:" + cum);
					// System.err.println("opm:" + opm);
					// int s_p = curr / opm;
					// int next = curr % opm;
					// curr = next;
					// a_list.add(suitable.get(ts).get(s_p));
					// ++ts;
					// System.err.println("last end ts:" + ts);
					// }
					// ist_list.add(a_list);
					// }
					// }
					// {
					// Iterator<LinkedList<Variable>> istitr = ist_list.iterator();
					// while (istitr.hasNext()) {
					// Insert ist = new Insert(sequence, i, optr, istitr.next());
					// mutates.add(ist);
					// }
					// }
				}
			}
		}
	}

	private void RecursiveToGenerateInsertOperations(int stmt_idx, TypedOperation optr, List<MutationOperation> mutates,
			List<LinkedList<Variable>> suitable, int index1, Stack<Variable> history) {
		if (index1 == suitable.size()) {
			LinkedList<Variable> a_list = new LinkedList<Variable>();
			Iterator<Variable> hitr = history.iterator();
			while (hitr.hasNext()) {
				Variable v = hitr.next();
				a_list.add(v);
			}
			Insert ist = new Insert(sequence, stmt_idx, optr, a_list);
			mutates.add(ist);
			return;
		} else {
			LinkedList<Variable> index1_vars = suitable.get(index1);
			for (int i = 0; i < index1_vars.size(); i++) {
				history.push(index1_vars.get(i));
				RecursiveToGenerateInsertOperations(stmt_idx, optr, mutates, suitable, index1 + 1, history);
				history.pop();
			}
		}
	}

	public void GenerateModifyOperations(List<MutationOperation> mutates) {
		// GenerateModifyReferenceOperations(mutates);
		GenerateModifyPrimitiveIntegralOperations(mutates);
		GenerateModifyPrimitiveRealOperations(mutates);
		GenerateModifyPrimitiveBooleanOperations(mutates);
		GenerateModifyStringOperations(mutates);
	}

	public void GenerateModifyReferenceOperations(List<MutationOperation> mutates) {
		TraceableSequenceFilteredIterator tsfi = new TraceableSequenceFilteredIterator(sequence);
		while (tsfi.HasNext()) {
			StatementWithIndex swi = tsfi.Next();
			int i = swi.GetIndex();
			Statement stmt = sequence.getStatement(i);
			TypeTuple input_types = stmt.getInputTypes();
			List<Sequence.RelativeNegativeIndex> params = stmt.getInputs();
			int it_len = input_types.size();
			for (int it = 0; it < it_len; it++) {
				Sequence.RelativeNegativeIndex rni = params.get(it);
				int rni_index = rni.index;
				Type tp = input_types.get(it);
				int j_len = i;
				for (int j = 0; j < j_len; j++) {
					Statement j_stmt = sequence.getStatement(j);
					Type o_t = j_stmt.getOutputType();
					if (o_t != null) {
						int j_rni_index = j - i;
						if (j_rni_index != rni_index && tp.isAssignableFrom(o_t)) {
							mutates.add(new ReferenceModify(sequence, i, it, new Variable(sequence, j)));
						}
					}
				}
			}
		}
	}

	public void GenerateModifyPrimitiveIntegralOperations(List<MutationOperation> mutates) {
		TraceableSequenceFilteredIterator tsfi = new TraceableSequenceFilteredIterator(sequence);
		while (tsfi.HasNext()) {
			StatementWithIndex swi = tsfi.Next();
			int i = swi.GetIndex();
			Statement stmt = sequence.getStatement(i);
			TypeTuple input_types = stmt.getInputTypes();
			int it_len = input_types.size();
			for (int it = 0; it < it_len; it++) {
				Type tp = input_types.get(it);
				if (tp.isPrimitive() && (tp.getCanonicalName().equals("java.lang.Character")
						|| tp.getCanonicalName().equals("char") || tp.getCanonicalName().equals("java.lang.Byte")
						|| tp.getCanonicalName().equals("byte") || tp.getCanonicalName().equals("java.lang.Short")
						|| tp.getCanonicalName().equals("short") || tp.getCanonicalName().equals("java.lang.Integer")
						|| tp.getCanonicalName().equals("int") || tp.getCanonicalName().equals("java.lang.Long")
						|| tp.getCanonicalName().equals("long"))) {
					PrimitiveIntegralModify p_m_neg = new PrimitiveIntegralModify(sequence, i, it, -1);
					PrimitiveIntegralModify p_m_pos = new PrimitiveIntegralModify(sequence, i, it, 1);
					mutates.add(p_m_neg);
					mutates.add(p_m_pos);
				}
			}
		}
	}

	public void GenerateModifyPrimitiveRealOperations(List<MutationOperation> mutates) {
		TraceableSequenceFilteredIterator tsfi = new TraceableSequenceFilteredIterator(sequence);
		while (tsfi.HasNext()) {
			StatementWithIndex swi = tsfi.Next();
			int i = swi.GetIndex();
			Statement stmt = sequence.getStatement(i);
			TypeTuple input_types = stmt.getInputTypes();
			int it_len = input_types.size();
			for (int it = 0; it < it_len; it++) {
				Type tp = input_types.get(it);
				if (tp.isPrimitive() && (tp.getCanonicalName().equals("float") || tp.getCanonicalName().equals("Float")
						|| tp.getCanonicalName().equals("double") || tp.getCanonicalName().equals("Double"))) {
					PrimitiveRealModify p_m_neg = new PrimitiveRealModify(sequence, i, it, -1.0);
					PrimitiveRealModify p_m_pos = new PrimitiveRealModify(sequence, i, it, 1.0);
					mutates.add(p_m_neg);
					mutates.add(p_m_pos);
				}
			}
		}
	}

	public void GenerateModifyPrimitiveBooleanOperations(List<MutationOperation> mutates) {
		TraceableSequenceFilteredIterator tsfi = new TraceableSequenceFilteredIterator(sequence);
		while (tsfi.HasNext()) {
			StatementWithIndex swi = tsfi.Next();
			int i = swi.GetIndex();
			Statement stmt = sequence.getStatement(i);
			TypeTuple input_types = stmt.getInputTypes();
			int it_len = input_types.size();
			for (int it = 0; it < it_len; it++) {
				Type tp = input_types.get(it);
				if (tp.isPrimitive() && (tp.getCanonicalName().equals("java.lang.Boolean")
						|| tp.getCanonicalName().equals("boolean"))) {
					PrimitiveBooleanModify p_m_neg = new PrimitiveBooleanModify(sequence, i, it);
					PrimitiveBooleanModify p_m_pos = new PrimitiveBooleanModify(sequence, i, it);
					mutates.add(p_m_neg);
					mutates.add(p_m_pos);
				}
			}
		}
	}

	public void GenerateModifyStringOperations(List<MutationOperation> mutates) {
		TraceableSequenceFilteredIterator tsfi = new TraceableSequenceFilteredIterator(sequence);
		while (tsfi.HasNext()) {
			StatementWithIndex swi = tsfi.Next();
			int i = swi.GetIndex();
			Statement stmt = sequence.getStatement(i);
			List<Sequence.RelativeNegativeIndex> input_variables = stmt.getInputs();
			TypeTuple input_types = stmt.getInputTypes();
			int it_len = input_types.size();
			for (int it = 0; it < it_len; it++) {
				Type tp = input_types.get(it);
				if (tp.isReferenceType()) {
					if (tp != null) {
						if (tp.getCanonicalName().equals("java.lang.String")) {
							Sequence.RelativeNegativeIndex rni = input_variables.get(it);
							int input_generate_statement_index = i + rni.index;
							Statement input_generate_statement = sequence.getStatement(input_generate_statement_index);
							if (input_generate_statement.isNonreceivingInitialization()) {
								Object string_value = input_generate_statement.getValue();
								if ((string_value != null) && (string_value instanceof String)) {
									String str_val = (String) string_value;
									System.out.println("String constant value:" + str_val);
									// generate remove char mutations
									int str_len = str_val.length();
									for (int c = 0; c < str_len; c++) {
										mutates.add(new StringRemoveModify(sequence, i, it, c));
									}
									// generate insert char mutations
									for (int c = 0; c <= str_len; c++) {
										mutates.add(new StringInsertModify(sequence, i, it, c));
									}
									// generate modify char mutations
									for (int c = 0; c < str_len; c++) {
										mutates.add(new StringAlterModify(sequence, i, it, c, -1));
										mutates.add(new StringAlterModify(sequence, i, it, c, 1));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void GenerateRemoveOperations(List<MutationOperation> mutates) {
		TraceableSequenceFilteredIterator tsfi = new TraceableSequenceFilteredIterator(sequence);
		while (tsfi.HasNext()) {
			StatementWithIndex swi = tsfi.Next();
			Remove rmv = new Remove(sequence, swi.GetIndex());
			mutates.add(rmv);
		}
	}
}
