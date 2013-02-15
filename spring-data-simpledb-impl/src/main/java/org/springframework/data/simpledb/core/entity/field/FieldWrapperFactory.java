package org.springframework.data.simpledb.core.entity.field;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.AbstractFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.ArraySimpleFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.CollectionSimpleFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.CoreSimpleFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.MapSimpleFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.NestedEntityFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.ObjectSimpleFieldWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.PrimitiveSimpleFieldWrapper;

public final class FieldWrapperFactory {
	
	private FieldWrapperFactory() {
		/* utility class */
	}

	public static <T, ID extends Serializable> AbstractFieldWrapper<T, ID> createFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		if(FieldTypeIdentifier.isOfType(field, FieldType.PRIMITIVE)) {
			return createPrimitiveFieldWrapper(field, parent, isNewParent); 
		} else if(FieldTypeIdentifier.isOfType(field, FieldType.CORE_TYPE)) {
			return createCoreFieldWrapper(field, parent, isNewParent); 
		} else if(FieldTypeIdentifier.isOfType(field, FieldType.COLLECTION)) {
			return createCollectionFieldWrapper(field, parent, isNewParent); 
		} else if(FieldTypeIdentifier.isOfType(field, FieldType.PRIMITIVE_ARRAY)) {
			return createArrayFieldWrapper(field, parent, isNewParent); 
		} else if(FieldTypeIdentifier.isOfType(field, FieldType.NESTED_ENTITY)) {
			return createNestedEntityFieldWrapper(field, parent, isNewParent); 
		} else if(FieldTypeIdentifier.isOfType(field, FieldType.MAP)) {
			return createMapFieldWrapper(field, parent, isNewParent); 
		}
		
		return createObjectFieldWrapper(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> PrimitiveSimpleFieldWrapper<T, ID> createPrimitiveFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new PrimitiveSimpleFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> CoreSimpleFieldWrapper<T, ID> createCoreFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new CoreSimpleFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> ArraySimpleFieldWrapper<T, ID> createArrayFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new ArraySimpleFieldWrapper<>(field, parent, isNewParent);
	}

	private static <T, ID extends Serializable> CollectionSimpleFieldWrapper<T, ID> createCollectionFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new CollectionSimpleFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> NestedEntityFieldWrapper<T, ID> createNestedEntityFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new NestedEntityFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> MapSimpleFieldWrapper<T, ID> createMapFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new MapSimpleFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> ObjectSimpleFieldWrapper<T, ID> createObjectFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new ObjectSimpleFieldWrapper<>(field, parent, isNewParent);
	}

}
