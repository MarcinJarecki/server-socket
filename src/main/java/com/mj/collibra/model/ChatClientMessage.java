package com.mj.collibra.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Marcin Jarecki
 */
@Data
@Builder
public class ChatClientMessage {
    private String clientName;
    private String clientMessage;
    private long charStartTime;
}
