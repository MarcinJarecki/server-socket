package com.mj.collibra.command;

/**
 * @author Marcin Jarecki
 */
public enum GraphClientCommand {
    /**
     * First chars at client message related with building graph
     */
    ADD_NODE("ADD NODE"),
    ADD_EDGE("ADD EDGE"),
    REMOVE_NODE("REMOVE NODE"),
    REMOVE_EDGE("REMOVE EDGE"),
    SHORTES_PATH("SHORTEST_PATH"),
    CLOSER_THAN("CLOSER THAN"),
    UNDEFINED("");

    private String command;

    GraphClientCommand(String command){
        this.command = command;
    }

    public String getCommand(){
        return this.command;
    }
}
