package org.springframework.data.simpledb.util.marshaller;

/**
 * This class is used to serialize and deserialize objects. It contains the object and additional information
 */
public class Wrapper {
    //object to be store into database
    private Object attributeContent;
    //object instantiated class
    private String attributeClassName;
    //if the object is a Set or an List, this field represent the generic parameter type
    private String genericValueClassName;
    //if the object is a Map, this field represents the key type
    private String genericKeyClassName;

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

    public String getGenericValueClassName() {
        return genericValueClassName;
    }

    public void setGenericValueClassName(String genericValueClassName) {
        this.genericValueClassName = genericValueClassName;
    }

    public String getGenericKeyClassName() {
        return genericKeyClassName;
    }

    public void setGenericKeyClassName(String genericKeyClassName) {
        this.genericKeyClassName = genericKeyClassName;
    }
}
