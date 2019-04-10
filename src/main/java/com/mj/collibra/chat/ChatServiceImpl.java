package com.mj.collibra.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
import com.mj.collibra.command.enums.ChatClientCommand;
import com.mj.collibra.command.enums.ChatServerCommand;
import com.mj.collibra.command.enums.CommonServerCommand;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;


/**
 * @author Marcin Jarecki
 */
@Service()
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Override
    public String startSessionResponse(UUID uuid) {
        return ChatClientCommand.START.getCommandName() + " " + uuid.toString();
    }

    @Override
    public ChatServerResponse createResponseToClient(ChatClientMessage chatClientMessage) {
        String clientName;
        String message = chatClientMessage.getClientMessage();
        if (message.length() > 0) {
            if (message.indexOf(ChatClientCommand.START.getCommandName()) == 0) {
                clientName = message.substring(ChatClientCommand.START.getCommandName().length()).trim();
                return ChatServerResponse.builder()
                        .clientName(clientName)
                        .serverResponse(ChatServerCommand.START.getCommandName() + clientName)
                        .build();
            } else if (message.indexOf(ChatClientCommand.END.getCommandName()) == 0) {
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
    public String endSessionResponse(String clientName, long chatStartTime) {
        if (clientName == null) {
            clientName = "";
        }
        long chatDuration = Instant.now().toEpochMilli() - chatStartTime;

        return new StringBuffer()
                .append(ChatServerCommand.END_PART_1.getCommandName())
                .append(clientName)
                .append(ChatServerCommand.END_PART_2.getCommandName())
                .append(chatDuration)
                .append(ChatServerCommand.END_PART_3.getCommandName())
                .toString();
    }

    private ChatServerResponse notSupportedCommand() {
        return ChatServerResponse.builder()
                .clientName(null)
                .serverResponse(CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommandName())
                .build();
    }

}
