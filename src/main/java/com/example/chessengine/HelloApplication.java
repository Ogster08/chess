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
    /**
     * The window that the pages are loaded onto.
     */
    private Stage stage;

    /**
     * The current GameState object being used.
     */
    private GameState gameState = null;

    /**
     * The menuScene, so it can be loaded to the stage.
     */
    private Scene menuScene;

    /**
     * Loads the menu page, and creates a new scene with it.
     * Sets up the stage, with the title and loading the menuScene.
     * @param stage The window that the pages are loaded onto.
     * @throws IOException error if something goes wrong when loading the menu page.
     */
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

    /**
     * Stops the engine thread if it is running when the program is stopped.
     */
    @Override
    public void stop() {
        if  (gameState != null && gameState.getClass() == EngineGameState.class){
            ((EngineGameState) gameState).stopEngineThread();
        }
    }

    /**
     * Main method, launches the GUI, and builds the openings book file if the environment variable BUILD_BOOKS is 'true'.
     * @param args The program arguments (none used)
     */
    public static void main(String[] args) {
        if ("true".equals(System.getenv("BUILD_BOOK"))){
            BookCreator bc = new BookCreator();
            bc.createBook();
        }
        launch();
    }

    @Override
    public void gameSwitcher(Class<?> GameStateClass, Colour colour) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(HelloApplication.class.getResource("game-page.fxml"));
        Scene scene = new Scene(gameLoader.load(), 480, 480);
        stage.setScene(scene);
        if (GameStateClass == GameState.class) gameState = new GameState(gameLoader.getController());
        if (GameStateClass == EngineGameState.class) gameState = new EngineGameState(gameLoader.getController(), colour);
        if (gameLoader.getController().getClass() == ChessController.class) ((ChessController) gameLoader.getController()).setSceneSwitcher(this);
    }

    @Override
    public void menuSwitcher() throws IOException {
        if  (gameState != null && gameState.getClass() == EngineGameState.class){
            ((EngineGameState) gameState).stopEngineThread();
        }
        stage.setScene(menuScene);
    }
}