package org.springframework.data.simpledb.core;

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
        try {
            if(entityInformation.getItemName(item)==null){
                setItemName(item, UUID.randomUUID().toString());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void setItemName(T item, String itemName) throws NoSuchFieldException, IllegalAccessException {
        final Field idField = item.getClass().getDeclaredField(entityInformation.getItemNameFieldName(item));
        idField.setAccessible(Boolean.TRUE);
        idField.set(item, itemName);
    }
}
