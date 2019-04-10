package com.mj.collibra.server;

import com.mj.collibra.ServerApplicationTests;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.CommandResponseService;
import com.mj.collibra.common.ConfigurationService;
import com.mj.collibra.common.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServerApplicationTests.class,
        initializers = ConfigFileApplicationContextInitializer.class)
public class SocketServerTest {

    @MockBean
    SocketServer socketServer;

    @MockBean
    private CommandResponseService commandResponseService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ConfigurationService configurationService;

    @MockBean
    private SessionService sessionService;

    @Before
    public void setUp()  {
        socketServer = new SocketServer(chatService, commandResponseService, configurationService, sessionService);
    }

    @Test
    public void run() {
        assertNotNull(socketServer);
    }
}