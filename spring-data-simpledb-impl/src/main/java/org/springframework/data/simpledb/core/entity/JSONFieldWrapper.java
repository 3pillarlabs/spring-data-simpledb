package org.springframework.data.simpledb.core.entity;

import org.springframework.data.simpledb.core.entity.json.JsonMarshaller;

import java.io.Serializable;
import java.lang.reflect.Field;

public class JSONFieldWrapper <T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

    public JSONFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
        super(field, parent, isNewParent);
    }

    @Override
    public String serializeValue() {
        if(getFieldValue() != null) {
            return JsonMarshaller.getInstance().marshall(getFieldValue());
        }

        return null;
    }

    @Override
    public Object deserializeValue(String value) {
        Object ret = null;

        if(value != null) {
            ret = JsonMarshaller.getInstance().unmarshall(value, getField().getType());
        }

        return ret;
    }
}
