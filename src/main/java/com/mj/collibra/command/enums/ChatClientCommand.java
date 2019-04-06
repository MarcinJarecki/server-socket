package com.mj.collibra.command.enums;

/**
 * @author Marcin Jarecki
 */
public enum ChatClientCommand implements Command {
    /**
     * First chars at client message related with chat
     */
    START("HI, I'M "),
    END("BYE MATE!");

    private String command;
    private int length;

    ChatClientCommand(String command){
        this.command = command;
        this.length = command.length();
    }

    @Override
    public String getCommandName() {
        return this.command;
    }

    @Override
    public int getLength() { return this.length;}

}
