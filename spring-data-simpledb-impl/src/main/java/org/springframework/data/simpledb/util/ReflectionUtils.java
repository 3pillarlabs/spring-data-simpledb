package org.springframework.data.simpledb.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

public final class ReflectionUtils {

	private static final String METHOD_SETTER = "setter";
	private static final String METHOD_GETTER = "getter";
	private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

	private ReflectionUtils() {
		// utility class
	}

	public static Object callGetter(Object obj, String fieldName) {
		try {
			Method getterMethod = retrieveGetterFrom(obj.getClass(), fieldName);
			Assert.notNull(getterMethod, "No getter found for: " + fieldName);

			return getterMethod.invoke(obj);

		} catch(IllegalAccessException e) {
			throw toMappingException(e, METHOD_GETTER, fieldName, obj);
		} catch(InvocationTargetException e) {
			throw toMappingException(e, METHOD_GETTER, fieldName, obj);
		} catch(IllegalArgumentException e) {
			throw toMappingException(e, METHOD_GETTER, fieldName, obj);
		}
	}

	public static void callSetter(Object obj, String fieldName, Object fieldValue) {
		try {
			Method setterMethod = retrieveSetterFrom(obj.getClass(), fieldName);
			Assert.notNull(setterMethod, "No setter found for: " + fieldName);
			setterMethod.invoke(obj, fieldValue);

		} catch(IllegalAccessException e) {
			throw toMappingException(e, METHOD_SETTER, fieldName, obj);
		} catch(InvocationTargetException e) {
			throw toMappingException(e, METHOD_SETTER, fieldName, obj);
		} catch(IllegalArgumentException e) {
			throw toMappingException(e, METHOD_SETTER, fieldName, obj);
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
		try {
			Field field = entityClazz.getDeclaredField(fieldName);
			return field.getType();
		} catch(NoSuchFieldException e) {
			throw new IllegalArgumentException("Field doesn't exist in entity :" + fieldName, e);
		}
	}

	public static boolean isFieldInClass(final Class<?> entityClazz, final String fieldName) {
		try {
			Field field = entityClazz.getDeclaredField(fieldName);
			return hasDeclaredGetterAndSetter(field, entityClazz);
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

	public static void assertThatFieldsDeclaredInClass(List<String> fieldNames, Class<?> domainClazz) {
		for(String eachEntry : fieldNames) {
			boolean isFieldDeclared = isFieldInClass(domainClazz, eachEntry);
			Assert.isTrue(isFieldDeclared, "no such field in entity class : " + eachEntry);
		}
	}

	private static MappingException toMappingException(Exception cause, String accessMethod, String fieldName,
			Object fieldObject) {
		return new MappingException("Could not call " + accessMethod + " for field " + fieldName + " in class:  "
				+ fieldObject.getClass(), cause);
	}

}
