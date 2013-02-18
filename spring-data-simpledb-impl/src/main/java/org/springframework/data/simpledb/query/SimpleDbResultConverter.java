package org.springframework.data.simpledb.query;

import org.springframework.data.simpledb.util.ReflectionUtils;

import java.util.*;

/**
 * Created by: mgrozea
 */
public final class SimpleDbResultConverter {
	
	private SimpleDbResultConverter() {
		/* utility class */
	}
	
    public static List<Object> filterNamedAttributesAsList(List<?> domainObjects, String attributeName){
        List<Object> ret = new ArrayList<>();
        for (Object object : domainObjects) {
            ret.add(ReflectionUtils.callGetter(object, attributeName));
        }
        return ret;
    }

    public static Set<Object> filterNamedAttributesAsSet(List<?> domainObjects, String attributeName){
        Set<Object> ret = new LinkedHashSet<>();
        for (Object object : domainObjects) {
            ret.add(ReflectionUtils.callGetter(object, attributeName));
        }
        return ret;
    }

    public static List<List<Object>> toListOfListOfObject(List<?> entityList, List<String> requestedQueryFieldNames) {
        if (entityList.size() > 0) {
            List<List<Object>> rows = new ArrayList<>();
            for (Object entity : entityList) {
                List<Object> cols = new ArrayList<>();
                for (String fieldName : requestedQueryFieldNames) {
                    Object value = ReflectionUtils.callGetter(entity, fieldName);
                    cols.add(value);
                }
                rows.add(cols);
            }
            return rows;
        } else {
            return Collections.EMPTY_LIST;
        }
    }


}
