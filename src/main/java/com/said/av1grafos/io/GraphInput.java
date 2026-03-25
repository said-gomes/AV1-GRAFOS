package com.said.av1grafos.io;

import com.said.av1grafos.model.GraphData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GraphInput {
    public static GraphData readGraphFile(String filePath) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            Map<String, List<String>> adjacencyMap = new HashMap<>();

            if (!scanner.hasNextInt()) {
                return new GraphData(adjacencyMap, false);
            }

            int verticesCount = scanner.nextInt();
            int edgesCount = scanner.nextInt();
            boolean directed = scanner.next().equalsIgnoreCase("D");

            for (int i = 0; i < verticesCount; i++) {
                adjacencyMap.put(String.valueOf(i), new ArrayList<>());
            }

            for (int i = 0; i < edgesCount; i++) {
                if (!scanner.hasNext()) {
                    break;
                }

                String source = scanner.next();
                String destination = scanner.next();

                adjacencyMap.computeIfAbsent(source, ignored -> new ArrayList<>()).add(destination);
                adjacencyMap.computeIfAbsent(destination, ignored -> new ArrayList<>());

                if (!directed) {
                    adjacencyMap.get(destination).add(source);
                }
            }

            return new GraphData(adjacencyMap, directed);
        }
    }
}
