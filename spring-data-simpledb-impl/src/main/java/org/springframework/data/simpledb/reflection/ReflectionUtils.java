package org.springframework.data.simpledb.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class ReflectionUtils {

	private static final String METHOD_SETTER = "setter";
	private static final String METHOD_GETTER = "getter";
	private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

	private ReflectionUtils() {
		// utility class
	}

	public static Object callGetter(Object obj, String fieldName) {
		Object object = null;
		try {
			if (obj != null) {
				Field field = getField(obj.getClass(), fieldName);
				if (isPersistentField(field)) {
					 object = getPersistentFieldValue(field, obj);
				} else {
					Method getterMethod = retrieveGetterFrom(obj.getClass(), fieldName);
					Assert.notNull(getterMethod, "No getter found for: " + fieldName);

					object = getterMethod.invoke(obj);
				}
			}
			return object;

		} catch(IllegalAccessException e) {
			throw toMappingException(e, METHOD_GETTER, fieldName, obj);
		} catch(InvocationTargetException e) {
			throw toMappingException(e, METHOD_GETTER, fieldName, obj);
		} catch(IllegalArgumentException e) {
			throw toMappingException(e, METHOD_GETTER, fieldName, obj);
		}
	}

	private static Object getPersistentFieldValue(Field field, Object obj) throws IllegalAccessException {
		boolean fieldAccessible = field.isAccessible();
		try {
			if (!fieldAccessible) {
				field.setAccessible(true);
			}
			return field.get(obj);
			
		} finally {
			field.setAccessible(fieldAccessible);
		}
	}

	public static void callSetter(Object obj, String fieldName, Object fieldValue) {
		try {
			Field field = getField(obj.getClass(), fieldName);
			if (isPersistentField(field)) {
				setPersistentFieldValue(field, obj, fieldValue);
			} else {
				Method setterMethod = retrieveSetterFrom(obj.getClass(), fieldName);
				Assert.notNull(setterMethod, "No setter found for: " + fieldName);
				setterMethod.invoke(obj, fieldValue);
			}

		} catch(IllegalAccessException e) {
			throw toMappingException(e, METHOD_SETTER, fieldName, obj);
		} catch(InvocationTargetException e) {
			throw toMappingException(e, METHOD_SETTER, fieldName, obj);
		} catch(IllegalArgumentException e) {
			throw toMappingException(e, METHOD_SETTER, fieldName, obj);
		}
	}

	private static void setPersistentFieldValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
		boolean fieldAccessible = field.isAccessible();
		try {
			if (!fieldAccessible) {
				field.setAccessible(true);
			}
			field.set(obj, fieldValue);
			
		} finally {
			field.setAccessible(fieldAccessible);
		}
	}

	/**
	 * This method checks if the declared Field is accessible through getters and setters methods Fields which have only
	 * setters OR getters and NOT both are discarded from serialization process
	 */
	public static <T> boolean hasDeclaredGetterAndSetter(final Field field, Class<T> entityClazz) {
		boolean hasDeclaredAccessorsMutators = true;

		Method getter = retrieveGetterFrom(entityClazz, field.getName());
		Method setter = retrieveSetterFrom(entityClazz, field.getName());

		if(getter == null || setter == null) {
			hasDeclaredAccessorsMutators = false;
		}

		return hasDeclaredAccessorsMutators;
	}

	public static Class<?> getFieldClass(final Class<?> entityClazz, final String fieldName) {
		Field field = getField(entityClazz, fieldName);
		return field.getType();
	}

	public static Field getField(final Class<?> entityClazz, final String fieldName) {
		try {
			return entityClazz.getDeclaredField(fieldName);
		} catch(NoSuchFieldException e) {
			throw new IllegalArgumentException("Field doesn't exist in entity :" + fieldName, e);
		}
	}

	public static boolean isOfType(Type type, final Class<?> entityClazz, final String fieldName) {
		try {
			Field field = entityClazz.getDeclaredField(fieldName);
			Type fieldType = field.getGenericType();

			return isSameConcreteType(type, fieldType);

		} catch(NoSuchFieldException e) {
			throw new IllegalArgumentException("Field doesn't exist in entity :" + fieldName, e);
		}
	}

	public static boolean isListOfListOfObject(Type type) {
		if(type instanceof ParameterizedType) {
			ParameterizedType secondGenericType = (ParameterizedType) type;
			Class<?> rowType = (Class<?>) secondGenericType.getRawType();
			if(!List.class.isAssignableFrom(rowType)) {
				return false;
			}
			Class<?> genericObject = (Class<?>) secondGenericType.getActualTypeArguments()[0];

			if(genericObject.equals(Object.class)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> getReferencedAttributeNames(Class<?> clazz) {
		List<String> referenceFields = new ArrayList<String>();

		for(Field eachField : clazz.getDeclaredFields()) {

			if(eachField.getAnnotation(Reference.class) != null) {
				referenceFields.add(eachField.getName());
			}
		}
		return referenceFields;
	}


	public static List<Field> getReferenceAttributesList(Class<?> clazz) {
		List<Field> references = new ArrayList<Field>();
		List<String> referencedFieldNames = ReflectionUtils.getReferencedAttributeNames(clazz);

		for(String fieldName : referencedFieldNames) {
			Field referenceField = ReflectionUtils.getField(clazz, fieldName);
			references.add(referenceField);
            /* recursive call */
			references.addAll(getReferenceAttributesList(referenceField.getType()));
		}

		return references;
	}

    /**
     * Get only the first Level of Nested Reference Attributes from a given class
     * @param clazz
     * @return List<Field> of referenced fields
     */
    public static List<Field> getFirstLevelOfReferenceAttributes(Class<?> clazz) {
        List<Field> references = new ArrayList<Field>();
        List<String> referencedFields = ReflectionUtils.getReferencedAttributeNames(clazz);

        for(String eachReference : referencedFields) {
            Field referenceField = ReflectionUtils.getField(clazz, eachReference);
            references.add(referenceField);
        }

        return references;
    }

	private static MappingException toMappingException(Exception cause, String accessMethod, String fieldName,
			Object fieldObject) {
		return new MappingException("Could not call " + accessMethod + " for field " + fieldName + " in class:  "
				+ fieldObject.getClass(), cause);
	}

	private static <T> Method retrieveGetterFrom(final Class<T> entityClazz, final String fieldName) {
		Method getterMethod;
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, entityClazz);
			getterMethod = descriptor.getReadMethod();
		} catch(IntrospectionException e) {
			getterMethod = null;
			LOG.debug("Field {} has not declared getter method", fieldName, e);
		}
		return getterMethod;
	}

	private static <T> Method retrieveSetterFrom(final Class<T> entityClazz, final String fieldName) {
		Method setterMethod;

		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, entityClazz);
			setterMethod = descriptor.getWriteMethod();
		} catch(IntrospectionException e) {
			setterMethod = null;
			LOG.debug("Field {} has not declared setter method", fieldName, e);
		}
		return setterMethod;
	}

	private static boolean isSameConcreteType(Type firstType, Type secondType) {
		if(firstType instanceof ParameterizedType && secondType instanceof ParameterizedType) {

			Type firstRawType = ((ParameterizedType) firstType).getRawType();
			Class<?> firstTypeClass = (Class<?>) firstRawType;
			Type secondRawType = ((ParameterizedType) secondType).getRawType();
			Class<?> secondTypeClass = (Class<?>) secondRawType;

			if(firstTypeClass.isAssignableFrom(secondTypeClass)) {
				Type firstTypeArgument = ((ParameterizedType) firstType).getActualTypeArguments()[0];
				Type secondTypeArgument = ((ParameterizedType) secondType).getActualTypeArguments()[0];
				return isSameConcreteType(firstTypeArgument, secondTypeArgument);
			}
			return false;
		} else {
			return firstType.equals(secondType);
		}
	}

	public static boolean isReference(Field field) {
		return field.getAnnotation(Reference.class) != null;
	}
	
	public static boolean isPersistentField(Field field) {
		return field.isAnnotationPresent(Persistent.class);
	}
	
	/**
	 * Retrieve the {@link Field} corresponding to the propertyPath in the given
	 * class.
	 * 
	 * @param clazz
	 * @param propertyPath
	 * @return
	 */
	public static Field getPropertyField(Class<?> clazz, String propertyPath) {
		
		Field propertyField = null;
		try {
			String[] properties = propertyPath.split("\\.");
			Field carField = clazz.getDeclaredField(properties[0]);
			if (properties.length == 1) {
				propertyField = carField;
			} else {
				String cdr = StringUtils.arrayToDelimitedString(
						Arrays.copyOfRange(properties, 1, properties.length), ".");
				propertyField = getPropertyField(carField.getType(), cdr);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error accessing propertyPath: " + propertyPath + 
					" on class: " + clazz.getName(), e);
		}
		return propertyField;
	}

}
