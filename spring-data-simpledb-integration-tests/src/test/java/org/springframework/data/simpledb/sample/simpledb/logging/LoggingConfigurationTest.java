package org.springframework.data.simpledb.sample.simpledb.logging;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbUser;
import org.springframework.data.simpledb.sample.simpledb.repository.BasicSimpleDbUserRepository;
import org.springframework.data.simpledb.sample.simpledb.repository.util.SimpleDbUserBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class LoggingConfigurationTest {

	@Autowired
	BasicSimpleDbUserRepository basicSimpleDbUserRepository;

	@After
	public void tearDown() {
		basicSimpleDbUserRepository.deleteAll();
	}

	@Test
	public void logging_configuration_should_log_entry_exit_points() {
		final String itemName = "ItemNameAOP";
		SimpleDbUser sampleBean = SimpleDbUserBuilder.createUserWithSampleAttributes(itemName);

		basicSimpleDbUserRepository.save(sampleBean);
	}
}
