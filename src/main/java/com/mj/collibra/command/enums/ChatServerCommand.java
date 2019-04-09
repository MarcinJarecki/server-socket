package com.mj.collibra.command.enums;

/**
 * @author Marcin Jarecki
 */
public enum ChatServerCommand implements Command {
    /**
     * First chars at server response message related with chat
     */
    START("HI "),
    END_PART_1("BYE "),
    END_PART_2(", WE SPOKE FOR "),
    END_PART_3(" MS");

    private String command;
    private int length;

    ChatServerCommand(String command) {
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
