package com.mj.collibra.chat;

import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ChatService {

    String startSessionResponse(UUID uuid);

    ChatServerResponse createResponseToClient(ChatClientMessage chatClientMessage);

    String endSessionResponse(String clientName, LocalDateTime chatTime);

}
