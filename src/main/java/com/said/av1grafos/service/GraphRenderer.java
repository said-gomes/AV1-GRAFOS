package com.said.av1grafos.service;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphRenderer {
    private static final String DEFAULT_VERTEX_STYLE = "vertex";
    private static final String VISITED_VERTEX_STYLE = "visited-vertex";
    private static final String PATH_VERTEX_STYLE = "path-vertex";

    private SmartGraphPanel<String, String> graphView;

    public void render(Map<String, List<String>> graph, boolean directed, AnchorPane container) {
        Graph<String, String> visualGraph = new GraphEdgeList<>();
        graph.keySet().forEach(visualGraph::insertVertex);

        Set<String> insertedEdges = new HashSet<>();
        int edgeIndex = 0;

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            for (String neighbor : entry.getValue()) {
                String edgeKey = directed
                        ? entry.getKey() + "->" + neighbor
                        : normalizeUndirectedEdge(entry.getKey(), neighbor);

                if (!insertedEdges.add(edgeKey)) {
                    continue;
                }

                visualGraph.insertEdge(entry.getKey(), neighbor, "e" + edgeIndex++);
            }
        }

        graphView = new SmartGraphPanel<>(visualGraph, new SmartCircularSortedPlacementStrategy());
        graphView.setPrefWidth(800);
        graphView.setPrefHeight(600);

        container.getChildren().setAll(graphView);
        Platform.runLater(graphView::init);
    }

    public void resetStyles(Iterable<String> nodes) {
        for (String node : nodes) {
            applyStyle(node, DEFAULT_VERTEX_STYLE);
        }
    }

    public void markVisited(String node) {
        applyStyle(node, VISITED_VERTEX_STYLE);
    }

    public void resetNode(String node) {
        applyStyle(node, DEFAULT_VERTEX_STYLE);
    }

    public void highlightPath(List<String> path) {
        path.forEach(node -> applyStyle(node, PATH_VERTEX_STYLE));
    }

    private void applyStyle(String node, String styleClass) {
        if (graphView == null || node == null) {
            return;
        }

        try {
            graphView.getStylableVertex(node).setStyleClass(styleClass);
        } catch (RuntimeException ignored) {
        }
    }

    private String normalizeUndirectedEdge(String source, String target) {
        return source.compareTo(target) <= 0 ? source + "--" + target : target + "--" + source;
    }
}
