package org.springframework.data.simpledb.core.entity.field.wrapper;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public MapSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

    @Override
    public List<String> serializeValue() {
        final List<String> fieldValues = new ArrayList<>();


        if(getFieldValue() != null) {
            ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
            Class keyTypeClass = (Class) parameterizedType.getActualTypeArguments()[0];
            Class genericTypeClass = (Class) parameterizedType.getActualTypeArguments()[1];

            String fieldMarshaled2JSON = JsonMarshaller.getInstance().marshall(getFieldValue(), genericTypeClass.getName(), keyTypeClass.getName());
            fieldValues.add(fieldMarshaled2JSON);
        }

        return fieldValues;
    }

    @Override
    public Object deserializeValue(List<String> value) {
        Assert.isTrue(value.size() <= 1);


        Map jsonCollection = null;
        if (value.size() > 0) {

            String fieldValue = value.get(0);
            jsonCollection = (Map<?,?>) JsonMarshaller.getInstance().unmarshallWrapperMap(fieldValue);
        }
        return jsonCollection;
    }
}
