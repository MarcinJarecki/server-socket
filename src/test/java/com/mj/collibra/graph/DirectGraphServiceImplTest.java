package com.mj.collibra.graph;

import com.mj.collibra.command.enums.GraphServerCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class DirectGraphServiceImplTest {

    private DirectGraphServiceImpl directGraphServiceImpl;

    private String[] nodeNames = {"0", "1", "2", "3", "4", "5"};
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
        directGraphServiceImpl = new DirectGraphServiceImpl();
        Arrays.stream(nodeNames).forEach(nodeName -> directGraphServiceImpl.addNode(nodeName));
        Arrays.stream(edges).forEach(edge -> directGraphServiceImpl.addEdge(edge[0], edge[1], edge[2]));
    }


    @Test
    public void shouldGraphBeDefined() {
        assertNotNull(directGraphServiceImpl);
    }

    @Test
    public void shouldAddNode() {
        String newNodeName = "999";
        String expectedResponse = GraphServerCommand.NODE_ADDED.getCommandName();

        String response = directGraphServiceImpl.addNode(newNodeName);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotAddExistedNode() {
        String existedNode = nodeNames[1];
        String expectedResponse = GraphServerCommand.NODE_ALREADY_EXISTS.getCommandName();

        String response = directGraphServiceImpl.addNode(existedNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldRemoveExistedNode() {
        String existedNode = nodeNames[1];
        String expectedResponse = GraphServerCommand.NODE_REMOVED.getCommandName();

        String response = directGraphServiceImpl.removeNode(existedNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotRemoveWhenNodeNotExist() {
        String newNodeName = "999";
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.removeNode(newNodeName);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldAddEdge() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode, "15"};
        String expectedResponse = GraphServerCommand.EDGE_ADDED.getCommandName();

        String response = directGraphServiceImpl.addEdge(edge[0], edge[1], edge[2]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotAddEdgeWhenSourceNodeNotExist() {
        String sourceNode = "0000";
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode, "15"};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.addEdge(edge[0], edge[1], edge[2]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotAddEdgeWhenDestinationNodeNotExist() {
        String sourceNode = nodeNames[1];
        String destinationNode = "999";
        String[] edge = {sourceNode, destinationNode, "15"};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.addEdge(edge[0], edge[1], edge[2]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldRemoveEdge() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode};
        String expectedResponse = GraphServerCommand.EDGE_REMOVED.getCommandName();

        String response = directGraphServiceImpl.removeEdge(edge[0], edge[1]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotRemoveEdgeWhenSourceNotExist() {
        String sourceNode = "999";
        String destinationNode = nodeNames[2];
        String[] edge = {sourceNode, destinationNode};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.removeEdge(edge[0], edge[1]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotRemoveEdgeWhenDestinationNotExist() {
        String sourceNode = nodeNames[1];
        String destinationNode = "999";
        String[] edge = {sourceNode, destinationNode};
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.removeEdge(edge[0], edge[1]);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldFindShortestPathCase1() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[5];
        String shortestPathWeight = "10";

        String result = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(shortestPathWeight, result);
    }

    @Test
    public void shouldFindShortestPathCase2() {
        String sourceNode = nodeNames[5];
        String destinationNode = nodeNames[0];
        String shortestPathWeight = Integer.toString(Integer.MAX_VALUE);

        String result = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(shortestPathWeight, result);
    }

    @Test
    public void shouldFindShortestPathCase3() {
        String sourceNode = nodeNames[0];
        String destinationNode = nodeNames[5];
        String shortestPathWeight = "14";

        String result = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(shortestPathWeight, result);
    }

    @Test
    public void shouldFindShortestPathCase4() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[3];
        String shortestPathWeight = "2";

        String result = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(shortestPathWeight, result);
    }

    @Test
    public void shouldFindShortestPathCase5() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[1];
        String shortestPathWeight = "0";

        String result = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(shortestPathWeight, result);
    }

    @Test
    public void shouldFindShortestPathCase6() {
        String sourceNode = nodeNames[1];
        String destinationNode = nodeNames[0];
        String shortestPathWeight = "8";

        String result = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(shortestPathWeight, result);
    }

    @Test
    public void shouldNotReturnShortestPathWhenSourceNodeNotExist() {
        String sourceNode = "999";
        String destinationNode = nodeNames[2];
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.shortestPath(sourceNode, destinationNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotReturnShortestPathWhenDestinationNodeNotExist() {
        String sourceNode = nodeNames[1];
        String destinationNode = "999";
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.removeEdge(sourceNode, destinationNode);

        assertEquals(expectedResponse, response);
    }

    /// TODO
    @Test
    public void shouldReturnCloserThanCase1() {
        String sourceNode = nodeNames[0];
        String weight = "5";
        HashSet<String> expectedResponse = new HashSet<>(2);
        expectedResponse.add(nodeNames[1]);
        expectedResponse.add(nodeNames[2]);

        String response = directGraphServiceImpl.closerThan(weight, sourceNode);

        assertEquals(expectedResponse.toString(), response);
    }

    @Test
    public void shouldReturnCloserThanCase2() {
        String sourceNode = nodeNames[4];
        String weight = "10";
        HashSet<String> expectedResponse = new HashSet<>(2);
        expectedResponse.add(nodeNames[0]);
        expectedResponse.add(nodeNames[1]);
        expectedResponse.add(nodeNames[2]);
        expectedResponse.add(nodeNames[3]);
        expectedResponse.add(nodeNames[5]);

        String response = directGraphServiceImpl.closerThan(weight, sourceNode);

        assertEquals(expectedResponse.toString(), response);
    }

    @Test
    public void shouldReturnCloserThanCase3() {
        String sourceNode = nodeNames[5];
        String weight = "1";
        String expectedResponse = "[]";

        String response = directGraphServiceImpl.closerThan(weight, sourceNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotReturnCloserThanWhenSourceNodeNotExist() {
        String sourceNode = "999";
        String weight = "10";
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.closerThan(weight, sourceNode);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldNotReturnCloserThanWhenWeightlessThanZero() {
        String sourceNode = nodeNames[1];
        String weight = "-1";
        String expectedResponse = GraphServerCommand.NODE_NOT_FOUND.getCommandName();

        String response = directGraphServiceImpl.closerThan(weight, sourceNode);

        assertEquals(expectedResponse, response);
    }
}