package com.mj.collibra.command.enums;

/**
 * @author Marcin Jarecki
 */
public enum GraphServerCommand implements Command {
    /**
     * First chars at client message related with building graph
     */
    NODE_ADDED("NODE ADDED"),
    NODE_REMOVED("NODE REMOVED"),
    EDGE_ADDED("EDGE ADDED"),
    EDGE_REMOVED("EDGE REMOVED"),
    NODE_ALREADY_EXISTS("ERROR: NODE ALREADY EXISTS"),
    NODE_NOT_FOUND("ERROR: NODE NOT FOUND");

    private String command;
    private int length;

    GraphServerCommand(String command) {
        this.command = command;
        this.length = command.length();
    }

    @Override
    public String getCommandName() {
        return this.command;
    }

    @Override
    public int getLength() {
        return this.length;
    }
}
