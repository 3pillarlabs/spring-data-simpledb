package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public abstract class AbstractSimpleFieldWrapper<T, ID extends Serializable> extends AbstractFieldWrapper<T, ID> {

    protected AbstractSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parentWrapper, boolean isNewParent) {
        super(field, parentWrapper, isNewParent);
    }


    @Override
    public final Map<String, String> serialize(String prefix){
        final Map<String, String> result = new HashMap<>();
        result.put(prefix.isEmpty() ? getFieldName() : prefix + "." + getFieldName(), serializeValue());
        
        return result;
    }

    public abstract String serializeValue();


    @Override
    public final Object deserialize(final Map<String, String> attributes) {
        Assert.isTrue(attributes.size() == 1);
        
        String attributeValue = attributes.values().iterator().next();
        Assert.notNull(attributeValue);
        
        return deserializeValue(attributeValue);
    }

    public abstract Object deserializeValue(final String value);


    @Override
    public final void createInstance() {
        //Only applies to NestedEntities
    }
}
