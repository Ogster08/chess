module com.example.chessengine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.unsupported.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.example.chessengine to javafx.fxml;
    exports com.example.chessengine.UCI;
    opens com.example.chessengine.UCI to javafx.fxml;
    exports com.example.chessengine.Book;
    opens com.example.chessengine.Book to javafx.fxml;
    exports com.example.chessengine.Tablebase;
    opens com.example.chessengine.Tablebase to com.fasterxml.jackson.databind;
    exports com.example.chessengine.GUI;
    opens com.example.chessengine.GUI to javafx.fxml;
    exports com.example.chessengine.Engine;
    opens com.example.chessengine.Engine to javafx.fxml;
    exports com.example.chessengine.Board;
    opens com.example.chessengine.Board to javafx.fxml;
    exports com.example.chessengine.Board.Pieces;
    opens com.example.chessengine.Board.Pieces to javafx.fxml;
    exports com.example.chessengine.Board.Moves;
    opens com.example.chessengine.Board.Moves to javafx.fxml;
    exports com.example.chessengine;
}