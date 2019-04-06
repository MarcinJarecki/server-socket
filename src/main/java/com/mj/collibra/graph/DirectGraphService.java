package com.mj.collibra.graph;

import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import com.mj.collibra.command.enums.GraphServerCommand;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author Marcin Jarecki
 */
@Service
@Data
public class DirectGraphService {
    private ConcurrentHashMap<GraphNode, CopyOnWriteArrayList<GraphNode>> adjacencyNodes = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, GraphEdge> edges = new ConcurrentHashMap<>();

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
                    adjacencyNodes.values()
                            .stream()
                            .map(value -> value.remove(node))
                            .collect(Collectors.toList());
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
            int weight = Integer.parseInt(weightEdge);

            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (isNodeExist(nodeX) && isNodeExist(nodeY)) {
                    GraphEdge edge = new GraphEdge(nodeY, weight);
                    adjacencyNodes.get(nodeX).add(nodeY);
                    adjacencyNodes.get(nodeY).add(nodeX);
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
                    List<GraphNode> edgesNodeX = adjacencyNodes.get(nodeX);
                    List<GraphNode> edgesNodeY = adjacencyNodes.get(nodeY);
                    if (edgesNodeX != null) {
                        edgesNodeX.remove(nodeY);
                    }
                    if (edgesNodeY != null) {
                        edgesNodeY.remove(nodeX);
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

    private boolean isNodeExist(GraphNode node) {
        if (adjacencyNodes.size() > 0) {
            return adjacencyNodes.containsKey(node);
        } else {
            return false;
        }
    }


}
