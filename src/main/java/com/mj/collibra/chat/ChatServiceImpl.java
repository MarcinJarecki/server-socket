package com.mj.collibra.chat;

import com.mj.collibra.command.ChatClientCommand;
import com.mj.collibra.command.ChatServerCommand;
import com.mj.collibra.command.CommonServerCommand;
import com.mj.collibra.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
@Service("ChatService")
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Override
    public String startSessionResponse(UUID uuid) {
        return ChatClientCommand.START.getCommand() + uuid.toString();
    }

    @Override
    public ChatServerResponse createResponseToClient(ChatClientMessage chatClientMessage) {
        String clientName;
        String message = chatClientMessage.getClientMessage();
        if (message.length() > 0) {
            if (message.indexOf(ChatClientCommand.START.getCommand()) == 0) {
                clientName = message.substring(ChatClientCommand.START.getCommand().length());
                return  ChatServerResponse.builder()
                        .clientName(clientName)
                        .serverResponse(ChatServerCommand.START.getCommand() + clientName)
                        .build();
            } else if (message.indexOf(ChatClientCommand.END.getCommand()) == 0) {
                clientName = chatClientMessage.getClientName() != null ? chatClientMessage.getClientName() : "";
                return ChatServerResponse.builder()
                        .clientName(clientName)
                        .serverResponse(endSessionResponse(clientName, chatClientMessage.getCharStartTime()))
                        .build();
            } else {
                return notSupportedCommand();
            }
        }
        return notSupportedCommand();
    }

    @Override
    public String endSessionResponse(String clientName, LocalDateTime chatStartTime) {
        if (clientName == null) {clientName = "";}
        long chatDuration =  Duration.between(chatStartTime, LocalDateTime.now()).getSeconds() * 1000;
        return new StringBuffer ()
                .append(ChatServerCommand.END_PART_1.getCommand())
                .append(clientName)
                .append(ChatServerCommand.END_PART_2.getCommand())
                .append(chatDuration)
                .append(ChatServerCommand.END_PART_3.getCommand())
                .toString();
    }

    private ChatServerResponse notSupportedCommand() {
        return ChatServerResponse.builder()
                .clientName(null)
                .serverResponse(CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommand())
                .build();
    }

}
