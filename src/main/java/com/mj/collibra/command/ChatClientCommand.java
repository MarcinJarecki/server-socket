package com.mj.collibra.command;

/**
 * @author Marcin Jarecki
 */
public enum ChatClientCommand {
    /**
     * First chars at client message related with chat
     */
    START("HI, I'M "),
    END("BYE MATE!");

    private String command;

    ChatClientCommand(String command){
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

}
