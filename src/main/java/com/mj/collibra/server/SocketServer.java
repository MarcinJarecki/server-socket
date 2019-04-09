package com.mj.collibra.server;

import com.mj.collibra.command.CommandResponseService;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.common.ConfigurationService;
import com.mj.collibra.common.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Marcin Jarecki
 */
@Component
@Slf4j
public class SocketServer implements ApplicationListener<ApplicationReadyEvent> {

    private final CommandResponseService commandResponseService;
    private final ChatService chatService;
    private final ConfigurationService configurationService;
    private final SessionService sessionService;

    private ExecutorService executor = null;


    @Autowired
    public SocketServer(@Qualifier("ChatService") ChatService chatService,
                        CommandResponseService commandResponseService,
                        ConfigurationService configurationService,
                        SessionService sessionService) {
        this.chatService = chatService;
        this.commandResponseService = commandResponseService;
        this.configurationService = configurationService;
        this.sessionService = sessionService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            InetAddress inetAddress = InetAddress.getByName(configurationService.getHostName());
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, configurationService.getPort());
            serverSocket.bind(inetSocketAddress);
            log.info("--------------------------------------------------------------------");
            log.info("-- SERVER START on: {} port: {}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            log.info("--------------------------------------------------------------------");


            // TODO to pool
            executor = Executors.newFixedThreadPool(10);

            // noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                long chatStartTime = Instant.now().toEpochMilli();
                Runnable worker = new MessageHandler(clientSocket, chatStartTime, commandResponseService, chatService, sessionService);
                executor.execute(worker);
            }
        } catch (UnknownHostException e) {
            log.error("Cannon resolve hostName", e);
        } catch (IOException e) {
            log.error("Problem with start server socket", e);
        } finally {
            if (executor != null) {
                try {
                    executor.shutdown();
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("Server socket interrupted", e);
                    Thread.currentThread().interrupt();
                } finally {
                    executor.shutdownNow();
                }
            }
        }


    }
}
