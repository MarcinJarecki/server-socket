package com.mj.collibra.common;

import com.mj.collibra.model.Session;
import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
public interface SessionService {

    UUID generateUuid();

    boolean removeSession(UUID uuid);

    Session getSession(UUID uuid);

    boolean setSession(UUID uuid, Session session);

    boolean setSessionClientName(UUID uuid, String clientName);

    /**
     * Return connected client name
     *
     * @param uuid -session id
     * @return Connected client name
     */
    String getClientName(UUID uuid);

}
