package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.simpledb.core.entity.EntityWrapper;

public class CoreField<T, ID extends Serializable> extends InstantiableField<T, ID> {

	CoreField(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
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
