package com.said.av1grafos.model;

import java.util.List;

public record BfsMetrics(int maxLevel, List<String> nodesAtMaxLevel, List<String> neighborsUpToLevelK) {
}
