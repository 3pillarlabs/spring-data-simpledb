package org.springframework.data.simpledb.core;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class SimpleDbRequestBuilderTest {
    @Test
    public void splitAttributeChunks_should_split_attributes_longer_than_simple_db_limitation() throws Exception {


        Map<String, String> attributes = new LinkedHashMap<>();
        for (int i=0; i < SimpleDbRequestBuilder.MAX_NUMBER_OF_ATTRIBUTES_PER_SIMPLE_DB_REQUEST + 100; i++){
            attributes.put("Key: " + i, "Value: " + i);
        }

        List<Map<String,String>> chunks = SimpleDbRequestBuilder.splitToSimpleDbSupportedChunks(attributes);
        assertEquals(2, chunks.size());

        Map<String, String> firstChunk = chunks.get(0);
        assertEquals(SimpleDbRequestBuilder.MAX_NUMBER_OF_ATTRIBUTES_PER_SIMPLE_DB_REQUEST, firstChunk.size());


        Map<String, String> secondChunk = chunks.get(1);
        assertEquals(100, secondChunk.size());
        assertTrue(secondChunk.containsKey("Key: " + 256));
        assertTrue(secondChunk.containsKey("Key: " + 355));


    }
}
