/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.simpledb.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;


public class SimpleDbRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    private static final Class<?> PAB_POST_PROCESSOR = PersistenceAnnotationBeanPostProcessor.class;
    private static final Class<?> PET_POST_PROCESSOR = PersistenceExceptionTranslationPostProcessor.class;
    private static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config14.RepositoryConfigurationExtension#getRepositoryInterface()
     */
    public String getRepositoryFactoryClassName() {
        return JpaRepositoryFactoryBean.class.getName();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#getModulePrefix()
     */
    @Override
    protected String getModulePrefix() {
        return "jpa";
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config14.XmlRepositoryConfigurationSource)
     */
    @Override
    public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {

        Element element = config.getElement();

        postProcess(builder, element.getAttribute("transaction-manager-ref"),
                element.getAttribute("entity-manager-factory-ref"), config.getSource());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource)
     */
    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {

        AnnotationAttributes attributes = config.getAttributes();

        postProcess(builder, attributes.getString("transactionManagerRef"),
                attributes.getString("entityManagerFactoryRef"), config.getSource());
    }

    private void postProcess(BeanDefinitionBuilder builder, String transactionManagerRef, String entityManagerRef,
                             Object source) {

        transactionManagerRef = StringUtils.hasText(transactionManagerRef) ? transactionManagerRef
                : DEFAULT_TRANSACTION_MANAGER_BEAN_NAME;
        builder.addPropertyValue("transactionManager", transactionManagerRef);

        if (StringUtils.hasText(entityManagerRef)) {
            builder.addPropertyValue("entityManager", getEntityManagerBeanDefinitionFor(entityManagerRef, source));
        }
    }

    /**
     * Creates an anonymous factory to extract the actual {@link javax.persistence.EntityManager} from the
     * {@link javax.persistence.EntityManagerFactory} bean name reference.
     *
     * @param entityManagerFactoryBeanName
     * @param source
     * @return
     */
    private BeanDefinition getEntityManagerBeanDefinitionFor(String entityManagerFactoryBeanName, Object source) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition("org.springframework.orm.jpa.SharedEntityManagerCreator");
        builder.setFactoryMethod("createSharedEntityManager");
        builder.addConstructorArgReference(entityManagerFactoryBeanName);

        AbstractBeanDefinition bean = builder.getRawBeanDefinition();
        bean.setSource(source);

        return bean;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#registerBeansForRoot(org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.data.repository.config.RepositoryConfigurationSource)
     */
    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {

        super.registerBeansForRoot(registry, configurationSource);

        if (!hasBean(PET_POST_PROCESSOR, registry)) {

            AbstractBeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(PET_POST_PROCESSOR)
                    .getBeanDefinition();

            registerWithSourceAndGeneratedBeanName(registry, definition, configurationSource.getSource());
        }

        if (!hasBean(PAB_POST_PROCESSOR, registry)) {

            AbstractBeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(PAB_POST_PROCESSOR)
                    .getBeanDefinition();

            registerWithSourceAndGeneratedBeanName(registry, definition, configurationSource.getSource());
        }
    }
}
