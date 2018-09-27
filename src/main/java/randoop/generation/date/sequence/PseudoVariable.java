package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.influence.Reward;
import randoop.generation.date.influence.Rewardable;

public class PseudoVariable implements Rewardable {

	public PseudoSequence sequence = null;
	public int index = -1;

	// String headed_variable_string = null;

	public PseudoVariable(PseudoSequence sequence, int index) {
		this.sequence = sequence;
		this.index = index;
		Assert.isTrue(index >= 0);
	}

	// public void SetVariableToString(String variable_string) {
	// if (headed_variable_string == null) {
	// Assert.isTrue(this.headed_variable_string == null);
	// } else {
	// if (this.headed_variable_string == null) {
	// this.headed_variable_string = headed_variable_string;
	// } else {
	// Assert.isTrue(headed_variable_string.equals(this.headed_variable_string));
	// }
	// }
	// this.headed_variable_string = variable_string;
	// }

	public PseudoVariable CopySelfInDeepCloneWay(PseudoSequenceContainer container, Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
			DateGenerator dg) {// Map<PseudoVariable, PseudoSequence> class_object_headed_sequence
		PseudoSequence copied_sequence = sequence.CopySelfInDeepCloneWay(container, origin_copied_sequence_map, dg);
		PseudoVariable copied_pv = new PseudoVariable(copied_sequence, index);
		if (!dg.pseudo_variable_headed_sequence.containsKey(copied_pv)) {
			PseudoSequence headed_sequence = dg.pseudo_variable_headed_sequence.get(this);
//			System.out.println("sequence.getClass():" + sequence.getClass());
			if (headed_sequence == null) {
				Assert.isTrue(sequence.getClass().equals(DisposablePseudoSequence.class), "Unexpected sequence.getClass():" + sequence.getClass());
			} else {
				if (origin_copied_sequence_map.containsKey(headed_sequence)) {
					PseudoSequence copied_headed_sequence = origin_copied_sequence_map.get(headed_sequence);
					dg.pseudo_variable_headed_sequence.put(copied_pv, copied_headed_sequence);
				} else {
					PseudoSequence copied_headed_sequence = headed_sequence.CopySelfInDeepCloneWay(container, origin_copied_sequence_map, dg);
					dg.pseudo_variable_headed_sequence.put(copied_pv, copied_headed_sequence);
				}
			}
		}
		return copied_pv;
	}

//	public PseudoVariable CopySelfAndCitersInDeepCloneWay(
//			Map<PseudoSequence, PseudoSequence> origin_copied_sequence_map,
//			Map<PseudoVariable, PseudoSequence> class_object_headed_sequence) {
//		PseudoSequence copied_sequence = sequence.CopySelfAndCitersInDeepCloneWay(origin_copied_sequence_map,
//				class_object_headed_sequence);
//		return new PseudoVariable(copied_sequence, index);
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sequence.hashCode();
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PseudoVariable) {
			PseudoVariable pv = (PseudoVariable) obj;
			if (sequence == pv.sequence && index == pv.index) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "index:" + index + "#sequence:" + sequence;
	}

	@Override
	public Reward GetReward(ArrayList<String> interested_branch) {
		double[] concated_rewards = ArrayUtils.addAll(sequence.GetReward(interested_branch).GetRewards(), sequence.container.GetReward(interested_branch).GetRewards());
		return new Reward(concated_rewards);
	}

}
