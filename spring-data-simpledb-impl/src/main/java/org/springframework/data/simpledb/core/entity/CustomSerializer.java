package org.springframework.data.simpledb.core.entity;

public interface CustomSerializer<T> {

	String serialize(T entity);
	T deserialize(String toDeserialize);
}
