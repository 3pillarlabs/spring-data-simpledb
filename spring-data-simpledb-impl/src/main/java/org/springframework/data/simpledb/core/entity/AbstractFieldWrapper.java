package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.data.simpledb.reflection.ReflectionUtils;
import org.springframework.util.Assert;

public abstract class AbstractFieldWrapper<T, ID extends Serializable> {

	/* field metadata */
	private final Field field;
	private final EntityWrapper<T, ID> parentWrapper;

	protected AbstractFieldWrapper(final Field field, final EntityWrapper<T, ID> parentWrapper,
			final boolean isNewParent) {
		Assert.notNull(field);
		Assert.notNull(parentWrapper);

		this.field = field;
		this.parentWrapper = parentWrapper;

		this.field.setAccessible(Boolean.TRUE);

		if(isNewParent) {
			createInstance();
		}
	}

	public abstract Map<String, String> serialize(String prefix);

	public abstract Object deserialize(final Map<String, String> attributes);

	/**
	 * Template method.
	 * 
	 * Create an instance of the field and set it on the parentWrapper instance.
	 */
	public abstract void createInstance();

	public Field getField() {
		return this.field;
	}

	/**
	 * Sets value via setter
	 */
	public void setFieldValue(Object fieldValue) {
		ReflectionUtils.callSetter(parentWrapper.getItem(), field.getName(), fieldValue);
	}

	/**
	 * Retrieves value via getter
	 */
	public Object getFieldValue() {
		return ReflectionUtils.callGetter(parentWrapper.getItem(), field.getName());
	}

	public T getParentEntity() {
		return this.parentWrapper.getItem();
	}

	String getFieldName() {
		return field.getName();
	}

	EntityWrapper<T, ID> getParentWrapper() {
		return this.parentWrapper;
	}

}
