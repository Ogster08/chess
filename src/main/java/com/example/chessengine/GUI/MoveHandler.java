package com.example.chessengine.GUI;

import com.example.chessengine.Board.Moves.Move;

import java.util.List;

/**
 * Interface to define the methods to link moves between the virtual game, and display of the board.
 */
public interface MoveHandler {
    /**
     * If the move data provided corresponds to a legal move, then the move will be performed on the virtual board.
     * @param sourceRow The row of the piece trying to be moved.
     * @param sourceColumn The column of the piece trying to be moved.
     * @param targetRow The row of the target square of the move.
     * @param targetColumn The column of the target square of the move.
     * @return If the move is legal in the current board position.
     */
    boolean handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn);

    /**
     * Gets a list of all the legal moves, from the piece being dragged at.
     * Used to display the squares for the legal moves.
     * @param row The row of the piece.
     * @param col The column of the piece.
     * @return A list of all the legal moves that piece can do in the current position.
     */
    List<Move> getLegalMoves(int row, int col);
}
