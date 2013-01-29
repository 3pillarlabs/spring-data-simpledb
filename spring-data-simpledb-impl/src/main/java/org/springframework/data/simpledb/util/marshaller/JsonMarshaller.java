package org.springframework.data.simpledb.util.marshaller;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.mrbean.MrBeanModule;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.model.MappingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonMarshaller implements Marshaller {

    static class Wrapper {
        private Object endpointAttribute;
        private String endpointAttrClassName;

        public Object getEndpointAttribute() {
            return endpointAttribute;
        }

        public void setEndpointAttribute(Object endpointAttribute) {
            this.endpointAttribute = endpointAttribute;
        }

        public String getEndpointAttrClassName() {
            return endpointAttrClassName;
        }

        public void setEndpointAttrClassName(String endpointAttrClassName) {
            this.endpointAttrClassName = endpointAttrClassName;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(JsonMarshaller.class);

    private ObjectMapper mapper;

    public JsonMarshaller() {
        JsonFactory factory = new JsonFactory();
        mapper = new ObjectMapper(factory);
        JsonUnknownPropertyHandler jsonUnknownPropertyHandler = new JsonUnknownPropertyHandler();
        mapper.getDeserializationConfig().addHandler(jsonUnknownPropertyHandler);
        mapper.registerModule(new MrBeanModule());
    }

    @Override
    public Object unmarshal(String input) {
        Wrapper unmarshalledWrapper = unmarshal(input, Wrapper.class);

        String className = unmarshalledWrapper.getEndpointAttrClassName();
        try {
            Class clazz = Class.forName(className);
            String marshalledAttributes = marshal(unmarshalledWrapper.getEndpointAttribute());
            return unmarshal(marshalledAttributes, clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public <T> T unmarshal(String input, Class<T> clazz) {

        if (input == null) {
            log.warn("Null input given to unmarshal, will return null.");
            return null;
        }

        T unmarshalledObject;
        try {
            unmarshalledObject = mapper.readValue(input, clazz);
        } catch (JsonParseException e) {
            //in case of error, if the required class is string, just return the original input
            if (clazz.equals(String.class)) {
                return (T) input;
            }
            throw new MappingException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return unmarshalledObject;
    }

    @Override
    public <T> T unmarshal(String input, TypeReference<T> typeReference) {

        if (input == null) {
            log.warn("Null input given to unmarshal, will return null.");
            return null;
        }

        T unmarshalledObject;
        try {
            unmarshalledObject = mapper.<T>readValue(input, typeReference);
        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }

        return unmarshalledObject;
    }

    @Override
    public <T> T unmarshal(InputStream input, Class<T> clazz) {

        if (input == null) {
            log.warn("Null input given to unmarshal, will return null.");
            return null;
        }

        BufferedReader jsonReader = new BufferedReader(new InputStreamReader(input));
        StringBuilder jsonOut = new StringBuilder();
        try {
            String line = jsonReader.readLine();
            while (line != null) {
                jsonOut.append(line);
                line = jsonReader.readLine();
            }
            String json = jsonOut.toString();
            return unmarshal(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public <T> String marshal(T input) {

        if (input == null) {
            log.warn("Null input given to marshal, will return null.");
            return null;
        }

        String marshalledObject;
        try {
            marshalledObject = mapper.writeValueAsString(input);
        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }

        return marshalledObject;
    }

}
