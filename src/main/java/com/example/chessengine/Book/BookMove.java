package com.example.chessengine.Book;

/**
 * The BookMove record contains the information needed to find the corresponding Move object from a san move.
 * @param cellRow The row of the cell where the piece is being moved to.
 * @param cellCol The column of the cell where the piece is being moved to.
 * @param pieceRow The row of the piece being moved.
 * @param pieceCol The column of the piece being moved.
 */
public record BookMove(int cellRow, int cellCol, int pieceRow, int pieceCol) {
    /**
     * @return A string representation of the class, with each variable seperated by a space, all in square brackets
     */
    @Override
    public String toString() {
        return "[" + cellRow + " " + cellCol + " " + pieceRow + " " + pieceCol + "]";
    }
}