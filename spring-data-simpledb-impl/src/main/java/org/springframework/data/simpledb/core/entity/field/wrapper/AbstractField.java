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
	
	protected AbstractField(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		Assert.notNull(field);
		Assert.notNull(parent);
		
		this.field = field;
		this.parent = parent;
		
		this.field.setAccessible(Boolean.TRUE);
		
		if(isNewParent) {
			createInstance();
		}
	}
	
	/**
	 * @param prefix TODO
	 * @return serialized value in string representation
	 */
	public abstract Map<String, List<String>> serialize(String prefix);
	
	public void deserialize(final Map<String, List<String>> attributes) {
		/* you should not be here */
		Assert.state(true, "You should not be here!");
	}
	
	/**
	 * Convert and set the values on the parent instance 
	 */
	public abstract void deserialize(final List<String> value);
	
	/**
	 * Template method.
	 * 
	 * Create an instance of the field and set it on the parent instance.
	 */
	public abstract void createInstance();
	
	public Field getField() {
		return this.field;
	}
	
	public T getEntity() {
		return this.parent.getItem();
	}
	
	public Object getValue() {
		try {
			return this.field.get(parent.getItem());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new MappingException("Could not retrieve field value " + this.field.getName(), e);
		}
	}
	
	public String getName() {
		return field.getName();
	}
	
	EntityWrapper<T, ID> getParent() {
		return this.parent;
	}
	
}
