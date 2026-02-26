package com.example.chessengine.Book;

public record BookMove(int cellRow, int cellCol, int pieceRow, int pieceCol) {
    @Override
    public String toString() {
        return "[" + cellRow + " " + cellCol + " " + pieceRow + " " + pieceCol + "]";
    }
}