package com.mj.collibra.command;

/**
 * @author Marcin Jarecki
 */
public enum CommonServerCommand {
    /**
     * First chars at server response message
     */
    NOT_SUPPORTED_COMMAND("SORRY, I DIDN'T UNDERSTAND THAT");

    private String command;

    CommonServerCommand(String command){
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

}
