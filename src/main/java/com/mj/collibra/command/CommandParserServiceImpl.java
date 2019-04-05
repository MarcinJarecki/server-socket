package com.mj.collibra.command;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Marcin Jarecki
 */
@Service
public class CommandParserServiceImpl implements CommandParserService {

    @Override
    public TypeOfCommand getCommandType(String message) {
        if (message != null && message.length() > 0) {
            ChatClientCommand isChatMessage = Stream.of(ChatClientCommand.values())
                    .filter(command -> message.contains(command.getCommand()))
                    .findFirst()
                    .orElse(null);

            if (isChatMessage != null) {
                return TypeOfCommand.CHAT;
            }

            GraphClientCommand isGraphMessage = Stream.of(GraphClientCommand.values())
                    .filter(command -> message.contains(command.getCommand()))
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
                .filter(command -> message.contains(command.getCommand()))
                .findFirst().orElse(GraphClientCommand.UNDEFINED);

    }

    public String[] getCommandParameters(String message) {
        return message.split(" ");
    }


//    string[] numbers = Regex.Split(input, @"\D+");
//        foreach (string value in numbers)
//        {
//        if (!string.IsNullOrEmpty(value))
//        {
//        int i = int.Parse(value);
//        Console.WriteLine("Number: {0}", i);
//        }
//        }


}

