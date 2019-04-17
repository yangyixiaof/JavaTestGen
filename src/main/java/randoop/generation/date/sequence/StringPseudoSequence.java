package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.InfluenceOfTraceCompare;
import randoop.generation.date.mutation.Mutation;
import randoop.generation.date.mutation.StringMutation;
import randoop.generation.date.util.RandomStringUtil;
import randoop.operation.TypedOperation;
import randoop.types.Type;

public class StringPseudoSequence extends PseudoSequence {

	int ready_try_length = 0;

	int current_tried_string_length = 0;

	public static final int MaxSequenceLength = 1;
	private static final int max_range = 255;
	private static final int[] GapRanges = new int[] { 1, 2, 4, 8 };

	// {
	// deprecated block. it seems delta change is unnecessary
	// position delta, based on DeltaChangePseudoSequence this child class only
	// needs to record position information
	// int position = 0;
	// int delta = 0;
	// }

	Random random = new Random(10);

	// position inserted, the position range is 0 ... n, n is the length of content
	// of headed_variable

	// currently, entirely determined, add random factors.
	// optimize the already encountered situations.
	// Map<Integer, TreeMap<Integer, TreeMap<Integer, BeforeAfterLinkedSequence>>>
	// tried_value = new TreeMap<Integer, TreeMap<Integer, TreeMap<Integer,
	// BeforeAfterLinkedSequence>>>();
	// key: position; value: tried gap;
//	Map<Integer, TreeSet<Integer>> tried_gap_value = new TreeMap<Integer, TreeSet<Integer>>();

	// int recent_tried_position = -1;
	// Map<String, ArrayList<Integer>> tried_value_in_order = new TreeMap<String,
	// ArrayList<Integer>>();

	String content = "";

	// boolean is_mutating = true;
//	boolean is_making_plan = true;
	TreeMap<Integer, LinkedList<MutationPlan>> in_trying = new TreeMap<Integer, LinkedList<MutationPlan>>();
	// TreeMap<Integer, TreeSet<String>> already_tried_position_branches = new
	// TreeMap<Integer, TreeSet<String>>();
	// TreeMap<Integer, ArrayList<String>> plan_for_branches = new TreeMap<Integer,
	// ArrayList<String>>();

	BeforeAfterLinkedSequence recent_mutate_result = null;
	boolean recent_mutate_result_set_to_null = false;

	public static final String DefaultRandom = "DefaultRandom";
	public static final String NegativePrefix = "Negative_";
	public static final String PositivePrefix = "Positive_";

	public static final String NegativeRecord = "Negative_Record";
	public static final String PositiveRecord = "Positive_Record";

	public StringPseudoSequence() {// ArrayList<TypedOperation> operations
		super();// operations
		// System.out.println("StringPseudoSequence created!!!");
		// System.exit(1);
	}

	// public StringDeltaChangePseudoSequence(PseudoVariable pv,
	// ArrayList<TypedOperation> operations) {
	// super(pv, operations);
	// }

	// @Override
	// public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to,
	// ArrayList<String> interested_branch, DateGenerator dg) {
	// // fetch content of this headed variable
	// String content = dg.pseudo_variable_content.get(headed_variable);
	// // create input variable of operation
	// ArrayList<PseudoVariable> input_variables = new ArrayList<PseudoVariable>();
	// PseudoSequence copied_this = this.CopySelfAndCitersInDeepCloneWay(dg);
	// input_variables.add(copied_this.headed_variable);
	// int position = -1;
	// int character = 0;
	// // insert situation
	// if
	// (selected_to.toString().startsWith("randoop.generation.date.runtime.DateRuntimeSupport.InsertString"))
	// {
	// if (content == null) {
	// // use the default value
	// } else {
	// // select suitable position (random) and suitable character (random)
	// position = Randomness.nextRandomInt(content.length()+1);
	// character = Randomness.nextRandomInt(200);
	// }
	// } else if
	// (selected_to.toString().startsWith("randoop.generation.date.runtime.DateRuntimeSupport.ChangeDeltaInPositionOfString"))
	// {
	// if (content == null) {
	// // use the default value
	// } else {
	// // select suitable position (random) and suitable character delta (random)
	// // A A B B C
	// // A B C
	// // 10 20 : 30 40
	// // 11 15 : 20 40
	//
	// }
	// } else {
	// Assert.isTrue(false, "Error! Unsupported operation for String: " +
	// selected_to.toString());
	// }
	// PseudoVariable position_pv =
	// PrimitiveGeneratorHelper.CreatePrimitiveVariable(Type.forClass(int.class),
	// position);
	// PseudoVariable character_pv =
	// PrimitiveGeneratorHelper.CreatePrimitiveVariable(Type.forClass(int.class),
	// character);
	// input_variables.add(position_pv);
	// input_variables.add(character_pv);
	// PseudoVariable new_generated = copied_this.Append(selected_to,
	// input_variables);
	// // generate compare sequences
	// LinkedSequence before_linked_sequence =
	// this.container.GenerateLinkedSequence();
	// LinkedSequence after_linked_sequence =
	// copied_this.container.GenerateLinkedSequence();
	// BeforeAfterLinkedSequence result = new BeforeAfterLinkedSequence(selected_to,
	// new DeltaChangeTypedOperationMutated(copied_this, true, new_generated, true,
	// new_generated),
	// before_linked_sequence, after_linked_sequence);
	// return result;
	//
	// // compute whether changing a position is meaningful or not
	// // meaningful: the influenced position has not been exactly covered.
	// // not meaningful: the influenced position has been exactly covered.
	//
	// // record branch whole structure changing:
	// // if changed, e.g, appearance number of one same branch from 4 to 3: it
	// shows that influences really happen.
	// // in this situation, we can not decide whether the changes are over, so the
	// changing position should be less selected.
	// // this branch whole structure changed, decrease the weights of the selected
	// changing position.
	//
	// // all two schemas must be accompanied with trying-times discount
	//
	// // some operations are not in sampling, but in force-executing such as
	// modifying-directly-after-inserting
	// }

//	public void ResetMutateString(DateGenerator dg) {
		// is_mutating = true;
//		is_making_plan = true;
//	}

	public BeforeAfterLinkedSequence MutateString(DateGenerator dg) {
		// String trace_sig = container.trace_info.GetTraceSignature();
		BeforeAfterLinkedSequence result = null;
		LinkedSequence before_linked_sequence = null;
		// if (is_mutating) {
		if (in_trying.isEmpty()) {
			if (content.equals("")) {
				in_trying.put(-1, null);
			} else {
				// Map<Integer, TreeSet<String>> uncovered_position_branches = dg.branch_state
				// .GetNotCoveredAndWithInfluencePositionBranchesPairForTrace(container.trace_info);
				int clen = content.length();
				for (int i = 0; i < clen; i++) {
					LinkedList<MutationPlan> branch_try_times = new LinkedList<MutationPlan>();
					// branch_try_times.add(new MutationPlan(DefaultRandom, DefaultTaskTryTimes));
					branch_try_times.add(new MutationPlan(NegativePrefix, TaskState.Normal));
					branch_try_times.add(new MutationPlan(NegativeRecord, TaskState.Normal));
					branch_try_times.add(new MutationPlan(PositivePrefix, TaskState.Normal));
					branch_try_times.add(new MutationPlan(PositiveRecord, TaskState.Normal));
					// TreeSet<String> branches = uncovered_position_branches == null ? null :
					// uncovered_position_branches.get(i);
					// int bunch_size = (branches != null ? branches.size() : 0) + 1;
					// if (branches != null) {
					// Iterator<String> bitr = branches.iterator();
					// while (bitr.hasNext()) {
					// String branch = bitr.next();
					// branch_try_times.put(branch, OneTryTimes);
					// }
					// }
					// int trying_times = bunch_size * OneTryTimes;
					// System.out.println("trying_times:" + trying_times);
					in_trying.put(i, branch_try_times);
					// ArrayList<String> p_bs = new ArrayList<String>();
					// p_bs.add(DefaultBranch);
					// if (branches != null) {
					// p_bs.addAll(branches);
					// }
					// plan_for_branches.put(i, p_bs);
				}
			}
//			is_making_plan = false;
		}
		recent_mutate_result_set_to_null = false;
		String modified_content = "";
		Set<Integer> pkeys = in_trying.keySet();
		Iterator<Integer> pk_itr = pkeys.iterator();
		Assert.isTrue(pk_itr.hasNext());
		Integer pk = null;
		Integer removed_pk = null;
		// if (pk_itr.hasNext()) {
		pk = pk_itr.next();
		Integer new_gap_v_p = null;
		System.out.println("pk:" + pk + "; recent_mutate_result:" + recent_mutate_result);
		if (pk < 0) {
			before_linked_sequence = this.container.GetLinkedSequence();
			// modified_content = "0000000000";
			// random.nextInt(MaxSequenceLength)+1
			if (current_tried_string_length >= MaxSequenceLength) {
				current_tried_string_length = 0;
				modified_content = null;
				removed_pk = pk;
			} else {
				current_tried_string_length++;
				modified_content = RandomStringUtil.GenerateStringByDefaultChars(current_tried_string_length);
			}
			// recent_mutate_result = null;
			recent_mutate_result_set_to_null = true;
		} else {
			LinkedList<MutationPlan> remain = in_trying.get(pk);
			// ArrayList<String> cared_branches = plan_for_branches.get(pk);
			// Assert.isTrue(remain > 0);
			MutationPlan mp = remain.get(0);
			String cared_mutation = mp.in_try_mutate;
			TaskState r_state = mp.state;
			if (cared_mutation.equals(PositiveRecord) || cared_mutation.equals(NegativeRecord)) {
				String cared_mutation_prefix = cared_mutation.substring(0, cared_mutation.indexOf('_') + 1);
				Assert.isTrue(recent_mutate_result != null);
				InfluenceOfTraceCompare influence = recent_mutate_result.GetInfluence();
				Map<String, Influence> influs = influence.GetInfluences();
				Set<String> influ_keys = influs.keySet();
				Iterator<String> in_itr = influ_keys.iterator();
				while (in_itr.hasNext()) {
					String in_branch = in_itr.next();
					Influence influ = influs.get(in_branch);
					if (influ.GetInfluence() > 0.2) {
						remain.add(new MutationPlan(cared_mutation_prefix + in_branch, TaskState.Normal));
					}
				}
				Assert.isTrue(r_state == TaskState.Normal);
				remain.remove(0);
				recent_mutate_result = null;
			}
			StringBuilder modified_content_builder = new StringBuilder(content);
			mp = remain.get(0);
			cared_mutation = mp.in_try_mutate;
			r_state = mp.state;
//			r_num--;
			// if (cared_mutation.equals(DefaultRandom)) {
			// before_linked_sequence = this.container.GetLinkedSequence();
			// modified_content_builder.setCharAt(pk, (char) random.nextInt(max_range));
			// recent_mutate_result_set_to_null = true;
			// }
			// else
			// {
			String cared_branch = null;
			int direction = 0;
			if (cared_mutation.startsWith(NegativePrefix)) {
				cared_branch = cared_mutation.substring(NegativePrefix.length(), cared_mutation.length());
				direction = -1;
			} else if (cared_mutation.startsWith(PositivePrefix)) {
				cared_branch = cared_mutation.substring(PositivePrefix.length(), cared_mutation.length());
				direction = 1;
			}
			Assert.isTrue(cared_mutation.startsWith(NegativePrefix) || cared_mutation.startsWith(PositivePrefix));
			Assert.isTrue(cared_branch != null);
			if (cared_branch.equals("") || recent_mutate_result == null) {
				Assert.isTrue(recent_mutate_result == null);
				int before_v_p = this.content.charAt(pk);
				int gap_range_index = 0;
				if (!cared_branch.equals("")) {
					gap_range_index++;
					r_state = TaskState.Normal;
				} else {
					r_state = TaskState.Over;
				}
				new_gap_v_p = direction * GapRanges[gap_range_index];
				modified_content_builder.setCharAt(pk, (char) (before_v_p + new_gap_v_p));
				before_linked_sequence = this.container.GetLinkedSequence();
			} else {
				Assert.isTrue(recent_mutate_result != null);
				InfluenceOfTraceCompare influence = recent_mutate_result.GetInfluence();
				StringPseudoSequence before_mapping = (StringPseudoSequence) recent_mutate_result.before_linked_sequence.container
						.FetchStringPseudoSequence();
				Assert.isTrue(before_mapping != null);
				String before_content = before_mapping.content;
				int before_v_p = before_content.charAt(pk);
				StringPseudoSequence after_mapping = recent_mutate_result.after_linked_sequence.container
						.FetchStringPseudoSequence();
				String after_content = after_mapping.content;
				int after_v_p = after_content.charAt(pk);
//				int gap_v_p = after_v_p - before_v_p;
				Mutation mutate = recent_mutate_result.mutation;
				Assert.isTrue(mutate instanceof StringMutation);
				StringMutation string_mutate = (StringMutation)mutate;
				Integer gap_v_p = string_mutate.GetDelta();
				Assert.isTrue(gap_v_p != null);
				Influence influ = influence.GetInfluences().get(cared_branch);
				if (r_state == TaskState.Normal) {
					if (influ.GetInfluence() > 0.2) {
						before_linked_sequence = recent_mutate_result.after_linked_sequence;
						new_gap_v_p = (int) Math.ceil(gap_v_p * 2);
						int modified_v_p = after_v_p + new_gap_v_p;
						modified_content_builder.setCharAt(pk, (char) (modified_v_p));
						int origin_v_p = this.content.charAt(pk);
						if (Math.abs(modified_v_p - origin_v_p) >= (max_range + 1) / 2) {
							r_state = TaskState.Over;
						} else {
							r_state = TaskState.Normal;
						}
					} else {
						r_state = TaskState.LinearConverge;
					}
				}
				if (r_state == TaskState.LinearConverge) {
					new_gap_v_p = gap_v_p / 2;
					if (new_gap_v_p == 0) {
						new_gap_v_p = (random.nextInt((max_range+1)/2) + 1) * direction;
						r_state = TaskState.Over;
						modified_content_builder.setCharAt(pk, (char) (after_v_p + new_gap_v_p));
						before_linked_sequence = recent_mutate_result.after_linked_sequence;
					} else {
						if (influ.GetInfluence() > 0.2) {
							modified_content_builder.setCharAt(pk, (char) (after_v_p + new_gap_v_p));
							before_linked_sequence = recent_mutate_result.after_linked_sequence;
						} else {
							modified_content_builder.setCharAt(pk, (char) (before_v_p + new_gap_v_p));
							before_linked_sequence = recent_mutate_result.before_linked_sequence;
						}
					}
				}
				if (r_state == TaskState.Over) {
					recent_mutate_result_set_to_null = true;
				}
			}
			// }
			if (r_state == TaskState.Over) {
				remain.remove(0);// cared_mutation
			} else {
				mp.state = r_state;
			}
			modified_content = modified_content_builder.toString();
			if (remain.size() == 0) {
				removed_pk = pk;
			}
		}
		// }
		if (removed_pk != null) {
			in_trying.remove(removed_pk);
			// plan_for_branches.remove(removed_pk);
		}
		
		Assert.isTrue(pk != null && modified_content != null && !modified_content.equals(""));
//		if (pk != null && modified_content != null && !modified_content.equals("")) {
		StringPseudoSequence copied_this = (StringPseudoSequence) this.CopySelfAndCitersInDeepCloneWay(dg);
		copied_this.container.SetStringLength(modified_content.length());
		// copied_this.container.SetLogicMapping(this, copied_this);
		TypedOperation to = TypedOperation.createPrimitiveInitialization(Type.forClass(String.class),
				modified_content);
		copied_this.statements.set(0, new PseudoStatement(to, new ArrayList<PseudoVariable>()));
		copied_this.content = modified_content;
		LinkedSequence after_linked_sequence = copied_this.container.GetLinkedSequence();

		// debug
		// System.out.println("Begin");
		// System.out.println("content of after_linked_sequence:" +
		// after_linked_sequence .toString());
		// System.out.println("end sequence of after_linked_sequence:" +
		// copied_this.container.end.toString());
		// System.out.println("end sequence of this:" + this.container.end.toString());
		// System.out.println("End" );

//			StringPseudoSequence before_string_sequence = before_linked_sequence.container.FetchStringPseudoSequence();
		StringMutation string_mutation = null;
		if (pk >= 0) {
//				int modified_gap = modified_content.charAt(pk) - before_string_sequence.content.charAt(pk);
//				TreeSet<Integer> gaps = before_string_sequence.tried_gap_value.get(pk);
//				if (gaps == null) {
//					gaps = new TreeSet<Integer>();
//					before_string_sequence.tried_gap_value.put(pk, gaps);
//				}
//				gaps.add(modified_gap);
			string_mutation = new StringMutation(pk, new_gap_v_p);
		}
		result = new BeforeAfterLinkedSequence(to, string_mutation, before_linked_sequence, after_linked_sequence, in_trying.isEmpty());
		recent_mutate_result = result;
			// if (before_linked_sequence == null) {
			// System.out.println("pk:" + pk);
			// }
//		}
		if (recent_mutate_result_set_to_null) {
			recent_mutate_result = null;
		}
		// if (result == null) {
		// is_mutating = false;
		// }
		// }
		Assert.isTrue(result != null);
		Assert.isTrue(result.before_linked_sequence != null && result.after_linked_sequence != null);
//		else {
//			Assert.isTrue(in_trying.size() == 0);
//		}
		return result;
	}

	// @Override
	// public BeforeAfterLinkedSequence Mutate(TypedOperation selected_to,
	// ArrayList<String> interested_branch,
	// Map<Class<?>, ArrayList<PseudoVariable>> class_pseudo_variable,
	// Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
	// if (selected_to.getInputTypes().size() == 1) {
	// return super.Mutate(selected_to, interested_branch, dg);
	// } else {
	// int next_position = 0;
	// double next_delta = 1.0;
	// double in_use_delta = 0.0;
	// int max_range = 1;
	// if (headed_variable_string != null) {
	// max_range = headed_variable_string.length();
	// }
	// // compute position
	// double rnd = Math.random();
	// if (rnd <= 0.5) {
	// // use current position.
	// next_position = position;
	// } else {
	// // use new position.
	// next_position = Randomness.nextRandomInt(max_range);
	// }
	//
	// BeforeAfterLinkedSequence result = null;
	// ArrayList<PseudoVariable> input_pseudo_variables = new
	// ArrayList<PseudoVariable>();
	// // initialize candidates.
	// HashMap<PseudoSequence, PseudoSequence> origin_copied_sequence_map = new
	// HashMap<PseudoSequence, PseudoSequence>();
	// DeltaChangePseudoSequence ps =
	// (DeltaChangePseudoSequence)this.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
	// class_object_headed_sequence);
	// ps.SetPreviousSequence(this);
	//
	// HashSet<Double> have_tried_delta =
	// have_tried_delta_for_position.get(next_position);
	// if (have_tried_delta == null) {
	// have_tried_delta = new HashSet<Double>();
	// have_tried_delta_for_position.put(next_position, have_tried_delta);
	// }
	//
	// Map<String, Influence> in_use_influence = null;
	// if (delta != 0) {
	// in_use_delta = delta;
	// in_use_influence = all_branches_influences_compared_to_previous;
	// } else {
	// PseudoSequence headed_variable_sequence = this.headed_variable.sequence;
	// if (headed_variable_sequence instanceof DeltaChangePseudoSequence) {
	// DeltaChangePseudoSequence ndcps =
	// (DeltaChangePseudoSequence)headed_variable_sequence;
	// double headed_variable_sequence_delta = ndcps.delta;
	// if (headed_variable_sequence_delta != 0) {
	// in_use_delta = headed_variable_sequence_delta;
	// in_use_influence = ndcps.all_branches_influences_compared_to_previous;
	// }
	// }
	// }
	// double influence = 0.0;
	// if (in_use_influence != null) {
	// influence =
	// SimpleInfluenceComputer.ComputeAveragedInfluence(interested_branch,
	// in_use_influence);
	// }
	// next_delta = SequenceGeneratorHelper.ComputeDelta(in_use_delta, influence,
	// have_tried_delta);
	// ps.delta = next_delta;
	// input_pseudo_variables.add(0, ps.headed_variable);
	// ArrayList<TypedOperation> dp_operations = new ArrayList<TypedOperation>();
	// DisposablePseudoSequence dps = new DisposablePseudoSequence(dp_operations);
	// TypedOperation dp_op =
	// TypedOperation.createPrimitiveInitialization(JavaTypes.DOUBLE_TYPE,
	// next_delta);
	// dp_operations.add(dp_op);
	// PseudoVariable dpv = dps.Append(dp_op, new ArrayList<PseudoVariable>(),
	// class_object_headed_sequence);
	// dps.SetHeadedVariable(dpv);
	// input_pseudo_variables.add(1, dpv);
	// ArrayList<TypedOperation> dp_operations2 = new ArrayList<TypedOperation>();
	// DisposablePseudoSequence dps2 = new DisposablePseudoSequence(dp_operations2);
	// TypedOperation dp_op2 =
	// TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE,
	// next_position);
	// dp_operations2.add(dp_op2);
	// PseudoVariable dpv2 = dps2.Append(dp_op2, new ArrayList<PseudoVariable>(),
	// class_object_headed_sequence);
	// dps.SetHeadedVariable(dpv2);
	// LinkedSequence before_linked_sequence = this.GenerateLinkedSequence();
	// PseudoVariable pv = ps.Append(selected_to, input_pseudo_variables,
	// class_object_headed_sequence);
	// ps.ReplacePseudoVariableInCitesAndCiters(ps, headed_variable, pv);
	// LinkedSequence after_linked_sequence = ps.GenerateLinkedSequence();
	// result = new BeforeAfterLinkedSequence(selected_to, ps.headed_variable, ps,
	// before_linked_sequence,
	// after_linked_sequence);
	// return result;
	// }
	// }

	// @Override
	// public double GetPunishment(TypedOperation selected_op) {
	// if (selected_op.getInputTypes().size() == 1) { // refer to
	// randoop.generation.date.runtime.DateRuntimeSupport.AppendString
	// return super.GetPunishment(selected_op);
	// }
	// return 0.0;
	// }

	public String GetContent() {
		return content;
	}

	public String GetContentWithTheFormOfEachCharIntegerValue() {
		StringBuilder sb = new StringBuilder();
		int cl = content.length();
		for (int i = 0; i < cl; i++) {
			sb.append(((int) content.charAt(i)) + "#");
		}
		return sb.toString();
	}

}

// class TriedChars {
//
// Set<Integer> tried_set = new TreeSet<Integer>();
// // the key is mutated from value
// Map<Integer, Integer> track = new TreeMap<Integer, Integer>();
// Map<Integer, PseudoVariable> tried_crsp_variable = new TreeMap<Integer,
// PseudoVariable>();
//
// }

class MutationPlan {

	String in_try_mutate = null;
	TaskState state = null;

	public MutationPlan(String in_try_mutate, TaskState state) {
		this.in_try_mutate = in_try_mutate;
		this.state = state;
	}

}

enum TaskState {
	Normal,
	LinearConverge,
	Over
}
