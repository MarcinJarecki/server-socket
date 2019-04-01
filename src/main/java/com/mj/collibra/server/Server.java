package com.mj.collibra.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;


@Component
@Slf4j
public class Server {
    @Value("${server.port}")
    private int port;

    @Value("${server.hostName}")
    private String hostName;

    @Value("${server.connection.timeout}")
    private int timeout;

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    boolean clientIsConnected = true;
    int MAX_SERVER_TIMEOUT = 30000;

    private String chatServerInitMessage = "HI, I'M ";
    private String chatClientInitResponse = "HI, I'M ";
    private String chatServerResponse = "HI ";
    private String chatClientEndCommand = "BYE MATE!";
    private String chatNotSupportedCommand = "SORRY, I DIDN'T UDERSTAND THAT";

    private String clientName;
    private LocalDateTime charStartTime;

    public Server() {
        this.start();
    }

    public void start() {
        this.hostName = "localhost";
        this.port = 50000;
        this.timeout = 301000;


        log.info("--------------------------------------------------------------------");
        log.info("-- SERVER START on host:port: {}:{}", this.hostName, Integer.toString(this.port));
        log.info("--------------------------------------------------------------------");
        this.hostName = "localhost";

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // chat
            UUID uuid = UUID.randomUUID();
            String initMessage = chatServerInitMessage.concat(uuid.toString());
            log.debug("Server say: {}", initMessage);
            out.println(initMessage);

            String message;
            while ((message = in.readLine()) != null && clientIsConnected) {
                setTimeout(() -> {
                    System.out.println("TIMEOUT");

                    Duration chatDuration = Duration.between(charStartTime, LocalDateTime.now());
                    long chatTime =  chatDuration.getSeconds() * 1000;
                    String response = "BYE ".concat(clientName)
                            .concat(", WE SPOKE FOR ")
                            .concat(Long.toString(chatTime))
                            .concat(" MS");
                    log.debug("Server say: {}", response);
                    out.println(response);
                }, 30000);
                charStartTime = LocalDateTime.now();
                if (message.length() > 0) {
                    log.debug("Client say: {}", message);
                    if (message.indexOf(chatClientInitResponse) == 0) {
                        clientName = message.substring(chatClientInitResponse.length());
                        String response = chatServerResponse.concat(clientName);
                        log.debug("Server say: {}", response);
                        out.println(response);
                    } else if (message.indexOf(chatClientEndCommand) == 0) {
                        Duration chatDuration = Duration.between(charStartTime, LocalDateTime.now());
                        long chatTime =  chatDuration.getSeconds() * 1000;
                        String response = "BYE ".concat(clientName)
                                .concat(", WE SPOKE FOR ")
                                .concat(Long.toString(chatTime))
                                .concat(" MS");
                        log.debug("Server say: {}", response);
                        out.println(response);
                    } else {
                        log.debug("Server say: {}", chatNotSupportedCommand);
                        out.println(chatNotSupportedCommand);
                    }
                } else {
                    log.debug("Server say: {}", chatNotSupportedCommand);
                    out.println(chatNotSupportedCommand);
                }
            }

        } catch (SocketTimeoutException exception) {
            Duration chatDuration = Duration.between(charStartTime, LocalDateTime.now());
            long chatTime =  chatDuration.getSeconds() * 1000;
            String response = "BYE ".concat(clientName)
                    .concat(", WE SPOKE FOR ")
                    .concat(Long.toString(chatTime))
                    .concat(" MS");
            log.debug("Server say: {}", response);
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
                System.err.println(e);
            }
        }).start();
    }

}
