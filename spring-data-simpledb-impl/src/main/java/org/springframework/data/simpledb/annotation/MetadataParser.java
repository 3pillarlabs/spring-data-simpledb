package org.springframework.data.simpledb.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.simpledb.util.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public final class MetadataParser {


    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataParser.class);
    public static final String FIELD_NAME_DEFAULT_ID = "id";

    private MetadataParser(){
        //Utility class
    }

    /**
     * Domain name are computed based on class names: UserJob -> user_job
     * @param clazz
     * @return
     */
    public static String getDomain(Class clazz){
        String camelCaseString = clazz.getSimpleName();

        String[] words = StringUtil.splitCamelCaseString(camelCaseString);

        return StringUtil.combineLowerCase(words, "_");
    }


    public static String getItemName(Object object){
        Field idField = getIdField(object);

        if (idField != null){
            try {
                idField.setAccessible(true);
                return (String) idField.get(object);
            } catch (IllegalAccessException e) {
                LOGGER.error("Could not read simpleDb item name", e);
            }
        }

        return null;
    }

    public static Field getIdField(Object object){
        Class<?> clazz = object.getClass();
        Field idField = null;

        for (Field f : clazz.getDeclaredFields()) {
            //named id or annotated with Id
            if(f.getName().equals(FIELD_NAME_DEFAULT_ID) || f.getAnnotation(Id.class) != null){
                if(idField != null) {
                    throw new RuntimeException("You cannot have two id fields");
                }
                idField = f;
            }

        }

        return idField;
    }

    public static Map<String, String> getAttributes(Object object){
        Class clazz = object.getClass();
        for (Field f: clazz.getDeclaredFields()) {
            Attributes attributes = f.getAnnotation(Attributes.class);
            if (attributes != null){
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
    
    public static Field getAttributesField(Object object){
        Class clazz = object.getClass();
        for (Field f: clazz.getDeclaredFields()) {
            //annotated with Attributes
            Attributes attributes = f.getAnnotation(Attributes.class);
            if (attributes != null){
                return f;
            }
        }

        return null;
    }

    public static List<Field> getPrimitiveFields(Object object) {
        List<Field> fieldList = new ArrayList<>();

        for(Field field : object.getClass().getDeclaredFields()) {

               if(field.getAnnotation(Attributes.class) == null
                    && field.getAnnotation(Transient.class) == null
                    && !(field.equals(MetadataParser.getIdField(object)))
                    && field.getType().isPrimitive()) {

                fieldList.add(field);
            }
        }

        return fieldList;
    }


}
