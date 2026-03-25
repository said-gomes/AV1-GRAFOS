package com.said.av1grafos.service;

import com.said.av1grafos.model.TraversalAlgorithm;
import com.said.av1grafos.model.TraversalResult;
import com.said.av1grafos.model.TraversalStep;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class TraversalService {

    public TraversalResult run(TraversalAlgorithm algorithm, Map<String, List<String>> graph, String source, String target) {
        return switch (algorithm) {
            case BFS -> runBfs(graph, source, target);
            case DFS -> runDfs(graph, source, target);
        };
    }

    private TraversalResult runBfs(Map<String, List<String>> graph, String source, String target) {
        Map<String, String> predecessors = initializePredecessors(graph);
        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        List<TraversalStep> steps = new ArrayList<>();

        queue.addLast(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();

            if (current.equals(target)) {
                steps.add(new TraversalStep(current, List.copyOf(queue)));
                break;
            }

            for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (visited.add(neighbor)) {
                    predecessors.put(neighbor, current);
                    queue.addLast(neighbor);
                }
            }

            steps.add(new TraversalStep(current, List.copyOf(queue)));
        }

        return new TraversalResult(TraversalAlgorithm.BFS, source, target, predecessors, List.copyOf(steps));
    }

    private TraversalResult runDfs(Map<String, List<String>> graph, String source, String target) {
        Map<String, String> predecessors = initializePredecessors(graph);
        Deque<String> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        List<TraversalStep> steps = new ArrayList<>();

        stack.push(source);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!visited.add(current)) {
                continue;
            }

            if (current.equals(target)) {
                steps.add(new TraversalStep(current, snapshot(stack)));
                break;
            }

            List<String> neighbors = new ArrayList<>(graph.getOrDefault(current, Collections.emptyList()));
            Collections.reverse(neighbors);

            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    predecessors.putIfAbsent(neighbor, current);
                    stack.push(neighbor);
                }
            }

            steps.add(new TraversalStep(current, snapshot(stack)));
        }

        return new TraversalResult(TraversalAlgorithm.DFS, source, target, predecessors, List.copyOf(steps));
    }

    private Map<String, String> initializePredecessors(Map<String, List<String>> graph) {
        Map<String, String> predecessors = new LinkedHashMap<>();
        graph.keySet().forEach(node -> predecessors.put(node, null));
        return predecessors;
    }

    private List<String> snapshot(Deque<String> structure) {
        return List.copyOf(new ArrayList<>(structure));
    }
}
