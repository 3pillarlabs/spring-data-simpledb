package org.springframework.data.simpledb.core;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
        SimpleDbEntity sdbEntity = new SimpleDbEntity(readEntityInformation(), object);
        sdbEntity.generateIdIfNotSet();
        assertNotNull(object.getItemName());

    }

    @Test
    public void generateId_should_not_overwrite_existing_id(){
        SampleDomainObject object = new SampleDomainObject();
        object.setItemName("gigi");
        SimpleDbEntity sdbEntity = new SimpleDbEntity(readEntityInformation(), object);
        sdbEntity.generateIdIfNotSet();
        assertEquals("gigi",object.getItemName());
    }

    @Test
    public void generateId_should_create_distinct_values(){
        SampleDomainObject object1 = new SampleDomainObject();
        SampleDomainObject object2 = new SampleDomainObject();

        SimpleDbEntity sdbEntity1 = new SimpleDbEntity(readEntityInformation(), object1);
        sdbEntity1.generateIdIfNotSet();
        SimpleDbEntity sdbEntity2 = new SimpleDbEntity(readEntityInformation(), object2);
        sdbEntity2.generateIdIfNotSet();

        assertNotEquals(object1.getItemName(), object2.getItemName());


    }

    private SimpleDbEntityInformation<SampleDomainObject, String> readEntityInformation() {
        return (SimpleDbEntityInformation<SampleDomainObject, String>) SimpleDbEntityInformationSupport.getMetadata(SampleDomainObject.class);
    }
}
