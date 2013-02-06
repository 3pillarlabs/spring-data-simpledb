package org.springframework.data.simpledb.core.entity.field.wrapper;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public CollectionSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}


    @Override
    public List<String> serializeValue() {

        final List<String> fieldValues = new ArrayList<>();


        if(getFieldValue() != null) {
            String fieldMarshaled2JSON = new JsonMarshaller().marshall(getFieldValue());
            fieldValues.add(fieldMarshaled2JSON);
        }

        return fieldValues;

    }


    @SuppressWarnings("unchecked")
    @Override
    public Object deserializeValue(List<String> value) {
        Assert.isTrue(value.size() <= 1);


        Collection jsonCollection = null;
        if (value.size() > 0) {
            ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
            Class genericTypeClass = (Class) parameterizedType.getActualTypeArguments()[0];
            Class colectionClass = (Class) parameterizedType.getRawType();

            String fieldValue = value.get(0);
            jsonCollection = (Collection<?>) new JsonMarshaller().unmarshallCollection(fieldValue, colectionClass, genericTypeClass);
        }
        return jsonCollection;
    }

}
