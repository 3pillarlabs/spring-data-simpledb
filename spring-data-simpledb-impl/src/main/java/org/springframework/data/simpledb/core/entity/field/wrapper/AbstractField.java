package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.util.Assert;

public abstract class AbstractField<T, ID extends Serializable> {

	/* field metadata */
	private final Field field;
	private final EntityWrapper<T, ID> parent;
	
	protected AbstractField(final Field field, final EntityWrapper<T, ID> parent) {
		Assert.notNull(field);
		Assert.notNull(parent);
		
		this.field = field;
		this.parent = parent;
		
		this.field.setAccessible(Boolean.TRUE);
	}
	
	/**
	 * @param prefix TODO
	 * @return serialized value in string representation
	 */
	public abstract Map<String, List<String>> serialize(String prefix);
	
	/**
	 * Convert and set the values on the parent instance 
	 */
	public abstract void deserialize(final List<String> value);
	
	public Field getField() {
		return this.field;
	}

	/**
	 * Create an instance of this field and set it on the parent overriding
	 */
	public void createInstance() {
		
	}
	
	Object getValue() {
		try {
			return this.field.get(parent.getItem());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new MappingException("Could not retrieve field value " + this.field.getName(), e);
		}
	}
	
	String getName() {
		return field.getName();
	}
	
	EntityWrapper<T, ID> getParent() {
		return this.parent;
	}
	
}
