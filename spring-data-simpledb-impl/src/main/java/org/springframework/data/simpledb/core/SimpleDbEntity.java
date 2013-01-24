package org.springframework.data.simpledb.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class SimpleDbEntity <T, ID extends Serializable> {
    private SimpleDbEntityInformation<T, ?> entityInformation;
    private T item;

    public SimpleDbEntity(SimpleDbEntityInformation<T, ?> entityInformation, T item){
        this.entityInformation = entityInformation;
        this.item = item;
    }

    public SimpleDbEntity(SimpleDbEntityInformation<T, ?> entityInformation){
        this.entityInformation = entityInformation;
        try {
            this.item = entityInformation.getJavaType().newInstance();
        } catch (InstantiationException |IllegalAccessException e) {
            throw new MappingException("Could not instantiate object", e);
        }

    }

    public String getDomain(){
        return entityInformation.getDomain();
    }

    public String getItemName(){
        return entityInformation.getItemName(item);
    }

    public Map<String, String> getAttributes(){
        return entityInformation.getAttributes(item);
    }

    public T getItem(){
        return item;
    }


    public void generateId() {
        if(entityInformation.getItemName(item)==null){
            setId(UUID.randomUUID().toString());
        }
    }

    public void setId(String itemName)  {
        final Field idField;
        try {
            idField = item.getClass().getDeclaredField(entityInformation.getItemNameFieldName(item));
            idField.setAccessible(Boolean.TRUE);
            idField.set(item, itemName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MappingException("Could not set id field", e);
        }
    }

    public void setAttributes(Map<String, String> attributes) {
        try {
            final Field attributesField = item.getClass().getDeclaredField(entityInformation.getAttributesFieldName(item));

            attributesField.setAccessible(true);
            attributesField.set(item, attributes);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MappingException("Could not set attribute field", e);
        }

    }
}
