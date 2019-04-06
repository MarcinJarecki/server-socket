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
}
