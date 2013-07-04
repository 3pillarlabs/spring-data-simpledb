package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.attributeutil.AttributesKeySplitter;
import org.springframework.data.simpledb.attributeutil.SimpleDbAttributeValueSplitter;
import org.springframework.data.simpledb.reflection.FieldType;
import org.springframework.data.simpledb.reflection.FieldTypeIdentifier;
import org.springframework.data.simpledb.reflection.MetadataParser;
import org.springframework.data.simpledb.reflection.ReflectionUtils;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public class EntityWrapper<T, ID extends Serializable> {

	/* entity metadata */
	private final SimpleDbEntityInformation<T, ?> entityInformation;

	/* field wrappers */
	private final Map<String, AbstractFieldWrapper<T, ID>> wrappedFields = new HashMap<String, AbstractFieldWrapper<T, ID>>();

	private T item;

	public EntityWrapper(SimpleDbEntityInformation<T, ?> entityInformation, T item) {
		this.entityInformation = entityInformation;
		this.item = item;

		createFieldWrappers(false);
	}

	public EntityWrapper(SimpleDbEntityInformation<T, ?> entityInformation) {
		this.entityInformation = entityInformation;
		try {
			this.item = entityInformation.getJavaType().newInstance();

			createFieldWrappers(true);
		} catch(InstantiationException e) {
			throw new MappingException("Could not instantiate object", e);
		} catch(IllegalAccessException e) {
			throw new MappingException("Could not instantiate object", e);
		}
	}

	private void createFieldWrappers(final boolean isNew) {
		for(final Field field : MetadataParser.getSupportedFields(entityInformation.getJavaType())) {
			if(!FieldTypeIdentifier.isOfType(field, FieldType.ID, FieldType.ATTRIBUTES)) {
				wrappedFields.put(field.getName(), FieldWrapperFactory.createFieldWrapper(field, this, isNew));
			}
		}
	}

	public String getDomain() {
		return entityInformation.getDomain();
	}

	public String getItemName() {
		return entityInformation.getItemName(item);
	}

	public Map<String, String> getAttributes() {
		return entityInformation.getAttributes(item);
	}

	public T getItem() {
		return item;
	}

	public void generateIdIfNotSet() {
		if(entityInformation.getItemName(item) == null) {
			setId(UUID.randomUUID().toString());
		}
	}

	public void setId(String itemName) {
		final Field idField;
		try {
			idField = ReflectionUtils.getDeclaredFieldInHierarchy(
					item.getClass(), 
					entityInformation.getItemNameFieldName(item)); 
			idField.setAccessible(Boolean.TRUE);
			idField.set(item, itemName);
		} catch(NoSuchFieldException e) {
			throw new MappingException("Could not set id field", e);
		} catch(IllegalAccessException e) {
			throw new MappingException("Could not set id field", e);
		}
	}

	public Map<String, String> serialize() {
		return serialize("");
	}

	Map<String, String> serialize(final String fieldNamePrefix) {
		final Map<String, String> result = new HashMap<String, String>();

		for(final AbstractFieldWrapper<T, ID> wrappedField : wrappedFields.values()) {
			if(wrappedField.getFieldValue() != null) {
				result.putAll(wrappedField.serialize(fieldNamePrefix));
			}
		}

		return result;
	}

	public Object deserialize(final Map<String, String> attributes) {
		final Map<String, Map<String, String>> nestedFields = AttributesKeySplitter
				.splitNestedAttributeKeys(attributes);

		for(final Entry<String, Map<String, String>> nestedField : nestedFields.entrySet()) {
			/* call deserialize field with Map<String, String> */
			final String fieldName = nestedField.getKey();
			final Map<String, String> fieldAttributes = nestedField.getValue();
			AbstractFieldWrapper<T, ID> fieldWrapper = getWrapper(fieldName);

			Object convertedValue = fieldWrapper.deserialize(fieldAttributes);
			fieldWrapper.setFieldValue(convertedValue);
		}

		final Map<String, String> simpleFields = AttributesKeySplitter.splitSimpleAttributesKeys(attributes);
		for(final Entry<String, String> simpleField : simpleFields.entrySet()) {
			final String fieldName = simpleField.getKey();

			AbstractFieldWrapper<T, ID> fieldWrapper = getWrapper(fieldName);

			Map<String, String> fieldAttributes = new LinkedHashMap<String, String>();
			fieldAttributes.put(fieldName, simpleField.getValue());

			Object convertedValue = fieldWrapper.deserialize(fieldAttributes);
			fieldWrapper.setFieldValue(convertedValue);
		}

		return getItem();
	}

	private AbstractFieldWrapper<T, ID> getWrapper(String fieldName) {
		return wrappedFields.get(fieldName);
	}

	public Map<String, List<String>> toMultiValueAttributes() {
		final Map<String, String> result = new HashMap<String, String>();

		for(final AbstractFieldWrapper<T, ID> wrappedField : wrappedFields.values()) {
			if(wrappedField.getFieldValue() != null) {
				result.putAll(wrappedField.serialize(""));
			}
		}

		return SimpleDbAttributeValueSplitter.splitAttributeValuesWithExceedingLengths(result);
	}
}
