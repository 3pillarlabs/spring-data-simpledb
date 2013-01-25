package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class DomainItemBuilderTest {

    public static final String SAMPLE_ITEM_NAME = "SAMPLE_ITEM_NAME";
    public static final String SAMPLE_ATT_NAME = "name";
    public static final String SAMPLE_ATT_VALUE = "value";
    private DomainItemBuilder<SimpleDbSampleEntity, String> domainItemBuilder;

    @Test
    public void buildDomainItem_should_convert_item_name() {

        Item sampleItem = new Item(SAMPLE_ITEM_NAME, new ArrayList<Attribute>());
        SimpleDbEntityInformation<SimpleDbSampleEntity, String> entityInformation = SimpleDbSampleEntity.entityInformation();

        domainItemBuilder = new DomainItemBuilder<>();
        SimpleDbSampleEntity returnedDomainEntity = domainItemBuilder.buildDomainItem(entityInformation, sampleItem);

        assertEquals(SAMPLE_ITEM_NAME, returnedDomainEntity.getItemName());
    }

    @Test
    public void buildDomainItem_should_convert_attributes() {
        List<Attribute> attributeList = new ArrayList<>();
        attributeList.add(new Attribute(SAMPLE_ATT_NAME, SAMPLE_ATT_VALUE));

        Item sampleItem = new Item(SAMPLE_ITEM_NAME, attributeList);
        SimpleDbEntityInformation<SimpleDbSampleEntity, String> entityInformation = SimpleDbSampleEntity.entityInformation();

        domainItemBuilder = new DomainItemBuilder<>();
        SimpleDbSampleEntity returnedDomainEntity = domainItemBuilder.buildDomainItem(entityInformation, sampleItem);

        assertEquals(SAMPLE_ATT_VALUE, returnedDomainEntity.getAtts().get(SAMPLE_ATT_NAME));
    }


}
