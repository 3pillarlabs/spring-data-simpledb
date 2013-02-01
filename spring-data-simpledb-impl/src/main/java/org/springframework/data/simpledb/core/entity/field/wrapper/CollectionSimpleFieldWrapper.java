package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.*;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class CollectionSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public CollectionSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}


    @Override
    public List<String> serializeValue() {

        return SimpleDBAttributeConverter.coreTypesCollectionToSimpleDBAttributeValues(this.getFieldValue());

    }

    @Override
    public Object deserializeValue(List<String> value) {
        ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
        Class returnTypeClazz = (Class) parameterizedType.getActualTypeArguments()[0];
        Collection collection = (Collection<?>) getFieldValue();

        try {
            SimpleDBAttributeConverter.toDomainFieldPrimitiveCollection(value, collection, returnTypeClazz);
            return collection;
        } catch (ParseException e) {
            throw new MappingException("Cannot parse Object!");
        }
    }
}
