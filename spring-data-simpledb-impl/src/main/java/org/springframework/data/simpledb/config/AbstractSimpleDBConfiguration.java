package org.springframework.data.simpledb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.core.SimpleDbTemplate;

/**
 * Base class for Spring Data SimpleDB configuration using JavaConfig. <br/>
 * 
 * <ul/>
 * <li>support for useful bean injection </li>
 * <li>support for declaring SimpleDB configuration elements</li>
 * </ul>
 */
@Configuration
public abstract class AbstractSimpleDBConfiguration {


	@Bean
	public abstract SimpleDbTemplate simpleDBTemplate();
}
