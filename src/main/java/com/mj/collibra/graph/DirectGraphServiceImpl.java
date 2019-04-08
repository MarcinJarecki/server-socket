package com.mj.collibra.graph;

import com.mj.collibra.graph.algorithm.ShortestPathsFromSource;
import com.mj.collibra.model.Graph;
import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import com.mj.collibra.command.enums.GraphServerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Marcin Jarecki
 */
@Service
@Slf4j
public class DirectGraphServiceImpl implements DirectGraphService {

    private final Graph graph = new Graph();

    private ShortestPathsFromSource shortestPathsFromSource;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Autowired
    DirectGraphServiceImpl(ShortestPathsFromSource shortestPathsFromSource) {
        this.shortestPathsFromSource = shortestPathsFromSource;
    }

    @Override
    public String addNode(String name) {
        writeLock.lock();
        try {
            if (name != null) {
                GraphNode node = new GraphNode(name);
                if (!graph.isNodeExist(node)) {
                    graph.addNode(node);
                    return GraphServerCommand.NODE_ADDED.getCommandName();
                } else {
                    return GraphServerCommand.NODE_ALREADY_EXISTS.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_ALREADY_EXISTS.getCommandName();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String removeNode(String name) {
        writeLock.lock();
        try {
            if (name != null) {
                GraphNode node = new GraphNode(name);
                if (graph.isNodeExist(node)) {
                    graph.removeNode(node);
                    return GraphServerCommand.NODE_REMOVED.getCommandName();
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String addEdge(String nodeXName, String nodeYName, String weightEdge) {
        writeLock.lock();
        try {
            int weight = parseStringToInt(weightEdge);
            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (graph.isNodeExist(nodeX) && graph.isNodeExist(nodeY)) {
                    GraphEdge edge = new GraphEdge(nodeY, weight);
                    graph.addEdge(nodeX, edge);
                    return GraphServerCommand.EDGE_ADDED.getCommandName();
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public String removeEdge(String nodeXName, String nodeYName) {
        writeLock.lock();
        try {
            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (graph.isNodeExist(nodeX) && graph.isNodeExist(nodeY)) {
                    graph.removeEdge(nodeX, nodeY);
                    return GraphServerCommand.EDGE_REMOVED.getCommandName();
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String shortestPath(String nodeXName, String nodeYName) {
        writeLock.lock();
        try {
            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (graph.isNodeExist(nodeX) && graph.isNodeExist(nodeY)) {
                    Map<GraphNode, Integer> dist = shortestPathsFromSource.calculate(graph, nodeX);
                    int weightShortestPath = dist.get(nodeY);
                    return Integer.toString(weightShortestPath);
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String closerThan(String weightString, String nodeName) {
        writeLock.lock();
        try {
            int minWeight = 0;
            final int weight = parseStringToInt(weightString);
            if (nodeName != null && weight > minWeight) {
                GraphNode nodeX = new GraphNode(nodeName);
                if (graph.isNodeExist(nodeX)) {
                    Map<GraphNode, Integer> dist = shortestPathsFromSource.calculate(graph, nodeX);
                    List<String> resultNodesName = new ArrayList<>();
                    dist.forEach((node, value) -> {
                        if (value < weight && !node.getName().equals(nodeName)) {
                            resultNodesName.add(node.getName());
                        }
                    });
                    Collections.sort(resultNodesName);
                    return String.join(",", resultNodesName);
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return "";
            }
        } finally {
            writeLock.unlock();
        }
    }


    private int parseStringToInt(String valueString) {
        int value = 0;
        try {
            value = Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            log.warn("Problem with parse string to in = {}", valueString, e);
        }
        return value;
    }


}
