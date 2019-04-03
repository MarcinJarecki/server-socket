package com.mj.collibra.command;

/**
 * @author Marcin Jarecki
 */
public interface CommandService {

    /**
     * Return command type base on message content
     *
     * @param message - message from client
     * @return - type of message
     */
    TypeOfCommand getCommandType(String message);

    /**
     * Get graph command base on message
     *
     * @param message - message from client
     * @return - graph command
     */
    GraphClientCommand getGraphCommand(String message);
}
