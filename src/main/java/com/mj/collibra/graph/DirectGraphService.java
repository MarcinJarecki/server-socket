package com.mj.collibra.graph;

/**
 * @author Marcin Jarecki
 */
public interface DirectGraphService {

    /**
     * Add node to graph
     *
     * @param name -  graph node name
     * @return response to client
     */
    String addNode(String name);

    /**
     * Remove node from graph
     *
     * @param name - graph node name
     * @return response to client
     */
    String removeNode(String name);

    /**
     * Add edge to graph
     *
     * @param nodeXName  - graph node name
     * @param nodeYName  - graph node name
     * @param weightEdge - edge weight
     * @return response to client
     */
    String addEdge(String nodeXName, String nodeYName, String weightEdge);

    /**
     * Remove edge
     *
     * @param nodeXName - graph node name
     * @param nodeYName - graph node name
     * @return response to client
     */
    String removeEdge(String nodeXName, String nodeYName);

    /**
     * Return weight of shortest path from nodeXName to nodeYName
     *
     * @param nodeXName - graph node name
     * @param nodeYName - graph node name
     * @return weight of shortest path
     */
    String shortestPath(String nodeXName, String nodeYName);

    /**
     * Return list of node closer to nodeName that the given weight
     *
     * @param weight   - max sum of weight
     * @param nodeName - start node name
     * @return response to client
     */
    String closerThan(String weight, String nodeName);

}
