package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class DomainItemBuilderTest {

    public static final String SAMPLE_ITEM_NAME = "SAMPLE_ITEM_NAME";
    public static final String SAMPLE_ATT_NAME = "name";
    public static final String SAMPLE_ATT_VALUE = "value";
    public static final boolean SAMPLE_BOOLEAN_ATT_VALUE = Boolean.TRUE;
    private DomainItemBuilder<SampleDomainEntity, String> domainItemBuilder;

    @Test
    public void buildDomainItem_should_convert_item_name() {

        Item sampleItem = new Item(SAMPLE_ITEM_NAME, new ArrayList<Attribute>());
        SimpleDbEntityInformation<SampleDomainEntity, String> entityInformation = readEntityInformation();

        domainItemBuilder = new DomainItemBuilder<>();
        SampleDomainEntity returnedDomainEntity = domainItemBuilder.buildDomainItem(entityInformation, sampleItem);

        assertEquals(SAMPLE_ITEM_NAME, returnedDomainEntity.itemName);
    }

    @Test
    public void buildDomainItem_should_convert_attributes() {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute("booleanField", "" + SAMPLE_BOOLEAN_ATT_VALUE));
        
        Item sampleItem = new Item(SAMPLE_ITEM_NAME, attributeList);
        SimpleDbEntityInformation<SampleDomainEntity, String> entityInformation = readEntityInformation();

        domainItemBuilder = new DomainItemBuilder<>();
        SampleDomainEntity returnedDomainEntity = domainItemBuilder.buildDomainItem(entityInformation, sampleItem);

        assertEquals(SAMPLE_BOOLEAN_ATT_VALUE, returnedDomainEntity.booleanField == SAMPLE_BOOLEAN_ATT_VALUE);
    }

    // Entity for one-time used inside this Test
    static class SampleDomainEntity {

        @Id
        private String itemName;

        private boolean booleanField;
    }

    private SimpleDbEntityInformation<SampleDomainEntity, String> readEntityInformation() {
        return (SimpleDbEntityInformation<SampleDomainEntity, String>) SimpleDbEntityInformationSupport.getMetadata(SampleDomainEntity.class);
    }
}
