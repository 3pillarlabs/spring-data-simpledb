package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectResult;

public class DomainItemBuilder<T, ID extends Serializable> {

    public List<T> populateDomainItems(SimpleDbEntityInformation<T, ID> entityInformation, SelectResult selectResult) {
        final List<T> allItems = new ArrayList<T>();

        for (Item item : selectResult.getItems()) {
            allItems.add(buildDomainItem(entityInformation, item));
        }

        return allItems;
    }

    public T buildDomainItem(SimpleDbEntityInformation<T, ID> entityInformation, Item item) {
        EntityWrapper entity = new EntityWrapper(entityInformation);

        entity.setId(item.getName());
        final Map<String, String> attributes = convertSimpleDbAttributes(item.getAttributes());
        entity.deserialize(attributes);

        return (T) entity.getItem();
    }

    private Map<String, String> convertSimpleDbAttributes(List<Attribute> simpleDbAttributes) {
        final Map<String, String> attributes = new HashMap<String, String>();

        for (Attribute attr : simpleDbAttributes) {
            attributes.put(attr.getName(), attr.getValue());
        }

        return attributes;
    }
}
