package randoop.generation.date.tensorflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cern.colt.matrix.impl.DenseObjectMatrix1D;
import randoop.generation.date.embed.BranchIDAssigner;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.sequence.ExecutableSequence;

public class QTransition {

	TraceableSequence state = null;
	int action = -1;
	TraceableSequence next_state = null;
	Map<String, Double> influences = new HashMap<String, Double>();

	public QTransition(TraceableSequence state, int action, TraceableSequence next_state) {
		this.state = state;
		this.action = action;
		this.next_state = next_state;
		this.state.SetOutputQTransition(action, this);
		this.next_state.SetInputQTransition(this);
	}
	
	public void  SetUpInfluences(Map<String, Double> influences) {
		this.influences.putAll(influences);
	}
	
	public Map<String, Double> GetInfluences() {
		return influences;
	}
	
	public DenseObjectMatrix1D toBranchTensor(BranchIDAssigner branch_id_assigner) {
		DenseObjectMatrix1D one_branch_matrix = new DenseObjectMatrix1D(influences.size());
		Set<String> ikeys = influences.keySet();
		Iterator<String> ik_itr = ikeys.iterator();
		int j = 0;
		while (ik_itr.hasNext()) {
			String ik = ik_itr.next();
			one_branch_matrix.set(j, branch_id_assigner.AssignID(ik));
			j++;
		}
		return one_branch_matrix;
	}
	
	public DenseObjectMatrix1D toInfluenceTensor() {
		DenseObjectMatrix1D one_influence_matrix = new DenseObjectMatrix1D(influences.size());
		Set<String> ikeys = influences.keySet();
		Iterator<String> ik_itr = ikeys.iterator();
		int j = 0;
		while (ik_itr.hasNext()) {
			String ik = ik_itr.next();
			double val = influences.get(ik);
			one_influence_matrix.set(j, val);
			j++;
		}
		return one_influence_matrix;
	}
	
	@Override
	public String toString() {
		return state + "#" + action + "#" + next_state;
	}

	public ExecutableSequence GetExecutableSequence() {
		return new ExecutableSequence(next_state);
	}

	public TraceableSequence GetSourceSequence() {
		return state;
	}
	
	public TraceableSequence GetTargetSequence() {
		return next_state;
	}

}
