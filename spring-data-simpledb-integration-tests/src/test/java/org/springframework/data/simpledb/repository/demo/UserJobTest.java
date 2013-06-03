package org.springframework.data.simpledb.repository.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.attributeutil.SimpleDbAttributeValueSplitter;
import org.springframework.data.simpledb.domain.demo.Source;
import org.springframework.data.simpledb.domain.demo.UserJob;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class UserJobTest {

	static String STRING_OF_MAX_SIMPLE_DB_LENGTH = null;

	static {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < SimpleDbAttributeValueSplitter.MAX_ATTR_VALUE_LEN; i++) {
			builder.append("x");
		}

		STRING_OF_MAX_SIMPLE_DB_LENGTH = builder.toString();
	}

	@Autowired
	private UserJobRepository userJobRepository;

	@After
	public void tearDown() {
		userJobRepository.deleteAll();
	}

	@Test
	public void on_update_should_delete_attributes() {
		Source source = new Source();
		source.setToken("sample attribute");

		UserJob userJob = new UserJob();
		userJob.setSource(source);
		userJob.setStringField("test");

		userJob = userJobRepository.save(userJob);

		userJob.setSource(null);

		userJob = userJobRepository.save(userJob);

		final UserJob foundUserJob = userJobRepository.findOne(userJob
				.getItemId());

		// XXX: Fixed incorrect assertion & this fails the test!
		// assertNull(foundUserJob.getSource().getToken());
		assertNull(foundUserJob.getSource());
	}

	@Test
	public void long_values_should_split_and_recombine() {
		UserJob userJob = new UserJob();
		userJob.setStringField("test");
		Source source = new Source();
		source.setToken(STRING_OF_MAX_SIMPLE_DB_LENGTH + "c");
		userJob.setSource(source);

		userJob = userJobRepository.save(userJob);

		final UserJob foundUserJob = userJobRepository.findOne(userJob
				.getItemId());
		assertNotNull(foundUserJob);
		assertEquals("long tokens match", source.getToken(), foundUserJob
				.getSource().getToken());

	}

}
