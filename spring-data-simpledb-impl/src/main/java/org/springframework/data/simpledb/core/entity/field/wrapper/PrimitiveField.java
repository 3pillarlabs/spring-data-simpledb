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
import org.springframework.util.Assert;

public class PrimitiveField<T, ID extends Serializable> extends AbstractField<T, ID> {

	PrimitiveField(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
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
		Assert.notNull(value);
		Assert.isTrue(value.size() == 1);
		
		try {
			final Object convertedValue = SimpleDBAttributeConverter.toDomainFieldPrimitive(value.get(0), getField().getType());
			getField().set(getEntity(), convertedValue);
		} catch (IllegalArgumentException | IllegalAccessException | ParseException e) {
			throw new MappingException("Could not map attributes", e);
		}
	}
	
	@Override
	public void createInstance() {
		/* do nothing */
	}

}
