package org.springframework.data.simpledb.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

@Component
public final class MetadataParser {


    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataParser.class);

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

        String[] words = splitCamelCaseString(camelCaseString);

        return combineLowerCase(words,"_");
    }

    private static String[] splitCamelCaseString(String str) {
        return str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    private static String combineLowerCase(String[] values, String separator){
        String ret = "";
        for (String value: values){
            ret += value.toLowerCase()+separator;
        }

        return ret.substring(0, ret.length()-1);
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

    private static Field getIdField(Object object){
        Class clazz = object.getClass();
        for (Field f: clazz.getDeclaredFields()) {
            //named id
            if(f.getName().equals("id")){
                return f;
            }

            //or annotated with Id
            Id id = f.getAnnotation(Id.class);
            if (id != null){
                return f;
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
