package com.mj.collibra.chat;

import com.mj.collibra.command.enums.ChatClientCommand;
import com.mj.collibra.command.enums.ChatServerCommand;
import com.mj.collibra.command.enums.CommonServerCommand;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ChatServiceImplTest {

    private ChatService chatService;
    private UUID uuid;
    private String clientName;
    private long chatStartTime;

    @Before
    public void setUp() {
        uuid = UUID.randomUUID();
        chatService = new ChatServiceImpl();
        clientName = "Client name";
        chatStartTime = 10;
    }

    @Test
    public void startSessionResponse() {
        String expectedStartSessionResponse = ChatClientCommand.START.getCommandName() + " " + uuid.toString();

        String result = chatService.startSessionResponse(uuid);

        assertEquals(expectedStartSessionResponse, result);
    }

    @Test
    public void createResponseToClientStartCommand() {
        ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                .clientName(null)
                .clientMessage(ChatClientCommand.START.getCommandName() + clientName)
                .charStartTime(10)
                .build();

        ChatServerResponse result = chatService.createResponseToClient(chatClientMessage);

        assertEquals(clientName, result.getClientName());
        assertEquals(ChatServerCommand.START.getCommandName() + clientName, result.getServerResponse());
    }

    @Test
    public void createResponseToClientEndCommand() {
        ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                .clientName(clientName)
                .clientMessage(ChatClientCommand.END.getCommandName() + clientName)
                .charStartTime(10)
                .build();

        ChatServerResponse result = chatService.createResponseToClient(chatClientMessage);

        assertEquals(clientName, result.getClientName());
        assertTrue(result.getServerResponse().contains(ChatServerCommand.END_PART_1.getCommandName()));
        assertTrue(result.getServerResponse().contains(ChatServerCommand.END_PART_2.getCommandName()));
        assertTrue(result.getServerResponse().contains(ChatServerCommand.END_PART_3.getCommandName()));
        assertTrue(result.getServerResponse().contains(clientName));
    }

    @Test
    public void endSessionResponse() {

        String result = chatService.endSessionResponse(clientName, chatStartTime);

        assertTrue(result.contains(ChatServerCommand.END_PART_1.getCommandName()));
        assertTrue(result.contains(ChatServerCommand.END_PART_2.getCommandName()));
        assertTrue(result.contains(ChatServerCommand.END_PART_3.getCommandName()));
        assertTrue(result.contains(clientName));
    }

    @Test
    public void endSessionResponseWithoutClientName() {

        String result = chatService.endSessionResponse(null, chatStartTime);

        assertTrue(result.contains(ChatServerCommand.END_PART_1.getCommandName()));
        assertTrue(result.contains(ChatServerCommand.END_PART_2.getCommandName()));
        assertTrue(result.contains(ChatServerCommand.END_PART_3.getCommandName()));
    }

    @Test
    public void notSupportedCommand() {
        ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                .clientName(clientName)
                .clientMessage("BLA BLA" + clientName)
                .charStartTime(10)
                .build();

        ChatServerResponse result = chatService.createResponseToClient(chatClientMessage);

        assertEquals(CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommandName(), result.getServerResponse());
    }

    @Test
    public void emptyCommand() {
        ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                .clientName(null)
                .clientMessage("")
                .charStartTime(10)
                .build();

        ChatServerResponse result = chatService.createResponseToClient(chatClientMessage);

        assertEquals(CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommandName(), result.getServerResponse());
    }

}