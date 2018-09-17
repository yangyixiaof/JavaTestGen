package randoop.generation.date.influence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TraceInfo {
	
	Map<String, LinkedList<ValuesOfBranch>> vobs = new HashMap<String, LinkedList<ValuesOfBranch>>();
	List<StatementReturn> stmt_rets = new LinkedList<StatementReturn>();
	
	Map<String, Integer> branch_state = new HashMap<String, Integer>();
	
	// this is just an internal data structure.
	private List<Integer> ordered_address = new LinkedList<Integer>();
	
	LinkedList<ObjectAddressConstraint> obligatory_constraint = new LinkedList<ObjectAddressConstraint>();
	LinkedList<ObjectAddressConstraint> optional_constraint = new LinkedList<ObjectAddressConstraint>();
	
	public TraceInfo() {
	}
	
	public void AddOneReturnOfStatement(StatementReturn sr) {
		stmt_rets.add(sr);
	}
	
	public void AddOneValueOfBranch(String sig_info, ValuesOfBranch vob) {
		// set up value of branch.
		vob.SetUpOrderedMayInfluenceAddresses(ordered_address);
		// add value of branch to list.
		LinkedList<ValuesOfBranch> vob_list = vobs.get(sig_info);
		if (vob_list == null) {
			vob_list = new LinkedList<ValuesOfBranch>();
			vobs.put(sig_info, vob_list);
		}
		vob_list.add(vob);
	}
	
	public void AddOneObjectAddress(String catted, int object_address) {
		int idx = ordered_address.indexOf(object_address);
		if (idx != -1) {
			ordered_address.remove(object_address);
		}
		ordered_address.add(0, object_address);
	}

	public void AddObjectSameConstraint(int object_address1, int object_address2) {
		ObjectAddressSameConstraint oasc = new ObjectAddressSameConstraint(object_address1, object_address2);
		obligatory_constraint.add(oasc);
	}

	public void AddObjectTypeConstraint(boolean obligatory, Class<?> cls, int object_address) {
		ObjectAddressTypeConstraint oatc = new ObjectAddressTypeConstraint(obligatory, object_address, cls);
		if (obligatory) {
			obligatory_constraint.add(oatc);
		} else {
			optional_constraint.add(oatc);
		}
	}
	
	public Map<String, LinkedList<ValuesOfBranch>> GetValuesOfBranches() {
		return vobs;
	}
	
	public void IdentifyStatesOfBranches() {
		Set<String> vkeys = vobs.keySet();
		Iterator<String> vitr = vkeys.iterator();
		while (vitr.hasNext()) {
			String vk = vitr.next();
			LinkedList<ValuesOfBranch> vk_vobs = vobs.get(vk);
			Integer state = SimpleInfluenceComputer.IdentifyBranchState(vk_vobs);
			branch_state.put(vk, state);
		}
	}
	
	public Integer GetBranchState(String sig) {
		return branch_state.get(sig);
	}

//	public double Fitness(Map<String, Double> branch_priority, Map<String, Integer> branch_current_interest_state) {
//		Map<String, Integer> branch_priority_regular = new TreeMap<String, Integer>();
//		Set<String> bp_ks = branch_priority.keySet();
//		int branch_index = 0;
//		int branch_size = branch_priority.size();
//		Iterator<String> bp_itr = bp_ks.iterator();
//		while (bp_itr.hasNext()) {
//			int b_p = branch_size - branch_index;
//			String bk = bp_itr.next();
//			branch_priority_regular.put(bk, b_p);
//			branch_index++;
//		}
//		double fitness = 0.0;
//		Set<Entry<String, Integer>> this_interest_branches = new HashSet<>(branch_state.entrySet());
//		Set<Entry<String, Integer>> interest_branches = branch_current_interest_state.entrySet();
//		this_interest_branches.retainAll(interest_branches);
//		Iterator<Entry<String, Integer>> tib_itr = this_interest_branches.iterator();
//		while (tib_itr.hasNext()) {
//			Entry<String, Integer> sig_state = tib_itr.next();
//			String sig = sig_state.getKey();
//			double sig_fitness = ((double)(branch_priority_regular.get(sig)))/((double)(branch_size*1.5)) + branch_priority.get(sig);
//			fitness += sig_fitness;
//		}
//		return fitness;
//	}
	
	public LinkedList<ObjectAddressConstraint> GetObligatoryConstraint() {
		return obligatory_constraint;
	}
	
	public LinkedList<ObjectAddressConstraint> GetOptionalConstraint() {
		return optional_constraint;
	}
	
}
