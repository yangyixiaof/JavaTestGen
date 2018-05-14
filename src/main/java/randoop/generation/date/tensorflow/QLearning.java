package randoop.generation.date.tensorflow;

import java.io.IOException;
import java.io.InputStream;

import org.tensorflow.Graph;
import org.tensorflow.Session;

import com.google.common.io.ByteStreams;

public class QLearning {
	
	Graph graph = new Graph();
	Session session = new Session(graph);
	
	public QLearning() {
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
	        s_t, a_t, r_t, s_t_1 = randoop_interact()
	        d.store_transition(s_t, a_t, r_t, s_t_1)
	        this_turn_loss_value = QLearn(sess)(d.sample_minibatch())
	        print(this_turn_loss_value)
	}

}
