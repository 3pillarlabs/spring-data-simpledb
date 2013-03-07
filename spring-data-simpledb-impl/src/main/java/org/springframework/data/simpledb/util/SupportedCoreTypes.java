package org.springframework.data.simpledb.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

public enum SupportedCoreTypes {

	PRIMITIVE_TYPES {

		@Override
		public boolean isOfType(Class<?> clazz) {
			return clazz.isPrimitive();
		}
	},
	CORE_TYPES {

		@Override
		public boolean isOfType(Class<?> clazz) {
			return isOfType(clazz, SUPPORTED_CORE_TYPES);
		}
	},
	PRIMITIVE_ARRAYS {

		@Override
		public boolean isOfType(Class<?> clazz) {
			return isOfType(clazz, SUPPORTED_PRIMITIVE_ARRAYS);
		}
	};

	public abstract boolean isOfType(final Class<?> clazz);

	public static boolean isSupported(final Class<?> clazz) {
		return PRIMITIVE_TYPES.isOfType(clazz) || CORE_TYPES.isOfType(clazz) || PRIMITIVE_ARRAYS.isOfType(clazz);
	}

	static boolean isOfType(final Class<?> clazz, final Set<Class<?>> supportedTypes) {
		Assert.notNull(clazz);

		for(Class<?> supportedClazz : supportedTypes) {
			if(clazz == supportedClazz || supportedClazz.isAssignableFrom(clazz)) {
				return true;
			}
		}

		return false;
	}

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
}
