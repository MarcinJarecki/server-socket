package com.mj.collibra.command;

/**
 * @author Marcin Jarecki
 */
public enum ChatServerCommand {
    /**
     * First chars at server response message related with chat
     */
    START("HI "),
    END_PART_1("BYE "),
    END_PART_2(", WE SPOKE FOR "),
    END_PART_3(" MS");

    private String command;

    ChatServerCommand(String command){
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

}
