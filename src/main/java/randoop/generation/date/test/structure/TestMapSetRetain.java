package randoop.generation.date.test.structure;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class TestMapSetRetain {
	
	public static void main(String[] args) {
		Map<String, Integer> a = new TreeMap<String, Integer>();
		Map<String, Integer> b = new TreeMap<String, Integer>();
		a.put("aa", 1);
		a.put("ab", 2);
		a.put("ba", 3);
		b.put("bb", 1);
		b.put("ab", 4);
		b.put("ba", 3);
		Set<Entry<String, Integer>> aes = new HashSet<>(a.entrySet());
		Set<Entry<String, Integer>> bes = b.entrySet();
		aes.retainAll(bes);
		Iterator<Entry<String, Integer>> aes_itr = aes.iterator();
		while (aes_itr.hasNext()) {
			Entry<String, Integer> ae_e = aes_itr.next();
			System.out.println(ae_e);
		}
		System.out.println("a.size():" + a.size());
		System.out.println("b.size():" + b.size());
	}
	
}
