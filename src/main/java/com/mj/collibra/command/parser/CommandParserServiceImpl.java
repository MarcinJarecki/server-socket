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
                return CommandData.builder()
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
                return CommandData.builder()
                        .typeOfCommand(TypeOfCommand.GRAPH)
                        .command(graphClientCommand)
                        .arguments(getCommandParameters(graphClientCommand, message))
                        .build();
            }
        }

        return CommandData.builder()
                .typeOfCommand(TypeOfCommand.UNDEFINDED)
                .command(null)
                .arguments(null)
                .build();
    }

    private String[] getCommandParameters(Command command, String message) {
        int countOfSpaceAfterCommand = 1;
        if (message.length() > (command.getLength() +1) ) {
            String parameters = message.substring(command.getLength() + countOfSpaceAfterCommand);
            String[] result = parameters.split(" ");
            int pos = 0;
            for (String res : result) {
                result[pos] = res.trim();
                pos++;
            }
            return result;
        }
        return null;
    }

}

