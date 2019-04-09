package com.mj.collibra.server;

import com.mj.collibra.command.CommandResponseService;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.common.SessionService;
import com.mj.collibra.model.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author Marcin Jarecki
 */
@Slf4j
public class MessageHandler implements Runnable {

    private final Socket clientSocket;
    private final CommandResponseService commandResponseService;
    private final ChatService chatService;
    private final SessionService sessionService;
    private final int connectionTimeout;

    private long chatStartTime;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private UUID uuid;

    private static final String SERVER_SAY_LOG = "Server say: {}";

    MessageHandler(Socket clientSocket, CommandResponseService commandResponseService, ChatService chatService,
                   SessionService sessionService, int connectionTimeout) {
        this.clientSocket = clientSocket;
        this.commandResponseService = commandResponseService;
        this.chatService = chatService;
        this.sessionService = sessionService;
        this.connectionTimeout = connectionTimeout;
        initSession();
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String initMessage = chatService.startSessionResponse(uuid);
            log.debug(SERVER_SAY_LOG, initMessage);
            out.println(initMessage);


            Runnable timeoutTask = () -> {
                String response = chatService.endSessionResponse(sessionService.getClientName(uuid), chatStartTime);
                log.debug("Timeout with " + sessionService.getClientName(uuid) + SERVER_SAY_LOG, response);
                out.println(response);
            };
            scheduledExecutorService.schedule(timeoutTask, connectionTimeout, TimeUnit.SECONDS);

            String message;
            while ((message = in.readLine()) != null) {
                log.debug("{} - Client say: {}", sessionService.getClientName(uuid), message);
                String response = commandResponseService.createResponseToClient(uuid, message, chatStartTime);
                log.debug(sessionService.getClientName(uuid) + ": " + SERVER_SAY_LOG, response);
                out.println(response);
            }
        } catch (SocketTimeoutException exception) {
            log.debug(SERVER_SAY_LOG, "Connection Timeout Exception");
            stop();
        } catch (IOException e) {
            stop();
            log.error("IO Exception in MessageHandler:", e);
        } catch (Exception e) {
            stop();
            log.error("Exception in Thread Run in MessageHandler. Exception:", e);
        } finally {
            stop();
        }

    }

    private void stop() {
        sessionService.removeSession(uuid);
        try {
            scheduledExecutorService.shutdown();
            scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Timeout thread is interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            scheduledExecutorService.shutdownNow();
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (Exception e) {
            log.error("Problem with close socket server", e);
        }
    }

    private void initSession() {
        chatStartTime = Instant.now().toEpochMilli();
        uuid = sessionService.generateUuid();
        Session session = new Session();
        session.setUuid(uuid);
        session.setSessionStartTime(chatStartTime);
        if (!sessionService.setSession(uuid, session)) {
            log.warn("Duplicated client session uuid: {}", uuid);
        }
    }

}
