package com.mj.collibra.model;

import lombok.Data;

import java.util.UUID;

/**
 * @author Marcin Jarecki
 */
@Data
public class Session {
    private UUID uuid;
    private String clientName;
    private long sessionStartTime;
}
