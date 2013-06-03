package org.springframework.data.simpledb.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MapUtils {

	private MapUtils() {
		// utility class
	}

	/**
	 * Splits rawMap's entries into a number of chunk maps of max chunkSize elements
	 * 
	 * @param rawMap
	 * @param chunkSize
	 * @return
	 */
	public static List<Map<String, List<String>>> splitToChunksOfSize(Map<String, List<String>> rawMap, int chunkSize) {
		List<Map<String, List<String>>> mapChunks = new LinkedList<Map<String, List<String>>>();

		Set<Map.Entry<String, List<String>>> rawEntries = rawMap.entrySet();

		Map<String, List<String>> currentChunk = new LinkedHashMap<String, List<String>>();
		int rawEntryIndex = 0;
		for(Map.Entry<String, List<String>> rawEntry : rawEntries) {

			if(rawEntryIndex % chunkSize == 0) {
				if(currentChunk.size() > 0) {
					mapChunks.add(currentChunk);
				}
				currentChunk = new LinkedHashMap<String, List<String>>();
			}

			currentChunk.put(rawEntry.getKey(), rawEntry.getValue());

			rawEntryIndex++;

			if(rawEntryIndex == rawMap.size()) {
				// finished iterating
				mapChunks.add(currentChunk);
			}
		}

		return mapChunks;
	}
}
