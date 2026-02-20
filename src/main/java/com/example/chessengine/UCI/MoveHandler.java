package com.example.chessengine.UCI;

import java.util.List;

public interface MoveHandler {
    boolean handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn);
    List<Move> getLegalMoves(int row, int col);
}
