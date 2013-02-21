package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;

public class ArraySimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public ArraySimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}


    @Override
    public String serializeValue() {
        if(getFieldValue() != null) {
            return JsonMarshaller.getInstance().marshall(getFieldValue());
        }
        
        return null;
    }

    @Override
    public Object deserializeValue(String value) {
        Object jsonArray = null;
        
        if (value != null) {
            jsonArray = JsonMarshaller.getInstance().unmarshall(value, getField().getType());
        }
        
    	return jsonArray;
    }
}
