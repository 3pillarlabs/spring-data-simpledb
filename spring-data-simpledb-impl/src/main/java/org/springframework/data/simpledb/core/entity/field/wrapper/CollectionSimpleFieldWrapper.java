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
        try {
            ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
            Class returnTypeClazz = (Class) parameterizedType.getActualTypeArguments()[0];
            Collection collection = (Collection<?>) getInstanceOfCollectionBasedOn(getField());
            SimpleDBAttributeConverter.toDomainFieldPrimitiveCollection(value, collection, returnTypeClazz);
            return collection;
            
        } catch (IllegalAccessException | InstantiationException | ParseException e) {
            throw new MappingException("Cannot parse Object for instantiation!", e);
        }
    }

    /**
     * Not known at compile-time, need the Instance Type of the Collection
     * If the reference Type is of Type Interface the instantiation is done when de-serializing the SimpleDB
     * representation
     * 1. get type of collection to be de-serialized
     * 2. if Type is-a interface -> make a new instance of concrete default type
     * 3. if Type is-a concrete class -> create an instance of it
     */
    private Collection<?> getInstanceOfCollectionBasedOn(Field field) throws IllegalAccessException, InstantiationException {
        Collection<?> createdCollectionInstance = null;
        Class<?> fieldClass = field.getType();

        if(field.getType().isInterface()) { // default implementation is given
            if(fieldClass.isAssignableFrom(List.class)) {
                createdCollectionInstance = new ArrayList<>();

            } else if(fieldClass.isAssignableFrom(Set.class)) {
                createdCollectionInstance = new HashSet<>();

            } else if(fieldClass.isAssignableFrom(Collection.class)) {
                createdCollectionInstance = new ArrayList<>();
            }

        } else { // class is a concrete class
            ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
            Class returnTypeClazz = (Class) parameterizedType.getRawType();
            createdCollectionInstance = (Collection<?>) returnTypeClazz.newInstance();
        }

        return createdCollectionInstance;
    }

}
