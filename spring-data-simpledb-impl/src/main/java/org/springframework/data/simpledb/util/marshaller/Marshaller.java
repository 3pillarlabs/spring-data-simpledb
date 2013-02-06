package org.springframework.data.simpledb.util.marshaller;

/**
 * Provides a common interface for marshalling/unmarshalling POJOs
 */
public interface Marshaller {

    Object unmarshallWrapperObject(String input);

    <T> String marshall(T input);

}
