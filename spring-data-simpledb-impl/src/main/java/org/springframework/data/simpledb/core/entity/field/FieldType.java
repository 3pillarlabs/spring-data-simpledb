package org.springframework.data.simpledb.core.entity.field;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.util.Assert;

public enum FieldType {

	ID {
		private static final String FIELD_NAME_DEFAULT_ID = "id";

		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return field.getName().equals(FIELD_NAME_DEFAULT_ID) || field.getAnnotation(Id.class) != null;
		}
	},

	ATTRIBUTES {
		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return field.getAnnotation(Attributes.class) != null;
		}
	},

	PRIMITIVE {
		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return field.getType().isPrimitive();
		}
	},

	CORE_TYPE {
		@Override
		boolean isOfType(Field field) {
			return isOfType(field, SUPPORTED_CORE_TYPES);
		}
	},

	COLLECTION {
		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return Collection.class.isAssignableFrom(field.getType());
		}
	},

	PRIMITIVE_ARRAY {
		@Override
		boolean isOfType(Field field) {
			return isOfType(field, SUPPORTED_PRIMITIVE_ARRAYS);
		}
	},

	NESTED_ENTITY {
		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return ! isOfType(field, ID, ATTRIBUTES, PRIMITIVE, CORE_TYPE, COLLECTION, PRIMITIVE_ARRAY);
		}
	},

	MAP {
		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return Map.class.isAssignableFrom(field.getType());
		}
	};

	abstract boolean isOfType(Field field);
	
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
	
	static boolean isOfType(final Field field, final Set<Class<?>> supportedTypes) {
		Assert.notNull(field);

		final Class<?> type = field.getType();

		for (Class<?> clazz: supportedTypes) {
			if (type == clazz || clazz.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}
	
	static boolean isOfType(final Field field, final FieldType... fieldTypes) {
		for(final FieldType fieldType: fieldTypes) {
			if(fieldType.isOfType(field)) {
				return true;
			}
		}

		return false;
	}
}
