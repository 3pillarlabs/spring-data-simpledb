package org.springframework.data.simpledb.util.marshaller;

import org.codehaus.jackson.type.TypeReference;

import java.io.InputStream;

/**
 * Provides a common interface for marshalling/unmarshalling POJOs
 */
public interface Marshaller {

    Object unmarshal(String input);

    /**
     * Populates an object from a string representation.
     *
     * @param clazz the class of the object to populate.
     * @param input the string to unmarshall.
     * @return An object populated from unmarshalling the string representation.
     */
    <T extends Object> T unmarshal(String input, Class<T> clazz);


    /**
     * Populates an object from input stream providing a representation for the destination object.
     *
     * @param clazz the class of the object to populate.
     * @param input the input stream to take object's representation from.
     * @return An instance of the object populated from the representation.
     */
    <T extends Object> T unmarshal(InputStream input, Class<T> clazz);


    <T extends Object> T unmarshal(String input, TypeReference<T> clazz);

    /**
     * Creates a string representation of an object.
     *
     * @param input the object to marshall.
     * @return A string representation of the object.
     */
    <T extends Object> String marshal(T input);


}
