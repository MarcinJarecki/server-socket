package com.mj.collibra.common;

import com.mj.collibra.model.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;

public class SessionServiceImplTest {

    private SessionService sessionService;

    private String clientName;
    private Session session;
    private UUID uuid;

    @Before
    public void setUp() {
        long sessionStartTime = 123123123;
        sessionService = new SessionServiceImpl();

        uuid = UUID.randomUUID();
        clientName = "test client name";
        session = new Session();
        session.setUuid(uuid);
        session.setSessionStartTime(sessionStartTime);
        session.setClientName(clientName);
    }

    @Test
    public void generateUuid() {
        UUID uuid = UUID.randomUUID();

        UUID result = sessionService.generateUuid();

        assertNotEquals(uuid, result);
    }

    @Test
    public void setSession() {
        boolean result = sessionService.setSession(uuid, session);

        assertTrue(result);
    }

    @Test
    public void setSessionWithoutExistedUiid() {
        sessionService.setSession(uuid, session);

        boolean result = sessionService.setSession(uuid, session);

        assertFalse(result);
    }

    @Test
    public void removeSession() {
        sessionService.setSession(uuid, session);

        sessionService.removeSession(uuid);
        Session result = sessionService.getSession(uuid);

        assertNull(result);
    }

    @Test
    public void getSession() {
        sessionService.setSession(uuid, session);

        Session result = sessionService.getSession(uuid);

        assertEquals(session, result);
    }


    @Test
    public void getClientName() {
        sessionService.setSession(uuid, session);

        String result = sessionService.getClientName(uuid);

        assertEquals(clientName, result);
    }

    @Test
    public void getClientNameForNotExistedSession() {
        UUID newUuid = UUID.randomUUID();
        sessionService.setSession(uuid, session);

        String result = sessionService.getClientName(newUuid);

        assertEquals("", result);
    }

    @Test
    public void setSessionClientName() {
        sessionService.setSession(uuid, session);
        String newClientName = "new client name";

        sessionService.setSessionClientName(uuid, newClientName);
        String result = sessionService.getClientName(uuid);

        assertEquals(newClientName, result);
    }
}