package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.attributeutil.SimpleDbAttributeValueSplitter;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectResult;

public class DomainItemBuilder<T> {

	public List<T> populateDomainItems(SimpleDbEntityInformation<T, ?> entityInformation, SelectResult selectResult) {
		final List<T> allItems = new ArrayList<T>();

		for(Item item : selectResult.getItems()) {
			allItems.add(populateDomainItem(entityInformation, item));
		}

		return allItems;
	}

    /**
     * Used during deserialization process, each item being populated based on attributes retrieved from DB
     * @param entityInformation
     * @param item
     * @return T the Item Instance
     */
	public T populateDomainItem(SimpleDbEntityInformation<T, ?> entityInformation, Item item) {
		return buildDomainItem(entityInformation, item);
	}

	private T buildDomainItem(SimpleDbEntityInformation<T, ?> entityInformation, Item item) {
		EntityWrapper<T, ?> entity = new EntityWrapper<T, Serializable>(entityInformation);

		entity.setId(item.getName());
		final Map<String, String> attributes = convertSimpleDbAttributes(item.getAttributes());
		entity.deserialize(attributes);

		return entity.getItem();
	}

	private Map<String, String> convertSimpleDbAttributes(List<Attribute> simpleDbAttributes) {
		final Map<String, List<String>> multiValueAttributes = new HashMap<String, List<String>>();
		for (Attribute attr : simpleDbAttributes) {
			if (!multiValueAttributes.containsKey(attr.getName())) {
				multiValueAttributes.put(attr.getName(), new ArrayList<String>());
			}
			multiValueAttributes.get(attr.getName()).add(attr.getValue());
		}

		return SimpleDbAttributeValueSplitter.combineAttributeValuesWithExceedingLengths(multiValueAttributes);
	}
}
