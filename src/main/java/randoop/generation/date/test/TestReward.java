package randoop.generation.date.test;

import java.util.HashMap;
import java.util.Map;

import randoop.generation.date.influence.Reward;
import randoop.generation.date.util.NormalizeUtil;

public class TestReward {
	
	public TestReward() {
	}
	
	public void test1() {
//		Reward r1 = new Reward(0.7, 1/100);
		Reward r2 = new Reward(0.5, 1.0/9.0);
		Reward r3 = new Reward(0.33, 1.0/9.0);
		Reward r4 = new Reward(0, 1.0/4.0);
		Map<Object, Reward> wait_select = new HashMap<Object, Reward>();
//		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
//		wait_select.put(o1, r1);
		wait_select.put(o2, r2);
		wait_select.put(o3, r3);
		wait_select.put(o4, r4);
		Map<Object, Reward> r_wait_select = NormalizeUtil.ProbabilizeRewards(wait_select);
//		System.out.println("o1:" + r_wait_select.get(o1).toString() + "$" + r_wait_select.get(o1).GetReward());
		System.out.println("o2:" + r_wait_select.get(o2).toString() + "$" + r_wait_select.get(o2).GetReward());
		System.out.println("o3:" + r_wait_select.get(o3).toString() + "$" + r_wait_select.get(o3).GetReward());
		System.out.println("o4:" + r_wait_select.get(o4).toString() + "$" + r_wait_select.get(o4).GetReward());
	}
	
	public void test2() {
		
	}
	
	public static void main(String[] args) {
		TestReward tr = new TestReward();
		tr.test1();
		tr.test2();
	}
	
}
