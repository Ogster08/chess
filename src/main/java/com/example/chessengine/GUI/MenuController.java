package com.example.chessengine.GUI;

import com.example.chessengine.Board.Colour;
import com.example.chessengine.UCI.EngineGameState;
import com.example.chessengine.UCI.GameState;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * The FXMl controller for the menu page
 */
public class MenuController {
    /**
     * The GridPane object for the menu buttons and text.
     */
    @FXML GridPane menuGrid;

    /**
     * The main container for the page.
     */
    @FXML VBox vBox;

    /**
     * The button object to start a multiplayer game.
     */
    @FXML Button multiplayerButton;

    /**
     * The button object to start a game against the engine.
     */
    @FXML Button engineButton;

    /**
     * The ToggleSwitch object used to change the player colour for a game against the engine.
     */
    private final ToggleSwitch colourToggle = new ToggleSwitch();

    /**
     * The object to switch the scene.
     */
    private SceneSwitcher sceneSwitcher;

    /**
     * Initialises the menu, by aligning the menu grid to the centre.
     * adds the colourToggle to the menu grid.
     */
    public void initialize(){
        vBox.setAlignment(Pos.CENTER);
        menuGrid.add(colourToggle, 0, 3);
    }

    /**
     * Switches to the game page, as a multiplayer game, if a SceneSwitcher has been set.
     * @throws IOException Error if the switch is unsuccessful.
     */
    public void setGameToMultiplayer() throws IOException {
        sceneSwitcher.gameSwitcher(GameState.class, colourToggle.switchOnProperty().get() ? Colour.BLACK: Colour.WHITE);
    }

    /**
     * Switches to the game page, as n engine game, if a SceneSwitcher has been set.
     * @throws IOException Error if the switch is unsuccessful.
     */
    public void setGameToEngine() throws IOException {
        sceneSwitcher.gameSwitcher(EngineGameState.class, colourToggle.switchOnProperty().get() ? Colour.BLACK: Colour.WHITE);
    }

    /**
     * @param sceneSwitcher The SCeneSwitcher object this will use.
     */
    public void setSceneSwitcher(SceneSwitcher sceneSwitcher) {
        this.sceneSwitcher = sceneSwitcher;
    }
}
