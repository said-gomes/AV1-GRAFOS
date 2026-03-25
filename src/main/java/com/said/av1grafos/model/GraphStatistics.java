package com.said.av1grafos.model;

import java.util.List;
import java.util.Map;

public record GraphStatistics(
        int vertexCount,
        int edgeCount,
        double density,
        Map<String, Integer> outDegrees,
        Map<String, Integer> inDegrees,
        List<String> degreeLines,
        List<Map.Entry<String, Integer>> topHubs
) {
}
