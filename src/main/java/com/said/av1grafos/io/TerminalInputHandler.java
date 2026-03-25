package com.said.av1grafos.io;

import com.said.av1grafos.model.GraphData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TerminalInputHandler {

    public static GraphData dataInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1) Carregar topologia de 'data.txt'");
        System.out.println("2) Digitar topologia manualmente");
        System.out.print("Escolha uma opcao: ");

        int option = scanner.nextInt();

        if (option == 1) {
            try {
                GraphData graphData = GraphInput.readGraphFile("src/main/resources/com/said/av1grafos/data.txt");
                System.out.println("Arquivo carregado com sucesso");
                return graphData;
            } catch (Exception e) {
                System.err.println("Erro ao ler arquivo: " + e.getMessage());
                return new GraphData(new HashMap<>(), false);
            }
        }

        System.out.println("\n--- Entrada Manual (Formato: n m tipo) ---");
        System.out.println("Exemplo: 3 2 U (3 vertices, 2 arestas, Nao-direcionado)");

        int vertices = scanner.nextInt();
        int edges = scanner.nextInt();
        boolean directed = scanner.next().equalsIgnoreCase("D");
        Map<String, List<String>> adjacencyMap = new HashMap<>();

        for (int i = 0; i < vertices; i++) {
            adjacencyMap.put(String.valueOf(i), new ArrayList<>());
        }

        System.out.println("Digite as " + edges + " arestas (u v):");
        for (int i = 0; i < edges; i++) {
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
