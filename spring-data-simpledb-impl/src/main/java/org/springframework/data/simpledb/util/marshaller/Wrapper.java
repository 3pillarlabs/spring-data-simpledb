package org.springframework.data.simpledb.util.marshaller;

/**
 * Created by: mgrozea
 */
public class Wrapper {
    private Object attributeContent;
    private String attributeClassName;
    private String genericValueClassName;
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
