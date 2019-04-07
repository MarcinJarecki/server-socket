package com.mj.collibra.server;

import com.mj.collibra.graph.DirectGraphService;
import com.mj.collibra.chat.ChatService;
import com.mj.collibra.command.parser.CommandParserServiceImpl;
import com.mj.collibra.command.enums.CommonServerCommand;
import com.mj.collibra.command.enums.GraphClientCommand;
import com.mj.collibra.model.ChatClientMessage;
import com.mj.collibra.model.ChatServerResponse;
import com.mj.collibra.model.CommandData;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.*;

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
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture = null;

    private String serverSayLog = "Server say: {}";

    MessageHandler(Socket clientSocket, ChatService chatService, CommandParserServiceImpl commandParserService, DirectGraphService directGraphService) {
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


            String message;
            while ((message = in.readLine()) != null) {
                chatStartTime = LocalDateTime.now();

                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }

                Runnable timeoutTask = () -> {
                    String response = chatService.endSessionResponse(clientName, chatStartTime);
                    log.debug("Timeout - " + serverSayLog, response);
                    out.println(response);
                };
                scheduledFuture = scheduledExecutorService.schedule(timeoutTask, 30, TimeUnit.SECONDS);

                log.debug("Client say: {}", message);

                if (message.length() > 0) {
                    CommandData commandData = commandParserService.createCommand(message);
                    switch (commandData.getTypeOfCommand()) {
                        case CHAT:
                            out.println(handleWithChatMessage(message));
                            break;
                        case GRAPH:
                            out.println(handleWithGraphCommand(commandData));
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
            log.error("Exception in Thread Run in MessageHandler. Exception:", e);
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

    private String handleWithGraphCommand(CommandData commandData) {
        String response;
        String nodeName;
        String nodeX;
        String nodeY;
        String edgeWeight;

        GraphClientCommand graphClientCommand = (GraphClientCommand) commandData.getCommand();
        String[] arguments = commandData.getArguments();

        switch (graphClientCommand) {
            case ADD_NODE:
                nodeName = arguments[0];
                response = directGraphService.addNode(nodeName);
                break;
            case REMOVE_NODE:
                nodeName = arguments[0];
                response = directGraphService.removeNode(nodeName);
                break;
            case ADD_EDGE:
                nodeX = arguments[0];
                nodeY = arguments[1];
                edgeWeight = arguments[2];
                response = directGraphService.addEdge(nodeX, nodeY, edgeWeight);
                break;
            case REMOVE_EDGE:
                nodeX = arguments[0];
                nodeY = arguments[1];
                response = directGraphService.removeEdge(nodeX, nodeY);
                break;
            case SHORTES_PATH:
                nodeX = arguments[0];
                nodeY = arguments[1];
                response = directGraphService.shortestPath(nodeX, nodeY);
                break;
            case CLOSER_THAN:
                response = "[Phase4-Node-0]";
                break;

            default:
                response = getUndefinedCommandResponse();;
                break;
        }
        log.debug("MAIN - " + serverSayLog, response);
        return response;
    }


    private String getUndefinedCommandResponse() {
        String response = CommonServerCommand.NOT_SUPPORTED_COMMAND.getCommandName();
        log.debug(serverSayLog, "UNDEFINED - " + response);
        return response;
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
