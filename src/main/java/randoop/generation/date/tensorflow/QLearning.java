package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import cern.colt.matrix.ObjectFactory1D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.impl.DenseObjectMatrix1D;
import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.BranchIDAssigner;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;

public class QLearning {

	// Graph graph;
	// Session session;
	ReplayMemory d;
	StateActionPool pool;

	TypedOperationIDAssigner operation_id_assigner = new TypedOperationIDAssigner();
	StringIDAssigner string_id_assigner = new StringIDAssigner();
	BranchIDAssigner branch_id_assigner = new BranchIDAssigner();

	JavaPythonCommunicator communicator = new JavaPythonCommunicator();

	// Map<TypedOperation, Integer> operation_id_map = new HashMap<TypedOperation,
	// Integer>();
	// Map<String, Integer> other_value_id_map = new HashMap<String, Integer>();

	public QLearning(ReplayMemory d, StateActionPool pool) {
		// import model
		// this.graph = new Graph();
		// try {
		// this.graph.importGraphDef(LoadGraphDef());
		// } catch (IllegalArgumentException | IOException e) {
		// e.printStackTrace();
		// }
		// this.session = new Session(graph);
		this.d = d;
		this.pool = pool;
	}

	// private byte[] LoadGraphDef() throws IOException {
	// try (InputStream is = getClass().getResourceAsStream("refined_deep_q.pb")) {
	// return ByteStreams.toByteArray(is);
	// }
	// }

	public void QLearn() {
		// the following two statements should be approximately handled in randoop step
		// procedure.
		// s_t, a_t, r_t, s_t_1 = randoop_interact()
		// d.store_transition(s_t, a_t, r_t, s_t_1)

		// self.s_t_batch = tf.placeholder(int_type, [2, None])
		// self.s_t_segment_batch = tf.placeholder(int_type, [None])
		// self.a_t_batch = tf.placeholder(int_type, [2, None])
		// self.a_t_segment_batch = tf.placeholder(int_type, [2, None])
		// self.r_t_batch = tf.placeholder(float_type, [None])
		// self.s_t_1_batch = tf.placeholder(int_type, [2, None])
		// self.s_t_1_segment_batch = tf.placeholder(int_type, [None])
		// self.s_t_1_actions_batch = tf.placeholder(int_type, [2, None])
		// self.s_t_1_actions_segment_batch = tf.placeholder(int_type, [None])

		DenseObjectMatrix2D s_t_batch = new DenseObjectMatrix2D(2, 0);
		DenseObjectMatrix1D s_t_segment_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix2D a_t_batch = new DenseObjectMatrix2D(2, 0);
		DenseObjectMatrix1D a_t_segment_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix1D r_t_branch_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix1D r_t_segment_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix1D r_t_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix2D s_t_1_batch = new DenseObjectMatrix2D(2, 0);
		DenseObjectMatrix1D s_t_1_segment_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix2D s_t_1_actions_batch = new DenseObjectMatrix2D(2, 0);
		DenseObjectMatrix1D s_t_1_actions_segment_batch = new DenseObjectMatrix1D(0);

		// the following implements QLearn(sess)(d.sample_minibatch())
		ArrayList<QTransition> transition_batch = d.SampleMiniBatch();
		System.out.println("sampled_transition_size:" + transition_batch.size());
		Iterator<QTransition> t_itr = transition_batch.iterator();
		while (t_itr.hasNext()) {
			QTransition q_t = t_itr.next();
			MutationOperation action = pool.GetAllActionsOfOneState(q_t.state).get(q_t.action);
			s_t_batch = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(s_t_batch,
					q_t.state.toComputeTensor(operation_id_assigner, string_id_assigner));
			s_t_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(s_t_segment_batch,
					ObjectFactory1D.dense.make(1, s_t_batch.columns()));
			a_t_batch = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(a_t_batch,
					action.toComputeTensor(operation_id_assigner, string_id_assigner));
			a_t_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(a_t_segment_batch,
					ObjectFactory1D.dense.make(1, a_t_batch.columns()));
			r_t_branch_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(r_t_branch_batch,
					q_t.toBranchTensor(branch_id_assigner));
			r_t_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(r_t_segment_batch,
					ObjectFactory1D.dense.make(1, r_t_batch.size()));
			r_t_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(r_t_batch, q_t.toInfluenceTensor());
			s_t_1_batch = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(s_t_1_batch,
					q_t.next_state.toComputeTensor(operation_id_assigner, string_id_assigner));
			s_t_1_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(s_t_1_segment_batch,
					ObjectFactory1D.dense.make(1, s_t_1_batch.columns()));
			ArrayList<MutationOperation> next_state_all_actions = pool.GetAllActionsOfOneState(q_t.next_state);
			Iterator<MutationOperation> nitr = next_state_all_actions.iterator();
			while (nitr.hasNext()) {
				MutationOperation mo = nitr.next();
				s_t_1_actions_batch = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(s_t_1_actions_batch,
						mo.toComputeTensor(operation_id_assigner, string_id_assigner));
			}
			s_t_1_actions_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense
					.append(s_t_1_actions_segment_batch, ObjectFactory1D.dense.make(1, s_t_1_actions_batch.columns()));
		}
		Map<String, Object> feed_dict = new TreeMap<String, Object>();
		feed_dict.put("s_t_batch", s_t_batch.toArray());
		feed_dict.put("s_t_segment_batch", s_t_segment_batch.toArray());
		feed_dict.put("a_t_batch", a_t_batch.toArray());
		feed_dict.put("a_t_segment_batch", a_t_segment_batch.toArray());
		feed_dict.put("r_t_branch_batch", r_t_branch_batch.toArray());
		feed_dict.put("r_t_segment_batch", r_t_segment_batch.toArray());
		feed_dict.put("r_t_batch", r_t_batch.toArray());
		feed_dict.put("s_t_1_batch", s_t_1_batch.toArray());
		feed_dict.put("s_t_1_segment_batch", s_t_1_segment_batch.toArray());
		feed_dict.put("s_t_1_actions_batch", s_t_1_actions_batch.toArray());
		feed_dict.put("s_t_1_actions_segment_batch", s_t_1_actions_segment_batch.toArray());
		JavaPythonRemoteInvoke result = communicator.RemoteCallPython(feed_dict, "learning");
		System.out.println("one learning step loss:" + result);
		// try (Tensor<Integer> ti_s_t_batch = Tensor.create(s_t_batch.toArray(),
		// Integer.class);Tensor<Integer> ti_s_t_segment_batch =
		// Tensor.create(s_t_segment_batch.toArray(), Integer.class);Tensor<Integer>
		// ti_a_t_batch = Tensor.create(a_t_batch.toArray(),
		// Integer.class);Tensor<Integer> ti_a_t_segment_batch =
		// Tensor.create(a_t_segment_batch.toArray(), Integer.class);Tensor<Integer>
		// ti_r_t_batch = Tensor.create(r_t_batch.toArray(),
		// Integer.class);Tensor<Integer> ti_s_t_1_batch =
		// Tensor.create(s_t_1_batch.toArray(), Integer.class);Tensor<Integer>
		// ti_s_t_1_segment_batch = Tensor.create(s_t_1_segment_batch.toArray(),
		// Integer.class);Tensor<Integer> ti_s_t_1_actions_batch =
		// Tensor.create(s_t_1_actions_batch.toArray(), Integer.class);Tensor<Integer>
		// ti_s_t_1_actions_segment_batch =
		// Tensor.create(s_t_1_actions_segment_batch.toArray(), Integer.class);) {
		// List<Tensor<?>> tensors = this.session.runner().feed("s_t_batch",
		// ti_s_t_batch).feed("s_t_segment_batch",
		// ti_s_t_segment_batch).feed("a_t_batch",
		// ti_a_t_batch).feed("a_t_segment_batch",
		// ti_a_t_segment_batch).feed("r_t_batch", ti_r_t_batch).feed("s_t_1_batch",
		// ti_s_t_1_batch).feed("s_t_1_segment_batch",
		// ti_s_t_1_segment_batch).feed("s_t_1_actions_batch",
		// ti_s_t_1_actions_batch).feed("s_t_1_actions_segment_batch",
		// ti_s_t_1_actions_segment_batch).fetch("q_learning_loss").fetch("q_learning_train").run();
		// Tensor<?> loss_value = tensors.get(0).expect(Float.class);
		// System.out.println("loss_value:" + loss_value.floatValue() +
		// "#loss_value_shape:" + loss_value);
		// }
	}

	public Map<String, Map<String, List<Double>>> QPredict(TraceableSequence state, Collection<MutationOperation> un_tried_actions,
			Map<String, Integer> un_covered_branches) {

		DenseObjectMatrix2D s_t_batch = new DenseObjectMatrix2D(2, 0);
		DenseObjectMatrix1D s_t_segment_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix2D a_t_batch = new DenseObjectMatrix2D(2, 0);
		DenseObjectMatrix1D a_t_segment_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix1D r_t_branch_batch = new DenseObjectMatrix1D(0);
		DenseObjectMatrix1D r_t_segment_batch = new DenseObjectMatrix1D(0);

		s_t_batch = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(s_t_batch,
				state.toComputeTensor(operation_id_assigner, string_id_assigner));
		s_t_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(s_t_segment_batch,
				ObjectFactory1D.dense.make(1, s_t_batch.columns()));

		Iterator<MutationOperation> nitr = un_tried_actions.iterator();
		while (nitr.hasNext()) {
			MutationOperation mo = nitr.next();
			a_t_batch = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(a_t_batch,
					mo.toComputeTensor(operation_id_assigner, string_id_assigner));
		}
		a_t_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(a_t_segment_batch,
				ObjectFactory1D.dense.make(1, a_t_batch.columns()));
		
		ArrayList<Integer> un_covered_branch_ids = new ArrayList<Integer>();
		Set<String> ucb_keys = un_covered_branches.keySet();
		Iterator<String> ucb_itr = ucb_keys.iterator();
		while (ucb_itr.hasNext()) {
			String ucb_sig = ucb_itr.next();
			Integer ucb_sig_state = un_covered_branches.get(ucb_sig);
			String bk = ucb_sig + ucb_sig_state;
			int b_id = branch_id_assigner.AssignID(bk);
			un_covered_branch_ids.add(b_id);
		}
		Iterator<Integer> uitr = un_covered_branch_ids.iterator();
		while (uitr.hasNext()) {
			Integer u = uitr.next();
			r_t_branch_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(r_t_branch_batch,
					ObjectFactory1D.dense.make(1, u));
		}
		r_t_segment_batch = (DenseObjectMatrix1D) ObjectFactory1D.dense.append(r_t_segment_batch,
				ObjectFactory1D.dense.make(1, r_t_branch_batch.size()));

		Map<String, Object> feed_dict = new TreeMap<String, Object>();
		feed_dict.put("s_t_batch", s_t_batch.toArray());
		feed_dict.put("s_t_segment_batch", s_t_segment_batch.toArray());
		feed_dict.put("a_t_batch", a_t_batch.toArray());
		feed_dict.put("a_t_segment_batch", a_t_segment_batch.toArray());
		feed_dict.put("r_t_branch_batch", r_t_branch_batch.toArray());
		feed_dict.put("r_t_segment_batch", r_t_segment_batch.toArray());
		JavaPythonRemoteInvoke result = communicator.RemoteCallPython(feed_dict, "predicting");
		System.out.println("one predicting step value:" + result);
		Gson gson = new Gson();
		Map<String, Map<String, List<Double>>> predicted_output = gson.fromJson(result.toString(),
				new TypeToken<Map<String, Map<String, List<Double>>>>() {
					private static final long serialVersionUID = 1L;
				}.getType());
		
		return predicted_output;
	}

}
