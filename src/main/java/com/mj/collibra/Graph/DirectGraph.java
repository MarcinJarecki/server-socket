package com.mj.collibra.Graph;

import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import com.mj.collibra.command.GraphServerCommand;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author Marcin Jarecki
 */
@Component
@Data
public class DirectGraph {
    private ConcurrentHashMap<GraphNode, CopyOnWriteArrayList<GraphNode>> adjacencyNodes;
    private ConcurrentHashMap<Integer, GraphEdge> edges;

    public String addNode(String name) {
        if (name != null && !isNodeExist(name)) {
            GraphNode node = new GraphNode(name);
            adjacencyNodes.putIfAbsent(node, new CopyOnWriteArrayList<>());
            return GraphServerCommand.NODE_ADDED.getCommand();
        } else {
            return GraphServerCommand.NODE_ALREADY_EXISTS.getCommand();
        }
    }

    public String removeNode(String name) {
        if(name != null && isNodeExist(name)) {
            GraphNode node = new GraphNode(name);
            adjacencyNodes.values()
                    .stream()
                    .map(value -> value.remove(node))
                    .collect(Collectors.toList());
            adjacencyNodes.remove(node);
            return GraphServerCommand.NODE_REMOVED.getCommand();
        } else {
            return GraphServerCommand.NODE_NOT_FOUND.getCommand();
        }
    }

    private boolean isNodeExist(String name) {
        GraphNode node = new GraphNode(name);
        return adjacencyNodes.containsKey(node);
    }
}

