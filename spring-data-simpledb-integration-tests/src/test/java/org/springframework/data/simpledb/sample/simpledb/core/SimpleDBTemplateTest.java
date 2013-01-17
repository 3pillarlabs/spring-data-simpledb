package org.springframework.data.simpledb.sample.simpledb.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.core.SimpleDbOperations;
import org.springframework.data.simpledb.core.SimpleDbTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
public class SimpleDBTemplateTest {

    @Autowired
    SimpleDbOperations operations;

    @Test
    public void operations_should_not_be_null() {
        Assert.assertNotNull(operations);
        SimpleDbTemplate template = (SimpleDbTemplate)operations;
        System.out.println(template.getAccessID()+":"+template.getSecretKey());
    }
}
