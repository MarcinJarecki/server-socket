package com.mj.collibra.graph;

import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import com.mj.collibra.command.enums.GraphServerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Marcin Jarecki
 */
@Service
@Slf4j
public class DirectGraphService {
    private ConcurrentHashMap<GraphNode, CopyOnWriteArrayList<GraphEdge>> adjacencyNodes = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<Integer, GraphEdge> edges = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public String addNode(String name) {
        writeLock.lock();
        try {
            if (name != null) {
                GraphNode node = new GraphNode(name);
                if (!isNodeExist(node)) {
                    adjacencyNodes.putIfAbsent(node, new CopyOnWriteArrayList<>());
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

    public String removeNode(String name) {
        readLock.lock();
        try {
            if (name != null) {
                GraphNode node = new GraphNode(name);
                if (isNodeExist(node)) {
                    adjacencyNodes.values().forEach(edgeList -> {
                        edgeList.forEach(edge -> {
                            if (edge.getNode().getName().equals(name)) {
                                edgeList.remove(edge);
                            }
                        });
                    });
                    adjacencyNodes.remove(node);
                    return GraphServerCommand.NODE_REMOVED.getCommandName();
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            readLock.unlock();
        }
    }

    public String addEdge(String nodeXName, String nodeYName, String weightEdge) {
        writeLock.lock();
        try {
            int weight = 0;
            try {
                weight = Integer.parseInt(weightEdge);
            } catch (NumberFormatException e) {
                log.warn("Problem with parse weight node = {}", weightEdge, e);
            }

            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (isNodeExist(nodeX) && isNodeExist(nodeY)) {
                    GraphEdge edge = new GraphEdge(nodeY, weight);
                    adjacencyNodes.get(nodeX).add(edge);
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

    public String removeEdge(String nodeXName, String nodeYName) {
        readLock.lock();
        try {
            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (isNodeExist(nodeX) && isNodeExist(nodeY)) {
                    List<GraphEdge> edgesNodeX = adjacencyNodes.get(nodeX);
                    if (edgesNodeX != null) {
                        edgesNodeX.forEach(edge -> {
                            if (edge.getNode().getName().equals(nodeYName)) {
                                edgesNodeX.remove(edge);
                            }
                        });
                    }
                    return GraphServerCommand.EDGE_REMOVED.getCommandName();
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            readLock.unlock();
        }
    }

    public String shortestPath(String nodeXName, String nodeYName) {
        readLock.lock();
        try {
            int result = Integer.MAX_VALUE;
            List<List<GraphEdge>> graphPaths = new ArrayList<>();
            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (isNodeExist(nodeX) && isNodeExist(nodeY)) {
                    List<GraphEdge> path = new LinkedList<>();
                    graphPaths = getAllPaths(nodeX, nodeY, path, graphPaths);

                    return Integer.toString(graphPaths.size());
                } else {
                    return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
                }
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            readLock.unlock();
        }
    }

    private List<List<GraphEdge>> getAllPaths(GraphNode startNode, GraphNode endNode, List<GraphEdge> path, List<List<GraphEdge>> graphPaths) {
        List<GraphEdge> edges = adjacencyNodes.get(startNode);

        if(path.isEmpty()) {
            path.add(new GraphEdge(startNode, 0));
        }

        if (edges == null) {
            graphPaths.add(path);
            return graphPaths;
        }

        if (startNode == endNode) {
            return  graphPaths;
        }

            edges.forEach(edge -> {
                if (!path.contains(edge)) {
                    path.add(edge);
                    log.warn("ADD to path = {}, path = {}", edge.getNode().getName(), path.toString());
                    getAllPaths(edge.getNode(), endNode, path, graphPaths);
                    path.remove(edge);
                }

            });
            return graphPaths;

    }

    private boolean isNodeExist(GraphNode node) {
        if (adjacencyNodes.size() > 0) {
            return adjacencyNodes.containsKey(node);
        } else {
            return false;
        }
    }

    private GraphEdge getGraphEdge(List<GraphEdge> adjacencyNodes, GraphNode node) {
        Optional<GraphEdge> result = adjacencyNodes.stream()
                .filter(edge -> edge.getNode().equals(node))
                .findFirst();
        return result.orElse(null);

    }


}
