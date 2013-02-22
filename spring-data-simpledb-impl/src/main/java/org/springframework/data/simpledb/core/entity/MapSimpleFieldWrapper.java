package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;

public class MapSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public MapSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
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
		Map<?, ?> jsonCollection = null;

		if (value != null) {
			jsonCollection = (Map<?, ?>) JsonMarshaller.getInstance().unmarshall(value, getField().getType());
		}

		return jsonCollection;
	}
}
