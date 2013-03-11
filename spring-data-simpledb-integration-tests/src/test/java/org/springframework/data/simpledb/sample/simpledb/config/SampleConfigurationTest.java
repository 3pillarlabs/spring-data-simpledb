package org.springframework.data.simpledb.sample.simpledb.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.core.SimpleDBTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfiguration.class)
public class SampleConfigurationTest {

    @Autowired
    private SimpleDBTemplate template;

    @Test
    public void testConfig() {
        assertNotNull(template);
    }
}