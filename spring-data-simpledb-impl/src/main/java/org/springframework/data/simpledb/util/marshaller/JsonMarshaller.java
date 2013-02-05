package org.springframework.data.simpledb.util.marshaller;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.mrbean.MrBeanModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;

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

    private ObjectMapper jsonMapper;

    public JsonMarshaller() {
        JsonFactory factory = new JsonFactory();
        jsonMapper = new ObjectMapper(factory);
        JsonUnknownPropertyHandler jsonUnknownPropertyHandler = new JsonUnknownPropertyHandler();
        jsonMapper.getDeserializationConfig().addHandler(jsonUnknownPropertyHandler);
        jsonMapper.registerModule(new MrBeanModule());
    }

    @Override
    public Object unmarshalWrapperObject(String input) {
        Wrapper unmarshalledWrapper = unmarshal(input, Wrapper.class);

        String className = unmarshalledWrapper.getAttributeClassName();
        try {
            Class objectClass = Class.forName(className);
            String marshalledAttributes = marshal(unmarshalledWrapper.getAttributeContent());
            return unmarshal(marshalledAttributes, objectClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public <T> T unmarshal(String jsonString, Class<T> objectClass) {
        Assert.notNull(jsonString);

        try {
            return (T) jsonMapper.readValue(jsonString, objectClass);
        } catch (JsonParseException e) {
            //in case of error, if the required class is string, just return the original jsonString
            if (objectClass.equals(String.class)) {
                return (T) jsonString;
            }
            throw new MappingException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MappingException("Could not unmarshal object: " + jsonString, e);
        }
    }

    public <T extends Collection> T unmarshalCollection(String jsonString, Class<T> collectionType, Class<?> genericType) {
        Assert.notNull(jsonString);
        try {
            return (T) jsonMapper.readValue(jsonString, jsonMapper.getTypeFactory().constructCollectionType(collectionType, genericType));
        } catch (IOException e) {
            throw new MappingException("Could not unmarshalWrapperObject collection: " + jsonString, e);
        }
    }

    public <T> String marshalWrapperObject(T input) {
        Wrapper inputWrapper = new Wrapper();
        inputWrapper.setAttributeClassName(input.getClass().getName());
        inputWrapper.setAttributeContent(input);

       return  marshal(inputWrapper);
    }


    @Override
    public <T> String marshal(T input) {
        Assert.notNull(input);
        try {
            return jsonMapper.writeValueAsString(input);
        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

}
