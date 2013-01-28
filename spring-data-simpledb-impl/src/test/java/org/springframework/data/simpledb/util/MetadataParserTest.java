package org.springframework.data.simpledb.util;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;

import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.DomainPrefix;

import static org.junit.Assert.*;

public class MetadataParserTest {

	private static final String SAMPLE_ITEM = "SAMPLE_ITEM";


	@DomainPrefix(value = "testDB")
	static class SampleAnnotatedId {
		@Id
		private String itemName = SAMPLE_ITEM;

		@Attributes
		private Map<String, String> atts = new LinkedHashMap<String, String>();
	}

	@Test
	public void should_read_Domain_value(){
		String domain = MetadataParser.getDomain(SampleAnnotatedId.class);
		assertEquals("testDB.sampleAnnotatedId", domain);
	}

	@Test
	public void should_read_annotated_id_value(){
		SampleAnnotatedId entity = new SampleAnnotatedId();
		String itemName = MetadataParser.getItemName(entity);
		assertEquals(SAMPLE_ITEM, itemName);
	}

	@Test
	public void should_read_Attributes(){
		SampleAnnotatedId entity = new SampleAnnotatedId();
		Map<String, String> attributes = MetadataParser.getAttributes(entity);
		assertNotNull(attributes);
	}



	static class SampleDeclaredId {

		private String id = SAMPLE_ITEM;

		@Attributes
		private Map<String, String> atts = new LinkedHashMap<String, String>();
	}

	@Test
	public void should_read_declared_id_value(){
		SampleDeclaredId entity = new SampleDeclaredId();
		String itemName = MetadataParser.getItemName(entity);
		assertEquals(SAMPLE_ITEM, itemName);
	}

	@Test
	public void should_read_declared_domain_value(){
		SampleDeclaredId entity = new SampleDeclaredId();
		String domain = MetadataParser.getDomain(entity.getClass());
		assertEquals("sampleDeclaredId", domain);
	}

	@Test
	public void getPrimitiveFields_should_return_list_of_primitives_bypassing_ID_Atrributes_and_Transient() throws Exception {
		List<Field> returnedPrimitives = MetadataParser.getPrimitiveHolders(new SampleDeclaredPrimitives());

		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("intPrimitive")));
		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("longPrimitive")));
		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("doublePrimitive")));
		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("booleanPrimitive")));

		assertFalse(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("shouldBeTransient"))) ;
		assertFalse(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("idField"))) ;
		assertFalse(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("someUsefullAttributes")));
	}
	
	@Test
	public void getPrimitiveFields_should_return_list_of_primitives_bypassing_ID_by_convention() throws Exception {
		List<Field> returnedPrimitivesConvention = MetadataParser.getPrimitiveHolders(new SampleDeclaredPrimitivesConventionId());

		assertFalse(returnedPrimitivesConvention.contains(SampleDeclaredPrimitivesConventionId.class.getDeclaredField("id"))) ;
		assertTrue(returnedPrimitivesConvention.contains(SampleDeclaredPrimitivesConventionId.class.getDeclaredField("intPrimitive"))) ;
	}

    @Test public void getPrimitiveHolders_should_return_list_of_primitives_wrappers() throws Exception {
        List<Field> returnedPrimitiveWrappers = MetadataParser.getPrimitiveHolders(new SamplePrimitivesWrapper());


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

    @Test public void isPrimitivesCollectionType_should_return_ObjectType() throws Exception {
        List<Field> returnedPrimitiveCollections = MetadataParser.getPrimitiveCollectionHolders(new SamplePrimitivesCollection());

        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("intPrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("longPrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("doublePrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("booleanPrimitives"))) ;
        assertTrue(returnedPrimitiveCollections.contains(SamplePrimitivesCollection.class.getDeclaredField("shortPrimitives"))) ;

    }

	@Test(expected = RuntimeException.class)
	public void two_ids_from_entity_shold_fail_on_runtime() {
		MetadataParser.getIdField(new TwoIdsShouldFail());
	}

	static class SampleDeclaredPrimitives {
		@Transient
		private int shouldBeTransient;
		@Id
		private int idField;
		private int intPrimitive;
		private long longPrimitive;
		private double doublePrimitive;
		private boolean booleanPrimitive;

		@Attributes Map<String, String> someUsefullAttributes = new HashMap<>();
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

    static class SampleDeclaredPrimitivesConventionId {
		String id;
		int intPrimitive;
	}

	static class TwoIdsShouldFail {
		String id;
		@Id String anotherId;
	}

}
