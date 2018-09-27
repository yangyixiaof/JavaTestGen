package randoop.generation.date.influence;

public class Reward {

	public double[] r = null;

	public Reward(double... r) {
		this.r = r;
	}

	public double GetReward() {
		double sum = 0.0;
		for (int i = 0; i < r.length; i++) {
			sum = sum + r[i];
		}
		return sum;
	}
	
	public double[] GetRewards() {
		return r;
	}
	
	public int GetNumberOfRewards() {
		return r.length;
	}

	public Reward CopySelf() {
		return new Reward(r.clone());
	}

}
