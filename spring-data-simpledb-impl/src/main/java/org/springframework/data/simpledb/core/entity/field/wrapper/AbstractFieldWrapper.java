package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.util.Assert;

public abstract class AbstractFieldWrapper<T, ID extends Serializable> {

	/* field metadata */
	private final Field field;
	private final EntityWrapper<T, ID> parentWrapper;
	
	protected AbstractFieldWrapper(final Field field, final EntityWrapper<T, ID> parentWrapper, final boolean isNewParent) {
		Assert.notNull(field);
		Assert.notNull(parentWrapper);
		
		this.field = field;
		this.parentWrapper = parentWrapper;
		
		this.field.setAccessible(Boolean.TRUE);
		
		if(isNewParent) {
			createInstance();
		}
	}

	public abstract Map<String, List<String>> serialize(String prefix);


	public abstract Object deserialize(final Map<String, List<String>> attributes);
	

    public void applyValue(T parentEntity, Object value){
        try {
            getField().set(parentEntity, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new MappingException("Could not map attributes", e);
        }

    }

	
	/**
	 * Template method.
	 * 
	 * Create an instance of the field and set it on the parentWrapper instance.
	 */
	public abstract void createInstance();
	
	public Field getField() {
		return this.field;
	}
	
	public T getParentEntity() {
		return this.parentWrapper.getItem();
	}
	
	Object getFieldValue() {
		try {
			return this.field.get(parentWrapper.getItem());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new MappingException("Could not retrieve field value " + this.field.getName(), e);
		}
	}
	
	String getFieldName() {
		return field.getName();
	}
	
	EntityWrapper<T, ID> getParentWrapper() {
		return this.parentWrapper;
	}
	
}
