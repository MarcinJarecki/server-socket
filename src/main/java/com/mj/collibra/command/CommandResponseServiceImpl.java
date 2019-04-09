package com.mj.collibra.command;

import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.enums.CommonServerCommand;
import com.mj.collibra.command.enums.GraphClientCommand;
import com.mj.collibra.command.parser.CommandParserService;
import com.mj.collibra.common.SessionService;
import com.mj.collibra.graph.DirectGraphService;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import com.mj.collibra.model.CommandData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
@Service
@Slf4j
public class CommandResponseServiceImpl implements CommandResponseService{

    private final DirectGraphService directGraphService;
    private final CommandParserService commandParserService;
    private final ChatService chatService;
    private final SessionService sessionService;

    @Autowired
    CommandResponseServiceImpl(ChatService chatService, CommandParserService commandParserService,
                               DirectGraphService directGraphService, SessionService sessionService) {
        this.chatService = chatService;
        this.commandParserService = commandParserService;
        this.directGraphService = directGraphService;
        this.sessionService = sessionService;
    }

    @Override
    public String createResponseToClient(UUID uuid, String message, long chatStartTime) {
        String responseToClient = getUndefinedCommandResponse();
        if (message.length() > 0) {
            CommandData commandData = commandParserService.createCommand(message);
            switch (commandData.getTypeOfCommand()) {
                case CHAT:
                    responseToClient = handleWithChatMessage(uuid, message, chatStartTime);
                    break;
                case GRAPH:
                    responseToClient = handleWithGraphCommand(commandData);
                    break;
                case UNDEFINDED:
                    responseToClient = getUndefinedCommandResponse();
                    break;
                default:
                    responseToClient = getUndefinedCommandResponse();
                    break;
            }
        }
            return responseToClient;
        }

    private String handleWithChatMessage(UUID uuid, String message, long chatStartTime) {
        ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                .clientName(sessionService.getClientName(uuid))
                .charStartTime(chatStartTime)
                .clientMessage(message)
                .build();
        ChatServerResponse response = chatService.createResponseToClient(chatClientMessage);
        if (response.getClientName() != null) {
            sessionService.setSessionClientName(uuid, response.getClientName());
            log.debug("clientName: {}", sessionService.getClientName(uuid));
        }
        return response.getServerResponse();
    }

    private String handleWithGraphCommand(CommandData commandData) {
        String response;
        String nodeName;
        String nodeX;
        String nodeY;
        String edgeWeight;

        GraphClientCommand graphClientCommand = (GraphClientCommand) commandData.getCommand();
        String[] arguments = commandData.getArguments();

        switch (graphClientCommand) {
            case ADD_NODE:
                nodeName = arguments[0];
                response = directGraphService.addNode(nodeName);
                break;
            case REMOVE_NODE:
                nodeName = arguments[0];
                response = directGraphService.removeNode(nodeName);
                break;
            case ADD_EDGE:
                nodeX = arguments[0];
                nodeY = arguments[1];
                edgeWeight = arguments[2];
                response = directGraphService.addEdge(nodeX, nodeY, edgeWeight);
                break;
            case REMOVE_EDGE:
                nodeX = arguments[0];
                nodeY = arguments[1];
                response = directGraphService.removeEdge(nodeX, nodeY);
                break;
            case SHORTEST_PATH:
                nodeX = arguments[0];
                nodeY = arguments[1];
                response = directGraphService.shortestPath(nodeX, nodeY);
                break;
            case CLOSER_THAN:
                nodeX = arguments[0];
                edgeWeight = arguments[1];
                response = directGraphService.closerThan(nodeX, edgeWeight);
                break;
            default:
                response = getUndefinedCommandResponse();
                break;
        }
        return response;
    }

    private String getUndefinedCommandResponse() {
        return CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommandName();
    }
}
