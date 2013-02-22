package org.springframework.data.simpledb.core.entity;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class CoreTypeWrapperTest {

	@Test
	public void deserialize_should_return_serialized_core_types() {
		SampleCoreType coreType = new SampleCoreType();
		coreType.strField = "simpleDB";
		coreType.dateField = new Date(1);
		coreType.longField = Long.valueOf(1000L);

		/* ----------------------- Serialize Representation ------------------------ */
		EntityWrapper<SampleCoreType, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class), coreType);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* ----------------------- De-serialize Representation ------------------------ */
		final EntityWrapper<SampleCoreType, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class));
		convertedEntity.deserialize(attributes);

		assertTrue(coreType.equals(convertedEntity.getItem()));
	}

	@Test
	public void serialize_of_null_values_should_return_void_after_deserialization() {
		// ----- Properties are NOT assigned ----- //
		SampleCoreType coreType = new SampleCoreType();

		/* ----------------------- Serialize Representation ------------------------ */
		EntityWrapper<SampleCoreType, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class), coreType);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* ----------------------- De-serialize Representation ------------------------ */
		final EntityWrapper<SampleCoreType, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class));
		convertedEntity.deserialize(attributes);

		assertTrue(coreType.equals(convertedEntity.getItem()));
		assertTrue(convertedEntity.getItem().strField == null);
		assertTrue(convertedEntity.getItem().dateField == null);
		assertTrue(convertedEntity.getItem().longField == null);

	}

	@Test
	public void serialize_should_return_attribute_name_key() {
		SampleCoreType coreType = new SampleCoreType();
		coreType.strField = "simpleDB";
		coreType.longField = Long.valueOf(1000L);
		coreType.dateField = Calendar.getInstance().getTime();

		/* ----------------------- Serialize Representation ------------------------ */
		EntityWrapper<SampleCoreType, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class), coreType);
		final Map<String, String> attributes = sdbEntity.serialize();

		assertTrue(attributes.size() == 3);

		for(String attributeName : AttributeUtil.<SampleCoreType>getAttributeNamesThroughReflection(SampleCoreType.class)) {
			assertTrue(attributes.containsKey(attributeName));
		}

	}


	private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
		return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
	}

	public static class SampleCoreType {
		private String strField;
		private Date dateField;
		private Long longField;

		public String getStrField() {
			return strField;
		}

		public void setStrField(String strField) {
			this.strField = strField;
		}

		public Date getDateField() {
			return dateField;
		}

		public void setDateField(Date dateField) {
			this.dateField = dateField;
		}

		public Long getLongField() {
			return longField;
		}

		public void setLongField(Long longField) {
			this.longField = longField;
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
