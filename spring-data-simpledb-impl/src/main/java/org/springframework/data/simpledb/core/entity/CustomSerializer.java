package org.springframework.data.simpledb.core.entity;

import java.io.IOException;

public interface CustomSerializer<T> {

	String serialize(T entity) throws IOException;
	T deserialize(String toDeserialize) throws IOException;
}
