package com.example.chessengine.UCI;

/**
 * interface to define the method to link cell changes and pieces updating valid moves
 */
public interface CellListener {
    /**
     * Method called when a cell is changed. It is implemented by all pieces, and is used to update pseudolegal moves.
     *
     * @param row The row of the changed cell
     * @param col The column of the changed cell
     * @param oldColour The colour of the piece that used to be in the cell
     * @param newColour The colour of the new pice in the cell
     */
    void CellChanged(int row, int col, Colour oldColour, Colour newColour);
}
