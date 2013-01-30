package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.data.simpledb.util.marshaller.JsonMarshaller;
import org.springframework.util.Assert;

public class ObjectField<T, ID extends Serializable> extends AbstractField<T, ID> {

	ObjectField(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

	@Override
	public Map<String, List<String>> serialize(String prefix) {
		final Map<String, List<String>> result = new HashMap<>();


        final List<String> fieldValues = new ArrayList<>();


        if(getValue() != null) {
            String fieldMarshaled2JSON = new JsonMarshaller().marshal(getValue());

            fieldValues.add(fieldMarshaled2JSON);
        }

        result.put(prefix.isEmpty() ? getName() : prefix + "." + getName(), fieldValues);
		
		return result;
	}

	@Override
	public void deserialize(List<String> value) {

        Assert.notNull(value);
        Assert.isTrue(value.size()<=1);

        try {
            if(value.size()>0) {
                final Object convertedValue = new JsonMarshaller().unmarshal(value.get(0));
                getField().set(getEntity(), convertedValue);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new MappingException("Could not map attributes", e);
        }
	}
	
	@Override
	public void createInstance() {
		/* do nothing */
	}

}
