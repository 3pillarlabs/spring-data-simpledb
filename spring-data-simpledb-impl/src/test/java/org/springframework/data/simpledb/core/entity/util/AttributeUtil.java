package org.springframework.data.simpledb.core.entity.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cclaudiu
 * 
 */
public final class AttributeUtil {

	/* ----- Utility method to fetch AttributeNames of declared Properties from Parameter Class ------ */
	public static <T> List<String> getAttributeNamesThroughReflection(Class<T> entityClazz) {
		List<String> attributeNames = new ArrayList<String>();

		for(Field eachDeclaredField : Arrays.asList(entityClazz.getDeclaredFields())) {
			attributeNames.add(eachDeclaredField.getName());
		}

		return attributeNames;
	}

}
