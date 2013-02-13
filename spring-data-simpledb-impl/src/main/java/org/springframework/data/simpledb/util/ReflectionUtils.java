package org.springframework.data.simpledb.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

public final class ReflectionUtils {

    private ReflectionUtils() {
        //utiliy class
    }

    public static Object callGetter(Object obj, String fieldName) {
        try {
            Method getterMethod = new PropertyDescriptor(fieldName, obj.getClass()).getReadMethod();
            Assert.notNull(getterMethod, "No Getter Found for corresponding field " + fieldName + " in class:  " + obj.getClass());
            return getterMethod.invoke(obj);

        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new MappingException("Could not call getter for field " + fieldName + " in class:  " + obj.getClass(), e);
        }
    }
}
