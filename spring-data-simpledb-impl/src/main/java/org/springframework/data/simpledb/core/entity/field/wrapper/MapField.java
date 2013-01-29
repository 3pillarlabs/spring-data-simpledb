package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.simpledb.core.entity.EntityWrapper;

public class MapField<T, ID extends Serializable> extends AbstractField<T, ID> {

	MapField(Field field, EntityWrapper<T, ID> parent) {
		super(field, parent);
	}

	@Override
	public List<String> serialize() {
		final List<String> result = new ArrayList<>();
		
		/* serialization routine goes here */
		
		return result;
	}

	@Override
	public void deserialize(List<String> value) {
	}

}
