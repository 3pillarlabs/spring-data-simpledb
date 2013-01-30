package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.*;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class CollectionField<T, ID extends Serializable> extends InstantiableField<T, ID> {

	CollectionField(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}


    @Override
    public Map<String, List<String>> serialize(String prefix) {
        final Map<String, List<String>> result = new HashMap<>();

        final List<String> fieldValues = new ArrayList<>();

        fieldValues.addAll(SimpleDBAttributeConverter.coreTypesCollectionToSimpleDBAttributeValues(this.getValue()));

        result.put(prefix.isEmpty() ? getName() : prefix + "." + getName(), fieldValues);

        return result;
    }


    @Override
    public void deserialize(List<String> value) {

        ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
        Class returnTypeClazz = (Class) parameterizedType.getActualTypeArguments()[0];
        Collection collection = (Collection<?>) getValue();

        try {
            SimpleDBAttributeConverter.toDomainFieldPrimitiveCollection(value, collection, returnTypeClazz);
        } catch (ParseException e) {
            throw new MappingException("Cannot parse Object!");
        }
    }

}
