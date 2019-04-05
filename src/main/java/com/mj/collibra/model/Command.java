package com.mj.collibra.model;

import com.mj.collibra.command.TypeOfCommand;
import lombok.Data;

@Data
public class Command {
    private String name;
    private TypeOfCommand typeOfCommand;
}
