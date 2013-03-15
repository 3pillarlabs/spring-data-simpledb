package org.springframework.data.simpledb.sample.simpledb.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.data.simpledb.sample.simpledb.config.SimpleDBJavaConfiguration;
import org.springframework.data.simpledb.sample.simpledb.domain.SimpleDbReferences;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleDBJavaConfiguration.class)
public class SimpleDbReferencesRepositoryTest {

	@Autowired
	private SimpleDbReferencesRepository repository;

	@Autowired
	SimpleDbTemplate simpleDBTemplate;

	@After
	public void tearDown() {
		simpleDBTemplate.deleteAll(SimpleDbReferences.class);
	}

	@Test
	public void manageDomains_should_create_domains_referred_by_repository() {
		AmazonSimpleDB sdb = simpleDBTemplate.getDB();

		final String domainPrefix = simpleDBTemplate.getSimpleDb().getDomainPrefix();

		ListDomainsResult listDomainsResult = sdb.listDomains(new ListDomainsRequest());
		List<String> domainNames = listDomainsResult.getDomainNames();

		assertThat(domainNames.contains(domainPrefix + ".simpleDbReferences"), is(true));
		assertThat(domainNames.contains(domainPrefix + ".firstNestedEntity"), is(true));
		assertThat(domainNames.contains(domainPrefix + ".secondNestedEntity"), is(true));

		Assert.assertNotNull(simpleDBTemplate);
	}
}
