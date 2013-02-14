package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class ArraySimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public ArraySimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}


    @Override
    public List<String> serializeValue() {
        return SimpleDBAttributeConverter.encodePrimitiveArray(this.getFieldValue());
    }

    @Override
    public Object deserializeValue(List<String> value) {
        try {
            Class<?> fieldClazz = getField().getType();
            return SimpleDBAttributeConverter.decodeToPrimitiveArray(value, fieldClazz.getComponentType());

        } catch (ParseException e) {
            throw new MappingException("Could not read object", e);
        }
    }
}
