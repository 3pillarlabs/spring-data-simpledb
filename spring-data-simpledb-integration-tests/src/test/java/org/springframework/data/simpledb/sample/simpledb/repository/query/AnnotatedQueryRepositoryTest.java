package org.springframework.data.simpledb.sample.simpledb.repository.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simpledb-consistent-repository-context.xml")
public class AnnotatedQueryRepositoryTest {

    public static final String SAMPLE_STRING_PARAM = "Test";

    @Autowired
    AnnotatedQueryRepository repository;

    @Test
    public void custom_methods_with_named_params_query_should_validate_params(){
        repository.sampleQueryMethodWithDeclaredParams(SAMPLE_STRING_PARAM);
    }


    @Test
    public void custom_methods_plain_queries_should_be_created(){
        repository.sampleQueryMethodWithPlainQuery(SAMPLE_STRING_PARAM);
    }

}
