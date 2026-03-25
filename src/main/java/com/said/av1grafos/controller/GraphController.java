package com.said.av1grafos.controller;

import com.said.av1grafos.model.BfsMetrics;
import com.said.av1grafos.model.GraphData;
import com.said.av1grafos.model.GraphStatistics;
import com.said.av1grafos.model.TraversalAlgorithm;
import com.said.av1grafos.model.TraversalResult;
import com.said.av1grafos.service.GraphRenderer;
import com.said.av1grafos.service.GraphStatisticsService;
import com.said.av1grafos.service.TraversalAnimator;
import com.said.av1grafos.service.TraversalService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GraphController {
    private static final int BFS_NEIGHBOR_DEPTH = 2;

    @FXML private TextArea outputArea;
    @FXML private AnchorPane graphContainer;
    @FXML private HBox dataStructureView;
    @FXML private TextField txtSource;
    @FXML private TextField txtTarget;
    @FXML private Label lblTipo;
    @FXML private Label lblOrdemTamanho;
    @FXML private Label lblDensidade;
    @FXML private Label lblDistanciaT;
    @FXML private TextArea txtGraus;
    @FXML private Button btnRunBfs;
    @FXML private Button btnRunDfs;
    @FXML private Label lblStructureTitle;

    private final TraversalService traversalService = new TraversalService();
    private final TraversalAnimator traversalAnimator = new TraversalAnimator();
    private final GraphStatisticsService statisticsService = new GraphStatisticsService();
    private final GraphRenderer graphRenderer = new GraphRenderer();

    private GraphData graphData;

    @FXML
    public void initialize() {
        resetDistanceLabel();
    }

    public void setGraphData(GraphData graphData) {
        this.graphData = graphData;

        if (graphData == null || graphData.adjacencyMap().isEmpty()) {
            outputArea.setText("Nenhum grafo foi carregado.\n");
            return;
        }

        graphRenderer.render(graphData.adjacencyMap(), graphData.directed(), graphContainer);
        updateStatistics();
        outputArea.setText("Grafo carregado. Pronto para simulacao.\n");
    }

    @FXML
    public void handleRunBFS() {
        runTraversal(TraversalAlgorithm.BFS);
    }

    @FXML
    public void handleRunDFS() {
        runTraversal(TraversalAlgorithm.DFS);
    }

    private void runTraversal(TraversalAlgorithm algorithm) {
        if (!hasGraph()) {
            appendOutput("Nenhum grafo disponivel.\n");
            return;
        }

        String source = txtSource.getText().trim();
        String target = txtTarget.getText().trim();

        if (!validateInput(source, target)) {
            return;
        }

        resetTraversalUi();
        lblStructureTitle.setText("Estrutura de Dados (" + traversalAnimator.labelFor(algorithm) + "):");

        TraversalResult result = traversalService.run(algorithm, graphData.adjacencyMap(), source, target);

        Task<Void> animationTask = traversalAnimator.createTask(
                result,
                this::appendOutput,
                this::updateStructureView,
                graphRenderer::markVisited,
                graphRenderer::resetNode,
                () -> finishTraversal(result)
        );

        animationTask.setOnRunning(event -> setControlsDisabled(true));
        animationTask.setOnSucceeded(event -> setControlsDisabled(false));
        animationTask.setOnFailed(event -> {
            setControlsDisabled(false);
            Throwable error = animationTask.getException();
            appendOutput("Erro durante a simulacao: "
                    + (error != null ? error.getMessage() : "desconhecido")
                    + System.lineSeparator());
        });

        Thread worker = new Thread(animationTask, algorithm.name().toLowerCase() + "-animation");
        worker.setDaemon(true);
        worker.start();
    }

    private boolean hasGraph() {
        return graphData != null && graphData.adjacencyMap() != null;
    }

    private boolean validateInput(String source, String target) {
        Map<String, List<String>> graph = graphData.adjacencyMap();

        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            appendOutput("Vertice inexistente.\n");
            return false;
        }

        return true;
    }

    private void finishTraversal(TraversalResult result) {
        if (result.algorithm() == TraversalAlgorithm.BFS) {
            appendBfsMetrics(result.source());
        }

        showFinalPath(result.predecessors(), result.source(), result.target());
    }

    private void appendBfsMetrics(String source) {
        BfsMetrics metrics = statisticsService.bfsMetrics(graphData.adjacencyMap(), source, BFS_NEIGHBOR_DEPTH);
        if (metrics == null) {
            return;
        }

        appendOutput("\n--- RESULTADO BFS ---\n");
        appendOutput("Maior nivel alcancado: " + metrics.maxLevel() + "\n");
        appendOutput("Vertices no nivel " + metrics.maxLevel() + ": " + metrics.nodesAtMaxLevel() + "\n");
        appendOutput("Vizinhanca ate nivel " + BFS_NEIGHBOR_DEPTH + ": " + metrics.neighborsUpToLevelK() + "\n");
    }

    private void showFinalPath(Map<String, String> predecessors, String source, String target) {
        List<String> path = buildPath(predecessors, target);

        if (!path.isEmpty() && source.equals(path.get(0))) {
            lblDistanciaT.setText("Distancia: " + (path.size() - 1));
            appendOutput("\n=== ROTA ENCONTRADA ===\n");
            appendOutput("Caminho: " + String.join(" -> ", path) + "\n");
            graphRenderer.highlightPath(path);
            return;
        }

        lblDistanciaT.setText("Inalcancavel");
        appendOutput("\n=== DESTINO INALCANCAVEL ===\n");
    }

    private List<String> buildPath(Map<String, String> predecessors, String target) {
        List<String> path = new ArrayList<>();
        String current = target;

        while (current != null) {
            path.add(current);
            current = predecessors.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    private void updateStatistics() {
        GraphStatistics statistics = statisticsService.summarize(graphData.adjacencyMap(), graphData.directed());

        lblTipo.setText("Tipo: " + (graphData.directed() ? "Direcionado" : "Nao-Direcionado"));
        lblOrdemTamanho.setText("|V|: " + statistics.vertexCount() + " | |E|: " + statistics.edgeCount());
        lblDensidade.setText(String.format("Densidade: %.4f", statistics.density()));

        StringBuilder builder = new StringBuilder("--- Graus dos Vertices ---\n");
        statistics.degreeLines().forEach(line -> builder.append(line).append('\n'));
        builder.append("\n--- Top-3 Hubs ---\n");
        statistics.topHubs().forEach(entry ->
                builder.append(entry.getKey()).append(" (Grau: ").append(entry.getValue()).append(")\n"));

        txtGraus.setText(builder.toString());
    }

    private void resetTraversalUi() {
        graphRenderer.resetStyles(graphData.adjacencyMap().keySet());
        dataStructureView.getChildren().clear();
        outputArea.clear();
        resetDistanceLabel();
    }

    private void resetDistanceLabel() {
        if (lblDistanciaT != null) {
            lblDistanciaT.setText("Distancia: -");
        }
    }

    private void updateStructureView(Collection<String> items) {
        dataStructureView.getChildren().clear();
        for (String item : items) {
            Label label = new Label(item);
            label.getStyleClass().add("structure-item");
            dataStructureView.getChildren().add(label);
        }
    }

    private void appendOutput(String message) {
        outputArea.appendText(message);
    }

    private void setControlsDisabled(boolean disabled) {
        btnRunBfs.setDisable(disabled);
        btnRunDfs.setDisable(disabled);
        txtSource.setDisable(disabled);
        txtTarget.setDisable(disabled);
    }
}
