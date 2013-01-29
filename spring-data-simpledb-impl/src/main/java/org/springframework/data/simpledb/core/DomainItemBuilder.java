package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainItemBuilder<T, ID extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbOperationsImpl.class);

    public List<T> populateDomainItems(SimpleDbEntityInformation<T, ID> entityInformation, SelectResult selectResult) {
        final List<T> allItems = new ArrayList<>();

        for (Item item : selectResult.getItems()) {
            allItems.add(buildDomainItem(entityInformation, item));
        }

        return allItems;
    }

    public T buildDomainItem(SimpleDbEntityInformation<T, ID> entityInformation, Item item) {
        SimpleDbEntity entity = new SimpleDbEntity(entityInformation);

        entity.setId(item.getName());
        final Map<String, List<String>> attributes = convertSimpleDbAttributes(item.getAttributes());
        entity.setAttributes(attributes);

        return (T) entity.getItem();
    }

    private Map<String, List<String>> convertSimpleDbAttributes(List<Attribute> simpleDbAttributes) {
        final Map<String, List<String>> attributes = new HashMap<>();
        
        for (Attribute attr : simpleDbAttributes) {
        	List<String> attributeValues = new ArrayList<>();
        	
        	if(attributes.containsKey(attr.getName())) {
        		attributeValues = attributes.get(attr.getName());
        	}
        	
        	attributeValues.add(attr.getValue());
            
            attributes.put(attr.getName(), attributeValues);
        }
        
        return attributes;
    }
}
