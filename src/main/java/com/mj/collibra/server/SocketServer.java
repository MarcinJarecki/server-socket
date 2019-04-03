package com.mj.collibra.server;

import com.mj.collibra.Graph.DirectGraph;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.CommandServiceImpl;
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

/**
 * @author Marcin Jarecki
 */
@Component
@Slf4j
public class SocketServer implements ApplicationListener<ApplicationReadyEvent> {

    private ChatService chatService;
    private CommandServiceImpl commandService;

    private ExecutorService executor = null;
    private DirectGraph directGraph;

    @Autowired
    public SocketServer(@Qualifier("ChatService") ChatService chatService,
                        CommandServiceImpl commandService) {
        this.chatService = chatService;
        this.commandService = commandService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        int port = 50000;
        String hostName = "localhost";

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("--------------------------------------------------------------------");
            log.info("-- SERVER START on host:port: {}:{}", hostName, port);
            log.info("--------------------------------------------------------------------");

            executor = Executors.newFixedThreadPool(5);

            directGraph = new DirectGraph();

            //noinspection InfiniteLoopStatement
            while(true){
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new MessageHandler(clientSocket, chatService, commandService);
                executor.execute(worker);
            }
        } catch (IOException e) {
            log.error("Problem with start server socket", e);
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }


    }
}
