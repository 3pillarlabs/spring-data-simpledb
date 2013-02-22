package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;

public class ObjectSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public ObjectSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
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
		try {
			if(value != null) {
				return JsonMarshaller.getInstance().unmarshall(value, getField().getType());
			}
		} catch (IllegalArgumentException e) {
			throw new MappingException("Could not map attributes", e);
		}

		return null;
	}

}
