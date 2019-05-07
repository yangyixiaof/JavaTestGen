package randoop.generation.date.mutation;

public class RandomMutationInfo {
	
	double prob_to_add_random_mutate = 0.0;
	
	public RandomMutationInfo(double prob_to_add_random_mutate) {
		this.prob_to_add_random_mutate = prob_to_add_random_mutate;
	}
	
	public double GetProbToAddRandomMutate() {
		return prob_to_add_random_mutate;
	}
	
}
