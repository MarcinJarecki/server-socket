package com.mj.collibra.command.enums;

/**
 * @author Marcin Jarecki
 */
public interface Command {
    /**
     * Name of command
     *
     * @return name of command
     */
    String getCommandName();

    /**
     *  length of command
     *
     * @return length of command
     */
    int getLength();
}
