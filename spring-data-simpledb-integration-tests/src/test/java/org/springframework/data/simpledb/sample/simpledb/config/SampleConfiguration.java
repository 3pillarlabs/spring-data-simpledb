package org.springframework.data.simpledb.sample.simpledb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.config.AbstractSimpleDBConfiguration;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.data.simpledb.core.SimpleDb;

@Configuration
public class SampleConfiguration extends AbstractSimpleDBConfiguration {

	@Override
	public SimpleDbTemplate simpleDBTemplate() {
		return new SimpleDbTemplate(new SimpleDb());
	}
}
