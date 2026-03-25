package com.said.av1grafos;

import com.said.av1grafos.controller.GraphController;
import com.said.av1grafos.io.TerminalInputHandler;
import com.said.av1grafos.model.GraphData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static GraphData inputGraphData;

    public static void main(String[] args) {
        inputGraphData = TerminalInputHandler.dataInput();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("graph-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        GraphController controller = fxmlLoader.getController();
        controller.setGraphData(inputGraphData);

        stage.setTitle("AV1 - Teoria dos Grafos");
        stage.setScene(scene);
        stage.show();
    }
}
