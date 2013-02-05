package org.springframework.data.simpledb.util.marshaller;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.mrbean.MrBeanModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.model.MappingException;

public class JsonMarshaller implements Marshaller {

    static class Wrapper {
        private Object attributeContent;
        private String attributeClassName;

        public Object getAttributeContent() {
            return attributeContent;
        }

        public void setAttributeContent(Object attributeContent) {
            this.attributeContent = attributeContent;
        }

        public String getAttributeClassName() {
            return attributeClassName;
        }

        public void setAttributeClassName(String attributeClassName) {
            this.attributeClassName = attributeClassName;
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

        String className = unmarshalledWrapper.getAttributeClassName();
        try {
            Class clazz = Class.forName(className);
            String marshalledAttributes = directMarshal(unmarshalledWrapper.getAttributeContent());
            return unmarshal(marshalledAttributes, clazz);
        } catch (ClassNotFoundException e) {
            throw new MappingException(e.getMessage(), e);
        }

    }

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

    public <T> String directMarshal(T input) {

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

    @Override
    public <T> String marshal(T input) {

        if (input == null) {
            log.warn("Null input given to marshal, will return null.");
            return null;
        }

        Wrapper inputWrapper = new Wrapper();
        inputWrapper.setAttributeClassName(input.getClass().getName());
        inputWrapper.setAttributeContent(input);

       return  directMarshal(inputWrapper);
    }

}
