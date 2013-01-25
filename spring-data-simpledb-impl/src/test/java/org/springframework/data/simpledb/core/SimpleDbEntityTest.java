package org.springframework.data.simpledb.core;

import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleDbEntityTest {

    @Test
    public void generateId_should_populate_itemName_of_Item(){
        SimpleDbSampleEntity object = new SimpleDbSampleEntity();
        SimpleDbEntity sdbEntity = new SimpleDbEntity(SimpleDbSampleEntity.entityInformation(), object);
        sdbEntity.generateIdIfNotSet();
        assertNotNull(object.getItemName());

    }

    @Test
    public void generateId_should_not_overwrite_existing_id(){
        SimpleDbSampleEntity object = new SimpleDbSampleEntity();
        object.setItemName("gigi");
        SimpleDbEntity sdbEntity = new SimpleDbEntity(SimpleDbSampleEntity.entityInformation(), object);
        sdbEntity.generateIdIfNotSet();
        assertEquals("gigi",object.getItemName());
    }

    @Test
    public void generateId_should_create_distinct_values(){
        SimpleDbSampleEntity object1 = new SimpleDbSampleEntity();
        SimpleDbSampleEntity object2 = new SimpleDbSampleEntity();

        SimpleDbEntity sdbEntity1 = new SimpleDbEntity(SimpleDbSampleEntity.entityInformation(), object1);
        sdbEntity1.generateIdIfNotSet();
        SimpleDbEntity sdbEntity2 = new SimpleDbEntity(SimpleDbSampleEntity.entityInformation(), object2);
        sdbEntity2.generateIdIfNotSet();

        assertNotEquals(object1.getItemName(), object2.getItemName());


    }

}
