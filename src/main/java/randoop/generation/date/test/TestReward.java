package randoop.generation.date.test;

import java.util.HashMap;
import java.util.Map;

import randoop.generation.date.influence.Reward;
import randoop.generation.date.util.NormalizeUtil;

public class TestReward {
	
	public TestReward() {
	}
	
	public void test1() {
//		Reward r1 = new Reward(-0.3, -100);
		Reward r2 = new Reward(-0.5, -9);
		Reward r3 = new Reward(-0.66, -9);
		Reward r4 = new Reward(-1.0, -1);
		Map<Object, Reward> wait_select = new HashMap<Object, Reward>();
//		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
//		wait_select.put(o1, r1);
		wait_select.put(o2, r2);
		wait_select.put(o3, r3);
		wait_select.put(o4, r4);
		NormalizeUtil.NormalizeRewards(wait_select);
//		System.out.println("o1:" + wait_select.get(o1).toString() + "$" + wait_select.get(o1).GetReward());
		System.out.println("o2:" + wait_select.get(o2).toString() + "$" + wait_select.get(o2).GetReward());
		System.out.println("o3:" + wait_select.get(o3).toString() + "$" + wait_select.get(o3).GetReward());
		System.out.println("o4:" + wait_select.get(o4).toString() + "$" + wait_select.get(o4).GetReward());
	}
	
	public void test2() {
		
	}
	
	public static void main(String[] args) {
		TestReward tr = new TestReward();
		tr.test1();
		tr.test2();
	}
	
}
