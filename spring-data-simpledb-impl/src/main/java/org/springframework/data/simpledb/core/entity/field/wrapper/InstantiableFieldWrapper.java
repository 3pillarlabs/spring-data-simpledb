package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;

public abstract class InstantiableFieldWrapper<T, ID extends Serializable> extends AbstractFieldWrapper<T, ID> {

	protected InstantiableFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

	@Override
	public void createInstance() {
		Object newInstance;
		try {
			newInstance = getField().getType().newInstance();
			getField().set(getParentEntity(), newInstance);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new MappingException("Could not instantiate object", e);
		}
	}
}
