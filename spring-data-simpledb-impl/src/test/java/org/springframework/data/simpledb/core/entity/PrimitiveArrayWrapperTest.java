package org.springframework.data.simpledb.core.entity;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class PrimitiveArrayWrapperTest {

    @Test
    public void deserialize_should_return_serialized_primitives_array() {
        SamplePrimitivesArray primitivesArray = new SamplePrimitivesArray();
        primitivesArray.longPrimitives = new long[]{123L, 234L, 345L};

        EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SamplePrimitivesArray, String> convertedEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class));
        convertedEntity.deserialize(attributes);

        assertTrue(primitivesArray.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_of_null_values_should_return_void_after_deserialization() {
        // ----- Properties are NOT assigned ----- //
        SamplePrimitivesArray primitivesArray = new SamplePrimitivesArray();

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* ----------------------- De-serialize Representation ------------------------ */
        final EntityWrapper<SamplePrimitivesArray, String> convertedEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class));
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
        primitivesArray.longPrimitives = new long[]{300L, 400L, 500L};

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertTrue(attributes.size() == 1);

        for(String attributeName : this.<SamplePrimitivesArray>getAttributeNamesThroughReflection(SamplePrimitivesArray.class)) {
            assertTrue(attributes.containsKey(attributeName));
        }

    }

    /* ----- Utility method to fetch AttributeNames of declared Properties from Parameter Class ------ */
    private <T> List<String> getAttributeNamesThroughReflection(Class<T> entityClazz) {
        List<String> attributeNames = new ArrayList<>();

        for(Field eachDeclaredField : Arrays.asList(entityClazz.getDeclaredFields())) {
            attributeNames.add(eachDeclaredField.getName());
        }

        return attributeNames;
    }



    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }


    static class SamplePrimitivesArray {
        private long[] longPrimitives;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SamplePrimitivesArray)) return false;

            SamplePrimitivesArray that = (SamplePrimitivesArray) o;

            if (!Arrays.equals(longPrimitives, that.longPrimitives)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + Arrays.hashCode(longPrimitives);
            return result;
        }
    }
}
