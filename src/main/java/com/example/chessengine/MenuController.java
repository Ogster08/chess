package com.example.chessengine;

import com.example.chessengine.UCI.EngineGameState;
import com.example.chessengine.UCI.GameState;
import com.example.chessengine.UCI.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MenuController {
    @FXML VBox vBox;
    @FXML Button multiplayerButton;
    @FXML Button engineButton;

    private SceneSwitcher sceneSwitcher;

    public void initialise(){
        vBox.setAlignment(Pos.CENTER);
    }

    public void setGameToMultiplayer() throws IOException {
        sceneSwitcher.switchScene(GameState.class);
    }

    public void setGameToEngine() throws IOException {
        sceneSwitcher.switchScene(EngineGameState.class);
    }

    public void setSceneSwitcher(SceneSwitcher sceneSwitcher) {
        this.sceneSwitcher = sceneSwitcher;
    }
}
