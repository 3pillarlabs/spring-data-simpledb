package org.springframework.data.simpledb.core.entity;

import org.springframework.data.annotation.Reference;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.data.simpledb.reflection.FieldType;
import org.springframework.data.simpledb.reflection.MetadataParser;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Wraps {@link FieldType#REFERENCE_ENTITY} fields. A reference field is annotated with {@link Reference}
 */
public class ReferenceEntityFieldWrapper<T, ID extends Serializable> extends AbstractSimpleFieldWrapper<T, ID> {

	public ReferenceEntityFieldWrapper(Field field, EntityWrapper<T, ID> parent, final boolean isNewParent) {
		super(field, parent, isNewParent);
	}

	@Override
	public String serializeValue() {
		final Object fieldValue = getFieldValue();

		return MetadataParser.getItemName(fieldValue);
	}

	/**
	 * Deserialization for nested reference fields is handled by {@link SimpleDbTemplate}
	 */
	@Override
	public Object deserializeValue(String value) {
		return null;
	}

}
