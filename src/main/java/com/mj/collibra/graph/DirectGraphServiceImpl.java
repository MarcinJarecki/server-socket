package com.mj.collibra.graph;

import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import com.mj.collibra.command.enums.GraphServerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Marcin Jarecki
 */
@Service
@Slf4j
public class DirectGraphServiceImpl implements DirectGraphService {
    private ConcurrentHashMap<GraphNode, CopyOnWriteArrayList<GraphEdge>> adjacencyNodes = new ConcurrentHashMap<>();

    private Set<GraphNode> settledNodes;
    private Set<GraphNode> unSettledNodes;
    private Map<GraphNode, GraphNode> predecessors;
    private Map<GraphNode, Integer> distance;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Override
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

    @Override
    public String removeNode(String name) {
        readLock.lock();
        try {
            if (name != null) {
                GraphNode node = new GraphNode(name);
                if (isNodeExist(node)) {
                    adjacencyNodes.values().forEach(edgeList ->
                            edgeList.forEach(edge -> {
                                if (edge.getNode().getName().equals(name)) {
                                    edgeList.remove(edge);
                                }
                            }));
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

    @Override
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

    @Override
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

    @Override
    public String shortestPath(String nodeXName, String nodeYName) {
        writeLock.lock();
        try {
            if (nodeXName != null && nodeYName != null) {
                GraphNode nodeX = new GraphNode(nodeXName);
                GraphNode nodeY = new GraphNode(nodeYName);
                if (isNodeExist(nodeX) && isNodeExist(nodeY)) {
                    createShortestPathAndDistance(nodeX);
                    int weightShortestPath = getDistanceWeight(nodeY);
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
        int weight = 0;
        int minWeight = 0;
        try {
            weight = Integer.parseInt(weightString);
        } catch (NumberFormatException e) {
            log.warn("Problem with parse weight in closerThan = {}", weightString, e);
        }

        readLock.lock();
        try {
            if (nodeName != null && weight > minWeight) {
                GraphNode nodeX = new GraphNode(nodeName);
                if (isNodeExist(nodeX) ) {

                    return "";
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

    private void createShortestPathAndDistance(GraphNode source) {
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();

        distance.put(source, 0);
        unSettledNodes.add(source);
        while (!unSettledNodes.isEmpty()) {
            GraphNode node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private GraphNode getMinimum(Set<GraphNode> nodes) {
        GraphNode minimum = null;
        for (GraphNode node : nodes) {
            if (minimum == null) {
                minimum = node;
            } else {
                if (getShortestDistance(node) < getShortestDistance(minimum)) {
                    minimum = node;
                }
            }
        }
        return minimum;
    }

    private void findMinimalDistances(GraphNode node) {
        List<GraphNode> adjacentNodes = getNeighbors(node);
        for (GraphNode target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private int getDistance(GraphNode node, GraphNode target) {
        for (GraphEdge edge : adjacencyNodes.get(node)) {
            if (edge.getNode().equals(target)) {
                return edge.getWeight();
            }
        }
        return Integer.MAX_VALUE;
    }

    private List<GraphNode> getNeighbors(GraphNode node) {
        List<GraphNode> neighbors = new ArrayList<>();
        adjacencyNodes.get(node).forEach(edge -> {
            if (!isSettled(edge.getNode())) {
                neighbors.add(edge.getNode());
            }
        });
        return neighbors;
    }


    private boolean isSettled(GraphNode node) {
        return settledNodes.contains(node);
    }

    private int getShortestDistance(GraphNode destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    private List<GraphNode> getPath(GraphNode target) {
        LinkedList<GraphNode> path = new LinkedList<>();
        GraphNode step = target;
        if (predecessors.get(step) == null) {
            return new ArrayList<>();
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    private int getDistanceWeight(GraphNode target) {
        if (distance.get(target) == null) {
            return Integer.MAX_VALUE;
        } else {
            return distance.get(target);
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
