package com.mj.collibra.chat;

import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
public interface ChatService {

    /**
     * Create chat server response
     *
     * @param uuid - UUID
     * @return - start chat session server response
     */
    String startSessionResponse(UUID uuid);

    /**
     * Create response related with command
     *
     * @param chatClientMessage - chat client message data
     * @return server response related with command
     */
    ChatServerResponse createResponseToClient(ChatClientMessage chatClientMessage);

    /**
     * Create end server chat response
     *
     * @param clientName - client name
     * @param chatStartTime - star chat time
     * @return - end chat session server response
     */
    String endSessionResponse(String clientName, long chatStartTime);

}
