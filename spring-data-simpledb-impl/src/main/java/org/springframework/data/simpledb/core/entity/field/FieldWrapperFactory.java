package org.springframework.data.simpledb.core.entity.field;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.core.entity.field.wrapper.*;

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
	
	private static <T, ID extends Serializable> PrimitiveFieldWrapper<T, ID> createPrimitiveFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new PrimitiveFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> CoreFieldWrapper<T, ID> createCoreFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new CoreFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> ArrayFieldWrapper<T, ID> createArrayFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new ArrayFieldWrapper<>(field, parent, isNewParent);
	}

	private static <T, ID extends Serializable> CollectionFieldWrapper<T, ID> createCollectionFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new CollectionFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> NestedEntityFieldWrapper<T, ID> createNestedEntityFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new NestedEntityFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> MapFieldWrapper<T, ID> createMapFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new MapFieldWrapper<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> ObjectFieldWrapper<T, ID> createObjectFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new ObjectFieldWrapper<>(field, parent, isNewParent);
	}

}
