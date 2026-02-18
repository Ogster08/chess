package com.example.chessengine;

import com.example.chessengine.UCI.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventListener;

public class HelloApplication extends Application implements MoveHandler {

    private Board board;
    private HelloController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 480);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();

        controller = fxmlLoader.getController();
        controller.setMoveHandler(this);

        board = createStartingChessboard();

        controller.updatePosition(board);
        //System.out.println(board.getPseudolegalMoves().size());
        //System.out.println(board.getPseudolegalMoves());
    }

    public static void main(String[] args) {
        launch();
    }

    public Board createStartingChessboard(){
        Board board = new Board();
        for (int col = 0; col < 8; col++) {
            board.addPiece(new Pawn(board, 1, col, Colour.WHITE));
        }
        board.addPiece(new Rook(board, 0, 0, Colour.WHITE, true));
        board.addPiece(new Rook(board, 0, 7, Colour.WHITE, true));

        board.addPiece(new Knight(board, 0, 1, Colour.WHITE));
        board.addPiece(new Knight(board, 0, 6, Colour.WHITE));

        board.addPiece(new Bishop(board, 0, 2, Colour.WHITE));
        board.addPiece(new Bishop(board, 0, 5, Colour.WHITE));

        board.addPiece(new Queen(board, 0, 3, Colour.WHITE));
        board.addPiece(new King(board, 0, 4, Colour.WHITE, true));

        for (int col = 0; col < 8; col++) {
            board.addPiece(new Pawn(board, 6, col, Colour.BLACK));
        }
        board.addPiece(new Rook(board, 7, 0, Colour.BLACK, true));
        board.addPiece(new Rook(board, 7, 7, Colour.BLACK, true));

        board.addPiece(new Knight(board, 7, 1, Colour.BLACK));
        board.addPiece(new Knight(board, 7, 6, Colour.BLACK));

        board.addPiece(new Bishop(board, 7, 2, Colour.BLACK));
        board.addPiece(new Bishop(board, 7, 5, Colour.BLACK));

        board.addPiece(new Queen(board, 7, 3, Colour.BLACK));
        board.addPiece(new King(board, 7, 4, Colour.BLACK, true));

        return board;
    }

    /**
     * @param sourceRow
     * @param sourceColumn
     * @param targetRow
     * @param targetColumn
     * @return
     */
    @Override
    public void handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
        for (Move move: board.getPseudolegalMoves()){
            if (move.p().getRow() == sourceRow && move.p().getCol() == sourceColumn && move.cell().getRow() == targetRow && move.cell().getCol() == targetColumn){
                board.movePiece(move);
                controller.updatePosition(board);
                return;
            }
        }
    }
}