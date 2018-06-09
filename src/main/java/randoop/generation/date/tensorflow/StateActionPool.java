package randoop.generation.date.tensorflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import randoop.generation.date.DateWtfException;
import randoop.generation.date.mutation.MutationAnalyzer;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.sequence.TraceableSequence;
import randoop.operation.TypedOperation;
import randoop.reflection.TypeInstantiator;

/**
 * A singleton class, memoizing available mutations of sequences.
 *
 * <p>this.state_actions stores...
 *
 * <p>If not stored, use MutationAnalyzer to analyze and to store.
 */
public class StateActionPool {

  TypeInstantiator ti = null;
  Collection<TypedOperation> candidates = null;

  Map<TraceableSequence, ArrayList<MutationOperation>> state_actions = new TreeMap<>();

  public StateActionPool(TypeInstantiator ti, Collection<TypedOperation> candidates) {
    this.ti = ti;
    this.candidates = candidates;
  }

  //	private void StoreAllActionsOfOneState(TraceableSequence state, ArrayList<MutationOperation>
  // actions) {
  //		Assert.isTrue(!state_actions.containsKey(state));
  //		state_actions.put(state, actions);
  //	}

  /**
   * "Memoizationalized" and wrapped version of MutationAnalyzer#GenerateMutationOperations
   *
   * @param state
   * @return
   */
  public ArrayList<MutationOperation> GetAllActionsOfOneState(TraceableSequence state) {
    if (!state_actions.containsKey(state)) {
      MutationAnalyzer analyzer = new MutationAnalyzer(state, ti);
      ArrayList<MutationOperation> candidateMutations = new ArrayList<>();
      try {
        analyzer.GenerateMutationOperations(candidates, candidateMutations);
//        System.out.println("candidateMutations_size:" + candidateMutations.size());
      } catch (DateWtfException e) {
        e.printStackTrace();
      }
      state_actions.put(state, candidateMutations);
    }
    return state_actions.get(state);
  }
}
