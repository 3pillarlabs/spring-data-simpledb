package org.springframework.data.simpledb.reflection;

import java.lang.reflect.Field;

public class FieldTypeIdentifier {

	protected FieldTypeIdentifier() {
		/* utility class */
	}

	public static boolean isOfType(final Field field, FieldType... fieldTypes) {
		checkValidity(field);
		for(final FieldType fieldType : fieldTypes) {
			if(fieldType.isOfType(field)) {
				return true;
			}
		}

		return false;
	}

	public static FieldType getFieldType(final Field field) {
		checkValidity(field);
		if(FieldType.ID.isOfType(field)) {
			return FieldType.ID;
		} else if(FieldType.CUSTOM_SERIALIZED.isOfType(field)){
			return FieldType.CUSTOM_SERIALIZED;
		}else if(FieldType.ATTRIBUTES.isOfType(field)) {
			return FieldType.ATTRIBUTES;
		} else if(FieldType.PRIMITIVE.isOfType(field)) {
			return FieldType.PRIMITIVE;
		} else if(FieldType.CORE_TYPE.isOfType(field)) {
			return FieldType.CORE_TYPE;
		} else if(FieldType.COLLECTION.isOfType(field)) {
			return FieldType.COLLECTION;
		} else if(FieldType.ARRAY.isOfType(field)) {
			return FieldType.ARRAY;
		} else if(FieldType.MAP.isOfType(field)) {
			return FieldType.MAP;
		} else if(FieldType.NESTED_ENTITY.isOfType(field)) {
			return FieldType.NESTED_ENTITY;
		} else if(FieldType.REFERENCE_ENTITY.isOfType(field)) {
			return FieldType.REFERENCE_ENTITY;
		} 

		return FieldType.OBJECT;
	}
	
	private static void checkValidity(final Field field) {
		if(ReflectionUtils.isCustom(field) && ReflectionUtils.isReference(field)){
			throw new IllegalStateException("Field "+field.getName()+" is both Custom and Reference, can be one");
		}
		if(ReflectionUtils.isCustom(field) && ReflectionUtils.isAttributes(field)){
			throw new IllegalStateException("Field "+field.getName()+" is both Custom and Attributes, can be one");
		}
		if(FieldType.ID.isOfType(field) && ReflectionUtils.isCustom(field)){
			throw new IllegalStateException("Field "+field.getName()+" is ID and cannot be custom serialized");
		}
	}


}
