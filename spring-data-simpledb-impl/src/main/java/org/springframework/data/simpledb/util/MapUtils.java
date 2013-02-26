package org.springframework.data.simpledb.util;

import java.util.*;


public final class MapUtils {

    private MapUtils(){
        //utility class
    }


    /**
     * Splits rawMap's entries into a number of chunk maps of max chunkSize elements
     * @param rawMap
     * @param chunkSize
     * @return
     */
    public static List<Map<String, String>> splitToChunksOfSize(Map<String, String> rawMap, int chunkSize) {
        List<Map<String, String>> mapChunks = new LinkedList<>();


        Set<Map.Entry<String, String>> rawEntries = rawMap.entrySet();

        Map<String, String> currentChunk = new LinkedHashMap<>();
        int rawEntryIndex = 0;
        for (Map.Entry<String, String> rawEntry : rawEntries) {

            if (rawEntryIndex % chunkSize == 0) {
                if (currentChunk.size() > 0) {
                    mapChunks.add(currentChunk);
                }
                currentChunk = new LinkedHashMap<>();
            }

            currentChunk.put(rawEntry.getKey(), rawEntry.getValue());

            rawEntryIndex++;

            if (rawEntryIndex == rawMap.size()) {
                //finished iterating
                mapChunks.add(currentChunk);
            }
        }

        return mapChunks;
    }
}
