package org.springframework.data.simpledb.repository.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.domain.demo.Source;
import org.springframework.data.simpledb.domain.demo.UserJob;
import org.springframework.data.simpledb.repository.demo.UserJobRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class UserJobTest {

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

		final UserJob foundUserJob = userJobRepository.findOne(userJob.getItemId());

		assertNull(foundUserJob.getSource().getToken());
	}
}
