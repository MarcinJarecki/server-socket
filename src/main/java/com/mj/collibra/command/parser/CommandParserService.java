package com.mj.collibra.command.parser;

import com.mj.collibra.command.enums.GraphClientCommand;
import com.mj.collibra.command.enums.TypeOfCommand;
import com.mj.collibra.model.CommandData;

/**
 * @author Marcin Jarecki
 */
public interface CommandParserService {

    /**
     * Return command object created from message content
     *
     * @param message - message from client
     * @return - command
     */
    CommandData createCommand(String message);

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
