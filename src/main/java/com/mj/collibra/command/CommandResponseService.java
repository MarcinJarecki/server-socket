package com.mj.collibra.command;

import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
public interface CommandResponseService {

    /**
     * Create response to connected client
     *
     * @param uuid          - session unique id
     * @param message       - message from client
     * @param chatStartTime - chat start time
     * @return response to client
     */
    String createResponseToClient(UUID uuid, String message, long chatStartTime);
}
