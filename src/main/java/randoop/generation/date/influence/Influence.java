package randoop.generation.date.influence;

public class Influence {
	
	double branch_gap_average = 0.0; // positive -> narrow or negative -> enlarge
	
	public Influence(double branch_gap_average) {
		this.branch_gap_average = branch_gap_average;
	}
	
	public double GetInfluence() {
		return branch_gap_average;
	}
	
}
