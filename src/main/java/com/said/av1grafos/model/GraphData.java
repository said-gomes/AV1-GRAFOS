package com.said.av1grafos.model;

import java.util.List;
import java.util.Map;

public record GraphData(Map<String, List<String>> adjacencyMap, boolean directed) {
}
