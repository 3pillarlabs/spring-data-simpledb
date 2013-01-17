package org.springframework.data.simpledb.annotation;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AnnotationParserTest {

    private static final String SAMPLE_DOMAIN = "SAMPLE_DOMAIN";
    private static final String SAMPLE_ITEM = "SAMPLE_ITEM";

    @Domain(SAMPLE_DOMAIN)
    static class Sample {
        @ItemName
        private String itemName = SAMPLE_ITEM;

        @Attributes
        private Map<String, String> atts = new LinkedHashMap<String, String>();
    }

    @Test
    public void should_read_Domain_value(){
        String domain = AnnotationParser.getDomain(Sample.class);
        assertEquals(SAMPLE_DOMAIN, domain);
    }

    @Test
    public void should_read_ItemName_value(){
        Sample entity = new Sample();
        String itemName = AnnotationParser.getItemName(entity);
        assertEquals(SAMPLE_ITEM, itemName);
    }

    @Test
    public void should_read_Attributes(){
        Sample entity = new Sample();
        Map<String, String> attributes = AnnotationParser.getAttributes(entity);
        assertNotNull(attributes);
    }

}
