package org.springframework.data.simpledb.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.springframework.data.simpledb.core.domain.SimpleDbReferencesEntity;
import org.springframework.data.simpledb.reflection.MetadataParser;
import org.springframework.data.simpledb.reflection.ReflectionUtils;

public class NestedReferencedDomainTest {

	@Test
	public void getReferencedAttributes_should_build_recursively_referenced_nested_domain_names() {

		List<Field> referencedFields = ReflectionUtils.getReferenceAttributesList(SimpleDbReferencesEntity.class);

		assertThat(referencedFields.size(), is(3));

		assertThat(referencedFields.get(0).getName(), is("notNestedDouble"));
		assertThat(referencedFields.get(1).getName(), is("firstNestedEntity"));
		assertThat(referencedFields.get(2).getName(), is("secondNestedEntity"));
	}

	@Test(expected = IllegalStateException.class)
	public void validateReferenceAnnotation_should_fail_for_missing_Id() {

		List<Field> referencedFields = ReflectionUtils.getReferenceAttributesList(SimpleDbReferencesEntity.class);
		MetadataParser.validateReferenceAnnotation(referencedFields.get(2));
	}

	@Test(expected = IllegalStateException.class)
	public void validateReferenceAnnotation_should_fail_for_not_nested_field_type() {

		List<Field> referencedFields = ReflectionUtils.getReferenceAttributesList(SimpleDbReferencesEntity.class);
		MetadataParser.validateReferenceAnnotation(referencedFields.get(0));
	}
}
