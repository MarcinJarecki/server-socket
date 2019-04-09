package com.mj.collibra.common;

import com.mj.collibra.model.Session;

import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
public interface SessionService {

    /**
     * Generate new session uuid
     *
     * @return uuid
     */
    UUID generateUuid();

    /**
     * Remove session with uuid
     *
     * @param uuid - session id
     */
    void removeSession(UUID uuid);

    /**
     * Return session with uuid
     *
     * @param uuid - session id
     * @return - session
     */
    Session getSession(UUID uuid);

    /**
     * Set session for current connection with client
     *
     * @param uuid - session id
     * @param session - session to set
     * @return - true if success, false when session don`t exist
     */
    boolean setSession(UUID uuid, Session session);

    /**
     * Set client name for current connection w
     *
     * @param uuid - session id
     * @param clientName - client name
     */
    void setSessionClientName(UUID uuid, String clientName);

    /**
     * Return connected client name
     *
     * @param uuid -session id
     * @return Connected client name
     */
    String getClientName(UUID uuid);

}
