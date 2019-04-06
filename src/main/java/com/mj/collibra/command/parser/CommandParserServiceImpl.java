package com.mj.collibra.command.parser;

import com.mj.collibra.command.enums.ChatClientCommand;
import com.mj.collibra.command.enums.Command;
import com.mj.collibra.command.enums.GraphClientCommand;
import com.mj.collibra.command.enums.TypeOfCommand;
import com.mj.collibra.model.CommandData;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * @author Marcin Jarecki
 */
@Service
public class CommandParserServiceImpl implements CommandParserService {


    @Override
    public CommandData createCommand(String message) {

        if (message != null && message.length() > 0) {
            // TODO Regex
            Command chatClientCommand = Stream.of(ChatClientCommand.values())
                    .filter(comm -> message.contains(comm.getCommandName()))
                    .findFirst()
                    .orElse(null);

            if (chatClientCommand != null) {
                return  CommandData.builder()
                        .typeOfCommand(TypeOfCommand.CHAT)
                        .command(chatClientCommand)
                        .arguments(getCommandParameters(chatClientCommand, message))
                        .build();
            }
            // TODO Regex
            Command graphClientCommand = Stream.of(GraphClientCommand.values())
                    .filter(comm -> message.contains(comm.getCommandName()))
                    .findFirst()
                    .orElse(null);

            if (graphClientCommand != null) {
                return  CommandData.builder()
                        .typeOfCommand(TypeOfCommand.GRAPH)
                        .command(graphClientCommand)
                        .arguments(getCommandParameters(graphClientCommand, message))
                        .build();
            }
        }

        return  CommandData.builder()
                .typeOfCommand(TypeOfCommand.UNDEFINDED)
                .command(null)
                .arguments(null)
                .build();
    }

    private String[] getCommandParameters(Command command, String message) {
        int countOfSpaceAfterCommand = 1;
        String parameters = message.substring(command.getLength() + countOfSpaceAfterCommand);
        return parameters.split(" ");
    }

    @Override
    public TypeOfCommand getCommandType(String message) {
        if (message != null && message.length() > 0) {
            ChatClientCommand isChatMessage = Stream.of(ChatClientCommand.values())
                    .filter(command -> message.contains(command.getCommandName()))
                    .findFirst()
                    .orElse(null);

            if (isChatMessage != null) {
                return TypeOfCommand.CHAT;
            }

            GraphClientCommand isGraphMessage = Stream.of(GraphClientCommand.values())
                    .filter(command -> message.contains(command.getCommandName()))
                    .findFirst()
                    .orElse(null);

            if (isGraphMessage != null) {
                return TypeOfCommand.GRAPH;
            }
        }
        return TypeOfCommand.UNDEFINDED;
    }

    @Override
    public GraphClientCommand getGraphCommand(String message) {
        return Stream.of(GraphClientCommand.values())
                .filter(command -> message.contains(command.getCommandName()))
                .findFirst().orElse(GraphClientCommand.UNDEFINED);
    }


}

