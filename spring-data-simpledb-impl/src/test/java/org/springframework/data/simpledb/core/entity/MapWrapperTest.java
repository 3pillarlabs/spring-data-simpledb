package org.springframework.data.simpledb.core.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class MapWrapperTest {

	@Test
	public void maps_of_byte_keys_are_converted_back_as_maps_of_String_keys() {
		SampleCoreMap simpleMap = new SampleCoreMap();
		simpleMap.setMapOfByte(new HashMap<Byte, Byte>());
		simpleMap.getMapOfByte().put(Byte.valueOf("1"), Byte.valueOf("1"));

		EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class), simpleMap);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* convert back */
		final EntityWrapper<SampleCoreMap, String> convertedEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class));
		convertedEntity.deserialize(attributes);

		SampleCoreMap returnedMap = convertedEntity.getItem();
		assertEquals(returnedMap.getMapOfByte().keySet().iterator().next(), "1");
	}

	@Test
	public void serialize_deserialize_map_of_strings() {
		SampleCoreMap simpleMap = new SampleCoreMap();
		simpleMap.setMapOfStrings(new HashMap<String, String>());
		simpleMap.getMapOfStrings().put("first", "firstValue");

		EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class), simpleMap);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* convert back */
		final EntityWrapper<SampleCoreMap, String> convertedEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class));
		convertedEntity.deserialize(attributes);

		assertTrue(simpleMap.equals(convertedEntity.getItem()));

	}

	@Test
	public void deserialize_should_return_null_for_not_instantiated_maps() {
		SampleCoreMap simpleMap = new SampleCoreMap();

		EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class), simpleMap);
		final Map<String, String> attributes = sdbEntity.serialize();

		/* convert back */
		final EntityWrapper<SampleCoreMap, String> convertedEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class));
		convertedEntity.deserialize(attributes);

		assertTrue(simpleMap.equals(convertedEntity.getItem()));

	}

	@Test
	public void serialize_should_return_attribute_name_key() {
		SampleCoreMap simpleMap = new SampleCoreMap();
		simpleMap.setMapOfStrings(new HashMap<String, String>());
		simpleMap.getMapOfStrings().put("first", "firstValue");
		simpleMap.setMapOfByte(new HashMap<Byte, Byte>());
		simpleMap.getMapOfByte().put(Byte.valueOf("1"), Byte.valueOf("1"));

		/* ----------------------- Serialize Representation ------------------------ */
		EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<SampleCoreMap, String>(
				this.<SampleCoreMap> readEntityInformation(SampleCoreMap.class), simpleMap);
		final Map<String, String> attributes = sdbEntity.serialize();

		assertTrue(attributes.size() == 2);

		for(String attributeName : AttributeUtil
				.<SampleCoreMap> getAttributeNamesThroughReflection(SampleCoreMap.class)) {
			assertTrue(attributes.containsKey(attributeName));
		}

	}

	private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
		return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E> getMetadata(clazz);
	}

	public static class SampleCoreMap {

		private Map<String, String> mapOfStrings;
		private Map<Byte, Byte> mapOfByte;

		public Map<String, String> getMapOfStrings() {
			return mapOfStrings;
		}

		public void setMapOfStrings(Map<String, String> mapOfStrings) {
			this.mapOfStrings = mapOfStrings;
		}

		public Map<Byte, Byte> getMapOfByte() {
			return mapOfByte;
		}

		public void setMapOfByte(Map<Byte, Byte> mapOfByte) {
			this.mapOfByte = mapOfByte;
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
