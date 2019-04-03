package com.mj.collibra.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Marcin Jarecki
 */
@Data
@Builder
public class ChatServerResponse {
    private String clientName;
    private String serverResponse;
}
