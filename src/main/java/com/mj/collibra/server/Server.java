package com.mj.collibra.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Server {
    @Value("${server.port:50000}")
    private int port;

    @Value("${server.hostName:localhost}")
    private String hostName;

    @Value("${server.connection.timeout:30}")
    private int timeout;

    private AsynchronousServerSocketChannel serverSocketChannel;
    private AsynchronousSocketChannel worker;

    public Server() {
        log.warn("SERVER START");
        this.hostName= "localhost";
        this.port = 5000;
        this.timeout = 30;
        try {
            this.serverSocketChannel = AsynchronousServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(this.hostName, this.port));
            Future<AsynchronousSocketChannel> acceptFuture = this.serverSocketChannel.accept();
            this.worker = acceptFuture.get();
        } catch (Exception e) {
            log.error("Problem with start socket server", e);
        } finally {
            try {
                this.serverSocketChannel.close();
            } catch (IOException e) {
                log.error("Problem with stop socket server", e);
            }
        }
    }


}
