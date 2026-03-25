package com.said.av1grafos.model;

import java.util.List;
import java.util.Map;

public record TraversalResult(
        TraversalAlgorithm algorithm,
        String source,
        String target,
        Map<String, String> predecessors,
        List<TraversalStep> steps
) {
}
