package com.example.chessengine;

import com.example.chessengine.UCI.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application implements SceneSwitcher{
    private Stage stage;
    private GameState gameState;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader menuLoader = new FXMLLoader(HelloApplication.class.getResource("menu-page.fxml"));
        Scene menuScene = new Scene(menuLoader.load(), 480, 480);
        if (menuLoader.getController().getClass() == MenuController.class) ((MenuController) menuLoader.getController()).setSceneSwitcher(this);
        stage.setTitle("Chess");
        stage.setScene(menuScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if  (gameState.getClass() == EngineGameState.class){
            ((EngineGameState) gameState).stopEngineThread();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * @param GameStateClass the class type of the GameState to be used
     */
    @Override
    public void switchScene(Class<?> GameStateClass) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(HelloApplication.class.getResource("game-page.fxml"));
        Scene scene = new Scene(gameLoader.load(), 480, 480);
        stage.setScene(scene);
        if (GameStateClass == GameState.class) gameState = new GameState(gameLoader.getController());
        if (GameStateClass == EngineGameState.class) gameState = new EngineGameState(gameLoader.getController(), Colour.WHITE);
    }
}