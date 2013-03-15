package org.springframework.data.simpledb.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbReferencesEntity;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

// TODO: check the domain prefixes for both domains-references
public class NestedReferencedDomainTest {

	@Test
	public void buildReferencedAttributes_should_build_recursively_referenced_nested_domain_names() {
		SimpleDbEntityInformation<SimpleDbReferencesEntity, String> entityInformation = SimpleDbReferencesEntity
				.entityInformation();

        List<Class<?>> builtListOfDomainRefs = new ArrayList<Class<?>>();

        entityInformation.buildReferencedAttributes(entityInformation.getJavaType(), builtListOfDomainRefs);

        assertThat(builtListOfDomainRefs.size(), is(2));

        assertTrue(builtListOfDomainRefs.contains(SimpleDbReferencesEntity.SecondNestedEntity.class));
        assertTrue(builtListOfDomainRefs.contains(SimpleDbReferencesEntity.FirstNestedEntity.class));
	}
}
