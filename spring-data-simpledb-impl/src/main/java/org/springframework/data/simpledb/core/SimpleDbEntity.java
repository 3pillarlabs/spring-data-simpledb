package org.springframework.data.simpledb.core;

import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

import java.io.Serializable;
import java.util.Map;

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


}
