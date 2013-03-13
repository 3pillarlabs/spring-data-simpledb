package org.springframework.data.simpledb.sample.simpledb.repository.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.core.ISimpleDbOperations;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.data.simpledb.core.SimpleDb;
import org.springframework.data.simpledb.repository.config.EnableSimpleDBRepositories;
import org.springframework.data.simpledb.repository.config.SimpleDbConfigParser;
import org.springframework.data.simpledb.sample.simpledb.repository.BasicSimpleDbUserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EnableSimpleDBRepositoriesAnnotationTest {

	@Configuration
	@EnableSimpleDBRepositories(basePackages = "org.springframework.data.simpledb.sample.simpledb.repository")
	static class Config {

		// Default value for simpleDbTemplateRef annotation attribute is simpleDBTemplate
		// The Template bean is needed by core framework so it needs to be provided
		@Bean
		public ISimpleDbOperations simpleDBTemplate() throws Exception {
			SimpleDb simpleDb = new SimpleDb("AKIAIVX775TRPPSZTEMQ", "Nzy6w0iq8JI+DHgdiPPiuqixiMoWQmPhWFgQzOZY");
			simpleDb.setConsistentRead(true);
			simpleDb.setDomainPrefix(SimpleDbConfigParser.readHostname() + "testDB");
			simpleDb.afterPropertiesSet();
			return new SimpleDbTemplate(simpleDb);
		}

	}

	@Autowired
	BasicSimpleDbUserRepository userRepository;

	@Test
	public void enable_repositories_should_be_used_by_core_spring_data() {
		assertNotNull(userRepository);
	}
}
