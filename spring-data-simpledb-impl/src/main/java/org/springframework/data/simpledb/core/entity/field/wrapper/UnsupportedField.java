package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.core.entity.EntityWrapper;

public class UnsupportedField<T, ID extends Serializable> extends AbstractField<T, ID> {

	UnsupportedField(Field field, EntityWrapper<T, ID> parent) {
		super(field, parent);
	}

	@Override
	public Map<String, List<String>> serialize(String prefix) {
		final Map<String, List<String>> result = new HashMap<>();
		
		/* serialization routine goes here */
		
		return result;
	}

	@Override
	public void deserialize(List<String> value) {
	}

}
