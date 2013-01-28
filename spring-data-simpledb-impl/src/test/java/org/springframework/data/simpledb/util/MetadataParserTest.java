package org.springframework.data.simpledb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.DomainPrefix;

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
		@SuppressWarnings("unused")
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
		List<Field> returnedPrimitives = MetadataParser.getPrimitiveFields(new SampleDeclaredPrimitives());

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
		List<Field> returnedPrimitivesConvention = MetadataParser.getPrimitiveFields(new SampleDeclaredPrimitivesConventionId());

		assertFalse(returnedPrimitivesConvention.contains(SampleDeclaredPrimitivesConventionId.class.getDeclaredField("id"))) ;
		assertTrue(returnedPrimitivesConvention.contains(SampleDeclaredPrimitivesConventionId.class.getDeclaredField("intPrimitive"))) ;
	}

	@Test(expected = RuntimeException.class)
	public void two_ids_from_entity_shold_fail_on_runtime() {
		MetadataParser.getIdField(new TwoIdsShouldFail());
	}

	@SuppressWarnings("unused")
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

	static class SampleDeclaredPrimitivesConventionId {
		String id;
		int intPrimitive;
	}

	static class TwoIdsShouldFail {
		String id;
		@Id String anotherId;
	}
	
}
