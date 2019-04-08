package com.mj.collibra.server;

import com.mj.collibra.command.CommandResponseService;
import com.mj.collibra.chat.ChatService;
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

    private long chatStartTime;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture = null;

    private static final String SERVER_SAY_LOG = "Server say: {}";

    MessageHandler(Socket clientSocket, CommandResponseService commandResponseService,  ChatService chatService) {
        this.clientSocket = clientSocket;
        this.commandResponseService = commandResponseService;
        this.chatService = chatService;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            UUID uuid = UUID.randomUUID();
            String initMessage = chatService.startSessionResponse(uuid);
            log.debug(SERVER_SAY_LOG, initMessage);
            out.println(initMessage);

            String message;
            boolean startChat = false;
            while ((message = in.readLine()) != null) {
                if (!startChat) {
                    chatStartTime = Instant.now().toEpochMilli();
                    startChat = true;
                }

                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }

                Runnable timeoutTask = () -> {
                    String response = chatService.endSessionResponse(commandResponseService.getClientName(), chatStartTime);
                    log.debug("Timeout with " + commandResponseService.getClientName() + SERVER_SAY_LOG, response);
                    out.println(response);
                };
                scheduledFuture = scheduledExecutorService.schedule(timeoutTask, 30, TimeUnit.SECONDS);

                log.debug("{} - Client say: {}", commandResponseService.getClientName(), message);
                String response = commandResponseService.createResponseToClient(message, chatStartTime);
                log.debug(commandResponseService.getClientName() + ": " + SERVER_SAY_LOG, response);
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

}
