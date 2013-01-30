package org.springframework.data.simpledb.util.marshaller;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.codehaus.jackson.map.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUnknownPropertyHandler extends DeserializationProblemHandler {
    private static final Logger LOG = LoggerFactory.getLogger(JsonUnknownPropertyHandler.class);

    @Override
    public boolean handleUnknownProperty(DeserializationContext ctxt, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
        JsonParser jsonParser = ctxt.getParser();
        LOG.warn("Unknown Json property: " + propertyName);
        jsonParser.skipChildren();

        return true;
    }
}
