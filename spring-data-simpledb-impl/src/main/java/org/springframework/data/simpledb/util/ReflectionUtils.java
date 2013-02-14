package org.springframework.data.simpledb.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

public final class ReflectionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

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

    /**
     * This method checks if the declared Field is accessible through getters and setters methods
     * Fields which have only setters OR getters and NOT both are discarded from serialization process
     */
    public static <T> boolean hasDeclaredGetterAndSetter(final Field field, Class<T> entityClazz) {
        boolean hasDeclaredAccessorsMutators = true;

        Method getter = retrieveGetterFrom(entityClazz, field);
        Method setter = retrieveSetterFrom(entityClazz, field);

        if(getter == null || setter == null) {
            hasDeclaredAccessorsMutators = false;
        }

        return hasDeclaredAccessorsMutators;
    }

    public static <T> Method retrieveGetterFrom(final Class<T> entityClazz, final Field field) {
        Method getterMethod;
        try {
            final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entityClazz);
            getterMethod = descriptor.getReadMethod();
        } catch (IntrospectionException e) {
            getterMethod = null;
            LOG.debug("Field {} has not declared getter method", field.getName(), e);
        }
        return getterMethod;
    }

    public static <T> Method retrieveSetterFrom(final Class<T> entityClazz, final Field field) {
        Method setterMethod;

        try {
            final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entityClazz);
            setterMethod = descriptor.getWriteMethod();
        } catch (IntrospectionException e) {
            setterMethod = null;
            LOG.debug("Field {} has not declared setter method", field.getName(), e);
        }
        return setterMethod;
    }
}
