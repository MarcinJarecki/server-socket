package com.mj.collibra.command.enums;

/**
 * @author Marcin Jarecki
 */
public enum GraphClientCommand implements Command {
    /**
     * First chars at client message related with building graph
     */
    ADD_NODE("ADD NODE"),
    ADD_EDGE("ADD EDGE"),
    REMOVE_NODE("REMOVE NODE"),
    REMOVE_EDGE("REMOVE EDGE"),
    SHORTEST_PATH("SHORTEST PATH"),
    CLOSER_THAN("CLOSER THAN");

    private String command;
    private int length;

    GraphClientCommand(String command){
        this.command = command;
        this.length = command.length();
    }

    @Override
    public String getCommandName(){
        return this.command;
    }

    @Override
    public int getLength() { return this.length;}
}
