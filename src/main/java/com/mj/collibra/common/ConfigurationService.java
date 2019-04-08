package com.mj.collibra.common;

/**
 * @author Marcin Jarecki
 */
public interface ConfigurationService {
    /**
     * Return port number read from properties file
     *
     * @return port number
     */
    int getPort();

    /**
     * Return host name read from properties file
     *
     * @return host name
     */
    String getHostName();

    /**
     * Return server connection timeout read from properties file
     *
     * @return server connection timeout
     */
    int getConnectionTimeout();
}
