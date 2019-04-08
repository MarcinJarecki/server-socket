package com.mj.collibra.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Value
@NoArgsConstructor
public class Graph {
    private ConcurrentHashMap<GraphNode, CopyOnWriteArrayList<GraphEdge>> adjacencyNodes = new ConcurrentHashMap<>();

    public void addNode(GraphNode node) {
        adjacencyNodes.putIfAbsent(node, new CopyOnWriteArrayList<>());
    }

    public void removeNode(GraphNode node) {
        adjacencyNodes.values().forEach(edgeList ->
                edgeList.forEach(edge -> {
                    if (edge.getNode().getName().equals(node.getName())) {
                        edgeList.remove(edge);
                    }
                }));
        adjacencyNodes.remove(node);
    }

    public void addEdge(GraphNode node, GraphEdge edge) {
        adjacencyNodes.get(node).add(edge);
    }

    public void removeEdge(GraphNode source, GraphNode destination) {
        List<GraphEdge> edges = adjacencyNodes.get(source);
        if (adjacencyNodes.get(source) != null) {
            edges.forEach(edge -> {
                if (edge.getNode().equals(destination)) {
                    edges.remove(edge);
                }
            });
        }
    }

    public boolean isNodeExist(GraphNode node) {
        if (adjacencyNodes.size() > 0) {
            return adjacencyNodes.containsKey(node);
        } else {
            return false;
        }
    }
}
