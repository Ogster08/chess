package com.example.chessengine;

import com.example.chessengine.UCI.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application{

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 480);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();

        EngineGameState engineGameState = new EngineGameState(fxmlLoader.getController(), Colour.WHITE);
    }

    public static void main(String[] args) {
        launch();
    }
}