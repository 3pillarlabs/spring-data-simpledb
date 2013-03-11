package org.springframework.data.simpledb.sample.simpledb.repository.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.simpledb.core.ISimpleDBOperations;
import org.springframework.data.simpledb.core.SimpleDBTemplate;
import org.springframework.data.simpledb.core.SimpleDb;
import org.springframework.data.simpledb.repository.config.EnableSimpleDBRepositories;
import org.springframework.data.simpledb.sample.simpledb.repository.BasicSimpleDbUserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EnableSimpleDBRepositoriesAnnotationTest {

	@Configuration
	@EnableSimpleDBRepositories(basePackages = "org.springframework.data.simpledb.sample.simpledb.repository")
	static class Config {

		// needed by core framework
		@Bean
		public ISimpleDBOperations simpleDBTemplate() throws Exception {
			return new SimpleDBTemplate(new SimpleDb());
		}

	}

	@Autowired
	BasicSimpleDbUserRepository userRepository;

	@Test
	public void enable_repositories_should_be_used_by_core_spring_data() {
		assertNotNull(userRepository);
	}
}
