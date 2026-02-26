module com.example.chessengine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.unsupported.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.example.chessengine to javafx.fxml;
    exports com.example.chessengine;
    exports com.example.chessengine.UCI;
    opens com.example.chessengine.UCI to javafx.fxml;
    exports com.example.chessengine.Book;
    opens com.example.chessengine.Book to javafx.fxml;
    exports com.example.chessengine.tablebase;
    opens com.example.chessengine.tablebase to com.fasterxml.jackson.databind;
}