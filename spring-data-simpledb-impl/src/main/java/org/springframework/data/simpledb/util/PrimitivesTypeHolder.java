package org.springframework.data.simpledb.util;

import org.springframework.util.Assert;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public enum PrimitivesTypeHolder {

    PRIMITIVE {
        @Override
        public boolean isOfType(Class<?> type) {
            return isSimpleType(type);
        }
    }, COLLECTION {
        @Override
        public boolean isOfType(Class<?> type) {
            return isPrimitivesCollectionType(type);
        }
    };

    public abstract boolean isOfType(Class<?> type);

    private static final Set<Class<?>> DEFAULT_PRIMITIVES = new HashSet<Class<?>>();
    private static final Set<Class<?>> DEFAULT_COLLECTIONS = new HashSet<Class<?>>();

    static {
        DEFAULT_PRIMITIVES.add(boolean.class);
        DEFAULT_PRIMITIVES.add(long.class);
        DEFAULT_PRIMITIVES.add(short.class);
        DEFAULT_PRIMITIVES.add(int.class);
        DEFAULT_PRIMITIVES.add(byte.class);
        DEFAULT_PRIMITIVES.add(float.class);
        DEFAULT_PRIMITIVES.add(double.class);
        DEFAULT_PRIMITIVES.add(char.class);
        DEFAULT_PRIMITIVES.add(Boolean.class);
        DEFAULT_PRIMITIVES.add(Long.class);
        DEFAULT_PRIMITIVES.add(Short.class);
        DEFAULT_PRIMITIVES.add(Integer.class);
        DEFAULT_PRIMITIVES.add(Byte.class);
        DEFAULT_PRIMITIVES.add(Float.class);
        DEFAULT_PRIMITIVES.add(Double.class);
        DEFAULT_PRIMITIVES.add(Character.class);
        DEFAULT_PRIMITIVES.add(String.class);
        DEFAULT_PRIMITIVES.add(Date.class);

        DEFAULT_COLLECTIONS.add(boolean[].class);
        DEFAULT_COLLECTIONS.add(long[].class);
        DEFAULT_COLLECTIONS.add(short[].class);
        DEFAULT_COLLECTIONS.add(int[].class);
        DEFAULT_COLLECTIONS.add(byte[].class);
        DEFAULT_COLLECTIONS.add(float[].class);
        DEFAULT_COLLECTIONS.add(double[].class);
        DEFAULT_COLLECTIONS.add(char[].class);
    }

    public static boolean isSimpleType(Class<?> type) {
        Assert.notNull(type);
        for (Class<?> clazz : DEFAULT_PRIMITIVES) {
            if (type == clazz || clazz.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimitivesCollectionType(Class<?> type) {
        Assert.notNull(type);

        for (Class<?> clazz : DEFAULT_COLLECTIONS) {
            if (type == clazz || clazz.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

}
