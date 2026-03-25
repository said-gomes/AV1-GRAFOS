package com.said.av1grafos.model;

import java.util.List;

public record TraversalStep(String currentNode, List<String> structureSnapshot) {
}
