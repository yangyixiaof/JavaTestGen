package randoop.generation.date.influence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.DateGenerator;

public class TraceInfo implements Rewardable {
	
	Map<String, InfoOfBranch> vobs = new HashMap<String, InfoOfBranch>();
	Map<String, Integer> vob_counts = new HashMap<String, Integer>();
	int total_count = 0;
	
//	List<StatementReturn> stmt_rets = new LinkedList<StatementReturn>();
	
//	Map<String, Integer> branch_state = new HashMap<String, Integer>();
	
	// this is just an internal data structure.
//	private List<Integer> ordered_address = new LinkedList<Integer>();
	
//	LinkedList<ObjectAddressConstraint> obligatory_constraint = new LinkedList<ObjectAddressConstraint>();
//	LinkedList<ObjectAddressConstraint> optional_constraint = new LinkedList<ObjectAddressConstraint>();
	
	// once set, not changed any more
	String trace_sig = null;
	
	public TraceInfo() {
	}
	
//	public void AddOneReturnOfStatement(StatementReturn sr) {
//		stmt_rets.add(sr);
//	}
	
	public void AddOneValueOfBranch(String sig_info, ValuesOfBranch vob) {
		// set up value of branch.
//		vob.SetUpOrderedMayInfluenceAddresses(ordered_address);
		// add value of branch to list.
		InfoOfBranch iob = vobs.get(sig_info);
		Integer iob_count = vob_counts.get(sig_info);
		if (iob == null) {
			Assert.isTrue(iob_count == null);
			iob = new InfoOfBranch();
			vobs.put(sig_info, iob);
			iob_count = 0;
		} else {
			Assert.isTrue(false, "Strange! each branch is unique!");
		}
		iob.HandleOneValueOfBranch(vob);
		iob_count++;
		vob_counts.put(sig_info, iob_count);
		total_count++;
	}
	
//	public void AddOneObjectAddress(String catted, int object_address) {
//		int idx = ordered_address.indexOf(object_address);
//		if (idx != -1) {
//			ordered_address.remove(object_address);
//		}
//		ordered_address.add(0, object_address);
//	}
//
//	public void AddObjectSameConstraint(int object_address1, int object_address2) {
//		ObjectAddressSameConstraint oasc = new ObjectAddressSameConstraint(object_address1, object_address2);
//		obligatory_constraint.add(oasc);
//	}
//
//	public void AddObjectTypeConstraint(boolean obligatory, Class<?> cls, int object_address) {
//		ObjectAddressTypeConstraint oatc = new ObjectAddressTypeConstraint(obligatory, object_address, cls);
//		if (obligatory) {
//			obligatory_constraint.add(oatc);
//		} else {
//			optional_constraint.add(oatc);
//		}
//	}
	 
	public Map<String, InfoOfBranch> GetInfoOfBranches() {
		return vobs;
	}
	
//	public void IdentifyStatesOfBranches() {
//		Set<String> vkeys = vobs.keySet();
//		Iterator<String> vitr = vkeys.iterator();
//		while (vitr.hasNext()) {
//			String vk = vitr.next();
//			LinkedList<ValuesOfBranch> vk_vobs = vobs.get(vk);
//			Integer state = SimpleInfluenceComputer.IdentifyBranchState(vk_vobs);
//			branch_state.put(vk, state);
//		}
//	}
	
	public Integer GetBranchStateForValueOfBranch(String sig_of_vob) {
		int li = sig_of_vob.lastIndexOf('#');
		String sig = sig_of_vob.substring(0, li);
		int vob_index = Integer.parseInt(sig_of_vob.substring(li+1));
		return vobs.get(sig).GetBranchState(vob_index);
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
	
//	public LinkedList<ObjectAddressConstraint> GetObligatoryConstraint() {
//		return obligatory_constraint;
//	}
//	
//	public boolean HasObjectAddressConstraints() {
//		return obligatory_constraint.size() > 0;
//	}
//	
//	public LinkedList<ObjectAddressConstraint> GetOptionalConstraint() {
//		return optional_constraint;
//	}
	
	public boolean BranchesExistInTrace() {
		return vobs.size() > 0;
	}
	
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		Set<String> bs_keys = branch_state.keySet();
//		for (String bs_key : bs_keys) {
//			Integer b_state = branch_state.get(bs_key);
//			sb.append(bs_key + ":" + b_state + "#");
//		}
//		return sb.toString();
//	}
	
	public String GetTraceSignature() {
		if (trace_sig == null) {
			StringBuilder builder = new StringBuilder();
			Set<String> vkeys = vobs.keySet();
			Iterator<String> vk_itr = vkeys.iterator();
			while (vk_itr.hasNext()) {
				String vk = vk_itr.next();
				InfoOfBranch ib = vobs.get(vk);
				String branch_sig = ib.GenerateBranchStateSignature();
				builder.append(vk.hashCode() + "#" + branch_sig);
			}
			trace_sig = builder.toString();
		}
		Assert.isTrue(trace_sig != null);
		return trace_sig;
	}

	@Override
	public Reward GetReward(DateGenerator dg) {
		return new Reward(vobs.size() + total_count);
	}
	
}
