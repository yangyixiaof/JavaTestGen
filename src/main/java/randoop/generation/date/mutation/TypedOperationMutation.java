package randoop.generation.date.mutation;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.random.RandomSelect;
import randoop.generation.date.sequence.BeforeAfterLinkedSequence;
import randoop.generation.date.sequence.PseudoSequence;
import randoop.generation.date.sequence.PseudoVariable;
import randoop.operation.TypedOperation;

public class TypedOperationMutation extends Mutation {
	
	TypedOperation to = null;
	Set<PseudoVariable> pvs = null;
	
	public TypedOperationMutation(InfluenceOfBranchChange influence, TypedOperation to, Set<PseudoVariable> pvs) {
		super(influence);
		this.to = to;
		this.pvs = pvs;
	}

	@Override
	public BeforeAfterLinkedSequence Apply(ArrayList<String> interested_branch, DateGenerator dg) {
		PseudoVariable selected_pv = (PseudoVariable) RandomSelect.RandomElementFromSetByRewardableElements(pvs, interested_branch, null);
//		PseudoVariable pv = Randomness.randomSetMember(pvs);
//		System.out.println("selected_to:" + to);
		// mutate existing sequence
//		PseudoVariableSelectFilter pvsf = new PseudoVariableSelectFilter(selected_to_class,
//				pseudo_variable_class);
//		PseudoVariable selected_pv = RandomSelect.RandomKeyFromMapByRewardableValueWithPenalizableValue(
//				pseudo_variable_branch_value_state, pseudo_variable_headed_sequence, interested_branch,
//				pvsf, selected_to);c
//		System.out.println("selected_pv:" + selected_pv);
		if (selected_pv != null) {
//			Class<?> sequence_type = dg.GetSequenceTypeFromTypedOperation(to);
			PseudoSequence selected_pv_headed_sequence = dg.pseudo_variable_headed_sequence.get(selected_pv);
			Assert.isTrue(selected_pv_headed_sequence != null);
//			String content = pseudo_variable_content.get(selected_pv);
//			selected_pv_headed_sequence.SetHeadedVariableString(content);
			return selected_pv_headed_sequence.Mutate(to, interested_branch, dg);
		}
		return null;
	}
	
}
