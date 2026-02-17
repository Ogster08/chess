package com.example.chessengine;

import com.example.chessengine.UCI.Board;
import com.example.chessengine.UCI.Colour;
import com.example.chessengine.UCI.Pawn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventListener;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 480);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();

        HelloController controller = fxmlLoader.getController();

        Board board = new Board();
        board.addPiece(new Pawn(board, 1, 2, Colour.WHITE));

        controller.updatePosition(board);
        System.out.println(board.getPseudolegalMoves());
    }

    public static void main(String[] args) {
        launch();
    }
}