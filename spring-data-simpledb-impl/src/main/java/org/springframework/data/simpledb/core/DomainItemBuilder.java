package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectResult;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		final Map<String, String> attributes = new HashMap<String, String>();

		for(Attribute attr : simpleDbAttributes) {
			attributes.put(attr.getName(), attr.getValue());
		}

		return attributes;
	}
}
