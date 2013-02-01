package org.springframework.data.simpledb.core.entity;

import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class CoreTypeWrapperTest {

    @Test
    public void deserialize_should_return_serialized_core_types() {
        SampleCoreType coreType = new SampleCoreType();
        coreType.strField = "simpleDB";
        coreType.dateField = new Date(1);
        coreType.longField = Long.valueOf(1000L);

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SampleCoreType, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class), coreType);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

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
        final Map<String, List<String>> attributes = sdbEntity.serialize();

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
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertTrue(attributes.size() == 3);

        for(String attributeName : AttributeUtil.<SampleCoreType>getAttributeNamesThroughReflection(SampleCoreType.class)) {
            assertTrue(attributes.containsKey(attributeName));
        }

    }


    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }

    static class SampleCoreType {
        private String strField;
        private Date dateField;
        private Long longField;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SampleCoreType)) return false;

            SampleCoreType that = (SampleCoreType) o;

            if (dateField != null ? !dateField.equals(that.dateField) : that.dateField != null) return false;
            if (strField != null ? !strField.equals(that.strField) : that.strField!= null) return false;
            if (longField != null ? !longField.equals(that.longField) : that.longField != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + strField.hashCode();
            result = 31 * result + (dateField == null ? 0 : dateField.hashCode());
            return result;
        }
    }
}
