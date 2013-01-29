package org.springframework.data.simpledb.util;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.DomainPrefix;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;

public class MetadataParserFieldDefinitionTest {

	private static final String SAMPLE_ITEM = "SAMPLE_ITEM";


    @Test
    public void getSupportedFields_should_return_list_of_primitives_wrappers() throws Exception {
        List<Field> returnedPrimitiveWrappers = MetadataParser.getSupportedFields(new SamplePrimitivesWrapper());


        assertFalse(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("id"))) ;

        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("integerField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("byteField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("floatField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("doubleField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("longField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("dateField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("stringField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("charField"))) ;
        assertTrue(returnedPrimitiveWrappers.contains(SamplePrimitivesWrapper.class.getDeclaredField("boolField"))) ;
    }

    @Test
    public void getPrimitiveCollectionFields_should_return_ObjectType() throws Exception {
        List<Field> returnedPrimitiveCollections = MetadataParser.getPrimitiveCollectionFields(new SamplePrimitivesCollection());

        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("intPrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("longPrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("doublePrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("booleanPrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("shortPrimitives"))) ;

    }


    static class SamplePrimitivesWrapper {
        private String id;
        private Integer integerField;
        private Double doubleField;
        private Float floatField;
        private Short shortField;
        private Long longField;
        private String stringField;
        private Date dateField;
        private Boolean boolField;
        private Character charField;
        private Byte byteField;
    }

    static class SamplePrimitivesCollection {
        private String id;
        private int[] intPrimitives;
        private long[] longPrimitives;
        private double[] doublePrimitives;
        private boolean[] booleanPrimitives;
        private short[] shortPrimitives;
    }


}
