package org.springframework.data.simpledb.reflection;

import java.lang.reflect.Field;
import java.util.Collection;
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
			return SupportedCoreTypes.PRIMITIVE_TYPES.isOfType(field.getType());
		}
	},

	CORE_TYPE {

		@Override
		boolean isOfType(Field field) {
			final boolean isCoreType = SupportedCoreTypes.CORE_TYPES.isOfType(field.getType());

			return isCoreType && !isOfType(field, ID, ATTRIBUTES);
		}
	},

	COLLECTION {

		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return Collection.class.isAssignableFrom(field.getType());
		}
	},

	ARRAY {

		@Override
		boolean isOfType(Field field) {
			final boolean isArrayType = SupportedCoreTypes.ARRAYS.isOfType(field.getType());

			return isArrayType;
		}
	},

	MAP {

		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return Map.class.isAssignableFrom(field.getType());
		}
	},

	OBJECT {

		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return field.getType().equals(Object.class);
		}
	},

	NESTED_ENTITY {

		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);
			return !(field.getType().equals(Class.class) || 
					isOfType(field, ID, ATTRIBUTES, PRIMITIVE, CORE_TYPE, 
							COLLECTION, ARRAY, MAP, OBJECT, REFERENCE_ENTITY));
		}
	},

	REFERENCE_ENTITY {

		@Override
		boolean isOfType(Field field) {
			Assert.notNull(field);

			return ReflectionUtils.isReference(field);
		}
	};

	abstract boolean isOfType(Field field);

	public static FieldType[] getSerializableFieldTypes() {
		return new FieldType[] { FieldType.PRIMITIVE, FieldType.CORE_TYPE, FieldType.NESTED_ENTITY,
				FieldType.REFERENCE_ENTITY, FieldType.COLLECTION, FieldType.ARRAY, FieldType.MAP,
				FieldType.OBJECT };
	}

	static boolean isOfType(final Class<?> fieldType, final Set<Class<?>> supportedTypes) {
		Assert.notNull(fieldType);

		for(Class<?> clazz : supportedTypes) {
			if(fieldType == clazz || clazz.isAssignableFrom(fieldType)) {
				return true;
			}
		}

		return false;

	}

	static boolean isOfType(final Field field, final FieldType... fieldTypes) {
		for(final FieldType fieldType : fieldTypes) {
			if(fieldType.isOfType(field)) {
				return true;
			}
		}

		return false;
	}
}
