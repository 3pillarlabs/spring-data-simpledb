package org.springframework.data.simpledb.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleDbConfigParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        SimpleDbConfig.createInstance(readProperty(element, "accessID"), readProperty(element, "secretKey"), readProperty(element, "domainManagementPolicy"));

        return null;
    }

    private String readProperty(Element element , String propertyName) {
        NodeList childNodes = element.getChildNodes();

        for (int i=0; i< childNodes.getLength(); i++){
            Node item = childNodes.item(i);
            NamedNodeMap attributes = item.getAttributes();
            if(attributes != null){
                Node name = attributes.getNamedItem("name");
                Node value = attributes.getNamedItem("value");

                if(name.getNodeValue().equals(propertyName)){
                    return value.getNodeValue();
                }
            }
        }
        return null;

    }
}
