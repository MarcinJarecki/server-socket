package com.mj.collibra.command;

/**
 * @author Marcin Jarecki
 */
public interface CommandResponseService {
    /**
     * Return conneted client name
     *
     * @return Connected client name
     */
    String getClientName();

    /**
     * Create response to connected client
     *
     * @param message - message from client
     * @param chatStartTime - chat start time
     * @return response to client
     */
    String createResponseToClient(String message, long chatStartTime);
}
