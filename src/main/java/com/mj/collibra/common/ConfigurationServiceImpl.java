package com.mj.collibra.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Marcin Jarecki
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    @Value("${server.port}")
    private int port;

    @Value("${server.hostName}")
    private String hostName;

    @Value("${server.connection.timeout}")
    private int connectionTimeout;

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

    @Override
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

}
