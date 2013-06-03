package org.springframework.data.simpledb.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MapUtilsTest {

	private final static int SAMPLE_MAP_SIZE = 256;

	@Test
	public void splitToChunksOfSize_should_split_entries_exceeding_size() throws Exception {

		Map<String, List<String>> attributes = new LinkedHashMap<String, List<String>>();
		for(int i = 0; i < SAMPLE_MAP_SIZE + 100; i++) {
			List<String> list = new ArrayList<String>();
			list.add("Value: " + i);
			attributes.put("Key: " + i, list);
		}

		List<Map<String, List<String>>> chunks = MapUtils.splitToChunksOfSize(attributes, SAMPLE_MAP_SIZE);
		assertEquals(2, chunks.size());

		Map<String, List<String>> firstChunk = chunks.get(0);
		assertEquals(SAMPLE_MAP_SIZE, firstChunk.size());

		Map<String, List<String>> secondChunk = chunks.get(1);
		assertEquals(100, secondChunk.size());
		assertTrue(secondChunk.containsKey("Key: " + 256));
		assertTrue(secondChunk.containsKey("Key: " + 355));

	}
}
