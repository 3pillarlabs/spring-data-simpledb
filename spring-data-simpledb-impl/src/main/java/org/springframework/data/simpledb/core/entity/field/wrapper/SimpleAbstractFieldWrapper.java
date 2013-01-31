package org.springframework.data.simpledb.core.entity.field.wrapper;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleAbstractFieldWrapper<T, ID extends Serializable> extends AbstractFieldWrapper<T, ID> {

    protected SimpleAbstractFieldWrapper(Field field, EntityWrapper<T, ID> parentWrapper, boolean isNewParent) {
        super(field, parentWrapper, isNewParent);
    }


    @Override
    public final Map<String, List<String>> serialize(String prefix){
        final Map<String, List<String>> result = new HashMap<>();
        List<String> serializedValues = serializeValue();
        result.put(prefix.isEmpty() ? getFieldName() : prefix + "." + getFieldName(), serializedValues);
        return result;
    }

    public abstract List<String> serializeValue();


    @Override
    public final Object deserialize(final Map<String, List<String>> attributes) {
        Assert.isTrue(attributes.size() == 1);
        return deserializeValue(attributes.values().iterator().next());
    }

    public abstract Object deserializeValue(final List<String> value);


    @Override
    public final void createInstance() {
        //Only applies to NestedEntities
    }
}
