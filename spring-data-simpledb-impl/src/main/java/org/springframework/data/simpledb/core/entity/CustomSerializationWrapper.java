package org.springframework.data.simpledb.core.entity;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.apache.commons.lang.SerializationException;
import org.codehaus.jackson.map.util.ClassUtil;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

public class CustomSerializationWrapper <T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T,ID> {

	
	private CustomSerializer<T> serializer;

	protected CustomSerializationWrapper(Field field,
				EntityWrapper<T, ID> parentWrapper, 
				boolean isNewParent, 
				Class<? extends CustomSerializer<T>> serializer) {
		super(field, parentWrapper, isNewParent);
		
		Assert.notNull(serializer, "No serializer passed");
		
		try {
			this.serializer = ClassUtil.createInstance(serializer, true);

		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String serializeValue() {
		if(getFieldValue() != null) {
			try {
				return serializer.serialize((T) getFieldValue());
			} catch (IOException e) {
				throw new MappingException("Could not marshall field "+getFieldName(), e);
			}
        }
		else{
			return null;
		}
	}

	@Override
	public Object deserializeValue(String value) {
		Object ret = null;
	
	    if(value != null) {
			try {
				ret = serializer.deserialize(value);
			} catch (IOException e) {
				throw new MappingException("Could not unmarshall object : " + value, e);
			}
	    }
	
	    return ret;
	}

}
