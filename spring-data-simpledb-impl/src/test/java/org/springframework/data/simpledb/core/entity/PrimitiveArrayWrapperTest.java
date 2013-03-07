package org.springframework.data.simpledb.core.entity;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class PrimitiveArrayWrapperTest {

	@Test
	public void deserialize_should_return_serialized_primitives_array() {
		SamplePrimitivesArray primitivesArray = new SamplePrimitivesArray();
		primitivesArray.longPrimitives = new long[] { 123L, 234L, 345L };

		EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<SamplePrimitivesArray, String>(
				this.<SamplePrimitivesArray> readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* convert back */
		final EntityWrapper<SamplePrimitivesArray, String> convertedEntity = new EntityWrapper<SamplePrimitivesArray, String>(
				this.<SamplePrimitivesArray> readEntityInformation(SamplePrimitivesArray.class));
		convertedEntity.deserialize(attributes);

		assertTrue(primitivesArray.equals(convertedEntity.getItem()));

	}

	@Test
	public void serialize_of_null_values_should_return_void_after_deserialization() {
		// ----- Properties are NOT assigned ----- //
		SamplePrimitivesArray primitivesArray = new SamplePrimitivesArray();

		/* ----------------------- Serialize Representation ------------------------ */
		EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<SamplePrimitivesArray, String>(
				this.<SamplePrimitivesArray> readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* ----------------------- De-serialize Representation ------------------------ */
		final EntityWrapper<SamplePrimitivesArray, String> convertedEntity = new EntityWrapper<SamplePrimitivesArray, String>(
				this.<SamplePrimitivesArray> readEntityInformation(SamplePrimitivesArray.class));
		convertedEntity.deserialize(attributes);

		assertTrue(primitivesArray.equals(convertedEntity.getItem()));
		assertTrue(convertedEntity.getItem().longPrimitives == null);

	}

	/**
	 * In order for this method to test the actual attributes key, the serialization works with a non-null instance
	 */
	@Test
	public void serialize_should_return_attribute_name_key() {
		SamplePrimitivesArray primitivesArray = new SamplePrimitivesArray();
		primitivesArray.longPrimitives = new long[] { 300L, 400L, 500L };

		/* ----------------------- Serialize Representation ------------------------ */
		EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<SamplePrimitivesArray, String>(
				this.<SamplePrimitivesArray> readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
		final Map<String, String> attributes = sdbEntity.serialize();

		assertTrue(attributes.size() == 1);

		for(String attributeName : AttributeUtil
				.<SamplePrimitivesArray> getAttributeNamesThroughReflection(SamplePrimitivesArray.class)) {
			assertTrue(attributes.containsKey(attributeName));
		}

	}

	private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
		return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E> getMetadata(clazz);
	}

	public static class SamplePrimitivesArray {

		private long[] longPrimitives;

		public long[] getLongPrimitives() {
			return longPrimitives;
		}

		public void setLongPrimitives(long[] longPrimitives) {
			this.longPrimitives = longPrimitives;
		}

		@Override
		public boolean equals(Object o) {
			return EqualsBuilder.reflectionEquals(this, o);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}
	}
}
