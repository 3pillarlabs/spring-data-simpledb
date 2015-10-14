package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public class AttributeFieldWrapper<T, ID extends Serializable> extends
		AbstractFieldWrapper<T, ID> {

	protected AttributeFieldWrapper(Field field,
			EntityWrapper<T, ID> parentWrapper, boolean isNewParent) {
		super(field, parentWrapper, isNewParent);
	}

	@Override
	public Map<String, String> serialize(String prefix) {
		Assert.isTrue(getFieldValue() instanceof Map);
		
		String actualPrefix = StringUtils.isEmpty(prefix)? 
				getFieldName() : 
				prefix+"."+getFieldName();
		
		Map<String, String> result = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		Map<String, String> value = (Map<String, String>) getFieldValue();
		
		for(Map.Entry<String, String> entry: value.entrySet()){
			if(StringUtils.isNotEmpty(entry.getValue()) && StringUtils.isNotEmpty(entry.getKey())){
				result.put(actualPrefix+"."+entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Override
	public Object deserialize(Map<String, String> attributes) {
		return attributes;
	}

	@Override
	public void createInstance() {
		// TODO Auto-generated method stub

	}

}
