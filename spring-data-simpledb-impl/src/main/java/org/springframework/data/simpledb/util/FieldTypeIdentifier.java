package org.springframework.data.simpledb.util;

import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public enum FieldTypeIdentifier {

    PRIMITIVE {
        @Override
        public boolean isOfType(Field field) {
        	 return field.getType().isPrimitive();
        }
    },
    
    CORE_TYPE {
        @Override
        public boolean isOfType(Field field) {
        	 return isCoreType(field);
        }
    },
    
    PRIMITIVE_ARRAY {
        @Override
        public boolean isOfType(Field field) {
            return isPrimitiveArray(field);
        }
    };

    public abstract boolean isOfType(Field field);

    private static final Set<Class<?>> SUPPORTED_CORE_TYPES = new HashSet<Class<?>>();
    static {
    	SUPPORTED_CORE_TYPES.add(Boolean.class);
    	SUPPORTED_CORE_TYPES.add(Number.class);
    	SUPPORTED_CORE_TYPES.add(Character.class);
    	SUPPORTED_CORE_TYPES.add(String.class);
    	SUPPORTED_CORE_TYPES.add(Date.class);
    }
    
    private static final Set<Class<?>> SUPPORTED_PRIMITIVE_ARRAYS = new HashSet<Class<?>>();
    static {
        SUPPORTED_PRIMITIVE_ARRAYS.add(boolean[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(long[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(short[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(int[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(byte[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(float[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(double[].class);
        SUPPORTED_PRIMITIVE_ARRAYS.add(char[].class);
    }
    
    private static boolean isCoreType(Field field) {
    	return isCompatibleType(SUPPORTED_CORE_TYPES, field);
    }

    private static boolean isPrimitiveArray(Field field) {
    	return isCompatibleType(SUPPORTED_PRIMITIVE_ARRAYS, field);
    }
    
    private static boolean isCompatibleType(final Set<Class<?>> supportedTypes, final Field field) {
    	Assert.notNull(field);
    	
        final Class<?> type = field.getType();

        for (Class<?> clazz: supportedTypes) {
            if (type == clazz || clazz.isAssignableFrom(type)) {
                return true;
            }
        }
        
        return false;
    }

}
