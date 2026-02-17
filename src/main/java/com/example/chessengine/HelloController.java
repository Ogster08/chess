package com.example.chessengine;

import com.example.chessengine.UCI.*;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HelloController {
    @FXML public GridPane ChessGrid;
    @FXML public StackPane BoardContainer;

    private Runnable onReadyCallback;

    public void setOnReadyCallback(Runnable callback){
        this.onReadyCallback = callback;
    }

    public void initialize(){
        ChessGrid.prefWidthProperty().bind(Bindings.min(BoardContainer.widthProperty(), BoardContainer.heightProperty()));
        ChessGrid.prefHeightProperty().bind(Bindings.min(BoardContainer.widthProperty(), BoardContainer.heightProperty()));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = new StackPane();
                if ((i+ j) % 2 == 0) square.setStyle("-fx-background-color: #F0D9B5");
                else square.setStyle("-fx-background-color: #B58863");
                ChessGrid.add(square, i, j);
                square.prefWidthProperty().bind(Bindings.min(ChessGrid.widthProperty().divide(8), ChessGrid.heightProperty().divide(8)));
                square.prefHeightProperty().bind(Bindings.min(ChessGrid.widthProperty().divide(8), ChessGrid.heightProperty().divide(8)));
            }
        }

        addPiece("/images/white queen.png", 1, 2);

        if (onReadyCallback != null) onReadyCallback.run();
    }

    private void addPiece(String imagePath, int row, int col){
        StackPane square = getSquare(row, col);
        assert square != null;

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView piece = new ImageView(image);

        piece.fitWidthProperty().bind(square.widthProperty().multiply(0.9));
        piece.fitHeightProperty().bind(square.heightProperty().multiply(0.9));
        piece.setPreserveRatio(true);


        square.getChildren().add(piece);
        StackPane.setAlignment(piece, Pos.CENTER);
    }

    private StackPane getSquare(int row, int col){
        // row and column conversion
        row = 7 - row;
        for (Node node: ChessGrid.getChildren()){
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col){
                return (StackPane) node;
            }
        }
        return null;
    }

    private static final Map<Class<?>, String> whitePieceToImagePath = new HashMap<>(){
        {
            put(Pawn.class, "/images/white pawn.png");
            put(Knight.class, "/images/white knight.png");
            put(Bishop.class, "/images/white bishop.png");
            put(Rook.class, "/images/white rook.png");
            put(Queen.class, "/images/white queen.png");
            put(King.class, "/images/white king.png");
        };
    };

    private static final Map<Class<?>, String> blackPieceToImagePath = new HashMap<>(){
        {
            put(Pawn.class, "/images/black pawn.png");
            put(Knight.class, "/images/black knight.png");
            put(Bishop.class, "/images/black bishop.png");
            put(Rook.class, "/images/black rook.png");
            put(Queen.class, "/images/black queen.png");
            put(King.class, "/images/black king.png");
        };
    };

    public void updatePosition(Board board){
        for (Node node: ChessGrid.getChildren()){
            ((StackPane) node).getChildren().clear();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getCell(i, j).getPiece();
                if (piece == null) continue;
                if (piece.getColour() == Colour.WHITE) addPiece(whitePieceToImagePath.get(piece.getClass()), i, j);
                else addPiece(blackPieceToImagePath.get(piece.getClass()), i, j);
            }
        }
    }

}