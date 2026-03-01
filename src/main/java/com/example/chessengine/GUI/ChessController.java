package com.example.chessengine.GUI;

import com.example.chessengine.Board.*;
import com.example.chessengine.Board.Moves.Move;
import com.example.chessengine.Board.Pieces.*;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;

/**
 * The FXML controller class for the game page.
 */
public class ChessController {
    /**
     * The FXMl button object to do a new game.
     */
    @FXML Button newGame;

    /**
     * The FXML GridPane object containing everything in the chess board.
     */
    @FXML GridPane ChessGrid;

    /**
     * The FXML StackPane object container containing the chess board.
     */
    @FXML StackPane BoardContainer;

    /**
     * The text object for the game end message.
     */
    private final Text text = new Text();
    /**
     * The rectangle to cover then board, with the game over text.
     */
    private final StackPane rect = new StackPane();

    /**
     * The object to switch the scene.
     */
    private SceneSwitcher sceneSwitcher;

    /**
     * The object to handle all moves tried by the player, and to get a list of moves a piece can do to display where it can move to.
     */
    private MoveHandler moveHandler;

    /**
     * The square of the piece being dragged.
     */
    private Pane currentOriginSquare = null;

    /**
     * The list of squares a selected piece can move to.
     */
    private final List<Pane> showMovesSquares = new ArrayList<>();

    /**
     * @param moveHandler The MoveHandler object this will use.
     */
    public void setMoveHandler(MoveHandler moveHandler){
        this.moveHandler = moveHandler;
    }

    /**
     * @param sceneSwitcher The SCeneSwitcher object this will use.
     */
    public void setSceneSwitcher(SceneSwitcher sceneSwitcher){
        this.sceneSwitcher = sceneSwitcher;
    }

    /**
     * Initialises the page, with a board that automatically grows and shrinks, and prepares the ending message.
     * Sets the drag events relevant to the squares.
     */
    public void initialize(){
        BoardContainer.setAlignment(Pos.CENTER_LEFT);
        rect.maxWidthProperty().bind(Bindings.min(ChessGrid.widthProperty(), ChessGrid.heightProperty()));
        rect.maxHeightProperty().bind(Bindings.min(ChessGrid.widthProperty(), ChessGrid.heightProperty()));
        text.setFont(new Font("Verdana", 40));
        rect.getChildren().add(text);
        rect.setStyle("-fx-background-color: rgba(100, 100, 100, 0.5);");


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
    }

    /**
     * Adds a piece to the board, making it automatically resize and be draggable.
     * @param image The image of the piece being added.
     * @param row The row where it is being added.
     * @param col The column where it is being added.
     */
    private void addPiece(Image image, int row, int col){
        StackPane square = getSquare(row, col);
        assert square != null;

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

    /**
     * Flips the row from what the Board class uses to where it is displayed and gets the square.
     * @param row The row of the square
     * @param col The column of the square
     * @return The square at the given row and column
     */
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

    /**
     * A hashmap for each piece class to the corresponding white piece file.
     */
    private static final Map<Class<?>, Image> whitePieceToImagePath = new HashMap<>(){
        {
            put(Pawn.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white pawn.png"))));
            put(Knight.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white knight.png"))));
            put(Bishop.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white bishop.png"))));
            put(Rook.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white rook.png"))));
            put(Queen.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white queen.png"))));
            put(King.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/white king.png"))));
        };
    };

    /**
     * A hashmap for each piece class to the corresponding black piece file.
     */
    private static final Map<Class<?>, Image> blackPieceToImagePath = new HashMap<>(){
        {
            put(Pawn.class,   new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black pawn.png"))));
            put(Knight.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black knight.png"))));
            put(Bishop.class, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black bishop.png"))));
            put(Rook.class,   new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black rook.png"))));
            put(Queen.class,  new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black queen.png"))));
            put(King.class,   new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black king.png"))));
        };
    };

    /**
     * Updates the display of where all the pieces are using the board given.
     * @param board The Board object used to update the positions.
     */
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

    /**
     * Updates the style of the current origin square, and then updates the new one.
     * @param square The new origin square (null if now new square)
     */
    private void updateOriginSquare(Pane square){
        if (currentOriginSquare != null) currentOriginSquare.getStyleClass().remove("origin-square");
        currentOriginSquare = square;
        if (currentOriginSquare != null) currentOriginSquare.getStyleClass().add("origin-square");
    }

    /**
     * Updates all the squares that have a dot on them to show which moves are legal for the selected piece.
     * @param moves The list of moves to update which moves to show
     */
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

    /**
     * Adds a grey half transparent dot to the square, that resizes automatically.
     * @param square The square where the dot is added to.
     */
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

    /**
     * Removes any dots from the square
     * @param square The square, where the dot is being removed from
     */
    private void removeDot(Pane square){
        square.getChildren().removeIf(node ->
            "dot".equals(node.getUserData())
        );
    }

    /**
     * Creates a dialog to choose the promotion piece, showing pieces of the correct colour.
     * Returns the class of the piece chosen, with null for the default, which reverts the move.
     * @param colour The colour the promotion piece will become.
     * @return The piece class chosen for promotion.
     */
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
            Image image;
            if (colour == Colour.WHITE) image = whitePieceToImagePath.get(c);
            else image = blackPieceToImagePath.get(pieces[i]);

            Button button = new Button();
            ImageView imageView = new ImageView(image);
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

    /**
     * Switches to the menu page if a SceneSwitcher has been set.
     * @throws IOException Error if the switch is unsuccessful.
     */
    public void loadMenu() throws IOException {
        if (sceneSwitcher != null) sceneSwitcher.menuSwitcher();
    }

    /**
     * sets the game end text to the string given.
     * Adds the game over display to the board container, to indicate the end of the game.
     * @param message the game end message to be displayed.
     */
    public void gameOverMessage(String message){
        text.setText(message);
        BoardContainer.getChildren().add(rect);
    }
}