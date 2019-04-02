package com.mj.collibra.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatClientMessage {
    private String clientName;
    private String clientMessage;
    private LocalDateTime charStartTime;
}