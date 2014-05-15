package org.springframework.data.simpledb.core.entity;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.springframework.data.annotation.Reference;
import org.springframework.data.simpledb.util.EntityInformationSupport;

public class CustomSerializerWrapperTest {

	@Test
	public void testCustomSerialization(){
		CustomSerialized cs = new CustomSerialized();
		cs.string="WOO";
		
		EntityWrapper<CustomSerialized, String> sdbEntity = new EntityWrapper<CustomSerialized, String>(
				EntityInformationSupport.readEntityInformation(CustomSerialized.class), cs);
		final Map<String, String> attributes = sdbEntity.serialize();
		assertEquals("Wrong number of attributes", 1, attributes.size());
		assertEquals("Serialization is wrong", "serialized:"+"WOO"+":dezilaires", attributes.entrySet().iterator().next().getValue());
		
		final EntityWrapper<CustomSerialized, String> convertedEntity = new EntityWrapper<CustomSerialized, String>(
				EntityInformationSupport.readEntityInformation(CustomSerialized.class));
		convertedEntity.deserialize(attributes);
		
		assertEquals(cs, convertedEntity.getItem());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCustomSerialization_mismatchingAnnotations(){
		
		CustomSerializedError cs = new CustomSerializedError();
		cs.string="WOO";
		
		EntityWrapper<CustomSerializedError, String> sdbEntity = new EntityWrapper<CustomSerializedError, String>(
				EntityInformationSupport.readEntityInformation(CustomSerializedError.class), cs);
		final Map<String, String> attributes = sdbEntity.serialize();
	}
	
	class CustomSerializedError{
		@Reference
		@CustomSerialize(serializer = MyCustomSerializer.class)
		private String string;

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}
		
		
	}
	
	public static class CustomSerialized{
		@CustomSerialize(serializer = MyCustomSerializer.class)
		private String string;

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((string == null) ? 0 : string.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CustomSerialized other = (CustomSerialized) obj;
			if (string == null) {
				if (other.string != null)
					return false;
			} else if (!string.equals(other.string))
				return false;
			return true;
		}
		
		
	}
	
	public static class MyCustomSerializer implements CustomSerializer<String>{
		
		
		@Override
		public String serialize(String entity) {
			return "serialized:"+entity+":dezilaires";
		}

		@Override
		public String deserialize(String toDeserialize) {
			return toDeserialize.substring(11, toDeserialize.length()-11);
		}
		
	}
}
