package org.springframework.data.simpledb.core.entity;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class EntityWrapperCollectionTest {

    static class SamplePrimitivesArray {
        @Id private String id;
        private long[] longPrimitives;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SamplePrimitivesArray)) return false;

            SamplePrimitivesArray that = (SamplePrimitivesArray) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (!Arrays.equals(longPrimitives, that.longPrimitives)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(longPrimitives);
            return result;
        }
    }

    static class SampleCoreCollection {
        @Id private String id;
        private Set<String> simpleDbCollectionAttributes;
    }

    static class SampleCoreType {
        @Id private String id;
        private String str;
        private Date date;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SampleCoreType)) return false;

            SampleCoreType that = (SampleCoreType) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (date != null ? !date.equals(that.date) : that.date != null) return false;
            if (!str.equals(that.str)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + str.hashCode();
            result = 31 * result + (date == null ? 0 : date.hashCode());
            return result;
        }
    }



    @Test public void test_serialize_deserialize_core_types() {
        SampleCoreType coreType = new SampleCoreType();
        coreType.str = "simpleDB";
        coreType.date = new Date(1);

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SampleCoreType, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class), coreType);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* ----------------------- De-serialize Representation ------------------------ */
        final EntityWrapper<SampleCoreType, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreType>readEntityInformation(SampleCoreType.class));
        convertedEntity.deserialize(attributes);

        assertTrue(coreType.equals(convertedEntity.getItem()));

    }

    @Test public void test_serialize_deserialize_primitives_array() {
        SamplePrimitivesArray primitivesArray = new SamplePrimitivesArray();
        primitivesArray.longPrimitives = new long[]{1L, 2L, 3L};

        EntityWrapper<SamplePrimitivesArray, String> sdbEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class), primitivesArray);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SamplePrimitivesArray, String> convertedEntity = new EntityWrapper<>(this.<SamplePrimitivesArray>readEntityInformation(SamplePrimitivesArray.class));
        convertedEntity.deserialize(attributes);

        assertTrue(primitivesArray.equals(convertedEntity.getItem()));

    }

    @Test public void test_serialize_deserialize_collections_of_core_types() {
//        EntityWrapper<SampleCoreCollection, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class), aDomain);
//        final Map<String, List<String>> attributes = sdbEntity.serialize();
//
//        /* convert back */
//        final EntityWrapper<SampleCoreCollection, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreCollection>readEntityInformation(SampleCoreCollection.class));
//        convertedEntity.deserialize(attributes);
//
//        assertTrue(aDomain.equals(convertedEntity.getItem()));

    }


    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }
}
