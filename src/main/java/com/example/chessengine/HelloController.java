package com.example.chessengine;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class HelloController {
    @FXML
    public GridPane ChessGrid;

    public void initialize(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pane square = new Pane();
                square.setPrefSize(100, 100);
                if ((i+ j) % 2 == 0) square.setStyle("-fx-background-color: #F0D9B5");
                else square.setStyle("-fx-background-color: #B58863");

                ChessGrid.add(square, i, j);
            }
        }
    }

}