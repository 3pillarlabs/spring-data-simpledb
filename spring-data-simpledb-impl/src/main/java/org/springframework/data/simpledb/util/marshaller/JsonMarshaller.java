package org.springframework.data.simpledb.util.marshaller;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.mrbean.MrBeanModule;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

/**
 * Marshall and unmarshall objects, collections and map field wrappers
 */
public final class JsonMarshaller {

    private static JsonMarshaller instance;
    private ObjectMapper jsonMapper;

    private JsonMarshaller() {
        JsonFactory factory = new JsonFactory();
        jsonMapper = new ObjectMapper(factory);
        JsonUnknownPropertyHandler jsonUnknownPropertyHandler = new JsonUnknownPropertyHandler();
        jsonMapper.getDeserializationConfig().addHandler(jsonUnknownPropertyHandler);
        jsonMapper.registerModule(new MrBeanModule());
    }

    public static JsonMarshaller getInstance() {
        if (instance == null) {
            instance = new JsonMarshaller();
        }

        return instance;
    }

    public <T> T unmarshall(String jsonString, Class<?> objectType) {
        Assert.notNull(jsonString);
        try {
            return (T) jsonMapper.readValue(jsonString, objectType);
        } catch (IOException e) {
            throw new MappingException("Could not unmarshall object : " + jsonString, e);
        }
    }


    public <T> String marshall(T input) {
        Assert.notNull(input);
        jsonMapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        jsonMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@class");
        try {
            return jsonMapper.writeValueAsString(input);
        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
    }


}
