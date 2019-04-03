package com.mj.collibra.server;

import com.mj.collibra.chat.ChatService;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public class MessageHandler implements Runnable{

    private final ChatService chatService;

    private final Socket clientSocket;
    private String clientName;
    private LocalDateTime chatStartTime;

    private String SERVER_SAY_LOG = "Server say: {}";

    public MessageHandler(Socket clientSocket, ChatService chatService) {
        this.clientSocket = clientSocket;
        this.chatService = chatService;
    }

    @Override
    public void run() {
        try(
                PrintWriter   out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                ) {

            UUID uuid = UUID.randomUUID();
            String initMessage = chatService.startSessionResponse(uuid);
            log.debug(SERVER_SAY_LOG, initMessage);
            out.println(initMessage);

            boolean setTimeoutEnable = true;
            String message;
            while ((message = in.readLine()) != null) {
                chatStartTime = LocalDateTime.now();
                if(setTimeoutEnable){
                    setTimeoutEnable = false;
                    setTimeout(() -> {
                        String response = chatService.endSessionResponse(clientName, chatStartTime);
                        log.debug("Timeout - " + SERVER_SAY_LOG, response);
                        out.println(response);
                    }, 30000+100);
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
            log.debug(SERVER_SAY_LOG, "Connection Timeout Exception");
            stop();
        } catch (IOException e) {
            stop();
            log.error("IO Exception in MessageHandler:", e);
        } catch (Exception e) {
            stop();
            log.error("Exceprion in Thread Run in MessageHandler. Exception:", e);
        }

    }

    private void stop() {
        try {
            if (clientSocket != null) {clientSocket.close();}
        } catch (Exception e) {
            log.error("Problem with close socket server", e);
        }
    }

    private static void setTimeout(Runnable runnable, int delay){
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
