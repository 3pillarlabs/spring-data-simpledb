package org.springframework.data.simpledb.annotation;

import org.junit.Test;
import org.springframework.data.annotation.Id;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DomainParsingTest {

    static class SampleDomain {
    }

    @Test
    public void should_read_simple_Domain_value(){
        String domain = MetadataParser.getDomain(SampleDomain.class);
        assertEquals("sample_domain", domain);
    }

    static class SampleCRUDDomain {
    }

    @Test
    public void should_read_Domain_with_acronyms(){
        String domain = MetadataParser.getDomain(SampleCRUDDomain.class);
        assertEquals("sample_crud_domain", domain);
    }

    static class Single {
    }

    @Test
    public void should_read_Domain_with_single_word(){
        String domain = MetadataParser.getDomain(Single.class);
        assertEquals("single", domain);
    }


}
