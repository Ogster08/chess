package com.example.chessengine;

import com.example.chessengine.UCI.*;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.*;

public class ChessController {
    @FXML Button newGame;
    @FXML GridPane ChessGrid;
    @FXML StackPane BoardContainer;

    private SceneSwitcher sceneSwitcher;
    private MoveHandler moveHandler;
    private Pane currentOriginSquare = null;
    private final List<Pane> showMovesSquares = new ArrayList<>();

    public void setMoveHandler(MoveHandler moveHandler){
        this.moveHandler = moveHandler;
    }

    public void setSceneSwitcher(SceneSwitcher sceneSwitcher){this.sceneSwitcher = sceneSwitcher;}

    public void initialize(){
        newGame.prefWidthProperty().bind(Bindings.min(ChessGrid.widthProperty(), ChessGrid.heightProperty()));
        ChessGrid.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        ChessGrid.prefWidthProperty().bind(Bindings.min(BoardContainer.widthProperty(), BoardContainer.heightProperty()));
        ChessGrid.prefHeightProperty().bind(Bindings.min(BoardContainer.widthProperty(), BoardContainer.heightProperty()));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = new StackPane();
                if ((i+ j) % 2 == 0) square.getStyleClass().add("light-square");
                else square.getStyleClass().add("dark-square");
                ChessGrid.add(square, i, j);

                square.prefWidthProperty().bind(Bindings.min(ChessGrid.widthProperty().divide(8), ChessGrid.heightProperty().divide(8)));
                square.prefHeightProperty().bind(Bindings.min(ChessGrid.widthProperty().divide(8), ChessGrid.heightProperty().divide(8)));

                int finalI1 = i;
                int finalJ1 = j;
                square.setOnDragOver(dragEvent -> {
                    if (dragEvent.getGestureSource() != square && dragEvent.getDragboard().hasString()){
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                        if (!square.getStyleClass().contains("highlighted-square")) square.getStyleClass().add("highlighted-square");
                    }
                    dragEvent.consume();
                });

                square.setOnDragExited(dragEvent -> {
                    square.getStyleClass().remove("highlighted-square");
                    dragEvent.consume();
                });

                // row and column conversion and temp variables needed
                int finalI = 7 - j;
                int finalJ = i;
                square.setOnDragDropped(dragEvent -> {
                    Dragboard dragboard = dragEvent.getDragboard();
                    if (dragboard.hasString()){
                        String[] parts = dragboard.getString().split(",");
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);
                        if (moveHandler != null) moveHandler.handleMove(row, col, finalI, finalJ);
                        updateOriginSquare(null);
                        dragEvent.setDropCompleted(true);
                    }
                    dragEvent.consume();
                });
            }
        }

        addPiece("/images/white queen.png", 1, 2);
    }

    private void addPiece(String imagePath, int row, int col){
        StackPane square = getSquare(row, col);
        assert square != null;

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView piece = new ImageView(image);

        piece.fitWidthProperty().bind(square.widthProperty().multiply(0.9));
        piece.fitHeightProperty().bind(square.heightProperty().multiply(0.9));
        piece.setPreserveRatio(true);

        piece.setOnDragDetected(mouseEvent -> {
            Dragboard dragboard = piece.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(row + "," + col);
            dragboard.setContent(content);
            if (!square.getStyleClass().contains("origin-square")) updateOriginSquare(square);
            if (moveHandler != null) updateShowMoveSquares(moveHandler.getLegalMoves(row, col));
            mouseEvent.consume();
        });

        square.getChildren().add(piece);
        StackPane.setAlignment(piece, Pos.CENTER);
    }

    private StackPane getSquare(int row, int col){
        // row conversion
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

    private void updateOriginSquare(Pane square){
        if (currentOriginSquare != null) currentOriginSquare.getStyleClass().remove("origin-square");
        currentOriginSquare = square;
        if (currentOriginSquare != null) currentOriginSquare.getStyleClass().add("origin-square");
    }

    private void updateShowMoveSquares(List<Move> moves){
        for (Pane square: showMovesSquares){
            removeDot(square);
        }
        showMovesSquares.clear();
        for (Move move: moves){
            Pane square = getSquare(move.cell().getRow(), move.cell().getCol());
            assert square != null;
            showMovesSquares.add(square);
            addDot(square);
        }
    }

    private void addDot(Pane square){

        Region dot = new Region();
        dot.setUserData("dot");
        dot.getStyleClass().add("dot");
        dot.setMouseTransparent(true);

        dot.prefWidthProperty().bind(square.widthProperty().multiply(0.25));
        dot.prefHeightProperty().bind(square.heightProperty().multiply(0.25));
        dot.maxWidthProperty().bind(dot.prefWidthProperty());
        dot.maxHeightProperty().bind(dot.prefHeightProperty());

        square.getChildren().add(dot);
        StackPane.setAlignment(dot, Pos.CENTER);
    }

    private void removeDot(Pane square){
        square.getChildren().removeIf(node ->
            "dot".equals(node.getUserData())
        );
    }

    public Class<?> choosePromotionPiece(Colour colour){
        Dialog<Class<?>> dialog = new Dialog<>();
        dialog.setTitle("Choose promotion");

        GridPane imageGrid = new GridPane();
        imageGrid.setHgap(10);
        imageGrid.setVgap(10);
        imageGrid.setPadding(new Insets(10));

        Class<?>[] pieces = {Queen.class, Rook.class, Bishop.class, Knight.class};

        for (int i = 0; i < 4; i++) {
            Class<?> c = pieces[i];
            String imagePath;
            if (colour == Colour.WHITE) imagePath = whitePieceToImagePath.get(c);
            else imagePath = blackPieceToImagePath.get(pieces[i]);

            Button button = new Button();
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
            imageView.setPreserveRatio(true);

            button.setGraphic(imageView);

            button.setOnAction(actionEvent -> {
                dialog.setResult(c);;
                dialog.close();
            });

            imageGrid.add(button, i, 0);
        }

        dialog.getDialogPane().setContent(imageGrid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> null);

        return dialog.showAndWait().orElse(null);
    }

    public void loadMenu() throws IOException {
        if (sceneSwitcher != null) sceneSwitcher.menuSwitcher();
    }

}