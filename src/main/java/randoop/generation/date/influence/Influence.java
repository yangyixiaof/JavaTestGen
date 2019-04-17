package randoop.generation.date.influence;

public class Influence {
	
	double branch_gap_influence = 0.0; // positive -> narrow or negative -> enlarge
	boolean flip_happen = false;
	
	public Influence(double branch_gap_average, boolean flip_happen) {
		this.branch_gap_influence = branch_gap_average;
		this.flip_happen = flip_happen;
	}
	
	public double GetInfluence() {
		return branch_gap_influence;
	}
	
	public boolean IsFlipHappen() {
		return flip_happen;
	}
	
}
