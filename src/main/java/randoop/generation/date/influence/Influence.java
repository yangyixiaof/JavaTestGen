package randoop.generation.date.influence;

public class Influence {
	
	double branch_gap_influence = 0.0; // positive -> narrow or negative -> enlarge
	double before_gap = 0.0;
	double after_gap = 0.0;
	boolean flip_happen = false;
	boolean hit_happen = false;
	
	public Influence(double branch_gap_influence, double before_gap, double after_gap, boolean flip_happen, boolean hit_happen) {
		this.branch_gap_influence = branch_gap_influence;
		this.before_gap = before_gap;
		this.after_gap = after_gap;
		this.flip_happen = flip_happen;
		this.hit_happen = hit_happen;
	}
	
	public double GetInfluence() {
		return branch_gap_influence;
	}
	
	public double GetBeforeGap() {
		return before_gap;
	}
	
	public double GetAfterGap() {
		return after_gap;
	}
	
	public boolean IsFlipHappen() {
		return flip_happen;
	}
	
	public boolean IsHitHappen() {
		return hit_happen;
	}
	
}
