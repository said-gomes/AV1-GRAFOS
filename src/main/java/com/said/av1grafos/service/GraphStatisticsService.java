package com.said.av1grafos.service;

import com.said.av1grafos.model.BfsMetrics;
import com.said.av1grafos.model.GraphStatistics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class GraphStatisticsService {

    public GraphStatistics summarize(Map<String, List<String>> graph, boolean directed) {
        Map<String, Integer> outDegrees = new LinkedHashMap<>();
        Map<String, Integer> inDegrees = new LinkedHashMap<>();
        int totalDegree = 0;

        graph.keySet().forEach(node -> {
            outDegrees.put(node, 0);
            inDegrees.put(node, 0);
        });

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String node = entry.getKey();
            List<String> neighbors = entry.getValue();
            outDegrees.put(node, neighbors.size());
            totalDegree += neighbors.size();

            for (String neighbor : neighbors) {
                inDegrees.merge(neighbor, 1, Integer::sum);
            }
        }

        int edgeCount = directed ? totalDegree : totalDegree / 2;
        int vertexCount = graph.size();
        double density = vertexCount > 1
                ? (double) (directed ? edgeCount : 2 * edgeCount) / (vertexCount * (vertexCount - 1))
                : 0.0;

        List<String> degreeLines = new ArrayList<>();
        for (String node : graph.keySet()) {
            if (directed) {
                degreeLines.add("v(" + node + ") In: " + inDegrees.getOrDefault(node, 0)
                        + " Out: " + outDegrees.getOrDefault(node, 0));
            } else {
                degreeLines.add("v(" + node + "): " + outDegrees.getOrDefault(node, 0));
            }
        }

        List<Map.Entry<String, Integer>> topHubs = outDegrees.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(3)
                .toList();

        return new GraphStatistics(
                vertexCount,
                edgeCount,
                density,
                Collections.unmodifiableMap(outDegrees),
                Collections.unmodifiableMap(inDegrees),
                List.copyOf(degreeLines),
                topHubs
        );
    }

    public BfsMetrics bfsMetrics(Map<String, List<String>> graph, String source, int maxNeighborDepth) {
        if (source == null || !graph.containsKey(source)) {
            return null;
        }

        Map<String, Integer> levels = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();

        levels.put(source, 0);
        queue.add(source);

        int maxLevel = 0;
        List<String> nodesAtMaxLevel = new ArrayList<>();
        List<String> neighborsUpToLevelK = new ArrayList<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int level = levels.get(current);

            if (level > maxLevel) {
                maxLevel = level;
                nodesAtMaxLevel.clear();
                nodesAtMaxLevel.add(current);
            } else if (level == maxLevel) {
                nodesAtMaxLevel.add(current);
            }

            if (level > 0 && level <= maxNeighborDepth) {
                neighborsUpToLevelK.add(current);
            }

            for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!levels.containsKey(neighbor)) {
                    levels.put(neighbor, level + 1);
                    queue.add(neighbor);
                }
            }
        }

        return new BfsMetrics(maxLevel, List.copyOf(nodesAtMaxLevel), List.copyOf(neighborsUpToLevelK));
    }
}
