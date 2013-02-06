package org.springframework.data.simpledb.util;

import org.junit.Test;
import org.springframework.data.simpledb.core.entity.field.FieldTypeIdentifier;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FieldTypeIdentifierTest {

    @Test public void hasDeclaredGetterAndSetter_should_retrieve_only_fields_with_declared_getters_and_setters() {

        List<Field> declaredFieldsWithAccessorsAndMutators = MetadataParser.getSupportedFields(new SampleBean());

        assertThat(declaredFieldsWithAccessorsAndMutators.size(), is(1));

        Field supportedField = declaredFieldsWithAccessorsAndMutators.get(0);

        assertThat(supportedField.getName(), is("withGetterAndSetter"));
        assertThat(FieldTypeIdentifier.hasDeclaredGetterAndSetter(supportedField, SampleBean.class), is(true));
    }

    private static class SampleBean {
        private String withoutGetterAndSetter;
        private Integer withGetterAndSetter;
        private Boolean onlyWithGetter;
        private Double onlyWithSetter;

        public Integer getWithGetterAndSetter() {
            return withGetterAndSetter;
        }

        public void setWithGetterAndSetter(Integer withGetterAndSetter) {
            this.withGetterAndSetter = withGetterAndSetter;
        }

        public Boolean getOnlyWithGetter() {
            return onlyWithGetter;
        }

        public void setOnlyWithSetter(Double onlyWithSetter) {
            this.onlyWithSetter = onlyWithSetter;
        }
    }
}
