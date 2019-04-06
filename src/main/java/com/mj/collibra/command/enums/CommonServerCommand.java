package com.mj.collibra.command.enums;

/**
 * @author Marcin Jarecki
 */
public enum CommonServerCommand implements Command {
    /**
     * First chars at server response message
     */
    NOT_SUPPORTED_COMMAND("SORRY, I DIDN'T UNDERSTAND THAT");

    private String command;
    private int length;

    CommonServerCommand(String command){
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
