package com.mj.collibra.server;

import com.mj.collibra.graph.DirectGraphService;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.parser.CommandParserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Marcin Jarecki
 */
@Component
@Slf4j
public class SocketServer implements ApplicationListener<ApplicationReadyEvent> {

    private ChatService chatService;
    private CommandParserServiceImpl commandService;

    private ExecutorService executor = null;
    private DirectGraphService directGraphService;

    @Autowired
    public SocketServer(@Qualifier("ChatService") ChatService chatService,
                        CommandParserServiceImpl commandService,
                        DirectGraphService directGraphService) {
        this.chatService = chatService;
        this.commandService = commandService;
        this.directGraphService = directGraphService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        int port = 50000;
        String hostName = "localhost";

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("--------------------------------------------------------------------");
            log.info("-- SERVER START on host:port: {}:{}", hostName, port);
            log.info("--------------------------------------------------------------------");

            // TODO to pool
            executor = Executors.newFixedThreadPool(10);

            directGraphService = new DirectGraphService();

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new MessageHandler(clientSocket, chatService, commandService, directGraphService);
                executor.execute(worker);
            }
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
