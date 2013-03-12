package org.springframework.data.simpledb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.core.SimpleDBTemplate;
import org.springframework.data.simpledb.core.SimpleDb;

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
	public SimpleDBTemplate simpleDBTemplate(){
        return new SimpleDBTemplate(simpleDb());
    }


    public abstract AWSCredentials getAWSCredentials();

    /**
     * Override this to configure non credential {@link SimpleDb}  properties
     */
    public void setExtraProperties(SimpleDb simpleDb){
    }

    public SimpleDb simpleDb(){
        AWSCredentials credentials = getAWSCredentials();
        SimpleDb simpleDb = new SimpleDb(credentials.getAccessID(), credentials.getSecretKey());
        setExtraProperties(simpleDb);
        simpleDb.afterPropertiesSet();
        return simpleDb;
    }

}
