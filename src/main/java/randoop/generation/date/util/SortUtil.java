package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortUtil {

	public static <K, V> ArrayList<K> SortMapByValue(Map<K, V> oriMap) {
		// if (oriMap == null || oriMap.isEmpty()) {
		// return null;
		// }
		ArrayList<K> sortedKeys = new ArrayList<K>();
		List<Map.Entry<K, V>> entryList = new ArrayList<Map.Entry<K, V>>(oriMap.entrySet());
		Collections.sort(entryList, new MapValueComparator<K, V>());

		Iterator<Map.Entry<K, V>> iter = entryList.iterator();
		Map.Entry<K, V> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedKeys.add(tmpEntry.getKey());
		}
		return sortedKeys;
	}

}

class MapValueComparator<K, V> implements Comparator<Map.Entry<K, V>> {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Entry<K, V> me1, Entry<K, V> me2) {
		return ((Comparable<V>) me1.getValue()).compareTo(me2.getValue());
	}

}
