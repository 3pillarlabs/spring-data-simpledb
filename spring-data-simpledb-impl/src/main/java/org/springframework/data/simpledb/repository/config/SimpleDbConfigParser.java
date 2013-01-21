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

        NodeList childNodes = element.getChildNodes();

        String accessID = null;
        String secretKey = null;
        String domainManagementPolicy = null;

        for (int i=0; i< childNodes.getLength(); i++){
            Node item = childNodes.item(i);
            NamedNodeMap attributes = item.getAttributes();
            if(attributes != null){
                Node name = attributes.getNamedItem("name");
                Node value = attributes.getNamedItem("value");

                if(name.getNodeValue().equals("accessID")){
                    accessID = value.getNodeValue();
                } else if (name.getNodeValue().equals("secretKey")){
                    secretKey =   value.getNodeValue();
                }  else if (name.getNodeValue().equals("domainManagementPolicy"))  {
                    domainManagementPolicy = value.getNodeValue();
                }

            }
        }

        SimpleDbConfig.createInstance(accessID, secretKey, domainManagementPolicy);

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
