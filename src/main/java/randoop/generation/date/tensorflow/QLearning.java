package randoop.generation.date.tensorflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.tensorflow.Graph;
import org.tensorflow.Session;

import com.google.common.io.ByteStreams;

import randoop.generation.date.mutation.operation.MutationOperation;

public class QLearning {
	
	Graph graph = new Graph();
	Session session = new Session(graph);
	ReplayMemory d = null;
	StateActionPool pool = null;
	
	public QLearning(ReplayMemory d, StateActionPool pool) {
		this.d = d;
		this.pool = pool;
		// import model
		try {
			graph.importGraphDef(LoadGraphDef());
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] LoadGraphDef() throws IOException {
		try (InputStream is = QLearning.class.getClassLoader().getResourceAsStream("refined_deep_q.pb")) {
			return ByteStreams.toByteArray(is);
		}
	}

	public void QLearn() {
		// the following two statements should be approximately handled in randoop step procedure.
//		s_t, a_t, r_t, s_t_1 = randoop_interact()
//		d.store_transition(s_t, a_t, r_t, s_t_1)
		ArrayList<QTransition> transition_batch = d.SampleMiniBatch();
		Iterator<QTransition> t_itr = transition_batch.iterator();
		while (t_itr.hasNext()) {
			QTransition q_t = t_itr.next();
			MutationOperation action = pool.GetAllActionsOfOneState(q_t.state).get(q_t.action);
			
			
			q_t.next_state;
			q_t.reward;
		}
        QLearn(sess)(d.sample_minibatch())
	}

}
