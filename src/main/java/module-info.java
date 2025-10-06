module com.example.chessengine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.chessengine to javafx.fxml;
    exports com.example.chessengine;
}