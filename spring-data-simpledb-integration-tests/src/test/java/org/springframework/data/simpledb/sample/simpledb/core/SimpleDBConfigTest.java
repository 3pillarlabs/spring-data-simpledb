package org.springframework.data.simpledb.sample.simpledb.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.simpledb.core.SimpleDbConfig;
import org.springframework.data.simpledb.core.SimpleDbOperationsImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-simpledb-repository-context.xml")
public class SimpleDBConfigTest {

    @Autowired
    SimpleDbConfig config;

    @Test
    public void should_read_from_config_file() {
        assertNotNull(config.getSecretKey());
    }
}
