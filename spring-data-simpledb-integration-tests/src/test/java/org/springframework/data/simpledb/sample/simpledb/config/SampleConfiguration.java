package org.springframework.data.simpledb.sample.simpledb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.config.AbstractSimpleDBConfiguration;
import org.springframework.data.simpledb.core.SimpleDBTemplate;
import org.springframework.data.simpledb.core.SimpleDb;

@Configuration
public class SampleConfiguration extends AbstractSimpleDBConfiguration {

	@Override
	public SimpleDBTemplate simpleDBTemplate() {
		return new SimpleDBTemplate(new SimpleDb());
	}
}
