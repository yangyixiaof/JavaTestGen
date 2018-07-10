package randoop.generation.date.tensorflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
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
	}
	
	public void  SetUpInfluences(Map<String, Double> influences) {
		this.influences.putAll(influences);
	}

	public DenseObjectMatrix2D toInfluenceTensor(BranchIDAssigner branch_id_assigner) {
		DenseObjectMatrix2D one_statement_matrix = new DenseObjectMatrix2D(2, influences.size());
		Set<String> ikeys = influences.keySet();
		Iterator<String> ik_itr = ikeys.iterator();
		int j = 0;
		while (ik_itr.hasNext()) {
			String ik = ik_itr.next();
			double val = influences.get(ik);
			one_statement_matrix.set(0, j, branch_id_assigner.AssignID(ik));
			one_statement_matrix.set(1, j, val);
			j++;
		}
		return one_statement_matrix;
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
