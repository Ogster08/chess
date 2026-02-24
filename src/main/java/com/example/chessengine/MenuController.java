package com.example.chessengine;

import com.example.chessengine.UCI.Colour;
import com.example.chessengine.UCI.EngineGameState;
import com.example.chessengine.UCI.GameState;
import com.example.chessengine.UCI.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MenuController {
    @FXML GridPane menuGrid;
    @FXML VBox vBox;
    @FXML Button multiplayerButton;
    @FXML Button engineButton;
    private ToggleSwitch colourToggle;

    private SceneSwitcher sceneSwitcher;

    public void initialize(){
        vBox.setAlignment(Pos.CENTER);

        colourToggle = new ToggleSwitch();
        menuGrid.add(colourToggle, 0, 3);
    }

    public void setGameToMultiplayer() throws IOException {
        sceneSwitcher.gameSwitcher(GameState.class, colourToggle.switchOnProperty().get() ? Colour.BLACK: Colour.WHITE);
    }

    public void setGameToEngine() throws IOException {
        sceneSwitcher.gameSwitcher(EngineGameState.class, colourToggle.switchOnProperty().get() ? Colour.BLACK: Colour.WHITE);
    }

    public void setSceneSwitcher(SceneSwitcher sceneSwitcher) {
        this.sceneSwitcher = sceneSwitcher;
    }
}
