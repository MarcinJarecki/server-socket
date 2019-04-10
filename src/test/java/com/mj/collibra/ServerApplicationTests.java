package com.mj.collibra;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ServerApplicationTests.class,
        initializers = ConfigFileApplicationContextInitializer.class)
public class ServerApplicationTests {

    @Test
    public void contextLoads() {
    }

}
