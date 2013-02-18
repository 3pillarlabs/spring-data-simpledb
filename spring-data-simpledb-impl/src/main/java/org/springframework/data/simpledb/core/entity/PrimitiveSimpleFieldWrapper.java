package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class PrimitiveSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public PrimitiveSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

    @Override
    public List<String> serializeValue() {
        final List<String> fieldValues = new ArrayList<>();

        fieldValues.add(SimpleDBAttributeConverter.encode(this.getFieldValue()));

        return fieldValues;
    }

    @Override
    public Object deserializeValue(List<String> value) {
        try {
            return SimpleDBAttributeConverter.decodeToFieldOfType(value.get(0), getField().getType());
        } catch (IllegalArgumentException | ParseException e) {
            throw new MappingException("Could not map attributes", e);
        }
    }

}
