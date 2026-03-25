module com.said.av1grafos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.brunomnsilva.smartgraph;

    opens com.said.av1grafos to javafx.fxml;
    opens com.said.av1grafos.controller to javafx.fxml;


    exports com.said.av1grafos;
    exports com.said.av1grafos.controller;
    exports com.said.av1grafos.model;
}