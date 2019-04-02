package com.mj.collibra.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatServerResponse {
    private String clientName;
    private String serverResponse;
}
