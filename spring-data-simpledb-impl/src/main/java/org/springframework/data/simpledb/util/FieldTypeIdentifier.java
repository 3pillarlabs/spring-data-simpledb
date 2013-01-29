package org.springframework.data.simpledb.util;

import org.springframework.util.Assert;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public enum FieldTypeIdentifier {

    PRIMITIVE {
        @Override
        public boolean isOfType(Class<?> type) {
            return isPrimitiveOrCoreType(type);
        }
    }, COLLECTION {
        @Override
        public boolean isOfType(Class<?> type) {
            return isPrimitiveArray(type);
        }
    };

    public abstract boolean isOfType(Class<?> type);

    private static final Set<Class<?>> SUPPORTED_PRIMITIVES_AND_CORE_TYPES = new HashSet<Class<?>>();
    private static final Set<Class<?>> SUPPORTED_PRIMITIVES_ARRAYS = new HashSet<Class<?>>();

    static {
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(boolean.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(long.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(short.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(int.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(byte.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(float.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(double.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(char.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Boolean.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Long.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Short.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Integer.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Byte.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Float.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Double.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Character.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(String.class);
        SUPPORTED_PRIMITIVES_AND_CORE_TYPES.add(Date.class);

        SUPPORTED_PRIMITIVES_ARRAYS.add(boolean[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(long[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(short[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(int[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(byte[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(float[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(double[].class);
        SUPPORTED_PRIMITIVES_ARRAYS.add(char[].class);
    }

    public static boolean isPrimitiveOrCoreType(Class<?> type) {
        Assert.notNull(type);
        for (Class<?> clazz : SUPPORTED_PRIMITIVES_AND_CORE_TYPES) {
            if (type == clazz || clazz.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimitiveArray(Class<?> type) {
        Assert.notNull(type);

        for (Class<?> clazz : SUPPORTED_PRIMITIVES_ARRAYS) {
            if (type == clazz || clazz.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

}
