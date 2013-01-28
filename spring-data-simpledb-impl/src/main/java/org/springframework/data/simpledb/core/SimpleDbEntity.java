package org.springframework.data.simpledb.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;
import org.springframework.data.simpledb.util.MetadataParser;
import org.springframework.data.simpledb.util.SimpleDBAttributeConverter;
import org.springframework.util.Assert;

public class SimpleDbEntity<T, ID extends Serializable> {

    private SimpleDbEntityInformation<T, ?> entityInformation;
    private T item;

    public SimpleDbEntity(SimpleDbEntityInformation<T, ?> entityInformation, T item) {
        this.entityInformation = entityInformation;
        this.item = item;
    }

    public SimpleDbEntity(SimpleDbEntityInformation<T, ?> entityInformation) {
        this.entityInformation = entityInformation;
        try {
            this.item = entityInformation.getJavaType().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MappingException("Could not instantiate object", e);
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

    public void setAttributes(Map<String, List<String>> attributes) {
        for (final String key : attributes.keySet()) {
            /* we only support simple primitives (list should be of size = 1) */
            final List<String> values = attributes.get(key);
            Assert.notNull(values);
            Assert.isTrue(values.size() == 1);

            try {
                final Field attributesField = item.getClass().getDeclaredField(key);
                {
                    attributesField.setAccessible(true);

                    //if(MetadataParser.)


                    attributesField.set(item, SimpleDBAttributeConverter.toDomainFieldPrimitive(values.get(0), attributesField.getType()));
                }
            } catch (Exception e) {
                throw new MappingException("Could not set attribute field " + key, e);
            }
        }

//        try {
//            final Field attributesField = item.getClass().getDeclaredField(entityInformation.getAttributesFieldName(item));
//
//            attributesField.setAccessible(true);
//            attributesField.set(item, attributes);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new MappingException("Could not set attribute field", e);
//        }
    }

    /**
     * @param fieldNamePrefix the prefix the attribute names should be prefixed with
     *
     * @return a map of all serialized field name with the corresponding list of values (if the field is a collection of primitives)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, List<String>> toAttributes(final String fieldNamePrefix) {
        final Map<String, List<String>> result = getSerializedPrimitiveAttributes(fieldNamePrefix);

        for (final Field itemField : MetadataParser.getNestedDomainFields(item)) {
            try {
                itemField.setAccessible(Boolean.TRUE);
                final Object nestedEntityInstance = itemField.get(item);

                final SimpleDbEntityInformation entityMetadata = SimpleDbEntityInformationSupport.getMetadata(nestedEntityInstance.getClass());
                final SimpleDbEntity nestedEntity = new SimpleDbEntity(entityMetadata, nestedEntityInstance);

                final String nestedEntityFieldName = itemField.getName();
                final String nestedEntityAttributePrefix = fieldNamePrefix.isEmpty() ? nestedEntityFieldName : fieldNamePrefix + "." + nestedEntityFieldName;

                /* recursive call */
                final Map<String, List<String>> serializedNestedEntity = nestedEntity.toAttributes(nestedEntityAttributePrefix);

                result.putAll(serializedNestedEntity);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new MappingException("Could not retrieve field value " + itemField.getName(), e);
            }
        }

        return result;
    }

    public Map<String, List<String>> toAttributes() {
        return toAttributes("");
    }

    /**
     * @return a map of serialized field name with the corresponding list of values (if the field is a collection of primitives)
     */
    public Map<String, List<String>> getSerializedPrimitiveAttributes() {
        return getSerializedPrimitiveAttributes("");
    }

    private Map<String, List<String>> getSerializedPrimitiveAttributes(final String prefix) {
        final Map<String, List<String>> result = new HashMap<>();

        for (final Field itemField : MetadataParser.getPrimitiveFields(item)) {
            final List<String> fieldValues = new ArrayList<>();

            try {
                itemField.setAccessible(Boolean.TRUE);
                fieldValues.add(SimpleDBAttributeConverter.toSimpleDBAttributeValue(itemField.get(item)));
            } catch (Exception e) {
                throw new MappingException("Could not retrieve field value " + itemField.getName(), e);
            }

            result.put(prefix.isEmpty() ? itemField.getName() : prefix + "." + itemField.getName(), fieldValues);
        }

        return result;
    }
}
