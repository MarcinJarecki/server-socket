package com.mj.collibra.command;

import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.enums.*;
import com.mj.collibra.command.parser.CommandParserService;
import com.mj.collibra.common.SessionService;
import com.mj.collibra.graph.DirectGraphService;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import com.mj.collibra.model.CommandData;
import com.mj.collibra.server.SocketServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CommandResponseServiceImplTest {

    @Autowired
    private CommandResponseService commandResponseService;

    @MockBean
    SocketServer socketServer;

    @MockBean
    private DirectGraphService directGraphService;

    @MockBean
    private CommandParserService commandParserService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private SessionService sessionService;

    private UUID uuid;
    private String message;
    private long chatStartTime;
    private String clientName;
    private ChatClientMessage chatClientMessage;

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        message = "Test message";
        chatStartTime = 123123123;
        clientName = "client name";
        chatClientMessage = ChatClientMessage.builder()
                .clientName(clientName)
                .charStartTime(chatStartTime)
                .clientMessage(message)
                .build();
    }

    @Test
    public void shouldHandleWithNotSupportedCommand() {
        CommandData commandData = CommandData.builder()
                .command(ChatClientCommand.START)
                .typeOfCommand(TypeOfCommand.UNDEFINDED)
                .arguments(new String[0])
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommandName(), result);
    }

    @Test
    public void shouldHandleWithCommandChatStart() {
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(ChatClientCommand.START)
                .typeOfCommand(TypeOfCommand.CHAT)
                .arguments(new String[0])
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        ChatServerResponse chatServerResponse = ChatServerResponse.builder()
                .clientName(clientName)
                .serverResponse(expectedResponse)
                .build();
        when(chatService.createResponseToClient(chatClientMessage)).thenReturn(chatServerResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandChatEnd() {
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(ChatClientCommand.END)
                .typeOfCommand(TypeOfCommand.CHAT)
                .arguments(new String[0])
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        ChatServerResponse chatServerResponse = ChatServerResponse.builder()
                .clientName(clientName)
                .serverResponse(expectedResponse)
                .build();
        when(chatService.createResponseToClient(chatClientMessage)).thenReturn(chatServerResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandGraphAddNode() {
        String[] arguments = new String[3];
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(GraphClientCommand.ADD_NODE)
                .typeOfCommand(TypeOfCommand.GRAPH)
                .arguments(arguments)
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        when(directGraphService.addNode(arguments[0])).thenReturn(expectedResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandGraphRemoveNode() {
        String[] arguments = new String[3];
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(GraphClientCommand.REMOVE_NODE)
                .typeOfCommand(TypeOfCommand.GRAPH)
                .arguments(arguments)
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        when(directGraphService.removeNode(arguments[0])).thenReturn(expectedResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandGraphAddEdge() {
        String[] arguments = new String[3];
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(GraphClientCommand.ADD_EDGE)
                .typeOfCommand(TypeOfCommand.GRAPH)
                .arguments(arguments)
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        when(directGraphService.addEdge(arguments[0], arguments[1], arguments[2])).thenReturn(expectedResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandGraphRemoveEdge() {
        String[] arguments = new String[3];
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(GraphClientCommand.REMOVE_EDGE)
                .typeOfCommand(TypeOfCommand.GRAPH)
                .arguments(arguments)
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        when(directGraphService.removeEdge(arguments[0], arguments[1])).thenReturn(expectedResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandGraphCloseThan() {
        String[] arguments = new String[3];
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(GraphClientCommand.CLOSER_THAN)
                .typeOfCommand(TypeOfCommand.GRAPH)
                .arguments(arguments)
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        when(directGraphService.closerThan(arguments[0], arguments[1])).thenReturn(expectedResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }

    @Test
    public void shouldHandleWithCommandGraphShortesPath() {
        String[] arguments = new String[3];
        String expectedResponse = "expected response";
        CommandData commandData = CommandData.builder()
                .command(GraphClientCommand.SHORTEST_PATH)
                .typeOfCommand(TypeOfCommand.GRAPH)
                .arguments(arguments)
                .build();
        when(commandParserService.createCommand(message)).thenReturn(commandData);
        when(sessionService.getClientName(uuid)).thenReturn(clientName);
        when(directGraphService.shortestPath(arguments[0], arguments[1])).thenReturn(expectedResponse);

        String result = commandResponseService.createResponseToClient(uuid, message, chatStartTime);

        assertEquals(expectedResponse, result);
    }
}