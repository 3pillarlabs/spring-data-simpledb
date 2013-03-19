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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.simpledb.repository.support.SimpleDbRepositoryFactoryBean;
import org.w3c.dom.Element;

/**
 * Responsibilities - provide repository factory bean - read additional properties - create root beans to be used by all
 * instantiated repositories
 * 
 * See JpaRepositoryConfigExtension
 */
public class SimpleDbRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {
	
	private static final String SIMPLEDB_TEMPLATE_REF = "simpledb-template-ref";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtension#getRepositoryInterface()
	 */
	@Override
	public String getRepositoryFactoryClassName() {
		return SimpleDbRepositoryFactoryBean.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#getModulePrefix()
	 */
	@Override
	protected String getModulePrefix() {
		// not for now. used for named queries
		return "simpleDb";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#postProcess(org.springframework
	 * .beans.factory.support.BeanDefinitionBuilder,
	 * org.springframework.data.repository.config14.XmlRepositoryConfigurationSource)
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
		Element element = config.getElement();
		ParsingUtils.setPropertyReference(builder, element, SIMPLEDB_TEMPLATE_REF, "simpleDbOperations");
	}

	/**
	 * We bind here the provided SimpleDB template bean specified by "simpleDbTemplateRef" annotation property <br/>
	 * to our internally used bean simpleDbOperations of class
	 * {@link org.springframework.data.simpledb.repository.support.SimpleDbRepositoryFactoryBean}. <br/>
	 * The bean will be used to construct repository implementations. <br/>
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		AnnotationAttributes attributes = config.getAttributes();
        builder.addPropertyReference("simpleDbOperations", attributes.getString("simpleDbTemplateRef"));
	}

}
