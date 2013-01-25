package org.springframework.data.simpledb.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class SimpleDbEntityTest {

    static class SampleDomainObject {
    	
        @Id
        private String itemName = null;

        public String getItemName() {
            return itemName;
        }
        public void setItemName(String itemName) {
            this.itemName = itemName;
        }
    }

    @Test
    public void generateId_should_populate_itemName_of_Item(){
        SampleDomainObject object = new SampleDomainObject();
        SimpleDbEntity<SampleDomainObject, String> sdbEntity = new SimpleDbEntity<>(this.<SampleDomainObject>readEntityInformation(SampleDomainObject.class), object);
        sdbEntity.generateIdIfNotSet();
        assertNotNull(object.getItemName());

    }

    @Test
    public void generateId_should_not_overwrite_existing_id(){
        SampleDomainObject object = new SampleDomainObject();
        object.setItemName("gigi");
        SimpleDbEntity<SampleDomainObject, String> sdbEntity = new SimpleDbEntity<>(this.<SampleDomainObject>readEntityInformation(SampleDomainObject.class), object);
        sdbEntity.generateIdIfNotSet();
        assertEquals("gigi",object.getItemName());
    }

    @Test
    public void generateId_should_create_distinct_values(){
        SampleDomainObject object1 = new SampleDomainObject();
        SampleDomainObject object2 = new SampleDomainObject();

        SimpleDbEntity<SampleDomainObject, String> sdbEntity1 = new SimpleDbEntity<>(this.<SampleDomainObject>readEntityInformation(SampleDomainObject.class), object1);
        sdbEntity1.generateIdIfNotSet();
        SimpleDbEntity<SampleDomainObject, String> sdbEntity2 = new SimpleDbEntity<>(this.<SampleDomainObject>readEntityInformation(SampleDomainObject.class), object2);
        sdbEntity2.generateIdIfNotSet();

        assertNotEquals(object1.getItemName(), object2.getItemName());
    }
    
    @SuppressWarnings("unchecked")
	private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }
    
    @Test
    public void test_getSerializedPrimitiveAttributes() throws ParseException {
    	final SampleEntity entity = new SampleEntity();
    	entity.setIntField(11);
    	entity.setLongField(123);
    	entity.setShortField((short) -12);
    	entity.setFloatField(-0.01f);
    	entity.setDoubleField(1.2d);
    	entity.setByteField((byte) 1);
    	entity.setBooleanField(Boolean.TRUE);
    	
        SimpleDbEntity<SampleEntity, String> sdbEntity = new SimpleDbEntity<>(this.<SampleEntity>readEntityInformation(SampleEntity.class), entity);

        assertNotNull(sdbEntity);
        
        final Map<String, List<String>> attributes = sdbEntity.getSerializedPrimitiveAttributes();
        assertNotNull(attributes);

        /* test int field */
        List<String> intValues = attributes.get("intField");
        assertNotNull(intValues);
        assertFalse(intValues.isEmpty());
        assertTrue(intValues.size() == 1);
        assertEquals(entity.getIntField(), ((Integer)SimpleDBAttributeConverter.toDomainFieldPrimitive(intValues.get(0), Integer.class)).intValue());
        
        /* test long field */
        List<String> longValues = attributes.get("longField");
        assertEquals(entity.getLongField(), ((Long)SimpleDBAttributeConverter.toDomainFieldPrimitive(longValues.get(0), Long.class)).longValue());
        
        /* test short field */
        List<String> shortValues = attributes.get("shortField");
        assertEquals(entity.getShortField(), ((Short)SimpleDBAttributeConverter.toDomainFieldPrimitive(shortValues.get(0), Short.class)).shortValue());

        /* test float field */
        List<String> floatValues = attributes.get("floatField");
        assertTrue(entity.getFloatField() == ((Float)SimpleDBAttributeConverter.toDomainFieldPrimitive(floatValues.get(0), Float.class)).floatValue());

        /* test double field */
        List<String> doubleValues = attributes.get("doubleField");
        assertTrue(entity.getDoubleField() == ((Double)SimpleDBAttributeConverter.toDomainFieldPrimitive(doubleValues.get(0), Double.class)).doubleValue());

        /* test byte field */
        List<String> byteValues = attributes.get("byteField");
        assertTrue(entity.getByteField() == ((Byte)SimpleDBAttributeConverter.toDomainFieldPrimitive(byteValues.get(0), Byte.class)).byteValue());

        /* test boolean field */
        List<String> booleanValues = attributes.get("booleanField");
        assertTrue(entity.getBooleanField() == ((Boolean)SimpleDBAttributeConverter.toDomainFieldPrimitive(booleanValues.get(0), Boolean.class)).booleanValue());
    }
}
