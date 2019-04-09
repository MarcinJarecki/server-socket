package com.mj.collibra.command.parser;

import com.mj.collibra.command.enums.ChatClientCommand;
import com.mj.collibra.command.enums.Command;
import com.mj.collibra.command.enums.GraphClientCommand;
import com.mj.collibra.command.enums.TypeOfCommand;
import com.mj.collibra.model.CommandData;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Marcin Jarecki
 */
@Service
public class CommandParserServiceImpl implements CommandParserService {

    private static final ConcurrentHashMap<String, Pattern> COMMAND_PATTERNS = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        Stream.of(ChatClientCommand.values()).forEach(command -> COMMAND_PATTERNS.put(command.getCommandName(), Pattern.compile(command.getCommandName())));
        Stream.of(GraphClientCommand.values()).forEach(command -> COMMAND_PATTERNS.put(command.getCommandName(), Pattern.compile(command.getCommandName())));
    }

    @Override
    public CommandData createCommand(String message) {

        if (message != null && message.length() > 0) {

            Command chatClientCommand = Stream.of(ChatClientCommand.values())
                    .filter(comm -> extractCommandFromMessage(comm.getCommandName(), message))
                    .findFirst()
                    .orElse(null);

            if (chatClientCommand != null) {
                return CommandData.builder()
                        .typeOfCommand(TypeOfCommand.CHAT)
                        .command(chatClientCommand)
                        .arguments(getCommandParameters(chatClientCommand, message))
                        .build();
            }

            Command graphClientCommand = Stream.of(GraphClientCommand.values())
                    .filter(comm -> extractCommandFromMessage(comm.getCommandName(), message))
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

    private boolean extractCommandFromMessage(String command, String message) {
        Pattern pattern = COMMAND_PATTERNS.get(command);
        Matcher match = pattern.matcher(message);
        return match.find();
    }

    private String[] getCommandParameters(Command command, String message) {
        int countOfSpaceAfterCommand = 1;
        if (message.length() > (command.getLength() + 1)) {
            String parameters = message.substring(command.getLength() + countOfSpaceAfterCommand);
            String[] result = parameters.split(" ");
            int pos = 0;
            for (String res : result) {
                result[pos] = res.trim();
                pos++;
            }
            return result;
        }
        return new String[0];
    }

}

