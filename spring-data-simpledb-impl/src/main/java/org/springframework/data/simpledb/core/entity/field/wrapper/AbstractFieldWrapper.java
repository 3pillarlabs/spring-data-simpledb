package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.internal.RestUtils;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.core.entity.field.FieldTypeIdentifier;
import org.springframework.data.simpledb.util.ReflectionUtils;
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
     * This Mutator should modify the state of the property through its correspondent Field setter method
     */
    public void setFieldValue(Object fieldValue){
        try {
            final Method setterMethod = ReflectionUtils.retrieveSetterFrom(parentWrapper.getItem().getClass(), field);

            Assert.notNull(setterMethod, "No Setter Found for corresponding Field=" + field.getName());

            setterMethod.invoke(parentWrapper.getItem(), fieldValue);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new MappingException("Exception occurred while trying to set value=" + fieldValue + " on Field=" + field.getName(), e);
        }

    }

    /**
     * This Accessor method should read the field through its correspondent Field accessor-method
     */
    public Object getFieldValue() {
        Object fieldValue = null;
        Method getterMethod = ReflectionUtils.retrieveGetterFrom(parentWrapper.getItem().getClass(), field);

        Assert.notNull(getterMethod, "No Getter Found for corresponding Field=" + field.getName());

        try {
            fieldValue = getterMethod.invoke(parentWrapper.getItem());
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new MappingException("Could not retrieve field value for Field=" + this.field.getName(), e);
        }
        return fieldValue;
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
