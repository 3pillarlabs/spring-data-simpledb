package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;
import org.springframework.util.Assert;

public class ObjectSimpleFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

    public ObjectSimpleFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
        super(field, parent, isNewParent);
    }

    @Override
    public List<String> serializeValue() {
        final List<String> fieldValues = new ArrayList<>();


        if(getFieldValue() != null) {
            String fieldMarshaled2JSON = new JsonMarshaller().marshal(getFieldValue());

            fieldValues.add(fieldMarshaled2JSON);
        }

        return fieldValues;
    }

    @Override
    public Object deserializeValue(List<String> value) {
        Assert.isTrue(value.size()<=1);

        try {
            if(value.size()>0) {
                return new JsonMarshaller().unmarshal(value.get(0));
            }
        } catch (IllegalArgumentException e) {
            throw new MappingException("Could not map attributes", e);
        }

        return null;
    }

}
