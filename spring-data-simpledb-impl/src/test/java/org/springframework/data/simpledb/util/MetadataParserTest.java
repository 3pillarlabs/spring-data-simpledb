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
	public void should_read_annotated_id_value() {
		SampleAnnotatedId entity = new SampleAnnotatedId();
		String itemName = MetadataParser.getItemName(entity);
		assertEquals(SAMPLE_ITEM, itemName);
	}

	@Test
	public void should_read_Attributes() {
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
	public void should_read_declared_id_value() {
		SampleDeclaredId entity = new SampleDeclaredId();
		String itemName = MetadataParser.getItemName(entity);
		assertEquals(SAMPLE_ITEM, itemName);
	}

	@Test
	public void getPrimitiveFields_should_return_list_of_primitives_bypassing_ID_Attributes_and_Transient()
			throws Exception {
		List<Field> returnedPrimitives = MetadataParser.getSupportedFields(SampleDeclaredPrimitives.class);

		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("intPrimitive")));
		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("longPrimitive")));
		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("doublePrimitive")));
		assertTrue(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("booleanPrimitive")));

		assertFalse(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("shouldBeTransient")));
		assertFalse(returnedPrimitives.contains(SampleDeclaredPrimitives.class.getDeclaredField("idField")));
		assertFalse(returnedPrimitives.contains(SampleDeclaredPrimitives.class
				.getDeclaredField("someUsefullAttributes")));
	}

	@Test
	public void getPrimitiveFields_should_return_list_of_primitives_bypassing_ID_by_convention() throws Exception {
		List<Field> returnedPrimitivesConvention = MetadataParser
				.getSupportedFields(SampleDeclaredPrimitivesConventionId.class);

		assertFalse(returnedPrimitivesConvention.contains(SampleDeclaredPrimitivesConventionId.class
				.getDeclaredField("id")));
		assertTrue(returnedPrimitivesConvention.contains(SampleDeclaredPrimitivesConventionId.class
				.getDeclaredField("intPrimitive")));
	}

	@Test(expected = RuntimeException.class)
	public void two_ids_from_entity_should_fail_on_runtime() {
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

		@Attributes
		Map<String, String> someUsefullAttributes = new HashMap<String, String>();

		public int getShouldBeTransient() {
			return shouldBeTransient;
		}

		public void setShouldBeTransient(int shouldBeTransient) {
			this.shouldBeTransient = shouldBeTransient;
		}

		public int getIdField() {
			return idField;
		}

		public void setIdField(int idField) {
			this.idField = idField;
		}

		public int getIntPrimitive() {
			return intPrimitive;
		}

		public void setIntPrimitive(int intPrimitive) {
			this.intPrimitive = intPrimitive;
		}

		public long getLongPrimitive() {
			return longPrimitive;
		}

		public void setLongPrimitive(long longPrimitive) {
			this.longPrimitive = longPrimitive;
		}

		public double getDoublePrimitive() {
			return doublePrimitive;
		}

		public void setDoublePrimitive(double doublePrimitive) {
			this.doublePrimitive = doublePrimitive;
		}

		public boolean isBooleanPrimitive() {
			return booleanPrimitive;
		}

		public void setBooleanPrimitive(boolean booleanPrimitive) {
			this.booleanPrimitive = booleanPrimitive;
		}

		public Map<String, String> getSomeUsefullAttributes() {
			return someUsefullAttributes;
		}

		public void setSomeUsefullAttributes(Map<String, String> someUsefullAttributes) {
			this.someUsefullAttributes = someUsefullAttributes;
		}
	}

	static class SampleDeclaredPrimitivesConventionId {

		String id;
		int intPrimitive;

		public int getIntPrimitive() {
			return intPrimitive;
		}

		public void setIntPrimitive(int intPrimitive) {
			this.intPrimitive = intPrimitive;
		}
	}

	static class TwoIdsShouldFail {

		String id;
		@Id
		String anotherId;
	}

}
