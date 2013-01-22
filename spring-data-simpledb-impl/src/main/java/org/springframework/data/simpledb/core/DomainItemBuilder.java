package org.springframework.data.simpledb.core;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.lang.reflect.Field;
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
        T domainItem = null;
        try {
            domainItem = entityInformation.getJavaType().newInstance();
            final Field idField = domainItem.getClass().getDeclaredField(entityInformation.getItemNameFieldName(domainItem));

            idField.setAccessible(true);
            idField.set(domainItem, item.getName());

            final Map<String, String> attributes = new HashMap<>();
            for (Attribute attr : item.getAttributes()) {
                attributes.put(attr.getName(), attr.getValue());
            }

            final Field attributesField = domainItem.getClass().getDeclaredField(entityInformation.getAttributesFieldName(domainItem));

            attributesField.setAccessible(true);
            attributesField.set(domainItem, attributes);

        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return domainItem;
    }
}
