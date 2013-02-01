package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.util.Assert;

public class CoreSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public CoreSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

    @Override
    public List<String> serializeValue() {
        final List<String> fieldValues = new ArrayList<>();

        fieldValues.add(SimpleDBAttributeConverter.toSimpleDBAttributeValue(this.getFieldValue()));

        return fieldValues;
    }

    @Override
    public Object deserializeValue(List<String> value) {
        Assert.isTrue(value.size() == 1);

        try {
            return SimpleDBAttributeConverter.toFieldOfType(value.get(0), getField().getType());
        } catch (IllegalArgumentException | ParseException e) {
            throw new MappingException("Could not map attributes", e);
        }
    }

}
