package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

	// int ready_try_length = 0;

	// int current_tried_string_length = 0;

	public static final int MAX_SEED_LENGTH = 1;
	
	// public static final int MaxSequenceLength = 1;
	private static final int max_range = 65535;
	private static final int[] GapRanges = new int[] { 1, 17, 59, 113 };

	private int position_random_times = 10;
	private int fixed_length_random_times = 10;

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
	// Map<Integer, TreeSet<Integer>> tried_gap_value = new TreeMap<Integer,
	// TreeSet<Integer>>();

	// int recent_tried_position = -1;
	// Map<String, ArrayList<Integer>> tried_value_in_order = new TreeMap<String,
	// ArrayList<Integer>>();

	String content = "";

	// boolean is_mutating = true;
	// boolean is_making_plan = true;
	// TreeMap<Integer, LinkedList<MutationPlan>> in_trying = new TreeMap<Integer,
	// LinkedList<MutationPlan>>();
	LinkedList<MutationPlan> in_trying = new LinkedList<MutationPlan>();
	// TreeMap<Integer, TreeSet<String>> already_tried_position_branches = new
	// TreeMap<Integer, TreeSet<String>>();
	// TreeMap<Integer, ArrayList<String>> plan_for_branches = new TreeMap<Integer,
	// ArrayList<String>>();

	HashMap<String, HashMap<PseudoSequenceContainer, Double>> linear_solve_seeds = new HashMap<String, HashMap<PseudoSequenceContainer, Double>>();

	String current_content = null;
	LinkedSequence current_linked_sequence = null;
	BeforeAfterLinkedSequence recent_mutate_result = null;
	// boolean recent_mutate_result_set_to_null = false;

	// Map<String, TreeSet<String>> cared_branch_encountered_new_branches = new
	// TreeMap<String, TreeSet<String>>();

	// public static final String DefaultRandom = "DefaultRandom";
	//
	// public static final String FixedLengthRandom = "FixedLengthRandom";
	//
	// public static final String PositionRandomMutation = "PositionRandomMutation";
	//
	// public static final String NegativePrefix = "Negative_";
	// public static final String PositivePrefix = "Positive_";
	//
	// public static final String Probe = "Probe";
	//
	// public static final String NegativeProbe = NegativePrefix + Probe;
	// public static final String PositiveProbe = PositivePrefix + Probe;
	//
	// public static final String Record = "Record";
	//
	// public static final String NegativeRecord = NegativePrefix + Record;
	// public static final String PositiveRecord = PositivePrefix + Record;

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

	// public void ResetMutateString(DateGenerator dg) {
	// is_mutating = true;
	// is_making_plan = true;
	// }

	private void GeneratePositionRandomMutationPlans(String m_content) {
		int clen = m_content.length();
		int number = Math.min(clen, position_random_times);
		for (int j = 0; j < number; j++) {
			int pos = random.nextInt(clen);
			in_trying.add(0, new PositionRandomMutationPlan(TaskKind.PositionRandomMutation, TaskState.Normal, pos));
		}
	}
	
	private void GeneratePositionLinearMutationPlans(String modified_content) {
		int clen = modified_content.length();
		int number = Math.min(clen, position_random_times);
		for (int j = 0; j < number; j++) {
			int pos = random.nextInt(clen);
			in_trying.add(0, new ProbeMutationPlan(TaskKind.PositiveProbe, TaskState.Normal, pos));
			in_trying.add(0, new ProbeMutationPlan(TaskKind.NegativeProbe, TaskState.Normal, pos));
		}
	}

	public BeforeAfterLinkedSequence MutateString(DateGenerator dg) {
		BeforeAfterLinkedSequence result = null;
		LinkedSequence before_linked_sequence = null;
		// if (is_mutating) {
		if (in_trying.isEmpty()) {
			if (content.equals("")) {
				in_trying.add(new RandomMutationPlan(TaskKind.DefaultRandom, TaskState.Normal, 10));
			} else {
				// Map<Integer, TreeSet<String>> uncovered_position_branches = dg.branch_state
				// .GetNotCoveredAndWithInfluencePositionBranchesPairForTrace(container.trace_info);
				GeneratePositionRandomMutationPlans(content);
				for (int i = 0; i < fixed_length_random_times; i++) {
					in_trying.add(new FixedLengthRandomMutationPlan(TaskKind.FixedLengthRandom, TaskState.Normal, i));
				}
				// for (int i = 0; i < clen; i++) {
				// LinkedList<MutationPlan> branch_try_times = new LinkedList<MutationPlan>();
				// // branch_try_times.add(new MutationPlan(DefaultRandom,
				// DefaultTaskTryTimes));
				// branch_try_times.add(new ProbeMutationPlan(NegativeProbe, TaskState.Normal,
				// 0));
				// branch_try_times.add(new MutationPlan(NegativeRecord, TaskState.Normal));
				// branch_try_times.add(new ProbeMutationPlan(PositiveProbe, TaskState.Normal,
				// 0));
				// branch_try_times.add(new MutationPlan(PositiveRecord, TaskState.Normal));
				// // TreeSet<String> branches = uncovered_position_branches == null ? null :
				// // uncovered_position_branches.get(i);
				// // int bunch_size = (branches != null ? branches.size() : 0) + 1;
				// // if (branches != null) {
				// // Iterator<String> bitr = branches.iterator();
				// // while (bitr.hasNext()) {
				// // String branch = bitr.next();
				// // branch_try_times.put(branch, OneTryTimes);
				// // }
				// // }
				// // int trying_times = bunch_size * OneTryTimes;
				// // System.out.println("trying_times:" + trying_times);
				// in_trying.put(i, branch_try_times);
				// // ArrayList<String> p_bs = new ArrayList<String>();
				// // p_bs.add(DefaultBranch);
				// // if (branches != null) {
				// // p_bs.addAll(branches);
				// // }
				// // plan_for_branches.put(i, p_bs);
				// }
			}
			// is_making_plan = false;
		}
		MutationPlan mp = in_trying.get(0);
		System.out.println("############## " + "MutationType:" + mp.getClass() + " ##############");
		before_linked_sequence = this.container.GetLinkedSequence();
		String modified_content = null;
		int r_direct = 0;
		TaskState r_pmp_state = null;
		int r_pmp_pos = -1;
		StringMutation string_mutation = null;
		boolean set_current = false;
		switch (mp.mutate_type) {
		case DefaultRandom:
			Assert.isTrue(mp instanceof RandomMutationPlan);
			RandomMutationPlan rmp = (RandomMutationPlan) mp;
			int len = random.nextInt(dg.curr_seed_length) + 1;
			modified_content = RandomStringUtil.GenerateStringByDefaultChars(len);
			set_current = true;
			rmp.random_mutate_time--;
			if (rmp.random_mutate_time == 0) {
				in_trying.remove(0);
			}
			GeneratePositionLinearMutationPlans(modified_content);
			break;
		case FixedLengthRandom:
			FixedLengthRandomMutationPlan flrmp = (FixedLengthRandomMutationPlan) mp;
			modified_content = RandomStringUtil.GenerateStringByDefaultChars(flrmp.fixed_length);
			set_current = true;
			in_trying.remove(0);
			GeneratePositionLinearMutationPlans(modified_content);
			break;
		case PositionRandomMutation:
			PositionRandomMutationPlan prmp = (PositionRandomMutationPlan) mp;
			int pos = prmp.position;
			StringBuilder sb = new StringBuilder(current_content);
			sb.setCharAt(pos, (char) (random.nextInt(max_range) + sb.charAt(pos)));
			modified_content = sb.toString();
			set_current = true;
			in_trying.remove(0);
			in_trying.add(0, new ProbeMutationPlan(TaskKind.PositiveProbe, TaskState.Normal, pos));
			in_trying.add(0, new ProbeMutationPlan(TaskKind.NegativeProbe, TaskState.Normal, pos));
			break;
		case NegativeProbe:
			ProbeMutationPlan npmp = (ProbeMutationPlan) mp;
			int np_pos = npmp.position;
			StringBuilder np_builder = new StringBuilder(current_content);
			np_builder.setCharAt(np_pos, (char) (np_builder.charAt(np_pos) + -1 * GapRanges[0]));
			string_mutation = new StringMutation(np_pos, -1 * GapRanges[0]);
			modified_content = np_builder.toString();
			before_linked_sequence = current_linked_sequence;
			in_trying.remove(0);
			in_trying.add(0, new ProbeMutationPlan(TaskKind.NegativeRecord, TaskState.Normal, np_pos));
			break;
		case PositiveProbe:
			ProbeMutationPlan ppmp = (ProbeMutationPlan) mp;
			int pp_pos = ppmp.position;
			StringBuilder pp_builder = new StringBuilder(current_content);
			pp_builder.setCharAt(pp_pos, (char) (pp_builder.charAt(pp_pos) + 1 * GapRanges[0]));
			string_mutation = new StringMutation(pp_pos, 1 * GapRanges[0]);
			modified_content = pp_builder.toString();
			before_linked_sequence = current_linked_sequence;
			in_trying.remove(0);
			in_trying.add(0, new ProbeMutationPlan(TaskKind.PositiveRecord, TaskState.Normal, pp_pos));
			break;
		case NegativeRecord:
			ProbeMutationPlan r_pmp = (ProbeMutationPlan) mp;
			r_direct = -1;
			r_pmp_state = r_pmp.state;
			r_pmp_pos = r_pmp.position;
		case PositiveRecord:
			ProbeMutationPlan pr_pmp = (ProbeMutationPlan) mp;
			if (r_direct == 0) {
				r_direct = 1;
			}
			r_pmp_state = pr_pmp.state;
			r_pmp_pos = pr_pmp.position;
			in_trying.remove(0);
			InfluenceOfTraceCompare r_influence = recent_mutate_result.GetInfluence();
			Map<String, Influence> influs = r_influence.GetInfluences();
			Set<String> influ_keys = influs.keySet();
			Iterator<String> in_itr = influ_keys.iterator();
			while (in_itr.hasNext()) {
				String in_branch = in_itr.next();
				Influence influ = influs.get(in_branch);
//				System.out.println("influ:" + influ.GetInfluence());
				if (!influ.IsHitHappen() && !influ.IsFlipHappen() && influ.GetInfluence() > 0.2) {
					Assert.isTrue(r_pmp_state == TaskState.Normal);
					in_trying.add(0, new BranchGuidedMutationPlan(TaskKind.BranchMutation, TaskState.Normal, r_pmp_pos,
							in_branch, r_direct));
				}
			}
			break;
		case BranchMutation:
			BranchGuidedMutationPlan bgmp = (BranchGuidedMutationPlan) mp;
			TaskState r_state = bgmp.state;
			int r_pos = bgmp.position;
			Assert.isTrue(recent_mutate_result != null);
			// cared_branch_encountered_new_branches.get(cared_branch)
			// .add(recent_mutate_result.before_linked_sequence.container.GetTraceInfo().GetTraceSignature());
			InfluenceOfTraceCompare influence = recent_mutate_result.GetInfluence();
			// handle other logic
			StringPseudoSequence before_mapping = (StringPseudoSequence) recent_mutate_result.before_linked_sequence.container
					.FetchStringPseudoSequence();
			Assert.isTrue(before_mapping != null);
			String before_content = before_mapping.content;
			int before_v_p = before_content.charAt(r_pos);
			StringPseudoSequence after_mapping = recent_mutate_result.after_linked_sequence.container
					.FetchStringPseudoSequence();
			String after_content = after_mapping.content;
			int after_v_p = after_content.charAt(r_pos);
			Mutation mutate = recent_mutate_result.mutation;
			Assert.isTrue(mutate instanceof StringMutation);
			StringMutation string_mutate = (StringMutation) mutate;
			Integer gap_v_p = string_mutate.GetDelta();
			Assert.isTrue(gap_v_p != null);
			Influence influ = influence.GetInfluences().get(bgmp.cared_branch);
			// handle mutate logic
			StringBuilder mcb = new StringBuilder(current_content);
			int new_gap_v_p = -1;
			if (r_state == TaskState.Normal) {
				if (influ != null && influ.GetInfluence() > 0.2 && !influ.IsFlipHappen()) {
					before_linked_sequence = recent_mutate_result.after_linked_sequence;
					new_gap_v_p = (int) Math.ceil(gap_v_p * 2);
					int modified_v_p = after_v_p + new_gap_v_p;
					mcb.setCharAt(r_pos, (char) (modified_v_p));
					int origin_v_p = this.current_content.charAt(r_pos);
					if (Math.abs(modified_v_p - origin_v_p) >= 4095) {
						r_state = TaskState.Over;
						in_trying.remove(0);
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
					new_gap_v_p = (random.nextInt((max_range + 1) / 2) + 1) * bgmp.direction;
					r_state = TaskState.Over;
					mcb.setCharAt(r_pos, (char) (after_v_p + new_gap_v_p));
					// is_random_mutating = true;
					before_linked_sequence = recent_mutate_result.after_linked_sequence;
					in_trying.remove(0);
				} else {
					if (influ != null && influ.GetInfluence() > 0.2 && !influ.IsFlipHappen()) {
						mcb.setCharAt(r_pos, (char) (after_v_p + new_gap_v_p));
						before_linked_sequence = recent_mutate_result.after_linked_sequence;
					} else {
						mcb.setCharAt(r_pos, (char) (before_v_p + new_gap_v_p));
						before_linked_sequence = recent_mutate_result.before_linked_sequence;
					}
				}
			}
			string_mutation = new StringMutation(r_pos, new_gap_v_p);
			modified_content = mcb.toString();
			break;
		default:
			new Exception("Strange mutation plan!!").printStackTrace();
			System.exit(1);
			break;
		}
		if (modified_content != null) {
			StringPseudoSequence copied_this = (StringPseudoSequence) this.CopySelfAndCitersInDeepCloneWay(dg);
			Assert.isTrue(modified_content != null);
			TypedOperation to = TypedOperation.createPrimitiveInitialization(Type.forClass(String.class), modified_content);
			copied_this.statements.set(0, new PseudoStatement(to, new ArrayList<PseudoVariable>()));
			copied_this.content = modified_content;
			LinkedSequence after_linked_sequence = copied_this.container.GetLinkedSequence();
			result = new BeforeAfterLinkedSequence(to, string_mutation, before_linked_sequence, after_linked_sequence,
					in_trying.isEmpty(), null); // is_random_mutating ? new RandomMutationInfo(1.0) :
			recent_mutate_result = result;
			Assert.isTrue(result.before_linked_sequence != null && result.after_linked_sequence != null);
			if (set_current) {
				current_content = modified_content;
				current_linked_sequence = after_linked_sequence;
			}
		}
		return result;
	}

	// private int HandleSeeds(DateGenerator dg) {
	// int num_of_interests = 0;
	// // String cts = container.trace_info.GetTraceSignature();
	// Set<String> lkeys = linear_solve_seeds.keySet();
	// Iterator<String> lk_itr = lkeys.iterator();
	// while (lk_itr.hasNext()) {
	// String lk = lk_itr.next();
	// // boolean first_encounter = dg.branch_state.StateFirstEncountered(lk);
	// HashMap<PseudoSequenceContainer, Double> c_gaps = linear_solve_seeds.get(lk);
	// ArrayList<PseudoSequenceContainer> sorted_cs =
	// SortUtil.SortMapByValue(c_gaps);
	// PseudoSequenceContainer gap_smallest_cs = sorted_cs.get(0);
	// String scs_content = gap_smallest_cs.string_sequence.content;
	// if (dg.content_container_map.containsKey(scs_content)) {
	// continue;
	// }
	// // if (lk.equals(cts)) {
	// // Assert.isTrue(!first_encounter);
	// // if (linear_solve_seeds.size() == 1) {
	// if (gap_smallest_cs != container) {
	// num_of_interests += SeedHelper.SeedIsInteresting(dg, gap_smallest_cs, 1.0);
	// }
	// // }
	// // }
	// // else {
	// // if (first_encounter) {
	// // num_of_interests += SeedHelper.SeedIsInteresting(dg, gap_smallest_cs,
	// // first_encounter, 1.0);
	// // }
	// // }
	// }
	// return num_of_interests;
	// }

	// private void HandleGeneratedStringState(PseudoSequenceContainer p_ctr, double
	// gap) {
	// String t_sig = p_ctr.trace_info.GetTraceSignature();
	// HashMap<PseudoSequenceContainer, Double> c_g = linear_solve_seeds.get(t_sig);
	// if (c_g == null) {
	// c_g = new HashMap<PseudoSequenceContainer, Double>();
	// linear_solve_seeds.put(t_sig, c_g);
	// }
	// c_g.put(p_ctr, gap);
	// }

	// private void HandleBranchState(BranchStateSummary bss, String cared_branch,
	// TraceInfo prev_trace,
	// TraceInfo curr_trace, Influence influ) {
	// String pt = prev_trace.GetTraceSignature();
	// String ct = curr_trace.GetTraceSignature();
	// if (!pt.equals(ct)) {
	// Integer pst = prev_trace.GetBranchStateForValueOfBranch(cared_branch);
	// Integer cst = curr_trace.GetBranchStateForValueOfBranch(cared_branch);
	// if (pst != null && cst != null && pst.intValue() != cst.intValue()) {
	// bss.CoveredBranchStateUpdate2(pt, cared_branch, pst, cst);
	// bss.CoveredBranchStateUpdate2(ct, cared_branch, cst, pst);
	// }
	// }
	// }

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

	TaskKind mutate_type = null;
	TaskState state = null;

	public MutationPlan(TaskKind mutate_type, TaskState state) {
		this.mutate_type = mutate_type;
		this.state = state;
	}

}

class ProbeMutationPlan extends MutationPlan {

	int position = -1;

	public ProbeMutationPlan(TaskKind mutate_type, TaskState state, int position) {
		super(mutate_type, state);
		this.position = position;
	}

}

class BranchGuidedMutationPlan extends MutationPlan {

	int position = -1;
	String cared_branch = null;
	int direction = 0;

	public BranchGuidedMutationPlan(TaskKind mutate_type, TaskState state, int position, String cared_branch,
			int direction) {
		super(mutate_type, state);
		this.position = position;
		this.cared_branch = cared_branch;
		this.direction = direction;
	}

}

class RandomMutationPlan extends MutationPlan {

	int random_mutate_time = 10;

	public RandomMutationPlan(TaskKind mutate_type, TaskState state, int random_mutate_time) {
		super(mutate_type, state);
		this.random_mutate_time = random_mutate_time;
	}

}

class FixedLengthRandomMutationPlan extends MutationPlan {

	int fixed_length = 1;

	public FixedLengthRandomMutationPlan(TaskKind mutate_type, TaskState state, int fixed_length) {
		super(mutate_type, state);
		this.fixed_length = fixed_length;
	}

}

class PositionRandomMutationPlan extends MutationPlan {

	int position = 10;

	public PositionRandomMutationPlan(TaskKind mutate_type, TaskState state, int position) {
		super(mutate_type, state);
		this.position = position;
	}

}

enum TaskState {
	Normal, LinearConverge, Over
}

enum TaskKind {
	DefaultRandom, FixedLengthRandom, PositionRandomMutation, NegativeProbe, PositiveProbe, NegativeRecord, PositiveRecord, BranchMutation
}
