package com.mj.collibra.graph;

import com.mj.collibra.command.enums.GraphServerCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class DirectGraphServiceTest {

    private DirectGraphService directGraphService;

    private String[] nodeNames = {"1", "2", "3", "4", "5"};
    // source, destination, weight
    private String[][] edges = {
            {"0", "1", "4"},
            {"0", "2", "3"},
            {"1", "3", "2"},
            {"1", "2", "5"},
            {"2", "3", "7"},
            {"3", "4", "2"},
            {"4", "0", "4"},
            {"4", "1", "4"},
            {"4", "5", "6"}
    };

    @Before
    public void setUp() {
        directGraphService = new DirectGraphService();
        Arrays.stream(nodeNames).forEach(nodeName -> directGraphService.addNode(nodeName));
        Arrays.stream(edges).forEach(edge -> directGraphService.addEdge(edge[0], edge[1], edge[2]));
    }


    @Test
    public void shouldGraphBeDefined() {
        assertNotNull(directGraphService);
    }

    @Test
    public void shouldAddNode() {
        String newNodeName = "999";
        String expectedResponse = GraphServerCommand.NODE_ADDED.getCommandName();

        String response = directGraphService.addNode(newNodeName);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotAddExistedNode() {
        String existedNode = nodeNames[1];
        String expectedResponse = GraphServerCommand.NODE_ALREADY_EXISTS.getCommandName();

        String response = directGraphService.addNode(existedNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldRemoveExistedNode() {
        String existedNode = nodeNames[1];
        String expectedResponse = GraphServerCommand.NODE_REMOVED.getCommandName();

        String response = directGraphService.removeNode(existedNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotRemoveWhenNodeNotExist() {
        String newNodeName = "999";
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphService.removeNode(newNodeName);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldAddEdge() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode, "15"};
        String expectedResponse = GraphServerCommand.EDGE_ADDED.getCommandName();

        String response = directGraphService.addEdge(edge[0], edge[1], edge[2]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotAddEdgeWhenSourceNodeNotExist() {
        String sourceNode = "0000";
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode, "15"};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphService.addEdge(edge[0], edge[1], edge[2]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotAddEdgeWhenDestinationNodeNotExist() {
        String sourceNode = nodeNames[1];
        String destinationNode = "999";
        String[] edge = {sourceNode, destinationNode, "15"};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphService.addEdge(edge[0], edge[1], edge[2]);

        assertEquals(expectedResponse, response);
    }

    ///

    @Test
    public void shouldRemoveEdge() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode};
        String expectedResponse = GraphServerCommand.EDGE_REMOVED.getCommandName();

        String response = directGraphService.removeEdge(edge[0], edge[1]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotRemoveEdgeWhenSourceNotExist() {
        String sourceNode = "999";
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphService.removeEdge(edge[0], edge[1]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotRemoveEdgeWhenDestinationNotExist() {
        String sourceNode = nodeNames[1];
        String destinationNode = "99";
        String[] edge = {sourceNode, destinationNode};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphService.removeEdge(edge[0], edge[1]);

        assertEquals(expectedResponse, response);
    }

///
    @Test
    public void shortestPath() {
    }
}