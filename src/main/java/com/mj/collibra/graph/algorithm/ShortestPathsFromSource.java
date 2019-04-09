package com.mj.collibra.graph.algorithm;

import com.mj.collibra.model.Graph;
import com.mj.collibra.model.GraphEdge;
import com.mj.collibra.model.GraphNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Marcin Jarecki
 */
@Component
@Slf4j
public class ShortestPathsFromSource {

    public Map<GraphNode, Integer> calculate(Graph graph, GraphNode source) {

        Map<GraphNode, Integer> distance = new HashMap<>(graph.getAdjacencyNodes().size());

        graph.getAdjacencyNodes().forEach((node, list) -> distance.put(node, Integer.MAX_VALUE));

        distance.put(source, 0);

        Set<GraphNode> settledNodes = new HashSet<>();
        Set<GraphNode> unsettledNodes = new HashSet<>();
        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            GraphNode currentNode = getLowestDistanceNode(unsettledNodes, distance);
            unsettledNodes.remove(currentNode);
            for (GraphEdge graphEdge : graph.getAdjacencyNodes().get(currentNode)) {
                GraphNode adjacentNode = graphEdge.getNode();
                Integer edgeWeigh = graphEdge.getWeight();

                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeigh, currentNode, distance);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return distance;
    }

    private void calculateMinimumDistance(GraphNode evaluationNode, Integer edgeWeigh, GraphNode sourceNode, Map<GraphNode, Integer> distance) {
        Integer sourceDistance = distance.get(sourceNode);
        if (sourceDistance + edgeWeigh < distance.get(evaluationNode)) {
            distance.put(evaluationNode, sourceDistance + edgeWeigh);
        }
    }

    private GraphNode getLowestDistanceNode(Set<GraphNode> unsettledNodes, Map<GraphNode, Integer> distance) {
        GraphNode lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (GraphNode node : unsettledNodes) {
            int nodeDistance = distance.get(node);
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
}
