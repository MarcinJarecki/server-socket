package com.mj.collibra.model;

/**
 * @author Marcin Jarecki
 */
public enum GraphServerCommand {
    /**
     * First chars at client message related with building graph
     */
    NODE_ADDED("NODE ADDED"),
    NODE_REMOVED("NODE ADDED"),
    EDGE_ADDED("EDGE ADDED"),
    EDGE_REMOVED("EDGE REMOVED"),
    NODE_ALREADY_EXISTS("ERROR: NODE ALREADY EXISTS"),
    NODE_NOT_FOUND("ERROR: NODE NOT FOUND");

    private String command;

    GraphServerCommand(String command){
        this.command = command;
    }

    public String getCommand(){
        return this.command;
    }
}
