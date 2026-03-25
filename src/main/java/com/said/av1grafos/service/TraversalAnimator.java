package com.said.av1grafos.service;

import com.said.av1grafos.model.TraversalAlgorithm;
import com.said.av1grafos.model.TraversalResult;
import com.said.av1grafos.model.TraversalStep;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;

public class TraversalAnimator {
    private static final int VISIT_DELAY_MS = 800;
    private static final int STRUCTURE_DELAY_MS = 600;

    public Task<Void> createTask(
            TraversalResult result,
            Consumer<String> log,
            Consumer<List<String>> updateStructure,
            Consumer<String> markVisited,
            Consumer<String> resetNode,
            Runnable onSucceeded
    ) {
        return new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> log.accept(System.lineSeparator()
                        + "[" + result.algorithm().name() + "] Iniciando: "
                        + result.source() + " -> " + result.target() + System.lineSeparator()));

                for (TraversalStep step : result.steps()) {
                    Platform.runLater(() -> {
                        markVisited.accept(step.currentNode());
                        updateStructure.accept(step.structureSnapshot());
                        log.accept("Visitando: " + step.currentNode() + System.lineSeparator());
                    });
                    sleep(VISIT_DELAY_MS);

                    Platform.runLater(() -> resetNode.accept(step.currentNode()));
                    sleep(STRUCTURE_DELAY_MS);
                }

                Platform.runLater(onSucceeded);
                return null;
            }
        };
    }

    public String labelFor(TraversalAlgorithm algorithm) {
        return algorithm == TraversalAlgorithm.BFS ? "Fila" : "Pilha";
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
