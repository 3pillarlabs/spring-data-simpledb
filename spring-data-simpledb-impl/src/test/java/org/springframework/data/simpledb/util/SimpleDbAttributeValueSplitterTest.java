package org.springframework.data.simpledb.util;

import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class SimpleDbAttributeValueSplitterTest {

    static String STRING_OF_MAX_SIMPLE_DB_LENGTH = null;
    private static final String SAMPLE_ATT_NAME = "sampleAttName";

    static {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < SimpleDbAttributeValueSplitter.MAX_SIMPLE_DB_ATTRIBUTE_VALUE_LENGTH; i++) {
            builder.append("x");
        }

        STRING_OF_MAX_SIMPLE_DB_LENGTH = builder.toString();
    }

    @Test
    public void splitAttributeValuesWithExceedingLengths_should_detect_long_attributes() throws Exception {
        Map<String, String> rawAttributes = new LinkedHashMap<>();
        rawAttributes.put(SAMPLE_ATT_NAME, STRING_OF_MAX_SIMPLE_DB_LENGTH + "c");

        Map<String, String> splitAttributes = SimpleDbAttributeValueSplitter.splitAttributeValuesWithExceedingLengths(rawAttributes);
        assertEquals(2, splitAttributes.keySet().size());

        String firstSplitAttribute = splitAttributes.values().iterator().next();
        assertEquals(STRING_OF_MAX_SIMPLE_DB_LENGTH, firstSplitAttribute);
    }

    @Test
    public void splitAttributeValuesWithExceedingLengths_should_not_split_short_attributes() throws Exception {
        Map<String, String> rawAttributes = new LinkedHashMap<>();
        rawAttributes.put(SAMPLE_ATT_NAME, "shortValue");

        Map<String, String> splitAttributes = SimpleDbAttributeValueSplitter.splitAttributeValuesWithExceedingLengths(rawAttributes);
        assertEquals(1, splitAttributes.keySet().size());

        String firstSplitAttribute = splitAttributes.values().iterator().next();
        assertEquals("shortValue", firstSplitAttribute);
    }


    @Test
    public void splitAttributeValues_should_be_recombined() throws Exception {
        Map<String, String> rawAttributes = new LinkedHashMap<>();
        rawAttributes.put(SAMPLE_ATT_NAME, STRING_OF_MAX_SIMPLE_DB_LENGTH + "c");

        Map<String, String> splitAttributes = SimpleDbAttributeValueSplitter.splitAttributeValuesWithExceedingLengths(rawAttributes);

        Map<String, String> recombinedAtts = SimpleDbAttributeValueSplitter.combineAttributeValuesWithExceedingLengths(splitAttributes);

        assertEquals(recombinedAtts.size(), rawAttributes.size());
    }


    @Test
    public void combineAttributeValue_should_return_initial_value() {
        Map<String, String> splitAttributes = new HashMap<>();
        splitAttributes.put(SimpleDbAttributeKeySplitter.convertKey("key", 2), "value2");
        splitAttributes.put(SimpleDbAttributeKeySplitter.convertKey("key", 1), "value1");
        splitAttributes.put(SimpleDbAttributeKeySplitter.convertKey("key", 3), "value3");

        List<String> keyGroup = new ArrayList<>(splitAttributes.keySet());

        String rawAttributeValue = SimpleDbAttributeValueSplitter.combineAttributeValues(keyGroup, splitAttributes);
        assertEquals("value1" + "value2" + "value3", rawAttributeValue);
    }


    @Test
    public void combineAttributeValue_should_return_initial_value_for_large_chunk_numbers() {
        Map<String, String> splitAttributes = new HashMap<>();
        StringBuilder expectedCombinedStringBuilder = new StringBuilder();
        for(int i = 0; i < 25; i++){
            splitAttributes.put(SimpleDbAttributeKeySplitter.convertKey("key", i), "value" +i);
            expectedCombinedStringBuilder.append("value"+i);
        }

        List<String> keyGroup = new ArrayList<>(splitAttributes.keySet());

        String rawAttributeValue = SimpleDbAttributeValueSplitter.combineAttributeValues(keyGroup, splitAttributes);

        assertEquals(expectedCombinedStringBuilder.toString(), rawAttributeValue);
    }



}
