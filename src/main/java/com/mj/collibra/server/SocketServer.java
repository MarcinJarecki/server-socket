package com.mj.collibra.server;

import com.mj.collibra.chat.ChatService;
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

@Component
@Slf4j
public class SocketServer implements ApplicationListener<ApplicationReadyEvent> {

    private ChatService chatService;
    private ExecutorService executor = null;

    @Autowired
    public void SocketServer(@Qualifier("ChatService") ChatService chatService) {
        this.chatService = chatService;
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

            while(true){
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new MessageHandler(clientSocket, chatService);
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
