package org.springframework.data.simpledb.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.simpledb.core.entity.EntityWrapper;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformation;
import org.springframework.data.simpledb.repository.support.entityinformation.SimpleDbEntityInformationSupport;

public class GetterAndSetterTest {

    @Test public void hasDeclaredGetterAndSetter_should_retrieve_only_fields_with_declared_getters_and_setters() {

        List<Field> declaredFieldsWithAccessorsAndMutators = MetadataParser.getSupportedFields(SampleBean.class);

        assertThat(declaredFieldsWithAccessorsAndMutators.size(), is(1));

        Field supportedField = declaredFieldsWithAccessorsAndMutators.get(0);

        assertThat(supportedField.getName(), is("withGetterAndSetter"));
        assertThat(ReflectionUtils.hasDeclaredGetterAndSetter(supportedField, SampleBean.class), is(true));
    }



    @Test(expected = AssertionError.class)
    public void retrieveGetter_and_retrieveSetter_From_returns_NULL_when_field_doesnt_declared_getter_and_setter() {
        SampleBean sampleBean = new SampleBean();
        sampleBean.withoutGetterAndSetter = "simple-db";

        EntityWrapper<SampleBean, String> sdbEntity = new EntityWrapper<SampleBean, String>(this.<SampleBean>readEntityInformation(SampleBean.class), sampleBean);
        final Map<String, String> attributes = sdbEntity.serialize();

        final EntityWrapper<SampleBean, String> convertedEntity = new EntityWrapper<SampleBean, String>(this.<SampleBean>readEntityInformation(SampleBean.class));
        convertedEntity.deserialize(attributes);

        assertThat(convertedEntity.getItem().withoutGetterAndSetter, is("simple-db"));
    }

    private <E> SimpleDbEntityInformation<E, String> readEntityInformation(Class<E> clazz) {
        return (SimpleDbEntityInformation<E, String>) SimpleDbEntityInformationSupport.<E>getMetadata(clazz);
    }

    public static class SampleBean {
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
