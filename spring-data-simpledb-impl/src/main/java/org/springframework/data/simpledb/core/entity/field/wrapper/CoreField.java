package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;

public class CoreField<T, ID extends Serializable> extends AbstractField<T, ID> {

	CoreField(Field field, EntityWrapper<T, ID> parent) {
		super(field, parent);
	}

    @Override
    public Map<String, List<String>> serialize(String prefix) {
        final Map<String, List<String>> result = new HashMap<>();

        final List<String> fieldValues = new ArrayList<>();

        fieldValues.add(SimpleDBAttributeConverter.toSimpleDBAttributeValue(this.getValue()));

        result.put(prefix.isEmpty() ? getName() : prefix + "." + getName(), fieldValues);

        return result;
    }

    @Override
	public void deserialize(List<String> value) {
	}

}
