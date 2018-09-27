package randoop.generation.date.influence;

public class Reward {

	public double[] rs = null;

	public Reward(double... rs) {
		this.rs = rs;
	}

	public double GetReward() {
		double sum = 0.0;
		for (int i = 0; i < rs.length; i++) {
			sum = sum + rs[i];
		}
		return sum;
	}
	
	public double[] GetRewards() {
		return rs;
	}
	
	public int GetNumberOfRewards() {
		return rs.length;
	}

	public Reward CopySelf() {
		return new Reward(rs.clone());
	}
	
	@Override
	public String toString() {
		String s = "";
		for (double d : rs) {
			s += d + "#";
		}
		return s;
	}

}
