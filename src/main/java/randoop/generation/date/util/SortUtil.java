package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortUtil {

	public static Map<String, Double> SortMapByValue(Map<String, Double> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(oriMap.entrySet());
		Collections.sort(entryList, new MapValueComparator());

		Iterator<Map.Entry<String, Double>> iter = entryList.iterator();
		Map.Entry<String, Double> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}

}

class MapValueComparator implements Comparator<Map.Entry<String, Double>> {

	@Override
	public int compare(Entry<String, Double> me1, Entry<String, Double> me2) {
		return me1.getValue().compareTo(me2.getValue());
	}

}
