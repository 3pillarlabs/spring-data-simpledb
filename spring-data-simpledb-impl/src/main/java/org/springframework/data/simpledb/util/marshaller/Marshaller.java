package org.springframework.data.simpledb.util.marshaller;

/**
 * Provides a common interface for marshalling/unmarshalling POJOs
 */
public interface Marshaller {

    Object unmarshal(String input);

    <T> String marshal(T input);

}
