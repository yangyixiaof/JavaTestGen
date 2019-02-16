package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.Influence;
import randoop.generation.date.influence.InfluenceOfTraceCompare;
import randoop.generation.date.mutation.StringMutation;
import randoop.generation.date.util.RandomStringUtil;
import randoop.operation.TypedOperation;
import randoop.types.Type;

public class StringPseudoSequence extends PseudoSequence {

	int ready_try_length = 0;
	public static final int MaxSequenceLength = 20;
	
	// {
	// deprecated block. it seems delta change is unnecessary
	// position delta, based on DeltaChangePseudoSequence this child class only
	// needs to record position information
	// int position = 0;
	// int delta = 0;
	// }
	
	Random random = new Random();

	// position inserted, the position range is 0 ... n, n is the length of content
	// of headed_variable
	
	// TODO currently, entirely determined, add random factors. 
	// TODO optimize the already encountered situations.
	Map<Integer, TreeMap<Integer, TreeMap<Integer, BeforeAfterLinkedSequence>>> tried_value = new TreeMap<Integer, TreeMap<Integer, TreeMap<Integer, BeforeAfterLinkedSequence>>>();
	
	// int recent_tried_position = -1;
//	Map<String, ArrayList<Integer>> tried_value_in_order = new TreeMap<String, ArrayList<Integer>>();

	String content = "";

	boolean is_mutating = false;
	boolean is_making_plan = true;
	TreeMap<Integer, Integer> plan = new TreeMap<Integer, Integer>();
	TreeMap<Integer, ArrayList<String>> plan_for_branches = new TreeMap<Integer, ArrayList<String>>();

	BeforeAfterLinkedSequence recent_mutate_result = null;

	public static final int OneTryTimes = 5;

	public StringPseudoSequence() {// ArrayList<TypedOperation> operations
		super();// operations
//		System.out.println("StringPseudoSequence created!!!");
//		System.exit(1);
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

	public void ResetMutateString(DateGenerator dg) {
		is_mutating = true;
		is_making_plan = true;
	}

	public BeforeAfterLinkedSequence MutateString(DateGenerator dg) {
		//  String trace_sig = container.trace_info.GetTraceSignature();
		BeforeAfterLinkedSequence result = null;
		LinkedSequence before_linked_sequence = null;
		if (is_mutating) {
			if (is_making_plan) {
				if (content.equals("")) {
					plan.put(-1, 1);
				} else {
					Map<Integer, TreeSet<String>> uncovered_position_branches = dg.branch_state
							.GetNotCoveredAndWithInfluencePositionBranchesPairForTrace(container.trace_info);
					int clen = content.length();
					for (int i = 0; i < clen; i++) {
						TreeSet<String> branches = uncovered_position_branches == null ? null : uncovered_position_branches.get(i);
						int bunch_size = (branches != null ? branches.size() : 0) + 1;
						plan.put(i, bunch_size * OneTryTimes);
						plan_for_branches.put(i, branches == null ? new ArrayList<String>() : new ArrayList<String>(branches));
					}
				}
				is_making_plan = false;
			}
			String modified_content = "";
			Set<Integer> pkeys = plan.keySet();
			Iterator<Integer> pk_itr = pkeys.iterator();
			Integer pk = null;
			Integer removed_pk = null;
			while (pk_itr.hasNext()) {
				pk = pk_itr.next();
				if (pk < 0) {
					before_linked_sequence = this.container.GetLinkedSequence();
//					modified_content = "0000000000";
					modified_content = RandomStringUtil.GenerateStringByDefaultChars(random.nextInt(MaxSequenceLength)+1);
					removed_pk = pk;
					recent_mutate_result = null;
					break;
				} else {
					Integer remain = plan.get(pk);
					ArrayList<String> cared_branches = plan_for_branches.get(pk);
					Assert.isTrue(remain > 0);
					StringBuilder modified_content_builder = new StringBuilder(content);
					if (recent_mutate_result != null) {
						InfluenceOfTraceCompare influence = recent_mutate_result.after_linked_sequence.container.influences_compared_to_previous_trace.get(recent_mutate_result.before_linked_sequence.container.trace_info);
						int index_of_influenced_branch = (int)Math.ceil((remain*1.0) / (OneTryTimes*1.0))-2;
						if (index_of_influenced_branch >= 0) {
							Assert.isTrue(index_of_influenced_branch < cared_branches.size());
							String cared_branch = cared_branches.get(index_of_influenced_branch);
//							String position_and_branch = pk + "#" + cared_branch;
//							ArrayList<Integer> value_in_order = tried_value_in_order.get(position_and_branch);
//							if (value_in_order == null) {
//								value_in_order = new ArrayList<Integer>();
//								tried_value_in_order.put(position_and_branch, value_in_order);
//							}
							StringPseudoSequence before_mapping = (StringPseudoSequence) recent_mutate_result.before_linked_sequence.container.GetLogicalMappingSequence(this);
							if (before_mapping == null) {
								before_mapping = this;
							}
							String before_content = before_mapping.content;
							int before_v_p = before_content.charAt(pk);
							StringPseudoSequence after_mapping = (StringPseudoSequence) recent_mutate_result.after_linked_sequence.container.GetLogicalMappingSequence(this);
							String after_content = after_mapping.content;
							int after_v_p = after_content.charAt(pk);
							int gap_v_p = after_v_p - before_v_p;
							Influence influ = influence.GetInfluences().get(cared_branch);
							if (influ.GetInfluence() > 0.2) {
								before_linked_sequence = recent_mutate_result.after_linked_sequence;
								int new_gap_v_p = (int) Math.ceil(gap_v_p *(1 + Math.random()));
								modified_content_builder.setCharAt(pk, (char) (after_v_p+new_gap_v_p));
							} else {
								before_linked_sequence = recent_mutate_result.before_linked_sequence;
								int new_gap_v_p = gap_v_p / 2;
								if (new_gap_v_p == 0) {
									new_gap_v_p = -gap_v_p;
								}
								modified_content_builder.setCharAt(pk, (char) (before_v_p+new_gap_v_p));
							}
						} else {
							before_linked_sequence = this.container.GetLinkedSequence();
							modified_content_builder.setCharAt(pk, (char) (content.charAt(pk)+random.nextInt(65535)));
						}
					} else {
						before_linked_sequence = this.container.GetLinkedSequence();
						modified_content_builder.setCharAt(pk, (char) (modified_content_builder.charAt(pk)+1));
					}
					remain--;
					modified_content = modified_content_builder.toString();
					if (remain == 0) {
						removed_pk = pk;
						recent_mutate_result = null;
					}
					break;
				}
			}
			if (removed_pk != null) {
				plan.remove(removed_pk);
				plan_for_branches.remove(removed_pk);
			}
			if (pk != null && !modified_content.equals("")) {
				StringPseudoSequence copied_this = (StringPseudoSequence) this.CopySelfAndCitersInDeepCloneWay(dg);
				copied_this.container.SetStringLength(modified_content.length());
				copied_this.container.SetLogicMapping(this, copied_this);
				TypedOperation to = TypedOperation.createPrimitiveInitialization(Type.forClass(String.class), modified_content);
				copied_this.statements.set(0, new PseudoStatement(to, new ArrayList<PseudoVariable>()));
				copied_this.content = modified_content;
				LinkedSequence after_linked_sequence = copied_this.container.GetLinkedSequence();
				
				// debug
				System.out.println("Begin");
				System.out.println("content of after_linked_sequence:" + after_linked_sequence .toString());
				System.out.println("end sequence of after_linked_sequence:" + copied_this.container.end.toString());
				System.out.println("end sequence of this:" + this.container.end.toString());
				System.out.println("End" );
				
				StringMutation string_mutation = null;
				if (pk >= 0) {
					string_mutation = new StringMutation(pk, (modified_content.charAt(pk)-content.charAt(pk)));
				}
				result = new BeforeAfterLinkedSequence(to, string_mutation, before_linked_sequence, after_linked_sequence);
				recent_mutate_result = result;
//				if (before_linked_sequence == null) {
//					System.out.println("pk:" + pk);
//				}
			}
			if (result == null) {
				is_mutating = false;
			}
		}
		if (result != null) {
			Assert.isTrue(result.before_linked_sequence != null && result.after_linked_sequence != null);
		}
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
