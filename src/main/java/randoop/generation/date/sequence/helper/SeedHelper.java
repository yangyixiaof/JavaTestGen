package randoop.generation.date.sequence.helper;

import java.util.LinkedList;
import java.util.TreeMap;

import randoop.generation.date.DateGenerator;
import randoop.generation.date.sequence.PseudoSequenceContainer;

public class SeedHelper {
	
	public static void SeedIsVeryInteresting(DateGenerator dg, PseudoSequenceContainer container) {
		TreeMap<Integer, LinkedList<PseudoSequenceContainer>> ctnrs = dg.GetContainers();
		LinkedList<PseudoSequenceContainer> psc_ll = ctnrs.get(container.GetStringLength());
		if (psc_ll == null) {
			psc_ll = new LinkedList<PseudoSequenceContainer>();
			ctnrs.put(container.GetStringLength(), psc_ll);
		}
		psc_ll.add(0, container);
		dg.content_container_map.put(container.FetchStringPseudoSequence().GetContent(), container);
	}
	
	public static int SeedIsInteresting(DateGenerator dg, PseudoSequenceContainer container, double prob_to_add) {
		int added = 0;
		TreeMap<Integer, LinkedList<PseudoSequenceContainer>> ctnrs = dg.GetContainers();
		LinkedList<PseudoSequenceContainer> psc_ll = ctnrs.get(container.GetStringLength());
//		Assert.isTrue(psc_ll != null);
		if (psc_ll == null) {
			psc_ll = new LinkedList<PseudoSequenceContainer>();
			ctnrs.put(container.GetStringLength(), psc_ll);
		}
//		if (first_encounter) {
////			ArrayList<Sequence> all_seqs = dg.GetAllSequencesInReference();
////			all_seqs.add(container.GetLinkedSequence());
//			psc_ll.add(0, container);
//			dg.content_container_map.put(container.FetchStringPseudoSequence().GetContent(), container);
//			added++;
//		} else {
			if (psc_ll.size() == 0) {
				prob_to_add = 1.0;
			}
			if (Math.random() < prob_to_add) {
				psc_ll.add(container);
				dg.content_container_map.put(container.FetchStringPseudoSequence().GetContent(), container);
				added++;
			}
//		}
		return added;
	}
	
}
