package org.springframework.data.simpledb.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.simpledb.annotation.Domain;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Map;

public final class AnnotationParser {


    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationParser.class);

    private AnnotationParser(){}//Utility class

    public static String getDomain(Class clazz){
        Domain domain = (Domain)clazz.getAnnotation(Domain.class);
        return domain.value();
    }

    public static String getItemName(Object object){
        Class clazz = object.getClass();
        for (Field f: clazz.getDeclaredFields()) {
            ItemName itemName = f.getAnnotation(ItemName.class);
            if (itemName != null){
                try {
                    f.setAccessible(true);
                    return (String) f.get(object);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not read simpleDb item name", e);
                }
            }
        }

        return null;
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
}
