package com.mj.collibra.command.parser;

import com.mj.collibra.command.enums.ChatClientCommand;
import com.mj.collibra.command.enums.GraphClientCommand;
import com.mj.collibra.command.enums.TypeOfCommand;
import com.mj.collibra.model.CommandData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandParserServiceImplTest {

    private CommandParserService commandParserService;

    private String commandHiArg;
    private String commandHi;
    private String commandBye;
    private String commandUnknown;
    private String commandUnknownEmpty;
    private String commandUnknownNull;
    private String commandAddNodeArg;
    private String commandAddNode;
    private String commandRemoveNodeArg;
    private String commandRemoveNode;
    private String[] commandAddEdgeArg;
    private String commandAddEdge;
    private String[] commandRemoveEdgeArg;
    private String commandRemoveEdge;
    private String[] commandCloserThatArg;
    private String commandCloserThat;
    private String[] commandShortestPathArg;
    private String commandShortestPath;

    @Before
    public void setUp() {

        commandParserService = new CommandParserServiceImpl();

        commandHiArg = "90617332-14ed-4209-a9a6-8176806c0247";
        commandHi = "HI, I'M " + commandHiArg;

        commandBye = "BYE MATE!";

        commandUnknown = "UNKNOWN";
        commandUnknownEmpty = "";
        commandUnknownNull = null;

        commandAddNodeArg = "Phase2-Node-0";
        commandAddNode = "ADD NODE " + commandAddNodeArg;

        commandRemoveNodeArg = "Phase2-Node-44";
        commandRemoveNode = "REMOVE NODE " + commandRemoveNodeArg;

        commandAddEdgeArg = new String[]{"Phase3-Node-333", "Phase3-Node-577 14"};
        commandAddEdge = "ADD EDGE " + commandAddEdgeArg[0] + " " + commandAddEdgeArg[1];

        commandRemoveEdgeArg = new String[]{"Phase2-Node-1", "Phase2-Node-43"};
        commandRemoveEdge = "REMOVE EDGE " + commandRemoveEdgeArg[0] + " " + commandRemoveEdgeArg[1];

        commandCloserThatArg = new String[]{"64", "Phase4-Node-927"};
        commandCloserThat = "CLOSER THAN " + commandCloserThatArg[0] + " " + commandCloserThatArg[1];

        commandShortestPathArg = new String[]{"Phase3-Node-823",  "Phase3-Node-558"};
        commandShortestPath = "SHORTEST PATH " + commandShortestPathArg[0] + " " + commandShortestPathArg[1];
    }

    @Test
    public void createCommandHi() {
        CommandData result = commandParserService.createCommand(commandHi);

        assertEquals(TypeOfCommand.CHAT, result.getTypeOfCommand());
        assertEquals(ChatClientCommand.START, result.getCommand());
        assertEquals(commandHiArg, result.getArguments()[0]);
    }

    @Test
    public void createCommandByeMate() {
        CommandData result = commandParserService.createCommand(commandBye);

        assertEquals(TypeOfCommand.CHAT, result.getTypeOfCommand());
        assertEquals(ChatClientCommand.END, result.getCommand());
        assertArrayEquals(new String[0], result.getArguments());
    }

    @Test
    public void createCommandUnknown() {
        CommandData result = commandParserService.createCommand(commandUnknown);

        assertEquals(TypeOfCommand.UNDEFINDED, result.getTypeOfCommand());
        assertNull(result.getCommand());
        assertNull(result.getArguments());
    }

    @Test
    public void createCommandUnknownForEmpty() {
        CommandData result = commandParserService.createCommand(commandUnknownEmpty);

        assertEquals(TypeOfCommand.UNDEFINDED, result.getTypeOfCommand());
        assertNull(result.getCommand());
        assertNull(result.getArguments());
    }

    @Test
    public void createCommandUnknownFoNull() {
        CommandData result = commandParserService.createCommand(commandUnknownNull);

        assertEquals(TypeOfCommand.UNDEFINDED, result.getTypeOfCommand());
        assertNull(result.getCommand());
        assertNull(result.getArguments());
    }

    @Test
    public void createCommandAddNode() {
        CommandData result = commandParserService.createCommand(commandAddNode);

        assertEquals(TypeOfCommand.GRAPH, result.getTypeOfCommand());
        assertEquals(GraphClientCommand.ADD_NODE, result.getCommand());
        assertEquals(commandAddNodeArg, result.getArguments()[0]);
    }

    @Test
    public void createCommandRemoveNode() {
        CommandData result = commandParserService.createCommand(commandRemoveNode);

        assertEquals(TypeOfCommand.GRAPH, result.getTypeOfCommand());
        assertEquals(GraphClientCommand.REMOVE_NODE, result.getCommand());
        assertEquals(commandRemoveNodeArg, result.getArguments()[0]);
    }

    @Test
    public void createCommandAddEdge() {
        CommandData result = commandParserService.createCommand(commandAddEdge);

        assertEquals(TypeOfCommand.GRAPH, result.getTypeOfCommand());
        assertEquals(GraphClientCommand.ADD_EDGE, result.getCommand());
        assertEquals(commandAddEdgeArg[0], result.getArguments()[0]);
    }

    @Test
    public void createCommandRemoveEdge() {
        CommandData result = commandParserService.createCommand(commandRemoveEdge);

        assertEquals(TypeOfCommand.GRAPH, result.getTypeOfCommand());
        assertEquals(GraphClientCommand.REMOVE_EDGE, result.getCommand());
        assertArrayEquals(commandRemoveEdgeArg, result.getArguments());
    }

    @Test
    public void createCommandCloserThan() {
        CommandData result = commandParserService.createCommand(commandCloserThat);

        assertEquals(TypeOfCommand.GRAPH, result.getTypeOfCommand());
        assertEquals(GraphClientCommand.CLOSER_THAN, result.getCommand());
        assertArrayEquals(commandCloserThatArg, result.getArguments());
    }

    @Test
    public void createCommandShortestPath() {
        CommandData result = commandParserService.createCommand(commandShortestPath);

        assertEquals(TypeOfCommand.GRAPH, result.getTypeOfCommand());
        assertEquals(GraphClientCommand.SHORTEST_PATH, result.getCommand());
        assertArrayEquals(commandShortestPathArg, result.getArguments());
    }



}