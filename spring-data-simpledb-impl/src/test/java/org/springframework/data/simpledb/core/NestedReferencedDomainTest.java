package org.springframework.data.simpledb.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbReferencesEntity;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;

public class NestedReferencedDomainTest {

	@Test
	public void getReferencedAttributes_should_build_recursively_referenced_nested_domain_names() {
		SimpleDbEntityInformation<SimpleDbReferencesEntity, String> entityInformation = SimpleDbReferencesEntity
				.entityInformation();

        List<Field> referencedFields = entityInformation.getReferencedAttributesList(entityInformation.getJavaType());

        assertThat(referencedFields.size(), is(2));

        assertThat(referencedFields.get(0).getName(), is("firstNestedEntity"));
        assertThat(referencedFields.get(1).getName(), is("secondNestedEntity"));
  	}

    @Test
    public void validateReferenceAnnotation_should_fail_for_missing_id(){
        SimpleDbEntityInformation<SimpleDbReferencesEntity, String> entityInformation = SimpleDbReferencesEntity
                .entityInformation();

        List<Field> referencedFields = entityInformation.getReferencedAttributesList(entityInformation.getJavaType());
        entityInformation.validateReferenceAnnotation(referencedFields.get(1));
    }
}
