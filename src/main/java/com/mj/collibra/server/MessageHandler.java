package com.mj.collibra.server;

import com.mj.collibra.Graph.DirectGraphService;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.CommandParserServiceImpl;
import com.mj.collibra.command.CommonServerCommand;
import com.mj.collibra.command.GraphClientCommand;
import com.mj.collibra.command.TypeOfCommand;
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

/**
 * @author Marcin Jarecki
 */
@Slf4j
public class MessageHandler implements Runnable {

    private final ChatService chatService;
    private final Socket clientSocket;
    private final CommandParserServiceImpl commandParserService;
    private final DirectGraphService directGraphService;

    private String clientName;
    private LocalDateTime chatStartTime;

    private String serverSayLog = "Server say: {}";

    public MessageHandler(Socket clientSocket, ChatService chatService, CommandParserServiceImpl commandParserService, DirectGraphService directGraphService) {
        this.clientSocket = clientSocket;
        this.chatService = chatService;
        this.commandParserService = commandParserService;
        this.directGraphService = directGraphService;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            UUID uuid = UUID.randomUUID();
            String initMessage = chatService.startSessionResponse(uuid);
            log.debug(serverSayLog, initMessage);
            out.println(initMessage);

            boolean setTimeoutEnable = true;
            String message;
            while ((message = in.readLine()) != null) {
                chatStartTime = LocalDateTime.now();
                if (setTimeoutEnable) {
                    setTimeoutEnable = false;
                    setTimeout(() -> {
                        String response = chatService.endSessionResponse(clientName, chatStartTime);
                        log.debug("Timeout - " + serverSayLog, response);
                        out.println(response);
                    }, 30000 + 100);
                }
                log.debug("Client say: {}", message);

                if (message.length() > 0) {
                    TypeOfCommand typeOfCommand = commandParserService.getCommandType(message);
                    switch (typeOfCommand) {
                        case CHAT:
                            out.println(handleWithChatMessage(message));
                            break;
                        case GRAPH:
                            out.println(handleWithGraphMessage(message));
                            break;
                        case UNDEFINDED:
                            out.println(getUndefinedCommandResponse());
                            break;
                        default:
                            out.println(getUndefinedCommandResponse());
                            break;
                    }
                } else {
                    out.println(getUndefinedCommandResponse());
                }
            }

        } catch (SocketTimeoutException exception) {
            log.debug(serverSayLog, "Connection Timeout Exception");
            stop();
        } catch (IOException e) {
            stop();
            log.error("IO Exception in MessageHandler:", e);
        } catch (Exception e) {
            stop();
            log.error("Exceprion in Thread Run in MessageHandler. Exception:", e);
        }

    }

    private String handleWithChatMessage(String message) {
        ChatClientMessage chatClientMessage = ChatClientMessage.builder()
                .clientName(clientName)
                .charStartTime(chatStartTime)
                .clientMessage(message)
                .build();
        ChatServerResponse response = chatService.createResponseToClient(chatClientMessage);
        if (response.getClientName() != null) {
            clientName = response.getClientName();
            log.debug("clientName: {}", clientName);
        }
        log.debug(serverSayLog, response.getServerResponse());

        return response.getServerResponse();
    }

    private String handleWithGraphMessage(String message) {
        GraphClientCommand graphClientCommand = commandParserService.getGraphCommand(message);
        String response = getUndefinedCommandResponse();

        switch (graphClientCommand) {
            case ADD_NODE:
                response = directGraphService.addNode(message);
                break;
            case REMOVE_NODE:
                response = directGraphService.removeNode(message);
                break;
            case ADD_EDGE:
                response = directGraphService.addEdge(message, "");
                break;
            case REMOVE_EDGE:
                response = directGraphService.removeEdge(message, "");
                break;
            case CLOSER_THAN:
                break;
            case SHORTES_PATH:
                break;
            case UNDEFINED:
                break;
            default:
                break;
        }
        log.debug(serverSayLog, response);
        return response;
    }

    private String getUndefinedCommandResponse(){
        return CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommand();
    }

    private void stop() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (Exception e) {
            log.error("Problem with close socket server", e);
        }
    }

    // TODO to sheduleExecutor
    private static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                log.error("Sleep thread error: ", e);
            }
        }).start();
    }
}
