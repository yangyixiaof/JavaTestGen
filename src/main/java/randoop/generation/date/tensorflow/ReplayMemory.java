package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

public class ReplayMemory {
	
	TreeMap<String, QTransition> all_transition_map = new TreeMap<String, QTransition>();
	ArrayList<QTransition> all_transition_list = new ArrayList<QTransition>();
	
	final int batch_size = 10;
	final double retain_threshold = 0.0;
	
	public ReplayMemory() {
	}
	
	public void StoreTransitions(List<QTransition> transitions) {
		for (QTransition qt : transitions) {
			String q_tran_representation = qt.toString();
			Assert.isTrue(!all_transition_map.containsKey(q_tran_representation));
			all_transition_map.put(q_tran_representation, qt);
			all_transition_list.add(qt);
		}
	}
	
	public ArrayList<QTransition> SampleMiniBatch() {
		ArrayList<QTransition> result = new ArrayList<QTransition>();
		int length = all_transition_list.size();
		int i = length-1;
		while (i >= 0) {
			double r_val = Math.random();
			System.out.println("r_val:" + r_val);
			if (r_val >= retain_threshold) {
				QTransition q_tran = all_transition_list.get(i);
				if (Math.random() <= (((double)i+1)/((double)length))) {
					result.add(q_tran);
				}
			}
			if (result.size() >= batch_size) {
				break;
			}
			i--;
		}
		return result;
	}
	
}
