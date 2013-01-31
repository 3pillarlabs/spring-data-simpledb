package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.util.Assert;

public class ArrayField<T, ID extends Serializable> extends AbstractField<T, ID> {

	ArrayField(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

    /**
     * @return Map<AttributeName, List<AttributeValues>
     **/
    @Override
    public Map<String, List<String>> serialize(String prefix) {
        String finalFieldName = prefix.isEmpty() ? super.getName() : prefix + "." + super.getName();
        return SimpleDBAttributeConverter.primitiveArraysToSimpleDBAttributeValues(finalFieldName, this.getValue());
    }

    @Override
	public void deserialize(List<String> value) {
        Assert.notNull(value);

        try {
            Class<?> fieldClazz = getField().getType();
            Object returnedRepresentation = SimpleDBAttributeConverter.toDomainFieldPrimitiveArrays(value, fieldClazz.getComponentType());
            getField().set(getEntity(), returnedRepresentation);
        } catch (IllegalAccessException | ParseException e) {
            throw new MappingException("Could not read object", e);
        }
    }

    /**
     * Any primitive array needs a SIZE when creating the instance of array
     * This information is known only at runtime
     */
    @Override
    public void createInstance() { }
}
