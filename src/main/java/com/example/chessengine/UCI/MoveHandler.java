package com.example.chessengine.UCI;

@FunctionalInterface
public interface MoveHandler {
    void handleMove(int sourceRow, int sourceColumn, int targetRow, int targetColumn);
}
