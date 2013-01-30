package org.springframework.data.simpledb.core.entity;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.core.entity.field.wrapper.AbstractField;
import org.springframework.data.simpledb.core.entity.field.wrapper.FieldWrapperFactory;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import  org.springframework.data.simpledb.util.AttributesKeySplitter;

import com.amazonaws.services.simpledb.model.Item;

public class EntityWrapper<T, ID extends Serializable> {

	/* entity metadata */
    private SimpleDbEntityInformation<T, ?> entityInformation;
    
    /* field wrappers */
    private List<AbstractField<T, ID>> wrappedFields = new ArrayList<>();
    
    private T item;
    
    private boolean isNew = false;

    public EntityWrapper(SimpleDbEntityInformation<T, ?> entityInformation, T item) {
        this.entityInformation = entityInformation;
        this.item = item;
        
        for(final Field field: item.getClass().getDeclaredFields()) {
        	wrappedFields.add(FieldWrapperFactory.createFieldWrapper(field, this));
        }
    }

    public EntityWrapper(SimpleDbEntityInformation<T, ?> entityInformation) {
        this.entityInformation = entityInformation;
        try {
            this.item = entityInformation.getJavaType().newInstance();
            this.isNew = true;
            
//            for(final Field field: item.getClass().getDeclaredFields()) {
//            	wrappedFields.add(FieldWrapperFactory.createFieldWrapper(field, this));
//            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Could not instantiate object", e);
        }

    }
    
    /**
     * @return true if the {@link Item} instance was created through reflection
     */
    public boolean isNew() {
    	return this.isNew;
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

    public void setAttributes(Map<String, List<String>> attributes) {
        try {
            setPrimitiveAttributes(attributes);

            //key is not primitive
            final Map<String, Map<String, List<String>>> nestedAttributeValues = AttributesKeySplitter.splitNestedAttributeKeys(attributes);
            if (nestedAttributeValues.size() > 0) {
                for (final String key : nestedAttributeValues.keySet()) {
                    final Field attributesField = item.getClass().getDeclaredField(key);

                    final SimpleDbEntityInformation entityMetadata = SimpleDbEntityInformationSupport.getMetadata(attributesField.getType());
                    final EntityWrapper nestedEntity = new EntityWrapper(entityMetadata);

                    /* recursive call */
                    nestedEntity.setAttributes(nestedAttributeValues.get(key));

                    attributesField.setAccessible(true);
                    attributesField.set(item, nestedEntity.getItem());
                }
            }
        } catch (Exception e) {
            throw new MappingException("Could not map attributes", e);
        }
    }

    private void setPrimitiveAttributes(Map<String, List<String>> attributes) throws NoSuchFieldException, IllegalArgumentException, SecurityException, ParseException, IllegalAccessException {
        for (final Entry<String, List<String>> entry : attributes.entrySet()) {
            final String key = entry.getKey();
            final List<String> values = entry.getValue();

            Assert.notNull(values);
            Assert.isTrue(values.size() == 1);

            if (AttributesKeySplitter.isPrimitiveKey(key)) {
                final Field attributesField = item.getClass().getDeclaredField(key);
                {
                    attributesField.setAccessible(true);
                    attributesField.set(item, SimpleDBAttributeConverter.toDomainFieldPrimitive(values.get(0), attributesField.getType()));
                }
            }
        }
    }

    /**
     * @return a map of serialized field name with the corresponding list of values (if the field is a collection of primitives)
     */
    public Map<String, List<String>> getSerializedPrimitiveAttributes() {
        return getSerializedPrimitiveAttributes("");
    }

    private Map<String, List<String>> getSerializedPrimitiveAttributes(final String prefix) {
        final Map<String, List<String>> result = new HashMap<>();

          for (final Field itemField : MetadataParser.getSupportedFields(item)) {
            final List<String> fieldValues = new ArrayList<>();

            try {
                itemField.setAccessible(Boolean.TRUE);
                fieldValues.add(SimpleDBAttributeConverter.toSimpleDBAttributeValue(itemField.get(item)));
            } catch (Exception e) {
                throw new MappingException("Could not retrieve field value " + itemField.getName(), e);
            }

            result.put(prefix.isEmpty() ? itemField.getName() : prefix + "." + itemField.getName(), fieldValues);
        }

        for (final Field primitiveCollectionField : MetadataParser.getPrimitiveCollectionFields(item)) {
            final List<String> fieldValues = new ArrayList<>();

            try {
                SimpleDBAttributeConverter.toSimpleDBAttributeValues(primitiveCollectionField.get(item));
            } catch (IllegalAccessException e) {
                throw new MappingException("Could not retrieve field values " + e);
            }

            try {

                primitiveCollectionField.setAccessible(Boolean.TRUE);
                primitiveCollectionField.get(item);

            } catch (Exception e) {
                throw new MappingException("Could not retrieve field value " + primitiveCollectionField.getName(), e);
            }


            result.put(prefix.isEmpty() ? primitiveCollectionField.getName() : prefix + "." + primitiveCollectionField.getName(), fieldValues);
        }

        return result;
    }
    
    /* ************************* refactored **************** */
    public Map<String, List<String>> serialize() {
        return serialize("");
    }
    public Map<String, List<String>> serialize(final String fieldNamePrefix) {
    	final Map<String, List<String>> result = new HashMap<>();
    	
    	for(final AbstractField<T, ID> wrappedField: wrappedFields) {
    		result.putAll(wrappedField.serialize(fieldNamePrefix));
    	}
    	
    	return result;
    }
    
    public void deserialize(Map<String, List<String>> attributes) {
    	
    }
}
