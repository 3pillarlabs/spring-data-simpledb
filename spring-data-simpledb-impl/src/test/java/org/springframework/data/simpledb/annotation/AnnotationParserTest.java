package org.springframework.data.simpledb.annotation;

import org.junit.Test;
import org.springframework.data.annotation.Id;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AnnotationParserTest {

    private static final String SAMPLE_DOMAIN = "SAMPLE_DOMAIN";
    private static final String SAMPLE_ITEM = "SAMPLE_ITEM";

    @Domain(SAMPLE_DOMAIN)
    static class SampleAnnotatedId {
        @Id
        private String itemName = SAMPLE_ITEM;

        @Attributes
        private Map<String, String> atts = new LinkedHashMap<String, String>();
    }

    @Test
    public void should_read_Domain_value(){
        String domain = AnnotationParser.getDomain(SampleAnnotatedId.class);
        assertEquals(SAMPLE_DOMAIN, domain);
    }

    @Test
    public void should_read_annotated_id_value(){
        SampleAnnotatedId entity = new SampleAnnotatedId();
        String itemName = AnnotationParser.getItemName(entity);
        assertEquals(SAMPLE_ITEM, itemName);
    }

    @Test
    public void should_read_Attributes(){
        SampleAnnotatedId entity = new SampleAnnotatedId();
        Map<String, String> attributes = AnnotationParser.getAttributes(entity);
        assertNotNull(attributes);
    }



    @Domain(SAMPLE_DOMAIN)
    static class SampleDeclaredId {

        private String id = SAMPLE_ITEM;

        @Attributes
        private Map<String, String> atts = new LinkedHashMap<String, String>();
    }

    @Test
    public void should_read_declared_id_value(){
        SampleDeclaredId entity = new SampleDeclaredId();
        String itemName = AnnotationParser.getItemName(entity);
        assertEquals(SAMPLE_ITEM, itemName);
    }


}
