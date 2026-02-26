package com.example.chessengine;

import com.example.chessengine.Board.Colour;
import com.example.chessengine.Book.BookCreator;
import com.example.chessengine.GUI.ChessController;
import com.example.chessengine.GUI.MenuController;
import com.example.chessengine.GUI.SceneSwitcher;
import com.example.chessengine.UCI.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application implements SceneSwitcher {
    private Stage stage;
    private GameState gameState = null;
    private Scene menuScene;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader menuLoader = new FXMLLoader(HelloApplication.class.getResource("menu-page.fxml"));
        menuScene = new Scene(menuLoader.load(), 480, 480);
        if (menuLoader.getController().getClass() == MenuController.class) ((MenuController) menuLoader.getController()).setSceneSwitcher(this);
        stage.setTitle("Chess");
        stage.setScene(menuScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if  (gameState != null && gameState.getClass() == EngineGameState.class){
            ((EngineGameState) gameState).stopEngineThread();
        }
    }

    public static void main(String[] args) {
        launch();
        //BookCreator bc = new BookCreator();
        //bc.createBook();
    }

    /**
     * @param GameStateClass the class type of the GameState to be used
     */
    @Override
    public void gameSwitcher(Class<?> GameStateClass, Colour colour) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(HelloApplication.class.getResource("game-page.fxml"));
        Scene scene = new Scene(gameLoader.load(), 480, 480);
        stage.setScene(scene);
        if (GameStateClass == GameState.class) gameState = new GameState(gameLoader.getController());
        if (GameStateClass == EngineGameState.class) gameState = new EngineGameState(gameLoader.getController(), colour);
        if (gameLoader.getController().getClass() == ChessController.class) ((ChessController) gameLoader.getController()).setSceneSwitcher(this);
    }

    /**
     * @throws IOException
     */
    @Override
    public void menuSwitcher() throws IOException {
        stage.setScene(menuScene);
    }
}