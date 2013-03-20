package org.springframework.data.simpledb.reflection;

import java.lang.reflect.Field;

public class FieldTypeIdentifier {

	protected FieldTypeIdentifier() {
		/* utility class */
	}

	public static boolean isSerializableField(Field field) {
		return isOfType(field, FieldType.getSerializableFieldTypes());
	}

	public static boolean isOfType(final Field field, FieldType... fieldTypes) {
		for(final FieldType fieldType : fieldTypes) {
			if(fieldType.isOfType(field)) {
				return true;
			}
		}

		return false;
	}

	public static FieldType getFieldType(final Field field) {
		if(FieldType.ID.isOfType(field)) {
			return FieldType.ID;
		} else if(FieldType.ATTRIBUTES.isOfType(field)) {
			return FieldType.ATTRIBUTES;
		} else if(FieldType.PRIMITIVE.isOfType(field)) {
			return FieldType.PRIMITIVE;
		} else if(FieldType.CORE_TYPE.isOfType(field)) {
			return FieldType.CORE_TYPE;
		} else if(FieldType.COLLECTION.isOfType(field)) {
			return FieldType.COLLECTION;
		} else if(FieldType.PRIMITIVE_ARRAY.isOfType(field)) {
			return FieldType.PRIMITIVE_ARRAY;
		} else if(FieldType.MAP.isOfType(field)) {
			return FieldType.MAP;
		} else if(FieldType.NESTED_ENTITY.isOfType(field)) {
			return FieldType.NESTED_ENTITY;
		} else if(FieldType.REFERENCE_ENTITY.isOfType(field)) {
			return FieldType.REFERENCE_ENTITY;
		}

		return FieldType.OBJECT;
	}

}
