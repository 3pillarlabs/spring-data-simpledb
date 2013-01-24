package org.springframework.data.simpledb.repository.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class SimpleDbRepositoryNamespaceHandler extends NamespaceHandlerSupport {
    /*
 * (non-Javadoc)
 *
 * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
 */
    @Override
    public void init() {
        RepositoryConfigurationExtension extension = new SimpleDbRepositoryConfigExtension();
        RepositoryBeanDefinitionParser repositoryBeanDefinitionParser = new RepositoryBeanDefinitionParser(extension);

        registerBeanDefinitionParser("repositories", repositoryBeanDefinitionParser);
        registerBeanDefinitionParser("config", new SimpleDbConfigParser());
    }
}
