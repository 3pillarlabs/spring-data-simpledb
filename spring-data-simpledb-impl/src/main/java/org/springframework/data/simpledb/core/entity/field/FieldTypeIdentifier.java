package org.springframework.data.simpledb.core.entity.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldTypeIdentifier {

   private static final Logger LOG = LoggerFactory.getLogger(FieldTypeIdentifier.class);

	protected FieldTypeIdentifier() {
		/* utility class */
	}


    public static boolean isSerializableField(Field field){
        return isOfType(field, FieldType.getSerializableFieldTypes());
    }

    public static boolean isOfType(final Field field, FieldType... fieldTypes) {
		for(final FieldType fieldType: fieldTypes) {
			if(fieldType.isOfType(field)) {
				return true;
			}
		}

		return false;
	}
	
	public static FieldType getFieldType(final Field field) {
		if(FieldType.ID.isOfType(field)) {
			return FieldType.ID;
		} else if(FieldType.ATTRIBUTES.isOfType(field)) {
			return FieldType.ATTRIBUTES;
		} else if(FieldType.PRIMITIVE.isOfType(field)) {
			return FieldType.PRIMITIVE;
		} else if(FieldType.CORE_TYPE.isOfType(field)) {
			return FieldType.CORE_TYPE;
		} else if(FieldType.COLLECTION.isOfType(field)) {
			return FieldType.COLLECTION;
		} else if(FieldType.PRIMITIVE_ARRAY.isOfType(field)) {
			return FieldType.PRIMITIVE_ARRAY;
		} else if(FieldType.MAP.isOfType(field)) {
			return FieldType.MAP;
		} else if(FieldType.NESTED_ENTITY.isOfType(field)) {
			return FieldType.NESTED_ENTITY;
		}
		
		return FieldType.OBJECT;
	}

    /**
     * This method checks if the declared Field is accessible through getters and setters methods
     * Fields which have only setters OR getters and NOT both are discarded from serialization process
     */
   public static <T> boolean hasDeclaredGetterAndSetter(final Field field, Class<T> entityClazz) {
       boolean hasDeclaredAccessorsMutators = true;

       Method getter = retrieveGetterFrom(entityClazz, field);
       Method setter = retrieveSetterFrom(entityClazz, field);

       if(getter == null || setter == null) {
           hasDeclaredAccessorsMutators = false;
       }

       return hasDeclaredAccessorsMutators;
   }

    public static <T> Method retrieveGetterFrom(final Class<T> entityClazz, final Field field) {
        Method getterMethod;
        try {
            final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entityClazz);
            getterMethod = descriptor.getReadMethod();
        } catch (IntrospectionException e) {
            getterMethod = null;
            LOG.debug("Field {} has not declared getter method", field.getName(), e);
        }
        return getterMethod;
    }

    public static <T> Method retrieveSetterFrom(final Class<T> entityClazz, final Field field) {
        Method setterMethod;

        try {
            final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entityClazz);
            setterMethod = descriptor.getWriteMethod();
        } catch (IntrospectionException e) {
            setterMethod = null;
            LOG.debug("Field {} has not declared setter method", field.getName(), e);
        }
        return setterMethod;
    }
}
