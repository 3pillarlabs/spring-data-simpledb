package org.springframework.data.simpledb.attributeutil;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SimpleDbAttributeValueSplitterTest {

	static String STRING_OF_MAX_SIMPLE_DB_LENGTH = null;
	private static final String SAMPLE_ATT_NAME = "sampleAttName";

	static {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < SimpleDbAttributeValueSplitter.MAX_ATTR_VALUE_LEN; i++) {
			builder.append("x");
		}

		STRING_OF_MAX_SIMPLE_DB_LENGTH = builder.toString();
	}

	@Test
	public void splitAttributeValuesWithExceedingLengths_should_detect_long_attributes() throws Exception {
		Map<String, String> rawAttributes = new LinkedHashMap<String, String>();
		rawAttributes.put(SAMPLE_ATT_NAME, STRING_OF_MAX_SIMPLE_DB_LENGTH + "c");

		Map<String, List<String>> splitAttributes = SimpleDbAttributeValueSplitter
				.splitAttributeValuesWithExceedingLengths(rawAttributes);
		assertEquals("count(keys) == 1", 1, splitAttributes.keySet().size());
		Iterator<List<String>> iterator = splitAttributes.values().iterator();
		List<String> next = null;
		if (iterator.hasNext()) {
			next = iterator.next(); 
		}
		assertNotNull(next);
		assertEquals("count(values) == 2", 2, next.size());
	}

	@Test
	public void splitAttributeValuesWithExceedingLengths_should_not_split_short_attributes() throws Exception {
		Map<String, String> rawAttributes = new LinkedHashMap<String, String>();
		rawAttributes.put(SAMPLE_ATT_NAME, "shortValue");

		Map<String, List<String>> splitAttributes = SimpleDbAttributeValueSplitter
				.splitAttributeValuesWithExceedingLengths(rawAttributes);
		assertEquals(1, splitAttributes.keySet().size());

		List<String> firstSplitAttribute = splitAttributes.values().iterator().next();
		assertEquals("shortValue", firstSplitAttribute.get(0));
	}

	@Test
	public void splitAttributeValues_should_be_recombined() throws Exception {
		Map<String, String> rawAttributes = new LinkedHashMap<String, String>();
		rawAttributes.put(SAMPLE_ATT_NAME, STRING_OF_MAX_SIMPLE_DB_LENGTH + "c");

		Map<String, List<String>> splitAttributes = SimpleDbAttributeValueSplitter
				.splitAttributeValuesWithExceedingLengths(rawAttributes);

		Map<String, String> recombinedAtts = SimpleDbAttributeValueSplitter
				.combineAttributeValuesWithExceedingLengths(splitAttributes);

		assertEquals(recombinedAtts.size(), rawAttributes.size());
	}

}
