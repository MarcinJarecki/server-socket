package com.mj.collibra.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Marcin Jarecki
 */
@Data
@AllArgsConstructor
public class GraphEdge {
    private GraphNode nodeX;
    private GraphNode nodeY;
    private int weight;
}
