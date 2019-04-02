package com.mj.collibra.server;

import com.mj.collibra.chat.ChatService;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.UUID;


@Component
@Slf4j
public class Server {
//    @Value("${server.port}")
    private int port;
//
//    @Value("${server.hostName}")
    private String hostName;
//
//    @Value("${server.connection.timeout}")
    private int timeout;

    private final ChatService chatService;

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String clientName;
    private LocalDateTime chatStartTime;

    private boolean clientIsConnected = true;
    private String SERVER_SAY_LOG = "Server say: {}";


    @Autowired
    public Server(@Qualifier("ChatService") ChatService chatService) {
        this.chatService = chatService;
        this.start();
    }

    public void start() {
        this.hostName = "localhost";
        this.port = 50000;
        this.timeout = 300000;

        log.info("--------------------------------------------------------------------");
        log.info("-- SERVER START on host:port: {}:{}", this.hostName, this.port);
        log.info("--------------------------------------------------------------------");

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            UUID uuid = UUID.randomUUID();
            String initMessage = chatService.startSessionResponse(uuid);
            log.debug(SERVER_SAY_LOG, initMessage);
            out.println(initMessage);

            boolean setTimeoutEnable = true;
            String message;
            while ((message = in.readLine()) != null && clientIsConnected) {
                chatStartTime = LocalDateTime.now();
                if(setTimeoutEnable){
                    setTimeoutEnable = false;
                    setTimeout(() -> {
                        String response = chatService.endSessionResponse(clientName, chatStartTime);
                        log.debug("Timeout - " + SERVER_SAY_LOG, response);
                        out.println(response);
                    }, 30000);
                }
                log.debug("Client say: {}", message);
                ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                        .clientName(clientName)
                        .charStartTime(chatStartTime)
                        .clientMessage(message)
                        .build();
                ChatServerResponse response = chatService.createResponseToClient(chatClientMessage);
                if (response.getClientName() != null) {
                    clientName = response.getClientName();
                    log.warn("clientName: {}", clientName);
                }
                log.debug(SERVER_SAY_LOG, response.getServerResponse());
                out.println(response.getServerResponse());
            }

        } catch (SocketTimeoutException exception) {
            String response =  chatService.endSessionResponse(clientName, chatStartTime);
            log.debug(SERVER_SAY_LOG, response);
            out.println(response);
            stop();
        } catch (UnknownHostException e) {
            log.error("Unknown host {}", hostName);
            System.exit(1);
        } catch (IOException e) {
            System.exit(1);
            log.error("Problem with start socket server", e);
        }

    }

    public void stop() {
        try {
            if (in != null) {in.close();}
            if (out != null){out.close();}
            if (clientSocket != null) {clientSocket.close();}
            if (serverSocket != null) {serverSocket.close();}
        } catch (Exception e) {
            log.error("Problem with close socket server", e);
        }
    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                log.error("Sleep thread error: ", e);
            }
        }).start();
    }

}
