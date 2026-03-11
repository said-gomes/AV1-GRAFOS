module com.said.av1grafos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.said.av1grafos to javafx.fxml;
    exports com.said.av1grafos;
}