package com.mj.collibra.chat;

import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service("ChatService")
@Slf4j
public class ChatServiceImpl implements ChatService {

    private static final String CHAT_SERVER_RESPONSE = "HI ";
    private static final String CHAT_CLIENT_INIT_RESPONSE = "HI, I'M ";
    private static final String CHAT_CLIENT_END_COMMAND = "BYE MATE!";
    private static final String NOT_SUPPORTED_COMMAND_RESPONSE = "SORRY, I DIDN'T UNDERSTAND THAT";

    @Override
    public String startSessionResponse(UUID uuid) {
        return CHAT_CLIENT_INIT_RESPONSE + uuid.toString();
    }

    @Override
    public ChatServerResponse createResponseToClient(ChatClientMessage chatClientMessage) {
        String clientName;
        String message = chatClientMessage.getClientMessage();
        if (message.length() > 0) {
            if (message.indexOf(CHAT_CLIENT_INIT_RESPONSE) == 0) {
                clientName = message.substring(CHAT_CLIENT_INIT_RESPONSE.length());
                return  ChatServerResponse.builder()
                        .clientName(clientName)
                        .serverResponse(CHAT_SERVER_RESPONSE + clientName)
                        .build();
            } else if (message.indexOf(CHAT_CLIENT_END_COMMAND) == 0) {
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
                .append("BYE ")
                .append(clientName)
                .append(", WE SPOKE FOR ")
                .append(chatDuration)
                .append(" MS")
                .toString();
    }

    private ChatServerResponse notSupportedCommand() {
        return ChatServerResponse.builder()
                .clientName(null)
                .serverResponse(NOT_SUPPORTED_COMMAND_RESPONSE)
                .build();
    }

}
