package org.springframework.data.simpledb.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.field.FieldType;
import org.springframework.data.simpledb.core.entity.field.FieldTypeIdentifier;
import org.springframework.data.simpledb.core.entity.field.wrapper.AbstractField;
import org.springframework.data.simpledb.core.entity.field.wrapper.FieldWrapperFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.util.AttributesKeySplitter;

public class EntityWrapper<T, ID extends Serializable> {

	/* entity metadata */
    private SimpleDbEntityInformation<T, ?> entityInformation;
    
    /* field wrappers */
    private Map<String, AbstractField<T, ID>> wrappedFields = new HashMap<>();
    
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
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Could not instantiate object", e);
        }
    }
    
    private void createFieldWrappers(final boolean isNew) {
        for(final Field field: item.getClass().getDeclaredFields()) {
        	if(! FieldTypeIdentifier.isOfType(field, FieldType.ID, FieldType.ATTRIBUTES)) {
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
        if (entityInformation.getItemName(item) == null) {
            setId(UUID.randomUUID().toString());
        }
    }

    public void setId(String itemName) {
        final Field idField;
        try {
            idField = item.getClass().getDeclaredField(entityInformation.getItemNameFieldName(item));
            idField.setAccessible(Boolean.TRUE);
            idField.set(item, itemName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MappingException("Could not set id field", e);
        }
    }
    
    /* ************************* refactored **************** */
    public Map<String, List<String>> serialize() {
        return serialize("");
    }
    public Map<String, List<String>> serialize(final String fieldNamePrefix) {
    	final Map<String, List<String>> result = new HashMap<>();

      // Serialization should go ONLY for NON-NULL Attribute Values
    	for(final AbstractField<T, ID> wrappedField: wrappedFields.values()) {
           if(wrappedField.getValue() != null) {
    	      	result.putAll(wrappedField.serialize(fieldNamePrefix));
           }
    	}
    	
    	return result;
    }
    
    public void deserialize(final Map<String, List<String>> attributes) {
    	/* handle nested fields */
    	final Map<String, Map<String, List<String>>> nestedFields = AttributesKeySplitter.splitNestedAttributeKeys(attributes);
    	for(final Entry<String, Map<String, List<String>>> nestedField: nestedFields.entrySet()) {
    		/* call deserialize field with Map<String, List<String>> */
    		final String fieldName = nestedField.getKey();
    		final Map<String, List<String>> fieldAttributes = nestedField.getValue();
			wrappedFields.get(fieldName).deserialize(fieldAttributes);
    	}
    	
    	/* handle not nested fields */
    	for(final Entry<String, List<String>> simpleField: attributes.entrySet()) {
    		if(AttributesKeySplitter.isSimpleKey(simpleField.getKey())) {
    			/* call deserialize field with List<String> */
        		final String fieldName = simpleField.getKey();
        		wrappedFields.get(fieldName).deserialize(simpleField.getValue());
    		}
    	}
    }
}
