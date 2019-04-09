package com.mj.collibra.common;

import com.mj.collibra.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Marcin Jarecki
 */
@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final ConcurrentHashMap<UUID, Session> sessionsStorage = new ConcurrentHashMap<>();

    @Override
    public UUID generateUuid() {
        return UUID.randomUUID();
    }

    @Override
    public void removeSession(UUID uuid) {
        Session session = getSession(uuid);
        if (session != null) {
            sessionsStorage.remove(uuid);
        }
    }

    @Override
    public Session getSession(UUID uuid) {
        return sessionsStorage.get(uuid);
    }

    @Override
    public boolean setSession(UUID uuid, Session session) {
        if (getSession(uuid) != null) {
            return false;
        } else {
            sessionsStorage.put(uuid, session);
        }
        return true;
    }

    @Override
    public String getClientName(UUID uuid) {
        if (getSession(uuid) == null) {
            return "";
        }
        return getSession(uuid).getClientName();
    }

    @Override
    public void setSessionClientName(UUID uuid, String clientName) {
        Session session = getSession(uuid);
        if (session != null) {
            session.setClientName(clientName);
        }
    }

}
