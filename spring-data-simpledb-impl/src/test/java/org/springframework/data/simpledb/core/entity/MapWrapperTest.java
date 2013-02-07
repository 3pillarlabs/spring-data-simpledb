package org.springframework.data.simpledb.core.entity;

import org.junit.Test;
import org.springframework.data.simpledb.core.entity.util.AttributeUtil;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class MapWrapperTest {



    @Test
    public void serialize_deserialize_map_of_core_types() {
        SampleCoreMap simpleMap = new SampleCoreMap();
        simpleMap.mapOfStrings = new HashMap<>();
        simpleMap.mapOfStrings.put("first", "firstValue");


        EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreMap>readEntityInformation(SampleCoreMap.class), simpleMap);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreMap, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreMap>readEntityInformation(SampleCoreMap.class));
        convertedEntity.deserialize(attributes);

        assertTrue(simpleMap.equals(convertedEntity.getItem()));

    }


    @Test public void deserialize_should_return_null_for_not_instantiated_maps() {
        SampleCoreMap simpleMap = new SampleCoreMap();

        EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreMap>readEntityInformation(SampleCoreMap.class), simpleMap);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        /* convert back */
        final EntityWrapper<SampleCoreMap, String> convertedEntity = new EntityWrapper<>(this.<SampleCoreMap>readEntityInformation(SampleCoreMap.class));
        convertedEntity.deserialize(attributes);

        assertTrue(simpleMap.equals(convertedEntity.getItem()));

    }

    @Test
    public void serialize_should_return_attribute_name_key() {
        SampleCoreMap simpleMap = new SampleCoreMap();
        simpleMap.mapOfStrings = new HashMap<>();
        simpleMap.mapOfStrings.put("first", "firstValue");

        /* ----------------------- Serialize Representation ------------------------ */
        EntityWrapper<SampleCoreMap, String> sdbEntity = new EntityWrapper<>(this.<SampleCoreMap>readEntityInformation(SampleCoreMap.class), simpleMap);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertTrue(attributes.size() == 1);

        for(String attributeName : AttributeUtil.<SampleCoreMap>getAttributeNamesThroughReflection(SampleCoreMap.class)) {
            assertTrue(attributes.containsKey(attributeName));
        }

    }

    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }


    static class SampleCoreMap {
        private Map<String, String> mapOfStrings;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SampleCoreMap that = (SampleCoreMap) o;

            if (mapOfStrings != null ? !mapOfStrings.equals(that.mapOfStrings) : that.mapOfStrings != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return mapOfStrings != null ? mapOfStrings.hashCode() : 0;
        }
    }
}
