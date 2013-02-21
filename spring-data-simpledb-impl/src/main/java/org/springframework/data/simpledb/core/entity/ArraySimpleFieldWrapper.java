package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;
import org.springframework.util.Assert;

public class ArraySimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public ArraySimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}


    @Override
    public List<String> serializeValue() {
    	final List<String> fieldValues = new ArrayList<>();
    	
        if(getFieldValue() != null) {
            String fieldMarshaled2JSON = JsonMarshaller.getInstance().marshall(getFieldValue());
            fieldValues.add(fieldMarshaled2JSON);
        }
        
        return fieldValues;
    }

    @Override
    public Object deserializeValue(List<String> value) {
        Assert.isTrue(value.size() <= 1);

        Object jsonArray = null;
        
        if (value.size() > 0) {
            String fieldValue = value.get(0);
            jsonArray = JsonMarshaller.getInstance().unmarshall(fieldValue, getField().getType());
        }
        
    	return jsonArray;
    }
}
