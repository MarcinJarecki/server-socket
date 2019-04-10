package com.mj.collibra.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConfigurationServiceImpl.class)
@TestPropertySource(locations = "classpath:applicationTest.properties")
public class ConfigurationServiceImplTest {

    @Autowired
    private ConfigurationService configurationService;

    private int expectedPort;
    private String expectedHostName;
    private int expectedConnectionTimeout;
    private int getExpectedExecutorPoolSize;

    @Before
    public void setUp() throws Exception {
        expectedPort = 60000;
        expectedHostName = "defaultHostname";
        expectedConnectionTimeout = 300;
        getExpectedExecutorPoolSize = 5;
    }

    @Test
    public void getPort() {
        assertEquals(expectedPort, configurationService.getPort());
    }

    @Test
    public void getHostName() {
        assertEquals(expectedHostName, configurationService.getHostName());
    }

    @Test
    public void getConnectionTimeout() {
        assertEquals(expectedConnectionTimeout, configurationService.getConnectionTimeout());
    }

    @Test
    public void getExecutorPoolSize() {
        assertEquals(getExpectedExecutorPoolSize, configurationService.getExecutorPoolSize());
    }
}