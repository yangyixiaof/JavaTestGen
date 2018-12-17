package randoop.generation.date.mutation.deprecate;

import java.util.ArrayList;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.InfluenceOfBranchChange;
import randoop.generation.date.sequence.BeforeAfterLinkedSequence;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.generation.date.sequence.PseudoSequenceContainer;

public class ObjectConstraintMutation extends Mutation {
	
	PseudoSequenceContainer container = null;

	public ObjectConstraintMutation(InfluenceOfBranchChange influence, PseudoSequenceContainer container) {
		super(influence);
		this.container = container;
	}
	
	protected PseudoSequenceContainer GeneratedMutatedNewContainer(DateGenerator dg) {
//		PseudoSequenceContainer new_container = container.MutateByApplyingOptionalConstraint(dg);
//		return new_container;
		return null;
	}

	@Override
	public BeforeAfterLinkedSequence Apply(ArrayList<String> interested_branch, DateGenerator dg) {
		PseudoSequenceContainer new_container = GeneratedMutatedNewContainer(dg);
		LinkedSequence before_linked_sequence = container.GenerateLinkedSequence();
		LinkedSequence after_linked_sequence = new_container.GenerateLinkedSequence();
		return new BeforeAfterLinkedSequence(null, new ObjectConstraintMutated(container), before_linked_sequence, after_linked_sequence);
	}
	
}
