package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;

import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;

public class CollectionSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public CollectionSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
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
		Collection<?> jsonCollection = null;
		if(value != null) {
			jsonCollection = (Collection<?>) JsonMarshaller.getInstance().unmarshall(value, getField().getType());
		}

		return jsonCollection;
	}

}
