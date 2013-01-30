package org.springframework.data.simpledb.core.entity.field.wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.core.entity.field.FieldType;
import org.springframework.data.simpledb.core.entity.field.FieldTypeIdentifier;

public class FieldWrapperFactory {
	
	private FieldWrapperFactory() {
		/* utility class */
	}

	public static <T, ID extends Serializable> AbstractField<T, ID> createFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
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
		
		return createUnsupportedFieldWrapper(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> PrimitiveField<T, ID> createPrimitiveFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new PrimitiveField<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> CoreField<T, ID> createCoreFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new CoreField<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> ArrayField<T, ID> createArrayFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new ArrayField<>(field, parent, isNewParent);
	}

	private static <T, ID extends Serializable> CollectionField<T, ID> createCollectionFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new CollectionField<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> NestedEntityField<T, ID> createNestedEntityFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new NestedEntityField<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> MapField<T, ID> createMapFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new MapField<>(field, parent, isNewParent);
	}
	
	private static <T, ID extends Serializable> UnsupportedField<T, ID> createUnsupportedFieldWrapper(final Field field, final EntityWrapper<T, ID> parent, final boolean isNewParent) {
		return new UnsupportedField<>(field, parent, isNewParent);
	}

}
