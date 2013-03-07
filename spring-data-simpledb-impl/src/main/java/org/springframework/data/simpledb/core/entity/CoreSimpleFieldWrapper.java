package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class CoreSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public CoreSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

	@Override
	public String serializeValue() {
		return SimpleDBAttributeConverter.encode(this.getFieldValue());
	}

	@Override
	public Object deserializeValue(String value) {
		try {
			return SimpleDBAttributeConverter.decodeToFieldOfType(value, getField().getType());
		} catch(IllegalArgumentException e) {
			throw new MappingException("Could not map attributes", e);
		} catch(ParseException e) {
			throw new MappingException("Could not map attributes", e);
		}
	}

}
