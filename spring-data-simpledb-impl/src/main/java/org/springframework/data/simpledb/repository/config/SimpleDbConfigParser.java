package org.springframework.data.simpledb.repository.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.simpledb.config.SimpleDbConfig;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleDbConfigParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		SimpleDbConfig config = SimpleDbConfig.getInstance();
		config.setAccessID(readProperty(element, "accessID"));
		config.setSecretKey(readProperty(element, "secretKey"));
		config.setDomainManagementPolicy(readProperty(element, "domainManagementPolicy"));
		config.setDomainPrefix(readProperty(element, "domainPrefix"));
		config.setDevDomainPrefix(Boolean.valueOf(readProperty(element, "dev")) ? readHostname() : null);
		config.setConsistentRead(readProperty(element, "consistentRead"));

		return null;
	}

	private String readProperty(Element element, String propertyName) {
		NodeList childNodes = element.getChildNodes();

		for(int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			if(attributes != null) {
				Node name = attributes.getNamedItem("name");
				Node value = attributes.getNamedItem("value");

				if(name.getNodeValue().equals(propertyName)) {
					return value.getNodeValue();
				}
			}
		}
		return null;

	}

	public static String readHostname() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return "dev_" + address.getHostName().replaceAll("\\W+", "_");
		} catch(UnknownHostException e) {
			throw new IllegalArgumentException("Could not read host name", e);
		}
	}
}
