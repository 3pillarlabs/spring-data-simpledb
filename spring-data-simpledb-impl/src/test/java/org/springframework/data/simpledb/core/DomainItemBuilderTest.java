package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbSampleEntity;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DomainItemBuilderTest {

	public static final String SAMPLE_ITEM_NAME = "SAMPLE_ITEM_NAME";
	private static final Boolean SAMPLE_BOOLEAN_ATT_VALUE = Boolean.TRUE;
	private DomainItemBuilder<SimpleDbSampleEntity> domainItemBuilder;

	@Test
	public void populateDomainItem_should_convert_item_name() {

		Item sampleItem = new Item(SAMPLE_ITEM_NAME, new ArrayList<Attribute>());
		SimpleDbEntityInformation<SimpleDbSampleEntity, String> entityInformation = SimpleDbSampleEntity
				.entityInformation();

		domainItemBuilder = new DomainItemBuilder<SimpleDbSampleEntity>();
		SimpleDbSampleEntity returnedDomainEntity = domainItemBuilder.populateDomainItem(entityInformation, sampleItem);

		assertEquals(SAMPLE_ITEM_NAME, returnedDomainEntity.getItemName());
	}

	@Test
	public void populateDomainItem_should_convert_attributes() {
		List<Attribute> attributeList = new ArrayList<Attribute>();
		attributeList.add(new Attribute("booleanField", "" + SAMPLE_BOOLEAN_ATT_VALUE));

		Item sampleItem = new Item(SAMPLE_ITEM_NAME, attributeList);
		SimpleDbEntityInformation<SimpleDbSampleEntity, String> entityInformation = SimpleDbSampleEntity
				.entityInformation();

		domainItemBuilder = new DomainItemBuilder<SimpleDbSampleEntity>();
		SimpleDbSampleEntity returnedDomainEntity = domainItemBuilder.populateDomainItem(entityInformation, sampleItem);

		assertTrue(returnedDomainEntity.getBooleanField() == SAMPLE_BOOLEAN_ATT_VALUE);
	}

}
