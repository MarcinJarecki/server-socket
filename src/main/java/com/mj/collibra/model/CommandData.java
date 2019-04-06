package com.mj.collibra.model;

import com.mj.collibra.command.enums.Command;
import com.mj.collibra.command.enums.TypeOfCommand;
import lombok.Builder;
import lombok.Data;

/**
 * @author Marcin Jarecki
 */
@Data
@Builder
public class CommandData {
    private Command command;
    private String[] arguments;
    private TypeOfCommand typeOfCommand;
}
