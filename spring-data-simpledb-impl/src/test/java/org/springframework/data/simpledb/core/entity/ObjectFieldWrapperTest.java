package org.springframework.data.simpledb.core.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class ObjectFieldWrapperTest {


    private static final String SAMPLE_STRING_VALUE = "foo";


    public static class AClass {
        private Object object = null;

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    public static class JSONCompatibleClass {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    @Test
    public void should_not_serialize_null_Object_fields_as_attribute_keys() {

        AClass aDomainObject = new AClass();

        EntityWrapper<AClass, String> sdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class), aDomainObject);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertEquals(0, attributes.size());
    }


    @Test
    public void should_deserialize_serialized_null_Object_fields() {

        AClass aDomainObject = new AClass();

        EntityWrapper<AClass, String> sdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class), aDomainObject);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        EntityWrapper<AClass, String> newSdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class));
        newSdbEntity.deserialize(attributes);
        AClass returnedEntity = newSdbEntity.getItem();

        assertEquals(null, returnedEntity.getObject());
    }


    @Test
    public void should_serialize_Object_fields_to_JSON() {

        AClass aDomainObject = new AClass();
        JSONCompatibleClass aJSONObject = new JSONCompatibleClass();
        aJSONObject.setName(SAMPLE_STRING_VALUE);

        aDomainObject.setObject(aJSONObject);


        EntityWrapper<AClass, String> sdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class), aDomainObject);
        final Map<String, List<String>> attributes = sdbEntity.serialize();

        assertNotNull(attributes);


        List<String> serializedValue = attributes.values().iterator().next();
        assertTrue(serializedValue.size() == 1);

        String jsonString = serializedValue.get(0);

        assertTrue(jsonString.contains(SAMPLE_STRING_VALUE));
    }

    @Test
    public void should_deserialize_JSON_to_Object_field() {

        AClass aDomainObject = new AClass();
        JSONCompatibleClass aJSONObject = new JSONCompatibleClass();
        aJSONObject.setName(SAMPLE_STRING_VALUE);

        aDomainObject.setObject(aJSONObject);


        EntityWrapper<AClass, String> sdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class), aDomainObject);
        final Map<String, List<String>> attributes = sdbEntity.serialize();


        EntityWrapper<AClass, String> newSdbEntity = new EntityWrapper<>(this.<AClass>readEntityInformation(AClass.class));
        newSdbEntity.deserialize(attributes);

        AClass returnedEntity = newSdbEntity.getItem();
        JSONCompatibleClass deserializedJSONObject = (JSONCompatibleClass) returnedEntity.getObject();

        assertEquals(aJSONObject.getName(), deserializedJSONObject.getName());


    }


    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }
}

