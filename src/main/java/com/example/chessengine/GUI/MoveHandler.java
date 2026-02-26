package com.example.chessengine.GUI;

import com.example.chessengine.Board.Moves.Move;

import java.util.List;

public interface MoveHandler {
    boolean handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn);
    List<Move> getLegalMoves(int row, int col);
}
