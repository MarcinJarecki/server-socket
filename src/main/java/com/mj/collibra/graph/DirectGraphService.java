package com.mj.collibra.graph;

import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import com.mj.collibra.command.enums.GraphServerCommand;
import lombok.Data;
import org.springframework.stereotype.Service;

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
            if (name != null && !isNodeExist(name)) {
                GraphNode node = new GraphNode(name);
                adjacencyNodes.putIfAbsent(node, new CopyOnWriteArrayList<>());
                return GraphServerCommand.NODE_ADDED.getCommandName();
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
            if (name != null && isNodeExist(name)) {
                GraphNode node = new GraphNode(name);
                adjacencyNodes.values()
                        .stream()
                        .map(value -> value.remove(node))
                        .collect(Collectors.toList());
                adjacencyNodes.remove(node);
                return GraphServerCommand.NODE_REMOVED.getCommandName();
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            readLock.unlock();
        }
    }

    public String addEdge(String x, String y, String weight) {
        writeLock.lock();
        try {
            if (x != null && y != null) {
                return GraphServerCommand.EDGE_ADDED.getCommandName();
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            writeLock.unlock();
        }

    }

    public String removeEdge(String x, String y) {
        readLock.lock();
        try {
            if (x != null && y != null) {
                return GraphServerCommand.EDGE_REMOVED.getCommandName();
            } else {
                return GraphServerCommand.NODE_NOT_FOUND.getCommandName();
            }
        } finally {
            readLock.unlock();
        }
    }

    private boolean isNodeExist(String name) {
        GraphNode node = new GraphNode(name);
        if (adjacencyNodes.size() > 0) {
            return adjacencyNodes.containsKey(node);
        } else {
            return false;
        }
    }


}
