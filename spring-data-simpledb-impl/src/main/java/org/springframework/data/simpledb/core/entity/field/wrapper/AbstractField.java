package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

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
	}
	
	/**
	 * @return serialized value in string representation
	 */
	public abstract List<String> serialize();
	
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
	
	protected EntityWrapper<T, ID> getParent() {
		return this.parent;
	}
	
}
