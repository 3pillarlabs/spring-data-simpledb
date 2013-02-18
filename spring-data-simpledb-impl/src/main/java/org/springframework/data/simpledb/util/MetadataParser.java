package org.springframework.data.simpledb.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.simpledb.annotation.Attributes;
import org.springframework.data.simpledb.annotation.DomainPrefix;
import org.springframework.data.simpledb.config.SimpleDbConfig;
import org.springframework.stereotype.Component;

@Component
public final class MetadataParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataParser.class);
    public static final String FIELD_NAME_DEFAULT_ID = "id";

    private MetadataParser() {
        //Utility class
    }

    /**
     * Domain name are computed based on class names: UserJob -> user_job
     *
     * @param clazz
     * @return
     */
    public static String getDomain(Class<?> clazz) {
        StringBuilder ret = new StringBuilder();

        String domainPrefix = getDomainPrefix(clazz);
        if (domainPrefix != null) {
            ret.append(domainPrefix);
            ret.append(".");
        }

        String camelCaseString = clazz.getSimpleName();

        ret.append(StringUtil.toLowerFirstChar(camelCaseString));

        return ret.toString();
    }

    public static String getItemName(Object object) {
        Field idField = getIdField(object);

        if (idField != null) {
            try {
                idField.setAccessible(true);
                return (String) idField.get(object);
            } catch (IllegalAccessException e) {
                throw new MappingException("Could not read simpleDb id field", e);
            }
        }

        return null;
    }

    public static Field getIdField(Object object) {
        return getIdField(object.getClass());
    }

    public static Field getIdField(Class<?> clazz) {
        Field idField = null;

        for (Field f : clazz.getDeclaredFields()) {
            //named id or annotated with Id
            if (f.getName().equals(FIELD_NAME_DEFAULT_ID) || f.getAnnotation(Id.class) != null) {
                if (idField != null) {
                    throw new MappingException("Multiple id fields detected for class " + clazz.getName());
                }
                idField = f;
            }

        }

        return idField;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getAttributes(Object object) {
        Class<?> clazz = object.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            Attributes attributes = f.getAnnotation(Attributes.class);
            if (attributes != null) {
                try {
                    f.setAccessible(true);
                    return (Map<String, String>) f.get(object);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not read simpleDb attributes", e);
                }
            }
        }

        return null;
    }

    public static Field getAttributesField(Object object) {
        Class<?> clazz = object.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            //annotated with Attributes
            Attributes attributes = f.getAnnotation(Attributes.class);
            if (attributes != null) {
                return f;
            }
        }

        return null;
    }

    public static List<Field> getSupportedFields(Object object) {
        List<Field> supportedFields = new ArrayList<>();

        for (Field field : object.getClass().getDeclaredFields()) {

            if (isSerializableFieldForObject(object, field)) {

                supportedFields.add(field);
            }
        }
        return supportedFields;
    }

    private static boolean isSerializableFieldForObject(Object object, Field field) {
        return ReflectionUtils.hasDeclaredGetterAndSetter(field, object.getClass())
                && FieldTypeIdentifier.isSerializableField(field)
                && !hasUnsupportedAnnotations(field)
                && !isIdForObject(field, object);
    }


    private static boolean hasUnsupportedAnnotations(Field field) {
        return (field.getAnnotation(Attributes.class) != null) || (field.getAnnotation(Transient.class) != null);
    }

    private static boolean isIdForObject(Field field, Object object) {
        return field.equals(MetadataParser.getIdField(object));
    }

    public static List<Field> getNestedDomainFields(Object object) {
        final List<Field> fieldList = new ArrayList<>();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (isNestedDomainField(field, object)) {
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    public static boolean isNestedDomainField(Field field, Object object) {
        return FieldTypeIdentifier.isOfType(field, FieldType.NESTED_ENTITY);
    }

    private static String getDomainPrefix(Class<?> clazz) {
        DomainPrefix domainPrefix = (DomainPrefix) clazz.getAnnotation(DomainPrefix.class);
        if (domainPrefix != null) {
            return domainPrefix.value();
        }

        return SimpleDbConfig.getInstance().getDomainPrefix();
    }
}
